package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class Playlist(
    val playlistId: Long,
    val name: String,
    val imageUrl: String? = null,
    val tracks: List<Song>? = null
)
