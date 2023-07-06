package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.contact.ContactModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.contact.DataContact
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.UiState
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.LIST_FAVORITE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMusicActivityViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {

    private val _uiStateMusic = MutableStateFlow<UiState<MusicEntity>>(UiState.Loading)
    val uiStateMusic: StateFlow<UiState<MusicEntity>> = _uiStateMusic

    private val _uiStateContact = MutableStateFlow<UiState<MutableList<ContactModel>>>(UiState.Loading)
    val uiStateContact: StateFlow<UiState<MutableList<ContactModel>>> = _uiStateContact

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

    fun getAllContact(context: Context) {
        viewModelScope.launch {
            DataContact.getAllContact(context).catch {
                _uiStateContact.value = UiState.Error(it.message.toString())
            }.collect {
                _uiStateContact.value = UiState.Success(it)
            }
        }
    }

    fun addFavorite(music: MusicEntity) {
        viewModelScope.launch {
            repository.updateFavorite(music.id, music.isFavorite)
            val lst = DataLocalManager.getListFavorite(LIST_FAVORITE)
            val item = lst.filter { it.id ==  music.id }
            if (item.isEmpty()) lst.add(0, music)
            DataLocalManager.setListFavorite(lst, LIST_FAVORITE)
        }
    }

    fun removeFavorite(music: MusicEntity) {
        viewModelScope.launch {
            repository.updateFavorite(music.id, music.isFavorite)
            val lst = DataLocalManager.getListFavorite(LIST_FAVORITE)
            val item = lst.filter { it.id == music.id }
            if (item.isNotEmpty()) lst.remove(item[0])
            DataLocalManager.setListFavorite(lst, LIST_FAVORITE)
        }
    }
}