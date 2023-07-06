package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R


class CustomSeekbarTwoWay(context: Context) : View(context) {

    companion object {
        var w = 0
    }

    private var paint: Paint
    private var paintProgress: Paint
    private var progress = 0
    private var max = 0
    private var value = 0
    private var sizeThumb = 0f
    private var sizeBg = 0f
    private var sizePos = 0f

    var onSeekbarResult: OnSeekbarResult? = null

    init {
        w = resources.displayMetrics.widthPixels
        sizeThumb = 3.33f * w / 100
        sizeBg = 0.556f * w / 100
        sizePos = 0.556f * w / 100
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.apply {
            clearShadowLayer()
            color = ContextCompat.getColor(context, R.color.gray_light)
            strokeWidth = sizeBg
        }
        canvas.drawLine(sizeThumb / 2, height / 2f, width - sizeThumb / 2, height / 2f, paint)

        paintProgress.apply {
            color = ContextCompat.getColor(context, R.color.red)
            strokeWidth = sizePos
        }
        val p = (width - sizeThumb) * progress / max + sizeThumb / 2
        canvas.drawLine(width / 2f, height / 2f, p, height / 2f, paintProgress)

        paint.apply {
            color = ContextCompat.getColor(context, R.color.red)
            setShadowLayer(sizeThumb / 8, 0f, 0f, Color.WHITE)
        }
        canvas.drawCircle(p, height / 2f, sizeThumb / 2, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onSeekbarResult?.onDown(this)
            MotionEvent.ACTION_MOVE -> {
                progress = ((x - sizeThumb / 2) * max / (width - sizeThumb)).toInt()

                if (progress < 0) progress = 0
                else if (progress > max) progress = max
                invalidate()

                value = if (progress > max / 2) progress - max / 2
                else -(max / 2 - progress)
                onSeekbarResult?.onMove(this, value)
            }
            MotionEvent.ACTION_UP -> onSeekbarResult?.onUp(this, value)
        }
        return true
    }

    fun getProgress(): Int{
        return progress
    }

    fun setProgress(progress: Int) {
        if (progress == 0) this.progress = max / 2
        else this.progress = max / 2 + progress
        invalidate()
    }

    fun setMax(max: Int) {
        this.max = max
        progress = max / 2
        invalidate()
    }
}