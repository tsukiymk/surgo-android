package app.surgo.data.repositories.search

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.data.daos.AlbumsDao
import app.surgo.data.daos.ArtistsDao
import app.surgo.data.daos.PlaylistsDao
import app.surgo.data.daos.SongsDao
import app.surgo.data.mappers.DataSourceToAlbumEntity
import app.surgo.data.mappers.DataSourceToArtistEntity
import app.surgo.data.mappers.DataSourceToMusicVideoEntity
import app.surgo.data.mappers.DataSourceToSongEntity
import app.surgo.data.resultentities.SearchResults
import app.surgo.shared.plugin.DataSourceManager
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
        get() = sourceManager.key

    private val searchDataSource: SearchDataSource
        get() = sourceManager.factory.searchDataSource()

    suspend fun search(query: String, types: Array<SearchType>): SearchResults {
        val searchResults = searchDataSource.search(query, types).getOrNull() ?: return SearchResults()

        return transactionRunner {
            SearchResults(
                artists = searchResults.artists.orEmpty()
                    .map {
                        val artist = DataSourceToArtistEntity(it, source)
                        artistsDao.insertOrUpdate(artist)
                    }
                    .map { artistsDao.getArtistByIdOrThrow(it) },
                albums = searchResults.albums.orEmpty()
                    .map {
                        val album = DataSourceToAlbumEntity(it, source)
                        albumsDao.insertOrUpdate(album)
                    }
                    .map { albumsDao.getAlbumByIdOrThrow(it) },
                songs = searchResults.songs.orEmpty()
                    .map {
                        DataSourceToSongEntity(it, source)
                    },
                videos = searchResults.videos.orEmpty()
                    .map {
                        DataSourceToMusicVideoEntity(it, source)
                    }
            )
        }
    }

    suspend fun suggestions(): List<String> {
        return searchDataSource.getSuggestion()
            .getOrNull() ?: emptyList()
    }
}
