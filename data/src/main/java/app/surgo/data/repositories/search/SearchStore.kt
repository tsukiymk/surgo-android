package app.surgo.data.repositories.search

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.AlbumsDao
import app.surgo.data.daos.ArtistsDao
import app.surgo.data.daos.PlaylistsDao
import app.surgo.data.daos.SongsDao
import app.surgo.data.resultentities.SearchResults
import app.surgo.data.DataSourceManager
import app.surgo.data.mappers.CatalogToAlbumEntity
import app.surgo.data.mappers.CatalogToArtistEntity
import app.surgo.data.mappers.CatalogToSongEntity
import app.surgo.data.mappers.CatalogToVideoEntity
import com.tsukiymk.surgo.openapi.datasource.SearchDataSource
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType
import javax.inject.Inject

class SearchStore @Inject constructor(
    private val sourceManager: DataSourceManager,
    private val transactionRunner: DatabaseTransactionRunner,
    private val albumsDao: AlbumsDao,
    private val artistsDao: ArtistsDao,
    private val playlistsDao: PlaylistsDao,
    private val songsDao: SongsDao,
) {
    private val source: Long
        get() = sourceManager.selectedSource

    private val searchDataSource: SearchDataSource
        get() = sourceManager[source].searchDataSource()

    suspend fun search(query: String, types: Array<SearchType>): SearchResults {
        val results = searchDataSource.catalog(
            query = query,
            types = types
        ).getOrNull()?.results ?: return SearchResults()

        return transactionRunner {
            SearchResults(
                artists = results.artist?.data.orEmpty()
                    .map {
                        val artist = CatalogToArtistEntity(it, source)
                        artistsDao.insertOrUpdate(artist)
                    }
                    .map { artistsDao.getArtistByIdOrThrow(it) },
                albums = results.album?.data.orEmpty()
                    .map {
                        val album = CatalogToAlbumEntity(it, source)
                        albumsDao.insertOrUpdate(album)
                    }
                    .map { albumsDao.getAlbumByIdOrThrow(it) },
                songs = results.song?.data.orEmpty()
                    .map {
                        CatalogToSongEntity(it, source)
                    },
                videos = results.video?.data.orEmpty()
                    .map {
                        CatalogToVideoEntity(it, source)
                    }
            )
        }
    }
}
