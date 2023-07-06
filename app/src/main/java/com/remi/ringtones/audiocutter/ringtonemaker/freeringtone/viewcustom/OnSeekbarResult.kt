package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.view.View

interface OnSeekbarResult {
    fun onDown(v: View)
    fun onMove(v: View, value: Int)
    fun onUp(v: View, value: Int)
}