package com.tsukiymk.surgo.di

import android.content.ComponentName
import android.content.Context
import app.surgo.core.media2.PlaybackConnection
import com.tsukiymk.surgo.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun providePlaybackConnection(
        @ApplicationContext context: Context
    ): PlaybackConnection = PlaybackConnection(
        context,
        ComponentName(context, MusicService::class.java)
    )
}
