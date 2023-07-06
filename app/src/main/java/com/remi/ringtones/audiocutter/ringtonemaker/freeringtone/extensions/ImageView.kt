package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions

import android.content.Context
import android.widget.ImageView
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.designvector.VectorChildFinder
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.designvector.VectorDrawableCompat

fun ImageView.changeColorIcon(context: Context, namePath: String, quantityPath: Int, resPath: Int,
                         colorBackground: Int, color: Int) {

    var vectorChild: VectorChildFinder? = null
    var path: VectorDrawableCompat.VFullPath

    for (i in 0 until quantityPath) {
        if (vectorChild == null) vectorChild = VectorChildFinder(context, resPath, this)

        path = vectorChild.findPathByName(namePath + (i + 1))
        if (i == 0) path.fillColor = colorBackground else path.fillColor = color
    }
}