package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseFragment
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.edit.EditMusicActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivityViewModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.adapter.MusicAdapter
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.FragAlbumBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.ID_MUSIC
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MY_SAVED
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class AlbumFrag: BaseFragment<FragAlbumBinding>(FragAlbumBinding::inflate) {

    companion object {
        private const val NAME_ALBUM = "NAME_ALBUM"
        fun newInstance(nameAlbum: String): AlbumFrag {
            val args = Bundle()
            args.putString(NAME_ALBUM, nameAlbum)
            val fragment = AlbumFrag()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var musicAdapter: MusicAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()
    private var nameAlbum = ""

    private var music: MusicEntity? = null
    private var mediaPlayer: MediaPlayer? = null
    private var curPosition = -1
    private var oldPosition = -1
    private var isPlayAudio = false
    private var timer: Timer? = null

    override fun setUp() {
        arguments?.getString(NAME_ALBUM, "")?.let {
            nameAlbum = it
        }
        binding.tvTitleAlbum.apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        binding.ivBack.setOnClickListener { parentFragmentManager.popBackStack("AlbumFrag", -1) }

        musicAdapter.newInstance(requireContext(), true, object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                music = ob as MusicEntity
                curPosition = position
                if (position == -1) {
                    if (isPlayAudio) handStop()

                    startActivity(Intent(requireContext(), EditMusicActivity::class.java).apply {
                        putExtra(ID_MUSIC, music!!.id)
                    })
                } else {
                    if (isPlayAudio) {
                        handStop()
                        if (oldPosition != position) handPlay()
                    } else handPlay()

                    oldPosition = position
                }
            }
        })
        binding.rcvMusic.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvMusic.adapter = musicAdapter

        lifecycleScope.launch {
            if (nameAlbum != "Favorite" && nameAlbum != "My Saved")
                viewModel.getAllMusicFromAlbum(nameAlbum)
            else {
                if (nameAlbum == "Favorite") viewModel.getAllMusicFavorite()
                if (nameAlbum == "My Saved") viewModel.getAllMusicSaved()
            }
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFavorite.collect {
                    when(it) {
                        is UiState.Success -> {
                            if (it.data.isNotEmpty()) {
                                musicAdapter.setData(it.data)
                                binding.tvTitleAlbum.text = it.data[0].album
                                binding.tvEmpty.visibility = View.GONE
                                binding.rcvMusic.visibility = View.VISIBLE
                            } else {
                                binding.tvTitleAlbum.text = ""
                                binding.rcvMusic.visibility = View.GONE
                                binding.tvEmpty.visibility = View.VISIBLE
                            }
                        }
                        is UiState.Error -> {}
                        is UiState.Loading -> {}
                    }
                }
            }
        }
    }

    fun handPlay() {
        if (oldPosition != -1) musicAdapter.notifyItemRangeChanged(oldPosition, 1, false)
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), Uri.parse(music!!.path))
                setOnPreparedListener {
                    isPlayAudio = true
                    start()
                    music!!.isPlay = true
                    musicAdapter.notifyItemRangeChanged(curPosition, 1, false)
                }
                setOnCompletionListener { handStop() }
                prepareAsync()
            }
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post {
                        musicAdapter.setProgress(mediaPlayer!!.currentPosition, curPosition)
                    }
                }

            }, 0, 500)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun handStop() {
        if (isPlayAudio) {
            music?.isPlay = false
            musicAdapter.notifyChange()
        }
        if (timer != null) {
            timer!!.purge()
            timer!!.cancel()
            timer = null
        }
        mediaPlayer?.let {
            if (!isPlayAudio) return
            if (it.isPlaying) {
                it.apply {
                    stop()
                    release()
                }
            }
            isPlayAudio = false
            musicAdapter.notifyChange()
        }
    }
}