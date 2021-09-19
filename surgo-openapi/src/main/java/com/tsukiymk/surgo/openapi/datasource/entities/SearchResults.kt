package com.tsukiymk.surgo.openapi.datasource.entities

data class SearchResults(
    val artists: List<Artist>? = null,
    val albums: List<Album>? = null,
    val playlists: List<Playlist>? = null,
    val songs: List<Song>? = null,
    val videos: List<MusicVideo>? = null
)
