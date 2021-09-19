package app.surgo.core.plugin.extensions

import app.surgo.data.repositories.albums.LocalAlbumsDataSource
import app.surgo.data.repositories.artists.LocalArtistsDataSource
import app.surgo.data.repositories.categories.LocalCategoriesDataSource
import app.surgo.data.repositories.playlists.LocalPlaylistsDataSource
import app.surgo.data.repositories.search.LocalSearchDataSource
import app.surgo.data.repositories.songs.LocalSongsDataSource
import com.tsukiymk.surgo.openapi.DataSourceFactory
import com.tsukiymk.surgo.openapi.datasource.*

class InternalDataSourceFactory : DataSourceFactory {
    override val name: String
        get() = "local"

    override fun albumsDataSource(): AlbumsDataSource {
        return LocalAlbumsDataSource()
    }

    override fun artistsDataSource(): ArtistsDataSource {
        return LocalArtistsDataSource()
    }

    override fun categoriesDataSource(): CategoriesDataSource {
        return LocalCategoriesDataSource()
    }

    override fun playlistsDataSource(): PlaylistsDataSource {
        return LocalPlaylistsDataSource()
    }

    override fun searchDataSource(): SearchDataSource {
        return LocalSearchDataSource()
    }

    override fun songsDataSource(): SongsDataSource {
        return LocalSongsDataSource()
    }
}
