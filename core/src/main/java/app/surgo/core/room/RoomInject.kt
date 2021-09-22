package app.surgo.core.room

import android.content.Context
import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomDatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.buildDatabase(context)
}

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseModuleBinds {
    @Singleton
    @Binds
    abstract fun provideDatabaseTransactionRunner(
        runner: RoomTransactionRunner
    ): DatabaseTransactionRunner
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun provideLastRequestDao(
        database: AppDatabase
    ): LastRequestsDao = database.lastRequestsDao()

    @Provides
    fun provideTracksDao(
        database: AppDatabase
    ): TracksDao = database.tracksDao()

    @Provides
    fun provideArtistsDao(
        database: AppDatabase
    ): ArtistsDao = database.artistsDao()

    @Provides
    fun provideSongsDao(
        database: AppDatabase
    ): SongsDao = database.songsDao()

    @Provides
    fun provideSongArtistsDao(
        database: AppDatabase
    ): SongArtistsDao = database.songArtistsDao()

    @Provides
    fun provideAlbumsDao(
        database: AppDatabase
    ): AlbumsDao = database.albumsDao()

    @Provides
    fun provideAlbumArtistsDao(
        database: AppDatabase
    ): AlbumArtistsDao = database.albumArtistsDao()

    @Provides
    fun provideVideosDao(
        database: AppDatabase
    ): VideosDao = database.videosDao()

    @Provides
    fun provideVideoArtistsDao(
        database: AppDatabase
    ): VideoArtistsDao = database.videoArtistsDao()

    @Provides
    fun providePlaylistsDao(
        database: AppDatabase
    ): PlaylistsDao = database.playlistsDao()

    @Provides
    fun providePlaylistSongsDao(
        database: AppDatabase
    ): PlaylistSongsDao = database.playlistSongsDao()

    @Provides
    fun provideRecommendedDao(
        database: AppDatabase
    ): RecommendedPlaylistsDao = database.recommendedDao()

    @Provides
    fun providePopularSongsDao(
        database: AppDatabase
    ): PopularSongsDao = database.popularSongsDao()

    @Provides
    fun providePopularPlaylistsDao(
        database: AppDatabase
    ): PopularPlaylistsDao = database.popularPlaylistsDao()
}
