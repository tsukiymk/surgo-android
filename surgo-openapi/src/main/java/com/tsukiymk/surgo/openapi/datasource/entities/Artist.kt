package com.tsukiymk.surgo.openapi.datasource.entities

@Deprecated("Use a {@link Catalog} instead.")
data class Artist(
    val artistId: Long,
    val name: String,
    val imageUrl: String? = null
)
