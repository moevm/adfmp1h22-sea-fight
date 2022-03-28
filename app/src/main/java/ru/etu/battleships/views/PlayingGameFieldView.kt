package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
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

    private val fillPaint = Paint()

    private val onTapListenerCallbacks: MutableList<(Point) -> Unit> = mutableListOf()

    private val cellDrawables = mutableListOf<Drawable>()
    private val killedShips = mutableListOf<Ship>()

    var gameModel: GameModel? = null

    var selectedPoint: Point? = null

    var areCrossLinesShowed = false

    init {
        fillPaint.style = Paint.Style.FILL
    }

    private fun getDrawable(resource: Int, bounds: Rect): Drawable {
        val drawable = ResourcesCompat.getDrawable(resources, resource, null)!!
        drawable.bounds = bounds
        return drawable
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun missCell(point: Point) = missCell(point.x, point.y)

    @Suppress("MemberVisibilityCanBePrivate")
    fun missCell(x: Int, y: Int): Boolean {
        if (x < 1 || y < 1 || x > 10 || y > 10) {
            return false
        }

        val bounds = Rect()
        cellGameToView(x, y).round(bounds)

        val animation = getDrawable(R.drawable.anim_splash, bounds) as AnimationDrawable
        animation.callback = object : AnimationDrawableCallback(animation, view = this) {
            override fun onAnimationComplete() {
                cellDrawables.remove(animation)
                cellDrawables.add(getDrawable(R.drawable.blur_point, bounds))
            }
        }
        cellDrawables.add(animation)
        animation.start()

        return true
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun hitCell(point: Point) = hitCell(point.x, point.y)

    @Suppress("MemberVisibilityCanBePrivate")
    fun hitCell(x: Int, y: Int): Boolean {
        if (x < 1 || y < 1 || x > 10 || y > 10) {
            return false
        }

        val bounds = Rect()
        cellGameToView(x, y).round(bounds)

        val animation = getDrawable(R.drawable.anim_explosion, bounds) as AnimationDrawable
        animation.callback = object : AnimationDrawableCallback(animation, view = this) {
            override fun onAnimationComplete() {
                cellDrawables.remove(animation)
                val cross = getDrawable(R.drawable.anim_cross, bounds) as AnimationDrawable
                cross.callback = AnimationDrawableCallback(cross, view = this@PlayingGameFieldView)
                cellDrawables.add(cross)
                cross.start()
            }
        }
        cellDrawables.add(animation)
        animation.start()

        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val (x, y) = coordsViewToGame(event.x, event.y)

        return when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                if (x in 1..10 && y in 1..10) {
                    selectedPoint = Point(x - 1, y - 1)
                    invalidate()
                    true
                } else {
                    selectedPoint = null
                    invalidate()
                    false
                }
            }

            MotionEvent.ACTION_UP -> {
                this.performClick()
                selectedPoint = null
                if (x in 1..10 && y in 1..10) {
                    onTapListenerCallbacks.forEach { callback ->
                        callback(Point(x, y))
                    }
                }
                invalidate()
                true
            }
            else -> true
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun intToColor(i: Int) = when (i) {
        1 -> Color.argb(128, 0, 0, 255)
        2 -> Color.argb(128, 255, 255, 0)
        3 -> Color.argb(128, 255, 0, 0)
        4 -> Color.argb(128, 0, 255, 0)
        else -> Color.argb(0, 0, 0, 0)
    }

    override fun drawState(canvas: Canvas) {
        if (BuildConfig.DEBUG) {
            gameModel?.getMatrix()?.forEachIndexed { y, row ->
                row.forEachIndexed { x, value ->
                    val (l, t) = coordsGameToView(x + 1, y + 1)
                    val (r, b) = coordsGameToView(x + 2, y + 2)

                    fillPaint.color = intToColor(value)
                    canvas.drawRect(l, t, r, b, fillPaint)
                }
            }
        }

        killedShips.forEach { ship ->
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

        cellDrawables.forEach { anim ->
            anim.draw(canvas)
        }

        if (selectedPoint != null && areCrossLinesShowed) {
            val color = when (gameModel!!.getCell(selectedPoint!!)) {
                CellState.FREE, CellState.OCCUPIED -> Color.argb(128, 0, 255, 0)
                CellState.MISS, CellState.HIT, CellState.KILLED -> Color.argb(128, 255, 0, 0)
            }
            val crossedPoints = mutableSetOf<Point>()
            crossedPoints.addAll((0 until 10).map { Point(it, selectedPoint!!.y) })
            crossedPoints.addAll((0 until 10).map { Point(selectedPoint!!.x, it) })
            crossedPoints.forEach { point ->
                val x = point.x
                val y = point.y
                val (l, t) = coordsGameToView(x + 1, y + 1)
                val (r, b) = coordsGameToView(x + 2, y + 2)

                fillPaint.color = color
                canvas.drawRect(l, t, r, b, fillPaint)
            }
        }
    }

    fun setOnTapListener(function: (Point) -> Unit) {
        onTapListenerCallbacks.add(function)
    }

    fun initGameField(ships: Set<Ship>) {
        gameModel = GameModel(ships)
        gameModel?.addOnCellChange { point, prev, next ->
            if (prev == CellState.FREE && next == CellState.MISS) {
                missCell(point.x + 1, point.y + 1)
            } else if (prev == CellState.OCCUPIED && next == CellState.HIT) {
                hitCell(point.x + 1, point.y + 1)
            }
        }
        gameModel?.addOnKill { ship ->
            killedShips.add(ship)
        }
    }
}
