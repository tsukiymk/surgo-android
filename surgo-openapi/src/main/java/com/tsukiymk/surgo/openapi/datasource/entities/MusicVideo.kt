package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class MusicVideo(
    val videoId: Long,
    val name: String,
    val imageUrl: String? = null
)
