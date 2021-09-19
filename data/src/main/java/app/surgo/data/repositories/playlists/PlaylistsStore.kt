package app.surgo.data.repositories.playlists

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.*
import app.surgo.data.entities.*
import app.surgo.data.mappers.DataSourceToAlbumEntity
import app.surgo.data.mappers.DataSourceToArtistEntity
import app.surgo.data.mappers.DataSourceToPlaylistEntity
import app.surgo.data.mappers.DataSourceToSongEntity
import app.surgo.data.repositories.lastrequests.LastRequestsStore
import app.surgo.shared.plugin.DataSourceManager
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.tsukiymk.surgo.openapi.datasource.PlaylistsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Singleton
class PlaylistsStore @Inject constructor(
    private val sourceManager: DataSourceManager,
    private val transactionRunner: DatabaseTransactionRunner,
    private val lastRequestsStore: LastRequestsStore,
    private val playlistsDao: PlaylistsDao,
    private val playlistSongsDao: PlaylistSongsDao,
    private val albumsDao: AlbumsDao,
    private val artistsDao: ArtistsDao,
    private val songsDao: SongsDao,
    private val songArtistsDao: SongArtistsDao,
    private val recommendedPlaylistsDao: RecommendedPlaylistsDao,
    private val popularPlaylistsDao: PopularPlaylistsDao
) {
    private val source: Long
        get() = sourceManager.key

    private val playlistsDataSource: PlaylistsDataSource
        get() = sourceManager.factory.playlistsDataSource()

    suspend fun fetchRecommended(pageSize: Int) {
        val playlists = playlistsDataSource.getRecommendedPlaylists(pageSize)
            .getOrNull() ?: emptyList()
        val entries = playlists.map { playlist ->
            val playlistId = playlistsDao.insertOrUpdate(
                DataSourceToPlaylistEntity(playlist, source)
            )

            RecommendedPlaylistEntry(playlistId = playlistId)
        }

        recommendedPlaylistsDao.deleteAll()
        recommendedPlaylistsDao.insertAll(entries)
    }

    suspend fun fetchPopular(pageSize: Int) {
        val playlists = playlistsDataSource.getPopularPlaylists(pageSize)
            .getOrNull() ?: emptyList()
        val entries = playlists.map { playlist ->
            val playlistId = playlistsDao.insertOrUpdate(
                DataSourceToPlaylistEntity(playlist, source)
            )

            PopularPlaylistEntry(playlistId = playlistId)
        }

        popularPlaylistsDao.deleteAll()
        popularPlaylistsDao.insertAll(entries)
    }

    suspend fun fetchPlaylistSongs(): Store<Long, List<PlaylistSongEntry>> = StoreBuilder.from(
        fetcher = Fetcher.of { playlistId ->
            playlistsDataSource.getPlaylist(
                playlistsDao.getPlaylistByIdOrThrow(playlistId).originId
            ).also {
                if (it.isSuccess) {
                    lastRequestsStore.updateLastRequest(Request.PLAYLIST_SONGS, playlistId)
                }
            }.getOrThrow()
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { playlistId ->
                playlistSongsDao.entriesForStore(playlistId)
                    .map { entries ->
                        when {
                            entries.isEmpty() -> null
                            lastRequestsStore.isRequestExpired(Request.PLAYLIST_SONGS, playlistId, Duration.ofDays(1)) -> null
                            else -> entries
                        }
                    }
            },
            writer = { playlistId, response ->
                transactionRunner {
                    response.tracks
                        ?.map { track ->
                            // TODO: It has a better way to insert.
                            val album = track.album ?: return@transactionRunner
                            val albumId = albumsDao.insertOrUpdate(
                                DataSourceToAlbumEntity(album, source)
                            )
                            val songId = songsDao.insertOrUpdate(
                                DataSourceToSongEntity(track, albumId, source)
                            )

                            // TODO: It has a better way to insert.
                            track.artists.orEmpty()
                                .map { artist ->
                                    val artistId = artistsDao.insertOrUpdate(
                                        DataSourceToArtistEntity(artist, source)
                                    )

                                    SongArtistEntry(
                                        songId = songId,
                                        artistId = artistId
                                    )
                                }
                                .let {
                                    songArtistsDao.insertOrUpdate(it)
                                }

                            PlaylistSongEntry(
                                playlistId = playlistId,
                                songId = songId
                            )
                        }
                        ?.let {
                            playlistSongsDao.deleteByPlaylistId(playlistId)
                            playlistSongsDao.insertOrUpdate(it)
                        }
                }
            },
            delete = playlistSongsDao::deleteByPlaylistId,
            deleteAll = playlistSongsDao::deleteAll
        )
    ).build()
}
