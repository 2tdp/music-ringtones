package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.FragSearchBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.ID_MUSIC
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class SearchFrag: BaseFragment<FragSearchBinding>(FragSearchBinding::inflate) {
    
    companion object {
        fun newInstance(): SearchFrag {
            val args = Bundle()

            val fragment = SearchFrag()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var adapterMusic: MusicAdapter

    private val viewModel: MainActivityViewModel by activityViewModels()

    private var music: MusicEntity? = null
    private var mediaPlayer: MediaPlayer? = null
    private var curPosition = -1
    private var oldPosition = -1
    private var isPlayAudio = false
    private var timer: Timer? = null
    var strSearch = ""

    override fun setUp() {
        adapterMusic.newInstance(requireContext(), false, object : ICallBackItem {
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
        binding.rcvSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvSearch.adapter = adapterMusic

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateMusicSearch.collect {
                    when (it) {
                        is UiState.Success -> {
                            if (strSearch == "") {
                                binding.rcvSearch.visibility = View.GONE
                                binding.tvNoData.visibility = View.GONE
                            } else {
                                if (it.data.isNotEmpty()) {
                                    binding.tvNoData.visibility = View.GONE
                                    binding.rcvSearch.visibility = View.VISIBLE
                                    adapterMusic.setData(it.data)
                                } else {
                                    binding.tvNoData.visibility = View.VISIBLE
                                    binding.rcvSearch.visibility = View.GONE
                                }
                            }
                        }

                        is UiState.Loading -> {

                        }

                        is UiState.Error -> {

                        }
                    }
                }
            }
        }
    }

    fun handPlay() {
        if (oldPosition != -1) adapterMusic.notifyItemRangeChanged(oldPosition, 1, false)
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), Uri.parse(music!!.path))
                setOnPreparedListener {
                    isPlayAudio = true
                    start()
                    music!!.isPlay = true
                    adapterMusic.notifyItemRangeChanged(curPosition, 1, false)
                }
                setOnCompletionListener { handStop() }
                prepareAsync()
            }

            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post {
                        adapterMusic.setProgress(mediaPlayer!!.currentPosition, curPosition)
                    }
                }

            }, 0, 500)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun handStop() {
        if (isPlayAudio) music?.isPlay = false
        if (timer != null) {
            timer!!.purge()
            timer!!.cancel()
            timer = null
        }
        mediaPlayer?.let {
            if (isPlayAudio) {
                if (it.isPlaying) {
                    it.apply {
                        stop()
                        release()
                    }
                }
                isPlayAudio = false
            }
        }
    }
}