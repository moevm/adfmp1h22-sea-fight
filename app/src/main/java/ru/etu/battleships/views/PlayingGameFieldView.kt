package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import ru.etu.battleships.BuildConfig
import ru.etu.battleships.R
import ru.etu.battleships.extUI.AnimationDrawableCallback
import ru.etu.battleships.model.CellState
import ru.etu.battleships.model.GameModel
import ru.etu.battleships.model.Orientation
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class PlayingGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {

    private val ships: MutableSet<Ship> = mutableSetOf()
    private val fillPaint = Paint()

    private val onTapListenerCallbacks: MutableList<(Point) -> Unit> = mutableListOf()

    private val hitCells: MutableList<Pair<Point, CellState>> = mutableListOf()
    private val splashAnimations = mutableListOf<AnimationDrawable>()

    var gameModel: GameModel? = null

    init {
        fillPaint.style = Paint.Style.FILL
    }

    fun hitCell(point: Point) = hitCell(point.x, point.y)

    @Suppress("MemberVisibilityCanBePrivate")
    fun hitCell(x: Int, y: Int): Boolean {
        if (x < 1 || y < 1 || x > 10 || y > 10) {
            return false
        }
        val cellState = gameModel!!.getCell(x - 1, y - 1)

        Log.d("TAP", "($x;$y) - $cellState")

        if (cellState == CellState.FREE) {
            val animation = ResourcesCompat.getDrawable(
                resources,
                R.drawable.splash_animation,
                null
            ) as AnimationDrawable
            splashAnimations.add(animation)

            val (left, top) = coordsGameToView(x, y)
            val (right, bottom) = coordsGameToView(x + 1, y + 1)
            val bounds = Rect()
            RectF(left, top, right, bottom).round(bounds)

            animation.bounds = bounds
            animation.callback = object : AnimationDrawableCallback(animation, view = this) {
                override fun onAnimationComplete() {
                    splashAnimations.remove(animation)
                    hitCells.add(Pair(Point(x, y), cellState))
                }
            }
            animation.start()
        }

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

    private val intToColor = mapOf(
        Pair(0, Color.argb(0, 0, 0, 0)),
        Pair(1, Color.argb(128, 0, 0, 255)),
        Pair(2, Color.argb(128, 255, 255, 0)),
        Pair(3, Color.argb(128, 255, 0, 0)),
        Pair(4, Color.argb(128, 0, 255, 0)),
    )

    override fun drawState(canvas: Canvas) {
        if (BuildConfig.DEBUG) {
            gameModel?.getMatrix()?.forEachIndexed { y, row ->
                row.forEachIndexed { x, value ->
                    val (l, t) = coordsGameToView(x + 1, y + 1)
                    val (r, b) = coordsGameToView(x + 2, y + 2)

                    fillPaint.color = intToColor[value]!!
                    canvas.drawRect(l, t, r, b, fillPaint)
                }
            }
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

        splashAnimations.forEach { anim ->
            anim.draw(canvas)
        }

        hitCells.forEach { (point, _) ->
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
