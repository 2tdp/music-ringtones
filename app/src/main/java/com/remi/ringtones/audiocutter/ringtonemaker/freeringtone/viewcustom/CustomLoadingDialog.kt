package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R

import java.util.*

class CustomLoadingDialog(context: Context) : DialogFragment(), DialogInterface.OnKeyListener {

    private val context: Context
    private var ivLoading: LottieAnimationView? = null
    private var dismissOnBackPress = false

    init {
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = ViewLoading(context)
        ivLoading = view.vLoading
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(true)
        dialog?.window?.setDimAmount(0.2f)
        ivLoading?.apply {
            setAnimation(R.raw.iv_loading)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null)
            fragmentManager.beginTransaction().remove(prevFragment).commit()

        transaction.addToBackStack(null)
        show(transaction, tag)
    }

    fun setDismissOnBackPress(dismissOnBackPress: Boolean) {
        this.dismissOnBackPress = dismissOnBackPress
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener(this)
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {

        // pass on to be processed as normal
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dismissOnBackPress) dismiss()
        }
        return false // pretend we've processed it
    }
}