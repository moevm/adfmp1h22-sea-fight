package ru.etu.battleships.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import ru.etu.battleships.R
import ru.etu.battleships.model.Orientation

class ShipView(context: Context, attrs: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    class DragShadowBuilder(private val shipView: ShipView) : View.DragShadowBuilder(shipView) {
        private val width: Float
        private val height: Float
        private val rot: Float

        init {
            val w = shipView.width * shipView.scaleX
            val h = shipView.height * shipView.scaleY
            when (shipView.orientation) {
                Orientation.HORIZONTAL -> {
                    width = w
                    height = h
                    rot = 0f
                }
                Orientation.VERTICAL -> {
                    width = h
                    height = w
                    rot = 90f
                }
            }
        }

        override fun onDrawShadow(canvas: Canvas) {
            canvas.scale(shipView.scaleX, shipView.scaleY, width / 2, height / 2)
            canvas.rotate(rot, width / 2, height / 2)
            canvas.translate((width - shipView.width) / 2, (height - shipView.height) / 2)
            super.onDrawShadow(canvas)
        }

        override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
            outShadowSize.set(width.toInt(), height.toInt())
            outShadowTouchPoint.set(outShadowSize.x / 2, outShadowSize.y / 2)
        }
    }

    val length: Int
    val index: Int
    var orientation: Orientation

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ShipView, 0, 0).apply {
            try {
                length = getInt(R.styleable.ShipView_length, 0)
                index = getInt(R.styleable.ShipView_index, 0)
                orientation = Orientation.values()
                    .first { it.ordinal == getInteger(R.styleable.ShipView_orientation, 0) }
            } finally {
                recycle()
            }
        }
    }

    fun rotate() {
        this.orientation = when (orientation) {
            Orientation.HORIZONTAL -> Orientation.VERTICAL
            Orientation.VERTICAL -> Orientation.HORIZONTAL
        }
    }
}
