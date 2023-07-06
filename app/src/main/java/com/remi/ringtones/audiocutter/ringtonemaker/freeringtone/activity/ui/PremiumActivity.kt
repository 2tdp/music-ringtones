package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivityPremiumBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.extensions.createBackground

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#FF49CC"))
    }

    override fun setUp() {
        binding.ctlWeek.createBackground(
            intArrayOf(ContextCompat.getColor(this, R.color.white38)),
            (10.5f * w).toInt(), -1, -1
        )
        binding.ctlMonth.createBackground(
            intArrayOf(ContextCompat.getColor(this, R.color.white38)),
            (10.5f * w).toInt(), -1, -1
        )
        binding.ctlYear.createBackground(
            intArrayOf(ContextCompat.getColor(this, R.color.white38)),
            (10.5f * w).toInt(), -1, -1
        )
        binding.tvUpgrade.createBackground(
            intArrayOf(Color.WHITE), (10.5f * w).toInt(), -1, -1
        )

        binding.ivBack.setOnClickListener { onBackPressed(false) }
        binding.ctlWeek.setOnClickListener { clickOption("week") }
        binding.ctlMonth.setOnClickListener { clickOption("month") }
        binding.ctlYear.setOnClickListener { clickOption("year") }
    }

    private fun clickOption(option: String) {
        when (option) {
            "week" -> {
                binding.imWeek.setImageResource(R.drawable.ic_select)

                binding.imMonth.setImageResource(R.drawable.ic_un_select)
                binding.imYear.setImageResource(R.drawable.ic_un_select)
            }

            "month" -> {
                binding.imMonth.setImageResource(R.drawable.ic_select)

                binding.imWeek.setImageResource(R.drawable.ic_un_select)
                binding.imYear.setImageResource(R.drawable.ic_un_select)
            }

            "year" -> {
                binding.imYear.setImageResource(R.drawable.ic_select)

                binding.imWeek.setImageResource(R.drawable.ic_un_select)
                binding.imMonth.setImageResource(R.drawable.ic_un_select)
            }
        }
    }
}