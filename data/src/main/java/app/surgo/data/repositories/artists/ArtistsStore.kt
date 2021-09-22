package app.surgo.data.repositories.artists

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.*
import app.surgo.data.entities.AlbumArtistEntry
import app.surgo.data.entities.PopularSongEntry
import app.surgo.data.entities.VideoArtistEntry
import app.surgo.data.mappers.CatalogToAlbumEntity
import app.surgo.data.mappers.CatalogToArtistEntity
import app.surgo.data.mappers.CatalogToSongEntity
import app.surgo.data.mappers.CatalogToVideoEntity
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.ArtistsDataSource
import com.tsukiymk.surgo.openapi.datasource.CatalogType
import com.tsukiymk.surgo.openapi.datasource.ViewType
import java.util.*
import javax.inject.Inject

class ArtistsStore @Inject constructor(
    private val sourceManager: DataSourceManager,
    private val transactionRunner: DatabaseTransactionRunner,
    private val artistsDao: ArtistsDao,
    private val songsDao: SongsDao,
    private val albumsDao: AlbumsDao,
    private val albumArtistsDao: AlbumArtistsDao,
    private val videosDao: VideosDao,
    private val videoArtistsDao: VideoArtistsDao,
    private val popularSongsDao: PopularSongsDao
) {
    private val source: Long
        get() = sourceManager.key

    private val artistsDataSource: ArtistsDataSource
        get() = sourceManager.factory.artistsDataSource()

    // TODO: Complex block
    suspend fun fetchCatalog(
        artistId: Long
    ) {
        val catalog = artistsDataSource.catalog(
            artistId = artistsDao.getArtistByIdOrThrow(artistId).originId,
            storefront = Locale.getDefault().language,
            views = arrayOf(ViewType.TOP_SONGS, ViewType.FULL_ALBUMS, ViewType.TOP_MUSIC_VIDEOS)
        ).getOrNull() ?: return

        when (catalog.type) {
            CatalogType.ARTISTS -> {
                // Update DB entity
                artistsDao.insertOrUpdate(
                    CatalogToArtistEntity(catalog, source)
                )

                catalog.views?.forEach { (t, view) ->
                    when (t) {
                        // Popular songs
                        ViewType.TOP_SONGS -> {
                            // Store songs
                            transactionRunner {
                                view.data?.map { songCatalog ->
                                    val songId = songsDao.insertOrUpdate(
                                        CatalogToSongEntity(songCatalog, source)
                                    )
                                    PopularSongEntry(
                                        artistId = artistId,
                                        songId = songId
                                    )
                                }?.let {
                                    popularSongsDao.deleteByArtistId(artistId)
                                    popularSongsDao.insertOrUpdate(it)
                                }
                            }
                        }
                        ViewType.FULL_ALBUMS -> {
                            // Store albums
                            transactionRunner {
                                view.data?.map { albumCatalog ->
                                    val albumId = albumsDao.insertOrUpdate(
                                        CatalogToAlbumEntity(albumCatalog, source)
                                    )
                                    AlbumArtistEntry(
                                        albumId = albumId,
                                        artistId = artistId
                                    )
                                }?.let {
                                    albumArtistsDao.deleteByArtistId(artistId)
                                    albumArtistsDao.insertOrUpdate(it)
                                }
                            }
                        }
                        ViewType.TOP_MUSIC_VIDEOS -> {
                            // Store music videos
                            transactionRunner {
                                view.data?.map { videoCatalog ->
                                    val videoId = videosDao.insertOrUpdate(
                                        CatalogToVideoEntity(videoCatalog, source)
                                    )
                                    VideoArtistEntry(
                                        videoId = videoId,
                                        artistId = artistId
                                    )
                                }?.let {
                                    videoArtistsDao.deleteByArtistId(artistId)
                                    videoArtistsDao.insertOrUpdate(it)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
            else -> return
        }
    }

    /*
    suspend fun fetchPopularSongs(): Store<Long, List<PopularSongEntry>> = StoreBuilder.from(
        fetcher = Fetcher.of { artistId ->
            artistsDataSource.getPopularSongs()
        }
    )
    */
}
