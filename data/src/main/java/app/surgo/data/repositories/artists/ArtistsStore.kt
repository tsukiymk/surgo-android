package app.surgo.data.repositories.artists

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.*
import app.surgo.data.entities.AlbumArtistEntry
import app.surgo.data.entities.PopularSongEntry
import app.surgo.data.mappers.CatalogToAlbumEntity
import app.surgo.data.mappers.CatalogToArtistEntity
import app.surgo.data.mappers.CatalogToSongEntity
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
    private val albumsDao: AlbumsDao,
    private val albumArtistsDao: AlbumArtistsDao,
    private val songsDao: SongsDao,
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
            views = arrayOf(ViewType.POPULAR_SONGS, ViewType.FULL_ALBUMS)
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
                        ViewType.POPULAR_SONGS -> {
                            // Store songs
                            transactionRunner {
                                view.data.orEmpty()
                                    .map { songCatalog ->
                                        val song = CatalogToSongEntity(
                                            songCatalog,
                                            source
                                        )

                                        val songId = songsDao.insertOrUpdate(song)
                                        PopularSongEntry(
                                            artistId = artistId,
                                            songId = songId
                                        )
                                    }
                                    .let {
                                        popularSongsDao.deleteByArtistId(artistId)
                                        popularSongsDao.insertOrUpdate(it)
                                    }
                            }
                        }
                        ViewType.FULL_ALBUMS -> {
                            // Store albums
                            transactionRunner {
                                view.data.orEmpty()
                                    .map { albumCatalog ->
                                        val album = CatalogToAlbumEntity(
                                            albumCatalog,
                                            source = source
                                        )

                                        val albumId = albumsDao.insertOrUpdate(album)
                                        AlbumArtistEntry(
                                            albumId =  albumId,
                                            artistId = artistId
                                        )
                                    }
                                    .let {
                                        albumArtistsDao.deleteByArtistId(artistId)
                                        albumArtistsDao.insertOrUpdate(it)
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
