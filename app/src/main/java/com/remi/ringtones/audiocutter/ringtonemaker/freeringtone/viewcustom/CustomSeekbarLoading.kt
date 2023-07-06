package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.graphics.*
import android.view.View
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils

class CustomSeekbarLoading(context: Context) : View(context) {

    companion object{
        var w = 0
    }

    private var paintPr: Paint
    private var paintStroke: Paint
    private var paint_Text: Paint
    private var path: Path
    private var rectF: RectF
    private var rectF2: RectF
    private var padding = 0f
    private var progress = 0
    private var floatsRadius: FloatArray
    private var floatsRadius2: FloatArray


    init {
        w = context.resources.displayMetrics.widthPixels

        path = Path()
        paintPr = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        paint_Text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            typeface = Utils.getTypeFace("stixtwo", "stixtwo_bold.ttf", context)
        }
        paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
        rectF = RectF()
        rectF2 = RectF()
        progress = 100
        padding = w / 100f

        val radius = w * 4f / 100
        floatsRadius = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        floatsRadius2 = floatArrayOf(
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3,
            radius * 2 / 3
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectF[padding, padding, width - padding] = height - padding
        path.reset()
        path.addRoundRect(rectF, floatsRadius, Path.Direction.CW)
        canvas.drawPath(path, paintStroke)
        if (progress > 0) rectF2[padding * 2.2f, padding * 2.2f, width - progress * rectF.width() / 100] = height - padding * 2.2f
        else rectF2[padding * 2.2f, padding * 2.2f, width - progress * rectF.width() / 100 - padding * 2.2f] = height - padding * 2.2f
        path.reset()
        path.addRoundRect(rectF2, floatsRadius2, Path.Direction.CW)
        canvas.drawPath(path, paintPr)
        drawCenter(canvas, paint_Text, "${99 - progress} %")
    }

    fun setProgress(progress: Int) {
        this.progress = 99 - progress
        invalidate()
    }

    private fun drawCenter(canvas: Canvas, paint: Paint, text: String) {
        val r = Rect()
        canvas.getClipBounds(r)
        val cHeight = r.height()
        val cWidth = r.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        val x = cWidth / 2f - r.width() / 2f - r.left
        val y = cHeight / 2f + r.height() / 2f - r.bottom
        canvas.drawText(text, x, y, paint)
    }

    fun setColorStroke(colorStroke: Int) {
        paintStroke.color = colorStroke
        invalidate()
    }

    fun setColorPro(colorPro: Int) {
        paintPr.color = colorPro
        invalidate()
    }

    fun setColorText(colorText: Int) {
        paint_Text.color = colorText
    }

    fun setTextSize(textSize: Float) {
        paint_Text.textSize = textSize
        invalidate()
    }

    fun setSizeStroke(stroke: Float) {
        paintStroke.strokeWidth = stroke
    }
}