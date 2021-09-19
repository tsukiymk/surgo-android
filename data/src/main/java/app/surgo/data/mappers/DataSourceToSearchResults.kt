package app.surgo.data.mappers

import app.surgo.data.resultentities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.entities.SearchResults as DSSearchResults

object DataSourceToSearchResults {
    operator fun invoke(from: DSSearchResults, source: Long): SearchResults {
        return SearchResults(
            artists = from.artists.orEmpty()
                .map { DataSourceToArtistEntity(it, source) },
            albums = from.albums.orEmpty()
                .map { DataSourceToAlbumEntity(it, source) },
            playlists = from.playlists.orEmpty()
                .map { DataSourceToPlaylistEntity(it, source) },
            songs = from.songs.orEmpty()
                .map { DataSourceToSongEntity(it, source) },
            videos = from.videos.orEmpty()
                .map { DataSourceToMusicVideoEntity(it, source) }
        )
    }
}
