package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class Album(
    val albumId: Long,
    val name: String,
    val imageUrl: String? = null,
    val songs: List<Song>? = null
)
