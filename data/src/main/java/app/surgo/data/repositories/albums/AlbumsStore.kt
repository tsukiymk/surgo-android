package app.surgo.data.repositories.albums

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.AlbumsDao
import app.surgo.data.daos.ArtistsDao
import app.surgo.data.daos.SongArtistsDao
import app.surgo.data.daos.SongsDao
import app.surgo.data.repositories.lastrequests.LastRequestsStore
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.AlbumsDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Singleton
class AlbumsStore @Inject constructor(
    private val sourceManager: DataSourceManager,
    private val transactionRunner: DatabaseTransactionRunner,
    private val lastRequestsStore: LastRequestsStore,
    private val albumsDao: AlbumsDao,
    private val artistsDao: ArtistsDao,
    private val songsDao: SongsDao,
    private val songArtistsDao: SongArtistsDao
) {
    private val source
        get() = sourceManager.key

    private val albumsDataSource: AlbumsDataSource
        get() = sourceManager.factory.albumsDataSource()

    /*
    suspend fun fetchAlbumSongs(): Store<Long, AlbumEntity> = StoreBuilder.from(
        fetcher = Fetcher.of { albumId ->
            albumsDataSource.getAlbum(
                albumsDao.getAlbumByIdOrThrow(albumId).originId
            ).also {
                if (it.isSuccess) {
                    lastRequestsStore.updateLastRequest(Request.ALBUM_SONGS, albumId)
                }
            }.getOrThrow()
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { albumId ->
                albumsDao.getAlbumByIdForStore(albumId)
                    .map { album ->
                        when {
                            lastRequestsStore.isRequestExpired(Request.ALBUM_SONGS, albumId, Duration.ofDays(28)) -> null
                            else -> album
                        }
                    }
            },
            writer = { albumId, response ->
                transactionRunner {
                    response.songs?.forEach { song ->
                        val album = albumsDao.getAlbumByIdOrThrow(albumId)
                        val songId = songsDao.insertOrUpdate(
                            DataSourceToSongEntity(song, album, source)
                        )

                        // TODO: It has a better way to insert.
                        song.artists.orEmpty()
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

                    }
                }
            },
            delete = albumsDao::deleteById,
            deleteAll = albumsDao::deleteAll
        )
    ).build()
     */
}
