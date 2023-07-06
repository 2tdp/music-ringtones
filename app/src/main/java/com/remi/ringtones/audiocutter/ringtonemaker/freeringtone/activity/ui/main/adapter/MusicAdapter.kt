package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemMusicBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils.Utils
import javax.inject.Inject


class MusicAdapter @Inject constructor() : RecyclerView.Adapter<MusicAdapter.MusicHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var isVisibleNumb = true
    private var lstMusic = mutableListOf<MusicEntity>()
    var strDurationPlay = ""

    fun newInstance(context: Context, isVisibleNumb: Boolean, callback: ICallBackItem) {
        this.context = context
        this.isVisibleNumb = isVisibleNumb
        this.callback = callback
    }

    fun setData(lstMusic: MutableList<MusicEntity>) {
        this.lstMusic = lstMusic

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstMusic.isNotEmpty()) lstMusic.size else 0
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        holder.bind(position)
    }

    inner class MusicHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        private val binding: ItemMusicBinding

        init {
            this.binding = binding as ItemMusicBinding
            binding.tvName.apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val music = lstMusic[position]

            if (isVisibleNumb) binding.ctlPos.visibility = View.VISIBLE
            else binding.ctlPos.visibility = View.GONE

            if (music.isPlay) {
                binding.ivPlayAudio.visibility = View.VISIBLE
                binding.tvPosition.visibility = View.GONE
            } else {
                binding.ivPlayAudio.visibility = View.GONE
                binding.tvPosition.apply {
                    visibility = View.VISIBLE
                    text = (position + 1).toString()
                }
            }
            binding.tvName.apply {
                text = music.songName
                setTextColor(if (music.isPlay) ContextCompat.getColor(context, R.color.main_color) else Color.WHITE)
            }
            val time = Utils.formatTime(music.duration)
            val strDuration = if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
            binding.tvDuration.text = if (music.isPlay) "$strDurationPlay / $strDuration" else strDuration

            binding.root.setOnClickListener {
                setCurrent(position)
                callback.callBack(music, position)
            }
            binding.ivMore.setOnClickListener { callback.callBack(music, -1) }
        }
    }

    fun setProgress(duration: Int, position: Int) {
        val time = Utils.formatTime(duration.toLong())
        strDurationPlay = if (time.size == 4) "${time[3]}:${time[2]}:${time[1]}" else "${time[2]}:${time[1]}"
        notifyItemRangeChanged(position, 1,false)
    }

    fun setCurrent(position: Int) {
        for (pos in lstMusic.indices) lstMusic[pos].isPlay = pos == position

        notifyChange()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}