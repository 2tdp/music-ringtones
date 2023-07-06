package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.graphics.Matrix
import android.view.MotionEvent
import java.lang.RuntimeException

class MatrixGestureDetector internal constructor(matrix: Matrix?, listener: OnMatrixChangeListener?) {
    internal interface OnMatrixChangeListener {
        fun onChange(matrix: Matrix?)
    }

    private var ptpIdx = 0
    private val mTempMatrix = Matrix()
    var mMatrix: Matrix
    private val mListener: OnMatrixChangeListener
    private val mSrc = FloatArray(4)
    private val mDst = FloatArray(4)
    private var mCount = 0

    init {
        if (matrix == null) throw RuntimeException("Matrix cannot be null")
        if (listener == null) throw RuntimeException("OnMatrixChangeListener cannot be null")
        mMatrix = matrix
        mListener = listener
    }

    fun onTouchEvent(event: MotionEvent, isMove: Boolean) {
        if (event.pointerCount > 2) {
            return
        }
        val action = event.actionMasked
        val index = event.actionIndex
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = index * 2
                mSrc[idx] = event.getX(index)
                mSrc[idx + 1] = event.getY(index)
                mCount++
                ptpIdx = 0
            }
            MotionEvent.ACTION_MOVE -> {
                var i = 0
                while (i < mCount) {
                    val idx = ptpIdx + i * 2
                    mDst[idx] = event.getX(i)
                    mDst[idx + 1] = event.getY(i)
                    i++
                }
                if (isMove) {
                    mTempMatrix.setPolyToPoly(mSrc, ptpIdx, mDst, ptpIdx, mCount)
                    mMatrix.postConcat(mTempMatrix)
                }
                mListener.onChange(mMatrix)
                System.arraycopy(mDst, 0, mSrc, 0, mDst.size)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.getPointerId(index) == 0) ptpIdx = 2
                mCount--
            }
        }
    }

    companion object {
        private const val TAG = "MatrixGestureDetector"
    }
}