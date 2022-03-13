package ru.etu.battleships

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach

class GameFieldView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {
    companion object {
        const val MIME_TYPE = "battleship/ship"
    }

    private val fillPaint = Paint()
    private val strokePaint = Paint()
    private val textPaint = Paint()

    private val strokeRatio = 1f / 10f

    private var offsetX = 0f
    private var offsetY = 0f
    private var cellSize = 0f

    private val ships: MutableSet<Ship> = mutableSetOf()
    private val shipToViewMap: MutableMap<Ship, View> = mutableMapOf()

    private val onShipDragCallbacks: MutableList<(Ship, View) -> Unit> = mutableListOf()
    private var previousTouchAction = MotionEvent.ACTION_UP

    private var lastDraggedShip: Ship? = null

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
    }

    fun getShips(): Set<Ship> {
        return ships.toSet()
    }

    fun setupPullView(pullLayout: LinearLayout) {
        pullLayout.setOnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(MIME_TYPE)
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
                    val v = event.localState as View
                    v.visibility = VISIBLE
                    view.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    view.invalidate()
                    true
                }
                else -> false
            }
        }

        pullLayout.forEach { linearLayout ->
            if (linearLayout !is LinearLayout) {
                return@forEach
            }
            linearLayout.forEach { ship ->
                ship.setOnTouchListener { view, _ ->
                    val (_, length, id) = view.resources.getResourceName(view.id)
                        .split("/")[1].split("_")
                    val data = ClipData("Ship", arrayOf(MIME_TYPE), ClipData.Item("${length}_$id"))
                    val shadowBuilder = DragShadowBuilder(view)
                    view.startDragAndDrop(data, shadowBuilder, view, 0)
                    view.performClick()
                }
            }
        }
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

    override fun onDragEvent(event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(MIME_TYPE)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val dragData = event.clipData.getItemAt(0).text

                invalidate()

                val v = event.localState as View
                val (length, shipId) = dragData.split("_").map { it.toInt() }
                val x = ((event.x - offsetX) / cellSize).toInt() - length / 2
                val y = ((event.y - offsetY) / cellSize).toInt()

                this.addShip(length, shipId, x, y, v)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                invalidate()
                val v = event.localState as View
                if (!event.result) {
                    if (lastDraggedShip == null) {
                        v.visibility = VISIBLE
                    } else {
                        this.addShip(
                            lastDraggedShip!!.length,
                            lastDraggedShip!!.id,
                            lastDraggedShip!!.position.x,
                            lastDraggedShip!!.position.y,
                            v
                        )
                    }
                }
                true
            }
            else -> false
        }
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

    private fun addShip(length: Int, shipId: Int, x: Int, y: Int, v: View): Boolean {
        val ship = Ship(length, Point(x, y), Orientation.HORIZONTAL, shipId)

        if (validateShipPosition(ship)) {
            ships.add(ship)
            shipToViewMap[ship] = v
            v.visibility = GONE
            return true
        }
        return false
    }

    fun removeShip(ship: Ship): Boolean {
        shipToViewMap.remove(ship)
        return ships.remove(ship)
    }

    fun allShipsArePlaced() = ships.sumOf { it.length } == (4 * 1 + 3 * 2 + 2 * 3 + 1 * 4)

    fun rotateLastShip(ship: Ship) {
        ship.rotate()
        if (!validateShipPosition(ship)) {
            ship.rotate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = ((event.x - offsetX) / cellSize).toInt()
        val y = ((event.y - offsetY) / cellSize).toInt()

        val ship = ships.find { ship ->
            when (ship.orientation) {
                Orientation.VERTICAL -> {
                    x == ship.position.x && y in ship.position.y until ship.position.y + ship.length
                }
                Orientation.HORIZONTAL -> {
                    y == ship.position.y && x in ship.position.x until ship.position.x + ship.length
                }
            }
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (ship != null && previousTouchAction == MotionEvent.ACTION_DOWN) {
                    lastDraggedShip = ship
                    onShipDragCallbacks.forEach { callback ->
                        callback(ship, shipToViewMap[ship]!!)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                this.performClick()
                Log.d("TAP", "($x;$y)")

                if (ship != null) {
                    // TODO: perform rotation
                    Log.d("TAP", "${ship.length}_${ship.id} - (${ship.position.x}; ${ship.position.y})")
                }
            }
        }
        previousTouchAction = event.action
        return true
    }

    fun addOnShipDragListener(function: (Ship, View) -> Unit) {
        onShipDragCallbacks.add(function)
    }
}
