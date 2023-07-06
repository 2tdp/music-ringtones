package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.widget.RelativeLayout
import com.airbnb.lottie.LottieAnimationView

class ViewLoading(context: Context): RelativeLayout(context) {

    companion object {
        var w = 0F
    }

    val vLoading: LottieAnimationView

    init {
        w = resources.displayMetrics.widthPixels / 100F

        vLoading = LottieAnimationView(context)
        addView(vLoading, LayoutParams(-1, (55.55f * w).toInt()).apply {
            addRule(CENTER_IN_PARENT, TRUE)
            leftMargin = (5.556f * w).toInt()
            rightMargin = (5.556f * w).toInt()
        })
    }
}