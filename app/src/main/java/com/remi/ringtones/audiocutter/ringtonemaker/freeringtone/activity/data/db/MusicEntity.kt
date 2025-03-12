package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.MUSIC_DB

@Entity(tableName = MUSIC_DB)
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "name")
    var songName: String = "",
    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean = false,
    @ColumnInfo(name = "duration")
    var duration: Long = 0L,
    @ColumnInfo(name = "path")
    var path: String = "",
    @ColumnInfo(name = "album")
    var album: String = "",
    @ColumnInfo(name = "uri")
    var uri: String = "",
    @ColumnInfo(name = "play")
    var isPlay: Boolean = false
)