package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.flow.merge

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

}

fun main() {

    val arr = merge(intArrayOf(1, 2, 3, 0, 0, 0), 3, intArrayOf(2, 5, 6), 3)

    print(arr)
}

//fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): MutableList<Int> {
//    val arrOut = mutableListOf<Int>()
//    for (i in 0 until m) arrOut.add(nums1.get(i))
//    for (i in 0 until n) arrOut.add(nums2.get(i))
//    for (i in 0 until arrOut.size - 1) {
//        if (arrOut.get(i) > arrOut.get(i + 1)) {
//            val c = arrOut.get(i)
//            arrOut.set(i, arrOut.get(i + 1))
//            arrOut.set(i + 1, c)
//        }
//    }
//
//    return arrOut
//}

fun merge(num1: IntArray, m: Int, num2: IntArray, n: Int): MutableList<Int> {
    val arrOut = mutableListOf<Int>()
    var i = 0
    while (i < m) {
        arrOut.add(num1.get(i))
        i++
    }
    var j = 0
    while (j < n) {
        arrOut.add(num2.get(j))
        j++
    }
    var x = 0
    while (x < arrOut.size - 1) {
        if (arrOut.get(x) > arrOut.get(x + 1)) {
            val c = arrOut.get(x)
            arrOut.set(x, arrOut.get(x + 1))
            arrOut.set(x + 1, c)
        }
        x++
    }

    return arrOut
}