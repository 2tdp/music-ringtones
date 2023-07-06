package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemOnBoardingBinding

class PagerOnBoardingAdapter(context: Context): RecyclerView.Adapter<PagerOnBoardingAdapter.PagerHolder>() {

    private val context: Context
    private var lstPage: ArrayList<Int>

    init {
        this.context = context
        this.lstPage = ArrayList()
    }

    fun setData(lstItem: ArrayList<Int>) {
        this.lstPage = lstItem

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(ItemOnBoardingBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        if (lstPage.isNotEmpty()) return lstPage.size
        return 0
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PagerHolder(private val binding: ItemOnBoardingBinding): ViewHolder(binding.root) {

        fun onBind(position: Int) {
            val item = lstPage[position]

            Glide.with(context)
                .load(item)
                .into(binding.imgItemOnBoarding)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}