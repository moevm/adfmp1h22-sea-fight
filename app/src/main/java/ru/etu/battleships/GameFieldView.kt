package ru.etu.battleships

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import androidx.core.content.res.ResourcesCompat

class GameFieldView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    private val fillPaint = Paint()
    private val strokePaint = Paint()
    private val textPaint = Paint()

    private val strokeRatio = 1f / 10f

    private var offsetX = 0f
    private var offsetY = 0f
    private var cellSize = 0f

    private val ships: MutableSet<Ship> = mutableSetOf()

    private val callbacks: MutableList<(View) -> Unit> = mutableListOf()

    private val map: MutableMap<Ship, View> = mutableMapOf()

    private var selectedShip: Ship? = null

    private val dragListener = OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text

                view.invalidate()

                val v = event.localState as View
                val destination = view as GameFieldView
                val hasPlaced = destination.addShip(dragData, event.x, event.y, v)
                hasPlaced
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }

    init {
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.WHITE

        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = Color.BLACK
        strokePaint.strokeCap = Paint.Cap.SQUARE

        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.BLACK

        if (!this.isInEditMode) {
            textPaint.typeface = ResourcesCompat.getFont(context, R.font.aladin__regular)
        }

        setOnDragListener(dragListener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val lowestSide = if (w > h) h else w
        val drawStep = lowestSide.toFloat() / (22f / this.strokeRatio + 2)

        this.offsetX = (w - lowestSide).toFloat() / 2f + drawStep
        this.offsetY = (h - lowestSide).toFloat() / 2f + drawStep
        strokePaint.strokeWidth = drawStep * 2

        this.cellSize = strokePaint.strokeWidth / this.strokeRatio

        this.textPaint.textSize = this.cellSize * 0.7f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawField(canvas)
        for (i in 1..10) {
            drawText(canvas, i.toString(), i, 0)
            drawText(canvas, "ABCDEFGHIK"[i - 1].toString(), 0, i)
        }

        ships.forEach { ship ->
            val x = ship.position.x
            val y = ship.position.y
            repeat(ship.length) {
                when (ship.orientation) {
                    Orientation.HORIZONTAL -> {
                        drawText(canvas, "S", x + it, y)
                    }
                    Orientation.VERTICAL -> {
                        drawText(canvas, "S", x, y + it)
                    }
                }
            }
        }
    }

    private fun drawField(canvas: Canvas?) {
        for (i in 0..10) {
            for (j in 0..10) {
                canvas?.drawRect(
                    offsetX + i * cellSize,
                    offsetY + j * cellSize,
                    offsetX + (i + 1) * cellSize,
                    offsetY + (j + 1) * cellSize,
                    fillPaint
                )

                canvas?.drawRect(
                    offsetX + i * cellSize,
                    offsetY + j * cellSize,
                    offsetX + (i + 1) * cellSize,
                    offsetY + (j + 1) * cellSize,
                    strokePaint
                )
            }
        }
    }

    private fun drawText(canvas: Canvas?, text: String, x: Int, y: Int) {
        val cellRect = RectF(
            offsetX + x * cellSize,
            offsetY + y * cellSize,
            offsetX + (x + 1) * cellSize,
            offsetY + (y + 1) * cellSize
        )

        val bounds = RectF(cellRect)

        bounds.right = textPaint.measureText(text)
        bounds.bottom = textPaint.descent() - textPaint.ascent()

        bounds.left += (cellRect.width() - bounds.right) / 2.0f
        bounds.top += (cellRect.height() - bounds.bottom) / 2.0f

        println(textPaint.typeface)

        canvas?.drawText(text, bounds.left, bounds.top - textPaint.ascent(), textPaint)
    }

    private fun validateShipPosition(currentShip: Ship): Boolean {
        fun getEndPoint(ship: Ship) = when (ship.orientation) {
            Orientation.VERTICAL -> Point(ship.position.x, ship.position.y + ship.length - 1)
            Orientation.HORIZONTAL -> Point(ship.position.x + ship.length - 1, ship.position.y)
        }

        val startCurrentPoint = currentShip.position
        val endCurrentPoint = getEndPoint(currentShip)
        if (
            startCurrentPoint.x < 1 ||
            startCurrentPoint.y < 1 ||
            endCurrentPoint.x > 11 ||
            endCurrentPoint.y > 11
        ) {
            return false
        }

        val currentShipRect = RectF(
            offsetX + (startCurrentPoint.x - 1) * cellSize,
            offsetY + (startCurrentPoint.y - 1) * cellSize,
            offsetX + (endCurrentPoint.x + 2) * cellSize,
            offsetY + (endCurrentPoint.y + 2) * cellSize
        )

        ships.filter { ship ->
            // TO THINK: maybe we need use different ids for all ships
            ship.length != currentShip.length || ship.id != currentShip.id
        }.forEach { ship ->
            val startPoint = ship.position
            val endPoint = getEndPoint(ship)
            val rect = RectF(
                offsetX + startPoint.x * cellSize,
                offsetY + startPoint.y * cellSize,
                offsetX + (endPoint.x + 1) * cellSize,
                offsetY + (endPoint.y + 1) * cellSize
            )

            if (currentShipRect.intersect(rect)) {
                return false
            }
        }
        return true
    }

    private fun addShip(dragData: CharSequence, x: Float, y: Float, v: View): Boolean {
        val i = ((x - offsetX) / cellSize).toInt()
        val j = ((y - offsetY) / cellSize).toInt()

        val (length, shipId) = dragData.split("_").map { it.toInt() }

        // TODO: add ghost ship
        val tempShip = Ship(length, Point(i - length / 2, j), Orientation.HORIZONTAL, shipId)

        if (validateShipPosition(tempShip)) {
            if (tempShip == selectedShip) {
                return true
            }
            ships.add(tempShip)
            map[tempShip] = v
            v.visibility = GONE
            ships.remove(selectedShip)
            return true
        }
        return false
    }

    fun allShipsArePlaced() = ships.sumOf { it.length } == (4 * 1 + 3 * 2 + 2 * 3 + 1 * 4)

    fun rotateLastShip(ship: Ship) {
        ship.rotate()
        if (!validateShipPosition(ship)) {
            ship.rotate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        val x = event.x
        val y = event.y
        val i = ((x - offsetX) / cellSize).toInt()
        val j = ((y - offsetY) / cellSize).toInt()

        ships.forEach { ship ->
            val hasFound = when (ship.orientation) {
                Orientation.VERTICAL -> {
                    i == ship.position.x && j in ship.position.y until ship.position.y + ship.length
                }
                Orientation.HORIZONTAL -> {
                    j == ship.position.y && i in ship.position.x until ship.position.x + ship.length
                }
            }
            if (hasFound) {
                selectedShip = ship
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        callbacks.forEach { callback ->
                            callback(map[ship]!!)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    fun selectedShipOut() {
        ships.remove(selectedShip)
        setSelectedShipToNull()
    }

    fun setSelectedShipToNull() {
        selectedShip = null
    }

    fun setOnShipDrag(function: (View) -> Unit) {
        callbacks.add(function)
    }
}
