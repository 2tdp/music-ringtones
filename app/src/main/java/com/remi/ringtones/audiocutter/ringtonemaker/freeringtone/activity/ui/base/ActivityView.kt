package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface ActivityView : BaseView {

    fun replaceFragment(fragment: Fragment, layout: Int, isAdd: Boolean, addBackStack: Boolean)
    fun replaceFragment(fragment: Fragment, isAdd: Boolean, addBackStack: Boolean)
    fun replaceFragment(fragment: Fragment, layout: Int, addBackStack: Boolean)
    fun replaceFragment(fragment: Fragment, addBackStack: Boolean)
    fun replaceFragment(manager: FragmentManager, fragment: Fragment, isAdd: Boolean, addBackStack: Boolean)

    fun replaceFragment(manager: FragmentManager, fragment: Fragment, layout: Int, isAdd: Boolean, addBackStack: Boolean)

    fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        shareElement: View,
        transitionName: String
    )

    fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    )

    fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        isAdd: Boolean,
        addBackStack: Boolean,
        enter: Int,
        exit: Int,
        popEnter: Int,
        popExit: Int
    )

    fun popBackStack(manager: FragmentManager)
    fun popBackStack()
    fun popBackStack(manager: FragmentManager, tag: String, inclusive: Int)
    fun popBackStack(tag: String, inclusive: Int)
    fun clearBackStack()
    fun onFragmentDetach(fragment: Fragment)
}