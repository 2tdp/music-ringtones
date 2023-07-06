package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.di

import android.content.Context
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicRoomDatabase
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = MusicRoomDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideDao(db: MusicRoomDatabase) = db.musicDao()

    @Provides
    fun provideEntity() = MusicEntity()


}