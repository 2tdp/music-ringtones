package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(music: MusicEntity): Long

    @Query("UPDATE music SET id = :newID WHERE id = :id")
    fun update(id: Long, newID: Long)

    @Delete
    suspend fun delete(music: MusicEntity)

    @Query("SELECT * from music")
    fun getAllMusic(): Flow<MutableList<MusicEntity>>

    @Query("SELECT * from music WHERE id = :id")
    fun getMusicWithId(id: Long): Flow<MusicEntity>

    @Query("SELECT album, count(*) from music group by album")
    fun getAllAlbum(): Flow<MutableList<AlbumsModel>>

    @Query("SELECT * from music WHERE album = :album")
    fun getAllMusicFromAlbum(album: String): Flow<MutableList<MusicEntity>>

    @Query("SELECT * FROM music WHERE name LIKE '%' || :str || '%'")
    fun getAllMusicFromText(str: String): Flow<MutableList<MusicEntity>>

    @Query("SELECT * from music WHERE favorite = :favorite")
    fun getAllMusicFavorite(favorite: Boolean): Flow<MutableList<MusicEntity>>

    @Query("SELECT * from music WHERE album = :name")
    fun getAllMusicSaved(name: String): Flow<MutableList<MusicEntity>>

    @Query("UPDATE music SET favorite = :isFavorite WHERE id = :id")
    fun updateFavoriteMusic(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM music")
    fun deleteDB()
}