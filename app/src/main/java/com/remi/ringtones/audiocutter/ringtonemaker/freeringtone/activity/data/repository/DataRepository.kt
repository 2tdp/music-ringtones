package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.AlbumsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicDAO
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MY_SAVED
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepository @Inject constructor(val musicDao: MusicDAO) {

     fun getAllMusic() : Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusic()
     }

     fun getAllAlbum(): Flow<MutableList<AlbumsModel>> {
          return musicDao.getAllAlbum()
     }

     fun getAllMusicFromAlbum(name: String): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFromAlbum(name)
     }

     fun getAllMusicFromText(str: String): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFromText(str)
     }

     fun getAllMusicFavorite(): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFavorite(true)
     }

     fun getAllMusicSaved(): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicSaved(MY_SAVED)
     }

     suspend fun getMusicWithId(id: Long) = musicDao.getMusicWithId(id)

     fun insert(musicEntity: MusicEntity): Long = musicDao.insert(musicEntity)

     fun updateWithId(id: Long, newID: Long) = musicDao.update(id, newID)

     fun updateFavorite(id: Long, isFavorite: Boolean) = musicDao.updateFavoriteMusic(id, isFavorite)

     fun deleteDB() = musicDao.deleteDB()
}