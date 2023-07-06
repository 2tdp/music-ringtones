package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base

import androidx.annotation.IdRes

interface BaseView {
    fun startIntent(nameActivity: String, isFinish: Boolean)
    fun hideKeyboard()
    fun showLoading()
    fun showLoading(cancelable: Boolean)
    fun hideLoading()
    fun showPopupMessage(msg: String, title: String)
    fun showPopupMessage(msg: String)
    fun showMessage(msg: String)
    fun showMessage(@IdRes stringId: Int)
    fun refresh()
}