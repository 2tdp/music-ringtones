package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.cutmusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CutMusicActivityViewModel @Inject constructor(val repository: DataRepository): ViewModel() {

    private val _uiStateMusic = MutableStateFlow<UiState<MusicEntity>>(UiState.Loading)
    val uiStateMusic: StateFlow<UiState<MusicEntity>> = _uiStateMusic

    fun getMusicWithId(id: Long) {
        if (id == -1L) _uiStateMusic.value = UiState.Error("Can't get music")
        else viewModelScope.launch {
            repository.getMusicWithId(id).catch {
                _uiStateMusic.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateMusic.value = UiState.Success(it)
            }
        }
    }

    fun insertMusic(music: MusicEntity): Long {
        return repository.insert(music)
    }

    fun updateMusic(id: Long, newID: Long) {
        repository.updateWithId(id, newID)
    }
}