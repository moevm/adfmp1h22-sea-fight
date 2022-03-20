package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.res.ResourcesCompat
import ru.etu.battleships.BuildConfig
import ru.etu.battleships.R
import ru.etu.battleships.extUI.AnimationDrawableCallback
import ru.etu.battleships.model.GameModel
import ru.etu.battleships.model.Point
import ru.etu.battleships.model.Ship

class PlayingGameFieldView(context: Context, attributeSet: AttributeSet?) :
    GameFieldView(context, attributeSet) {

    private val fillPaint = Paint()

    private val onTapListenerCallbacks: MutableList<(Point) -> Unit> = mutableListOf()

    private val cellDrawables = mutableListOf<Drawable>()

    var gameModel: GameModel? = null

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
                if (x in 1..10 && y in 1..10) {
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

        cellDrawables.forEach { anim ->
            anim.draw(canvas)
        }
    }

    fun setOnTapListener(function: (Point) -> Unit) {
        onTapListenerCallbacks.add(function)
    }

    fun initGameField(ships: Set<Ship>) {
        gameModel = GameModel(ships)
        gameModel?.addOnHit { point -> hitCell(point.x + 1, point.y + 1) }
        gameModel?.addOnMiss { point -> missCell(point.x + 1, point.y + 1) }
//        gameModel!!.setOnShipKilled { ship: Ship ->
//            val point = ship.position
//            val vertical = ship.orientation == Orientation.VERTICAL
//            val length = ship.length
//            if (vertical) {
//                ((point.x - 1)..(point.x + 1)).forEach { x ->
//                    ((point.y - 1)..(point.y + length)).forEach { y ->
//                        missCell(x, y)
//                    }
//                }
//            } else {
//                ((point.x - 1)..(point.x + length)).forEach { x ->
//                    ((point.y - 1)..(point.y + 1)).forEach { y ->
//                        missCell(x, y)
//                    }
//                }
//            }
//        }
    }
}
