package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MUSIC_DB

@Entity(tableName = MUSIC_DB)
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "name")
    val songName: String="",
    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean = false,
    @ColumnInfo(name = "duration")
    val duration: Long = 0L,
    @ColumnInfo(name = "path")
    val path: String = "",
    @ColumnInfo(name = "album")
    val album: String = "",
    @ColumnInfo(name = "uri")
    val uri: String = "",
    @ColumnInfo(name = "play")
    var isPlay: Boolean = false
){}