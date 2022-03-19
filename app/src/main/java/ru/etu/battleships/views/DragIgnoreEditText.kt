package ru.etu.battleships.views

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.inputmethod.EditorInfo

class DragIgnoreEditText(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    override fun onDragEvent(event: DragEvent?): Boolean {
        return when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> event.localState !is ShipView
            else -> super.onDragEvent(event)
        }
    }

    override fun onEditorAction(actionCode: Int) {
        if (actionCode == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
        }
        super.onEditorAction(actionCode)
    }
}
