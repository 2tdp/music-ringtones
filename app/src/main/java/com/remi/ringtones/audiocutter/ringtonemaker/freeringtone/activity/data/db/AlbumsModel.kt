package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db

import androidx.room.ColumnInfo
import java.io.Serializable

data class AlbumsModel (
    @ColumnInfo(name = "album")
    var nameAlbum: String = "",
    @ColumnInfo(name = "count(*)")
    var size: Int = 0) : Serializable {
}