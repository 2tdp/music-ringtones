package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom.ringdroid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.sqrt

/**
 * Represents a draggable start or end marker.
 *
 *
 * Most events are passed back to the client class using a listener interface.
 *
 *
 * This class directly keeps track of its own velocity, though,
 * accelerating as the user holds down the left or right arrows while this control is focused.
 */
class MarkerView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    interface MarkerListener {
        fun markerTouchStart(marker: MarkerView, pos: Float)
        fun markerTouchMove(marker: MarkerView, pos: Float)
        fun markerTouchEnd(marker: MarkerView)
        fun markerFocus(marker: MarkerView)
        fun markerLeft(marker: MarkerView, velocity: Int)
        fun markerRight(marker: MarkerView, velocity: Int)
        fun markerEnter(marker: MarkerView)
        fun markerKeyUp()
        fun markerDraw()
    }

    private var mVelocity: Int
    private var mListener: MarkerListener?

    init {
        // Make sure we get keys
        isFocusable = true
        mVelocity = 0
        mListener = null
    }

    fun setListener(listener: MarkerListener?) {
        mListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                requestFocus()
                // We use raw x because this window itself is going to move, which will screw up the "local" coordinates
                mListener!!.markerTouchStart(this, event.rawX)
            }

            MotionEvent.ACTION_MOVE ->  // We use raw x because this window itself is going to move, which will screw up the "local" coordinates
                mListener!!.markerTouchMove(this, event.rawX)

            MotionEvent.ACTION_UP -> mListener!!.markerTouchEnd(this)
        }
        return true
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (gainFocus && mListener != null) mListener!!.markerFocus(this)
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mListener != null) mListener!!.markerDraw()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        mVelocity++
        val v = sqrt((1 + mVelocity / 2f).toDouble()).toInt()
        if (mListener != null) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    mListener!!.markerLeft(this, v)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    mListener!!.markerRight(this, v)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    mListener!!.markerEnter(this)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        mVelocity = 0
        if (mListener != null) mListener!!.markerKeyUp()
        return super.onKeyDown(keyCode, event)
    }
}