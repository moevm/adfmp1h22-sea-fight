package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import ru.etu.battleships.model.CellState
import ru.etu.battleships.model.GameModel
import ru.etu.battleships.model.Orientation
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class PlayingGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {

    private val ships: MutableSet<Ship> = mutableSetOf()

    private val onTapListenerCallbacks: MutableList<(Point) -> Unit> = mutableListOf()

    private val hitCells: MutableList<Pair<Point, CellState>> = mutableListOf()

    var gameModel: GameModel? = null

    fun hitCell(point: Point) = hitCell(point.x, point.y)

    fun hitCell(x: Int, y: Int): Boolean {
        if (x < 1 || y < 1 || x > 10 || y > 10) {
            return false
        }
        hitCells.add(Pair(Point(x, y), gameModel!!.getCell(x, y)))
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
                if (x > 0 && y > 0) {
                    onTapListenerCallbacks.forEach { callback ->
                        callback(Point(x, y))
                    }
                }
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
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

        hitCells.forEach { (point, cellState) ->
            drawText(canvas, "X", point)
        }
    }

    fun setOnTapListener(function: (Point) -> Unit) {
        onTapListenerCallbacks.add(function)
    }

    fun initGameField(ships: Set<Ship>) {
//        this.ships.addAll(ships)
        gameModel = GameModel(ships)
        gameModel!!.setOnShipKilled { ship: Ship ->
            val point = ship.position
            val vertical = ship.orientation == Orientation.VERTICAL
            val length = ship.length
            if (vertical) {
                ((point.x - 1)..(point.x + 1)).forEach { x ->
                    ((point.y - 1)..(point.y + length)).forEach { y ->
                        hitCell(x, y)
                    }
                }
            } else {
                ((point.x - 1)..(point.x + length)).forEach { x ->
                    ((point.y - 1)..(point.y + 1)).forEach { y ->
                        hitCell(x, y)
                    }
                }
            }
        }
    }
}
