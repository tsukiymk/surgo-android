package app.surgo.data

import app.surgo.data.daos.*

interface DatabaseDaos {
    fun lastRequestsDao(): LastRequestsDao

    fun tracksDao(): TracksDao

    fun artistsDao(): ArtistsDao

    fun songsDao(): SongsDao

    fun songArtistsDao(): SongArtistsDao

    fun albumsDao(): AlbumsDao

    fun albumArtistsDao(): AlbumArtistsDao

    fun videosDao(): VideosDao

    fun videoArtistsDao(): VideoArtistsDao

    fun playlistsDao(): PlaylistsDao

    fun playlistSongsDao(): PlaylistSongsDao

    fun recommendedDao(): RecommendedPlaylistsDao

    fun popularSongsDao(): PopularSongsDao

    fun popularPlaylistsDao(): PopularPlaylistsDao
}
