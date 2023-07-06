package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback

import java.util.Objects

interface ICallBackDimensional {
    fun callBackItem(objects: Objects, callBackItem: ICallBackItem)

    fun callBackCheck(objects: Objects, check: ICallBackCheck)
}