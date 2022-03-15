package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import ru.etu.battleships.model.Orientation
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class PlayingGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {

    private val ships: MutableSet<Ship> = mutableSetOf()

    private val onTapListenerCallbacks: MutableList<(Point) -> Unit> = mutableListOf()

    private val hitCells: MutableList<Point> = mutableListOf()

    fun addShips(newShips: Set<Ship>) = ships.addAll(newShips)

    fun hitCell(cell: Point): Boolean {
        if (cell.x < 1 || cell.y < 1 || cell.x > 10 || cell.y > 10) {
            return false
        }
        hitCells.add(cell)
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = ((event.x - offsetX) / cellSize).toInt()
        val y = ((event.y - offsetY) / cellSize).toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // TODO: draw lines crossed at the cell
            }

            MotionEvent.ACTION_MOVE -> {
                // TODO: draw lines crossed at the cell
            }

            MotionEvent.ACTION_UP -> {
                this.performClick()
                Log.d("TAP", "($x;$y)")
                onTapListenerCallbacks.forEach { callback ->
                    callback(Point(x, y))
                }
            }
        }

        return true
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

        hitCells.forEach {
            drawText(canvas, "X", it.x, it.y)
        }
    }

    fun setOnTapListener(function: (Point) -> Unit) {
        onTapListenerCallbacks.add(function)
    }
}
