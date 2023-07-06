package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.view.View

fun View.loadBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    layout(left, top, right, bottom)
    draw(canvas)
    return bitmap
}

fun View.createBackground(colorArr: IntArray, border: Int, stroke: Int, colorStroke: Int) {
    background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = border.toFloat()
        if (stroke != -1) setStroke(stroke, colorStroke)

        if (colorArr.size >= 2) {
            colors = colorArr
            gradientType = GradientDrawable.LINEAR_GRADIENT
        } else setColor(colorArr[0])
    }
}