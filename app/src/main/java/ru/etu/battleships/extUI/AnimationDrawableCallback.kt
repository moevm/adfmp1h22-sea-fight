package ru.etu.battleships.extUI

import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Log
import android.view.View

open class AnimationDrawableCallback(
    animationDrawable: AnimationDrawable,
    callback: Drawable.Callback? = null,
    view: View? = null
) : Drawable.Callback {
    private var mLastFrame: Drawable? = null
    private var mWrappedCallback: Drawable.Callback? = null
    private var mView: View? = null
    private var mIsCallbackTriggered = false

    init {
        mLastFrame = animationDrawable.getFrame(animationDrawable.numberOfFrames - 1)
        mWrappedCallback = callback
        mView = view
    }

    override fun invalidateDrawable(who: Drawable) {
        mView?.postInvalidateOnAnimation(
            who.bounds.left,
            who.bounds.top,
            who.bounds.right,
            who.bounds.bottom,
        )
        mWrappedCallback?.invalidateDrawable(who)
        if (!mIsCallbackTriggered && mLastFrame != null && mLastFrame == who.current) {
            mIsCallbackTriggered = true
            onAnimationComplete()
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        mView?.postOnAnimationDelayed(what, `when` - SystemClock.uptimeMillis())
        mWrappedCallback?.scheduleDrawable(who, what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        mView?.removeCallbacks(what)
        mWrappedCallback?.unscheduleDrawable(who, what)
    }

    //
    // Public methods.
    //

    //
    // Public methods.
    //

    open fun onAnimationComplete() {}
}
