package com.tsukiymk.surgo.openapi.datasource.entities

data class Artist(
    val artistId: Long,
    val name: String,
    val imageUrl: String? = null
)
