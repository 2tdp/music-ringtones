package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.frag

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.AlbumsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseFragment
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivityViewModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.adapter.AlbumAdapter
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.FragAlbumsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlbumsFrag: BaseFragment<FragAlbumsBinding>(FragAlbumsBinding::inflate) {

    companion object {
        fun newInstance(): AlbumsFrag {
            val args = Bundle()

            val fragment = AlbumsFrag()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var albumsAdapter: AlbumAdapter
    private val viewModel: MainActivityViewModel by activityViewModels()
    private var albumFrag: AlbumFrag? = null

    override fun setUp() {
        albumsAdapter.newInstance(requireContext(), object : ICallBackItem {
            override fun callBack(ob: Any, position: Int) {
                val album = ob as AlbumsModel
                albumFrag = AlbumFrag.newInstance(album.nameAlbum)
                replaceFragment(parentFragmentManager, albumFrag!!, true, true)
            }
        })
        binding.rcvAlbum.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvAlbum.adapter = albumsAdapter

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateAlbums.collect {
                    when(it) {
                        is UiState.Success -> {
                            if (it.data.isNotEmpty()) {
                                albumsAdapter.setData(it.data)
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

    fun backAlbum(): Boolean {
        if (albumFrag != null)
            albumFrag?.let {
                it.handStop()
                parentFragmentManager.popBackStack("AlbumFrag", -1)
                albumFrag = null
                return true
            }

        return false
    }
}