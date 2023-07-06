package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.TAG
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils
import java.text.SimpleDateFormat
import java.util.Locale

class RangeSeekbar : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var w = 0f
    private val paint: Paint
    private val paintText: Paint
    private val paintPlayBack: Paint
    private var rectText: Rect
    private var sizeThumb = 0f
    private var sizeBg = 0f
    private var sizeProgress = 0f
    private var isStart = false
    private var isEnd = false
    var max = 100
    var progressStart = 30
    var progressEnd = 20
    var duration = 0L
    var playback = 0f

    var onSeekbarResult: OnRangeSeekbarResult? = null

    init {
        w = resources.displayMetrics.widthPixels / 100f
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.gray_main2)
            typeface = Utils.getTypeFace("nunito", "nunito_regular.ttf", context)
            textSize = 2.778f * w
            style = Paint.Style.FILL
        }
        paintPlayBack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D9D9D9")
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 0.556f * w
        }

        rectText = Rect()

        sizeThumb = 3.389f * w
        sizeBg = 1.11f * w
        sizeProgress = 1.11f * w
    }

    override fun onDraw(canvas: Canvas) {
        val pStart = (width - 2 * sizeThumb) * progressStart / max + sizeThumb
        val pEnd = (width - 2 * sizeThumb) * (max - progressEnd) / max + sizeThumb
        drawBg(canvas)
        drawProgressStart(pStart, canvas)
        drawProgressEnd(pEnd, canvas)
        drawOnPlay(canvas)
        drawThumbStart(pStart, canvas)
        drawThumbEnd(pEnd, canvas)
    }

    private fun drawBg(canvas: Canvas) {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.gray_main2)
            strokeWidth = sizeBg
        }
        canvas.drawLine(sizeThumb, height / 2f, width - sizeThumb, height / 2f, paint)
    }

    private fun drawProgressStart(p: Float, canvas: Canvas) {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.main_color)
            strokeWidth = sizeProgress
        }
        canvas.drawLine(sizeThumb, height / 2f, p, height / 2f, paint)
    }

    private fun drawProgressEnd(p: Float, canvas: Canvas) {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.main_color)
            strokeWidth = sizeProgress
        }
        canvas.drawLine(width - sizeThumb, height / 2f, p, height / 2f, paint)
    }

    private fun drawThumbStart(p: Float, canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.main_color)
        canvas.drawCircle(p, height / 2f, sizeThumb / 2, paint)

        val txt = convertProgressToTime(progressStart)
        paintText.getTextBounds(txt, 0, txt.length, rectText)
        canvas.drawText(txt, p - rectText.width() / 2f, height / 2f + 2 * sizeThumb, paintText)
    }

    private fun drawThumbEnd(p: Float, canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, R.color.main_color)
        canvas.drawCircle(p, height / 2f, sizeThumb / 2, paint)

        val txt = convertProgressToTime(max - progressEnd)
        paintText.getTextBounds(txt, 0, txt.length, rectText)
        canvas.drawText(txt, p - rectText.width() / 2f, height / 2f + 2 * sizeThumb, paintText)
    }

    private fun drawOnPlay(canvas: Canvas) {
        if (playback == 0f) return

        val proPlayBack = (width - sizeThumb) * playback / max
        canvas.drawLine(proPlayBack, 0f, proPlayBack, height.toFloat(), paintPlayBack)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val t = (event.x * max / (width - 2 * sizeThumb)).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onSeekbarResult?.onDown(this)
                if (t in progressStart - 5..progressStart + 5) {
                    isStart = true
                    isEnd = false
                }
                else if (t in max - progressEnd - 5 .. max - progressEnd + 5) {
                    isStart = false
                    isEnd = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isStart) {
                    val progress = ((event.x ) * max / (width - 2 * sizeThumb)).toInt()
                    progressStart = if (progress < 0) 0
                    else if (progress > max - progressEnd) max - progressEnd
                    else progress
                }
                else if (isEnd) {
                    val progress = ((event.x ) * max / (width - 2 * sizeThumb)).toInt()
                    progressEnd = if (progress > max) 0
                    else if (progress < progressStart) max - progressStart
                    else max - progress
                }
                invalidate()
                if (isStart) onSeekbarResult?.onMove(this, progressStart, max - progressEnd)
                else if (isEnd) onSeekbarResult?.onMove(this, progressStart, max - progressEnd)
            }

            MotionEvent.ACTION_UP -> {
                if (isStart) onSeekbarResult?.onMove(this, progressStart, max - progressEnd)
                else if (isEnd) onSeekbarResult?.onMove(this, progressStart, max - progressEnd)
                isStart = false
                isEnd = false
            }
        }
        return true
    }

    fun convertProgressToTime(progress: Int): String {
        val time = Utils.formatTime(progress * duration / 100)
        return if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
    }
}