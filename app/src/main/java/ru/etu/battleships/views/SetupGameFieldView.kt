package ru.etu.battleships.views

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.forEach
import ru.etu.battleships.model.Orientation
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class SetupGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {
    private val highlighterPaint = Paint()
    private val highlighter = RectF(0f, 0f, 0f, 0f)

    private val ships: MutableSet<Ship> = mutableSetOf()

    private val shipToViewMap: MutableMap<Ship, View> = mutableMapOf()
    private val onShipDragCallbacks: MutableList<(Ship, View) -> Unit> = mutableListOf()

    private var previousTouchAction = MotionEvent.ACTION_UP
    private var lastDraggedShip: Ship? = null
    private var pressedX = 0f
    private var pressedY = 0f

    init {
        highlighterPaint.style = Paint.Style.STROKE
        highlighterPaint.color = Color.RED
        highlighterPaint.strokeJoin = Paint.Join.ROUND
    }

    fun getShips() = ships.toSet()

    fun addOnShipDragListener(function: (Ship, View) -> Unit) {
        onShipDragCallbacks.add(function)
    }

    private fun setHighlighter(left: Int, top: Int, width: Int, height: Int) {
        val (leftView, topView) = coordsGameToView(left, top)
        val (rightView, bottomView) = coordsGameToView(left + width, top + height)
        highlighter.set(leftView, topView, rightView, bottomView)
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
                    val view = event.localState as View
                    view.visibility = VISIBLE
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

    override fun onDragEvent(event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(MIME_TYPE)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                invalidate()

                // TODO: create child of ImageView and add ship properties to it
                val view = event.localState as View
                val length = view.resources.getResourceName(view.id).split("/")[1].split("_")[1].toInt()

                val p = length * cellSize * cellSize
                val dashGapSize = p / kotlin.math.ceil(p / cellSize * 2)
                highlighterPaint.pathEffect = DashPathEffect(
                    floatArrayOf(dashGapSize * 0.5f, dashGapSize * 0.5f), 0f
                )

                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                // TODO: create child of ImageView and add ship properties to it
                val view = event.localState as View
                val (length, shipId) = view.resources.getResourceName(view.id)
                    .split("/")[1].split("_")
                    .slice(1..2).map { it.toInt() }
                var (x, y) = coordsViewToGame(event.x, event.y)
                x -= length / 2

                val ship = Ship(length, Point(x, y), Orientation.HORIZONTAL, shipId)
                highlighterPaint.color = if (validateShipPosition(ship)) {
                    Color.GREEN
                } else {
                    Color.RED
                }

                setHighlighter(x, y, length, 1)
                invalidate()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                highlighter.set(0f, 0f, 0f, 0f)
                invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val dragData = event.clipData.getItemAt(0).text

                val view = event.localState as View
                val (length, shipId) = dragData.split("_").map { it.toInt() }

                var (x, y) = coordsViewToGame(event.x, event.y)
                x -= length / 2

                invalidate()
                this.addShip(length, shipId, x, y, view)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                val view = event.localState as View
                if (!event.result) {
                    if (lastDraggedShip == null) {
                        view.visibility = VISIBLE
                    } else {
                        this.addShip(
                            lastDraggedShip!!.length,
                            lastDraggedShip!!.id,
                            lastDraggedShip!!.position.x,
                            lastDraggedShip!!.position.y,
                            view
                        )
                    }
                }
                highlighter.set(0f, 0f, 0f, 0f)
                invalidate()
                true
            }
            else -> false
        }
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
            endCurrentPoint.x > 10 ||
            endCurrentPoint.y > 10
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

    private fun addShip(length: Int, shipId: Int, x: Int, y: Int, view: View): Boolean {
        val ship = Ship(length, Point(x, y), Orientation.HORIZONTAL, shipId)

        if (validateShipPosition(ship)) {
            ships.add(ship)
            shipToViewMap[ship] = view
            view.visibility = GONE
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
        val (x, y) = coordsViewToGame(event.x, event.y)

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
            MotionEvent.ACTION_DOWN -> {
                pressedX = event.x
                pressedY = event.y
            }

            MotionEvent.ACTION_UP -> {
                this.performClick()
                Log.d("TAP", "($x;$y)")
                if (ship != null) {
                    // TODO: perform rotation
                    Log.d(
                        "TAP",
                        "${ship.length}_${ship.id} - (${ship.position.x}; ${ship.position.y})"
                    )
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (ship != null &&
                    (event.x - pressedX) !in -5f..5f &&
                    (event.y - pressedY) !in -5f..5f
                ) {
                    lastDraggedShip = ship
                    onShipDragCallbacks.forEach { callback ->
                        callback(ship, shipToViewMap[ship]!!)
                    }
                }
            }
        }
        previousTouchAction = event.action
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        highlighterPaint.strokeWidth = cellSize / 10f
    }

    override fun drawState(canvas: Canvas) {
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

        canvas.drawRect(highlighter, highlighterPaint)
    }
}
