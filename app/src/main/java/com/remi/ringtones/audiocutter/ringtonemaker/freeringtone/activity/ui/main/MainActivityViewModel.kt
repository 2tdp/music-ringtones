package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.AlbumsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MY_SAVED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val _uiStateAllMusic = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateAllMusic: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateAllMusic

    private val _uiStateAlbums = MutableStateFlow<UiState<MutableList<AlbumsModel>>>(UiState.Loading)
    val uiStateAlbums: StateFlow<UiState<MutableList<AlbumsModel>>> = _uiStateAlbums

    private val _uiStateFavorite = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateFavorite: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateFavorite

    private val _uiStateSaved = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateSaved: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateSaved

    private val _uiStateMusicSearch = MutableStateFlow<UiState<MutableList<MusicEntity>>>(UiState.Loading)
    val uiStateMusicSearch: StateFlow<UiState<MutableList<MusicEntity>>> = _uiStateMusicSearch

    fun getAllMusic() {
        viewModelScope.launch {
            repository.getAllMusic().catch {
                _uiStateAllMusic.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateAllMusic.value = UiState.Success(it)
            }
        }
    }

    fun getAllAlbum() {
        viewModelScope.launch {
            repository.getAllAlbum().catch {
                _uiStateAlbums.value = UiState.Error(it.message.toString())
            }.collect {
                val lstAlbum = it
                repository.getAllMusicFavorite().catch { throwable ->
                    _uiStateAlbums.value = UiState.Error(throwable.message.toString())
                }.collect {lstFavorite ->
                    lstAlbum.add(0, AlbumsModel("Favorite", lstFavorite.size))
                    repository.getAllMusicSaved().catch { throwable ->
                        _uiStateAlbums.value = UiState.Error(throwable.message.toString())
                    }.collect {lstSaved ->
                        lstAlbum.add(1, AlbumsModel("My Saved", lstSaved.size))
                        _uiStateAlbums.value = UiState.Success(lstAlbum)
                    }
                }
            }
        }
    }

    fun getAllMusicFromAlbum(name: String) {
        viewModelScope.launch {
            repository.getAllMusicFromAlbum(name).catch {
                _uiStateFavorite.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateFavorite.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicFavorite() {
        viewModelScope.launch {
            repository.getAllMusicFavorite().catch {
                _uiStateFavorite.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateFavorite.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicSaved() {
        viewModelScope.launch {
            repository.getAllMusicSaved().catch {
                _uiStateFavorite.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateFavorite.value = UiState.Success(it)
            }
        }
    }

    fun getAllMusicFromText(str: String) {
        viewModelScope.launch {
            repository.getAllMusicFromText(str).catch {
                _uiStateMusicSearch.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateMusicSearch.value = UiState.Success(it)
            }
        }
    }
}