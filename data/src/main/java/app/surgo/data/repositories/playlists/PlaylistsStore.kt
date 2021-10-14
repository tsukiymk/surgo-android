package app.surgo.data.repositories.playlists

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.*
import app.surgo.data.entities.PlaylistSongEntry
import app.surgo.data.entities.Request
import app.surgo.data.entities.SongArtistEntry
import app.surgo.data.mappers.CatalogToArtistEntity
import app.surgo.data.mappers.CatalogToSongEntity
import app.surgo.data.repositories.lastrequests.LastRequestsStore
import app.surgo.data.DataSourceManager
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.tsukiymk.surgo.openapi.datasource.PlaylistsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.util.*
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
    private val artistsDao: ArtistsDao,
    private val songsDao: SongsDao,
    private val songArtistsDao: SongArtistsDao
) {
    private val source: Long
        get() = sourceManager.selectedSource

    private val playlistsDataSource: PlaylistsDataSource
        get() = sourceManager[source].playlistsDataSource()

    suspend fun catalog(): Store<Long, List<PlaylistSongEntry>> = StoreBuilder.from(
        fetcher = Fetcher.of { playlistId ->
            playlistsDataSource.catalog(
                playlistsDao.getPlaylistByIdOrThrow(playlistId).originId,
                storefront = Locale.getDefault().language,
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
            writer = { playlistId, catalog ->
                transactionRunner {
                    catalog.relationships?.tracks?.data
                        ?.map { trackCatalog ->
                            // Do not fill album info
                            val songId = songsDao.insertOrUpdate(
                                CatalogToSongEntity(trackCatalog, source)
                            )

                            trackCatalog.relationships?.artists?.data.orEmpty()
                                .map { artistCatalog ->
                                    val artistId = artistsDao.insertOrUpdate(
                                        CatalogToArtistEntity(artistCatalog, source)
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
