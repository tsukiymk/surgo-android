package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class SearchResults(
    val artists: List<Artist>? = null,
    val albums: List<Album>? = null,
    val songs: List<Song>? = null,
    val videos: List<MusicVideo>? = null
)
