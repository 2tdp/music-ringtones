package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.graphics.PathParser
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.UtilsBitmap

class CustomDrawPathData(context: Context) : View(context) {
    private var path: Path? = null
    private var paint: Paint? = null
    private var rectF: RectF? = null

    init {
        path = Path()
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        rectF = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path!!.computeBounds(rectF!!, true)
        val x = (height * 0.5f * rectF!!.width() / rectF!!.height()).toInt()
        val y = (width * 0.5f * rectF!!.height() / rectF!!.width()).toInt()
        if (rectF!!.width() >= rectF!!.height())
            UtilsBitmap.drawIconWithPath(canvas, path!!, paint, width / 2f, width / 4, (height - y) / 2)
        else
            UtilsBitmap.drawIconWithPath(canvas, path!!, paint, width / 2f, (width - x) / 2, height  / 4)
    }

    fun setDataPath(lstPath: List<String>, isDecor: Boolean, isTemp: Boolean) {
        path!!.reset()
        for (pathData in lstPath) {
            if (pathData == "evenOdd") path!!.fillType = Path.FillType.EVEN_ODD
            else if (pathData.contains("#"))
                paint!!.color = Color.parseColor(pathData)
            else path!!.addPath(PathParser.createPathFromPathData(pathData))
        }
        if (isDecor) paint!!.color = Color.BLACK

        invalidate()
    }
}