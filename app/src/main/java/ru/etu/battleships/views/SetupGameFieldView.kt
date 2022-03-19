package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import ru.etu.battleships.R
import ru.etu.battleships.model.Orientation
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class SetupGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {
    private val highlighterPaint = Paint()
    private val highlighter = RectF(0f, 0f, 0f, 0f)

    private val paint = Paint()

    private val ships: MutableSet<Ship> = mutableSetOf()

    private val shipToViewMap: MutableMap<Ship, ShipView> = mutableMapOf()
    private val onShipDragCallbacks: MutableList<(Ship, ShipView) -> Unit> = mutableListOf()
    private val shipResources: Map<Int, Drawable?> = mapOf(
        Pair(1, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_1, null)),
        Pair(2, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_2, null)),
        Pair(3, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_3, null)),
        Pair(4, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_4, null)),
    )

    private val allShips = listOf(
        Ship(4, Point(0, 0), Orientation.HORIZONTAL, 1),

        Ship(3, Point(0, 0), Orientation.HORIZONTAL, 1),
        Ship(3, Point(0, 0), Orientation.HORIZONTAL, 2),

        Ship(2, Point(0, 0), Orientation.HORIZONTAL, 1),
        Ship(2, Point(0, 0), Orientation.HORIZONTAL, 2),
        Ship(2, Point(0, 0), Orientation.HORIZONTAL, 3),

        Ship(1, Point(0, 0), Orientation.HORIZONTAL, 1),
        Ship(1, Point(0, 0), Orientation.HORIZONTAL, 2),
        Ship(1, Point(0, 0), Orientation.HORIZONTAL, 3),
        Ship(1, Point(0, 0), Orientation.HORIZONTAL, 4),
    )

    private var previousTouchAction = MotionEvent.ACTION_UP
    private var lastDraggedShip: Ship? = null
    private var pressedX = 0f
    private var pressedY = 0f

    private val size = 10

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.YELLOW
        paint.strokeWidth = 5f

        highlighterPaint.style = Paint.Style.STROKE
        highlighterPaint.color = Color.RED
        highlighterPaint.strokeJoin = Paint.Join.ROUND
    }

    fun getShips() = ships.toSet()

    fun addOnShipDragListener(function: (Ship, View) -> Unit) {
        onShipDragCallbacks.add(function)
    }

    private fun setHighlighter(ship: Ship) {
        val width = if (ship.orientation == Orientation.HORIZONTAL) ship.length else 1
        val height = if (ship.orientation == Orientation.VERTICAL) ship.length else 1

        val (leftView, topView) = coordsGameToView(ship.position.x, ship.position.y)
        val (rightView, bottomView) = coordsGameToView(
            ship.position.x + width,
            ship.position.y + height
        )

        highlighter.set(leftView, topView, rightView, bottomView)
    }

    fun setupPullView(pullLayout: LinearLayout) {
        pullLayout.setOnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> event.localState is ShipView
                DragEvent.ACTION_DRAG_ENTERED -> true
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> true
                DragEvent.ACTION_DROP -> {
                    val shipView = event.localState as ShipView
                    shipView.orientation = Orientation.HORIZONTAL
                    shipView.visibility = VISIBLE
                    lastDraggedShip = null
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
                    val shadowBuilder = ShipView.DragShadowBuilder(view as ShipView)
                    view.startDragAndDrop(null, shadowBuilder, view, 0)
                    view.performClick()
                }
            }
        }
    }

    override fun onDragEvent(event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.localState is ShipView
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                invalidate()

                val shipView = event.localState as ShipView
                val length = shipView.length

                val p = length * cellSize * cellSize
                val dashGapSize = p / kotlin.math.ceil(p / cellSize * 2)
                highlighterPaint.pathEffect = DashPathEffect(
                    floatArrayOf(dashGapSize * 0.5f, dashGapSize * 0.5f), 0f
                )

                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                val shipView = event.localState as ShipView
                var (x, y) = coordsViewToGame(event.x, event.y)

                when (shipView.orientation) {
                    Orientation.HORIZONTAL -> {
                        x -= shipView.length / 2
                    }
                    Orientation.VERTICAL -> {
                        y -= shipView.length / 2
                    }
                }

                val ship = Ship(shipView.length, Point(x, y), shipView.orientation, shipView.index)
                highlighterPaint.color = if (validateShipPosition(ship)) Color.GREEN else Color.RED
                setHighlighter(ship)

                invalidate()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                highlighter.set(0f, 0f, 0f, 0f)
                invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val shipView = event.localState as ShipView

                var (x, y) = coordsViewToGame(event.x, event.y)
                when (shipView.orientation) {
                    Orientation.HORIZONTAL -> {
                        x -= shipView.length / 2
                    }
                    Orientation.VERTICAL -> {
                        y -= shipView.length / 2
                    }
                }

                invalidate()
                this.addShip(shipView.length, shipView.index, shipView.orientation, x, y, shipView)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                val view = event.localState as ShipView
                if (!event.result) {
                    if (lastDraggedShip == null) {
                        view.visibility = VISIBLE
                    } else {
                        this.addShip(
                            lastDraggedShip!!.length,
                            lastDraggedShip!!.id,
                            lastDraggedShip!!.orientation,
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

    private fun addShip(
        length: Int,
        index: Int,
        rot: Orientation,
        x: Int,
        y: Int,
        view: ShipView
    ): Boolean {
        val ship = Ship(length, Point(x, y), rot, index)

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
                if (ship != null) {
                    ship.rotate()
                    if (validateShipPosition(ship)) {
                        shipToViewMap[ship]?.rotate()
                    } else {
                        // TODO: Notify about bad rotation
                        ship.rotate()
                    }

                    invalidate()
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

    override fun performClick(): Boolean {
        super.performClick()
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

            val (left, top) = coordsGameToView(x, y)
            val (right, bottom) = coordsGameToView(x + ship.length, y + 1)
            val bounds = Rect()
            RectF(left, top, right, bottom).round(bounds)

            canvas.save()
            if (ship.orientation == Orientation.VERTICAL) {
                canvas.rotate(90f, left + cellSize / 2, top + cellSize / 2)
            }

            val drawable = shipResources[ship.length]!!
            drawable.bounds = bounds
            drawable.draw(canvas)
            canvas.restore()
        }

        canvas.drawRect(highlighter, highlighterPaint)
    }

    fun shuffleShips(shipViews: List<ShipView>) {
        ships.forEach { shipToViewMap.remove(it) }
        ships.clear()
        allShips.zip(shipViews).forEach { (ship, shipView) ->
            ship.orientation = Orientation.values().random()
            var points = findPossiblePoints(ship)

            if (points.isEmpty()) {
                ship.orientation = ship.orientation.next()
                points = findPossiblePoints(ship)
            }

            if (points.isEmpty()) {
                Log.e("shuffleShipsRecursion", "no available points")
                shuffleShips(shipViews)
                return
            }

            val point = points.random()
            val shipToPlace = Ship(ship.length, point, ship.orientation, ship.id)
            placeShip(shipToPlace, shipView)
        }
        invalidate()
    }

    private fun placeShip(ship: Ship, shipView: ShipView) {
        shipView.orientation = ship.orientation
        addShip(ship.length, ship.id, ship.orientation, ship.position.x, ship.position.y, shipView)
    }

    private fun findPossiblePoints(ship: Ship): List<Point> {
        return (0 until size)
            .flatMap { i -> (0 until size).map { j -> Point(i, j) } }
            .filter { validateShipPosition(Ship(ship.length, it, ship.orientation, -1)) }
    }
}
