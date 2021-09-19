package app.surgo.data.resultentities

import app.surgo.data.entities.*

data class SearchResults(
    val artists: List<ArtistEntity> = emptyList(),
    val albums: List<AlbumEntity> = emptyList(),
    val playlists: List<PlaylistEntity> = emptyList(),
    val songs: List<SongEntity> = emptyList(),
    val videos: List<VideoEntity> = emptyList()
) {
    fun isEmpty() = artists.isEmpty() &&
        albums.isEmpty() &&
        playlists.isEmpty() &&
        songs.isEmpty() &&
        videos.isEmpty()
}
