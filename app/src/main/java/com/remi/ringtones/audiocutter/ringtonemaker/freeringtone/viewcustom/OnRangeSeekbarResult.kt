package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.view.View

interface OnRangeSeekbarResult {
    fun onDown(v: View)
    fun onMove(v: View, start: Int, end: Int)
    fun onUp(v: View, start: Int, end: Int)
}