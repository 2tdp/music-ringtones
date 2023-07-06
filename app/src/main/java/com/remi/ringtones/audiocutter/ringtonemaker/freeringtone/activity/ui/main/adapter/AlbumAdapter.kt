package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.AlbumsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemAlbumBinding
import javax.inject.Inject

class AlbumAdapter @Inject constructor(): RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    private lateinit var context: Context
    private lateinit var callback: ICallBackItem
    private var lstAlbums = mutableListOf<AlbumsModel>()

    fun newInstance(context: Context, callback: ICallBackItem) {
        this.context = context
        this.callback = callback
    }

    fun setData(lstAlbums: MutableList<AlbumsModel>) {
        this.lstAlbums = lstAlbums

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        return AlbumHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstAlbums.isNotEmpty()) lstAlbums.size else 0
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        holder.onBind(position)
    }

    inner class AlbumHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val album = lstAlbums[position]
            binding.tvNameAlbums.apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }

            binding.tvNameAlbums.text = album.nameAlbum
            binding.tvSize.text = "${album.size} ${if (album.size > 1) "ringtones" else "ringtone"}"

            binding.root.setOnClickListener { callback.callBack(album, position) }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}