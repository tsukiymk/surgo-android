package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class Song(
    val songId: Long,
    val name: String,
    val songUrl: String? = null,
    val genre: String? = null,
    val duration: Long? = null,
    val album: Album? = null,
    val artists: List<Artist>? = null
)
