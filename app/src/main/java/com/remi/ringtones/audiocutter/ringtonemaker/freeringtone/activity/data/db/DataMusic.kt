package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db

import android.app.Activity
import android.content.ContentUris
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.repository.DataRepository
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.LIST_FAVORITE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager

object DataMusic {

    suspend fun getSongList(context: Activity, repository: DataRepository) {

        val lstFavorite = DataLocalManager.getListFavorite(LIST_FAVORITE)
        // To get all music files on the device.
        val selection = StringBuilder("is_music != 0 AND title != ''")

        // Display audios in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor = context.contentResolver.query(
            EXTERNAL_CONTENT_URI,
            null,
            selection.toString(),
            null,
            sortOrder
        )

        repository.deleteDB()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val contentUri = ContentUris.withAppendedId(EXTERNAL_CONTENT_URI, id)

                repository.insert(
                    MusicEntity(
                        id, title.replace(title.substring(title.lastIndexOf("."), title.length), ""),
                        checkFavorite(id, lstFavorite), duration, data,
                        album ?: "", contentUri.toString(), false
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor?.close()
    }

    private fun checkFavorite(id: Long, lstFavorite: MutableList<MusicEntity>) : Boolean {
        val item = lstFavorite.filter { it.id == id }
        if (item.isNotEmpty()) return true

        return false
    }
}