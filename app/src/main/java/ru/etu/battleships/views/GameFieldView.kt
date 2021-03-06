package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import ru.etu.battleships.R
import ru.etu.battleships.model.Point

open class GameFieldView(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private val fillPaint = Paint()
    private val strokePaint = Paint()
    private val textPaint = Paint()

    private val strokeRatio = 1f / 10f

    protected val shipResources: Map<Int, Drawable?> = mapOf(
        Pair(1, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_1, null)),
        Pair(2, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_2, null)),
        Pair(3, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_3, null)),
        Pair(4, ResourcesCompat.getDrawable(resources, R.drawable.ic_ship_4, null)),
    )

    // TODO: only cellSize guy should be visible, play around with coords converter to rid off offsets in subclasses
    protected var offsetX = 0f
        private set
    protected var offsetY = 0f
        private set
    protected var cellSize = 0.0f
        private set

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

    protected fun coordsGameToView(x: Int, y: Int) = floatArrayOf(
        x * cellSize + offsetX,
        y * cellSize + offsetY
    )

    protected fun coordsViewToGame(x: Float, y: Float) = intArrayOf(
        ((x - offsetX) / cellSize).toInt(),
        ((y - offsetY) / cellSize).toInt()
    )

    protected fun cellGameToView(x: Int, y: Int): RectF {
        val (left, top) = coordsGameToView(x, y)
        val (right, bottom) = coordsGameToView(x + 1, y + 1)
        return RectF(left, top, right, bottom)
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

    final override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawField(canvas)
        for (i in 1..10) {
            drawText(canvas, i.toString(), i, 0)
            drawText(canvas, "ABCDEFGHIK"[i - 1].toString(), 0, i)
        }

        if (canvas != null) {
            drawState(canvas)
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

    protected fun drawText(canvas: Canvas?, text: String, point: Point) =
        drawText(canvas, text, point.x, point.y)

    protected fun drawText(canvas: Canvas?, text: String, x: Int, y: Int) {
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

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun drawState(canvas: Canvas) {
    }
}
