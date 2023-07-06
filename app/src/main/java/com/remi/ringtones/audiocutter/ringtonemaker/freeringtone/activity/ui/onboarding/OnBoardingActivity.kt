package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.adapter.DepthPageTransformer
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityOnBoardingBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FIRST_INSTALL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    companion object {
        var w = 0F
    }

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#FF57AF"))
    }

    override fun setUp() {
        w = resources.displayMetrics.widthPixels / 100F
        binding.tvContinue.createBackground(
            intArrayOf(ContextCompat.getColor(this, R.color.white)),
            (8.4f * w).toInt(),
            -1, -1
        )
        binding.tvContinue.setOnClickListener {
            if (binding.viewpager.currentItem == 0)
                binding.viewpager.setCurrentItem(1, true)
            else {
                startIntent(MainActivity::class.java.name, false)
                DataLocalManager.setFirstInstall(FIRST_INSTALL, true)
            }
        }

        val pageAdapter = PagerOnBoardingAdapter(this).apply {
            setData(ArrayList<Int>().apply {
                add(R.drawable.img_on_boarding)
                add(R.drawable.img_on_boarding_2)
            })
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setPage(position)
            }
        })

        binding.viewpager.apply {
            setPageTransformer(DepthPageTransformer())
            adapter = pageAdapter
        }

        binding.indicator.attachTo(binding.viewpager)
    }

    private fun setPage(position: Int) {
        when (position) {
            0 -> {
                binding.tv1.apply {
                    text = resources.getString(R.string.ringtone)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 9.44f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_extra_bold.ttf", this@OnBoardingActivity)
                }
                binding.tv2.apply {
                    text = resources.getString(R.string.for_android)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 8.33f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_regular.ttf", this@OnBoardingActivity)
                }
                binding.tvDes.apply {
                    text = resources.getString(R.string.set_a_quick_ringtone_to_make_your_phone_more_dynamic)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 4.44f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_regular.ttf", this@OnBoardingActivity)
                }
            }

            1 -> {
                binding.tv1.apply {
                    text = resources.getString(R.string.tons_of)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 9.44f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_medium.ttf", this@OnBoardingActivity)
                }
                binding.tv2.apply {
                    text = resources.getString(R.string.ringtone)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 11.11f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_extra_bold.ttf", this@OnBoardingActivity)
                }
                binding.tvDes.apply {
                    text =
                        resources.getString(R.string.huge_collection_of_trendy_popular_ringtones_from_different_categories)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 4.44f * w)
                    typeface = Utils.getTypeFace("nunito", "nunito_regular.ttf", this@OnBoardingActivity)
                }
            }
        }

    }
}