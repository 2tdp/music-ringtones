package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils

fun TextView.textCustom(content: String, color: Int, textSize: Float, font: String, context: Context) {
    text = content
    setTextColor(color)
    setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    typeface = try {
        Utils.getTypeFace(font.substring(0, 8), "$font.ttf", context)
    } catch (e: Exception) {
        Utils.getTypeFace(font.substring(0, 8), "$font.otf", context)
    }
}