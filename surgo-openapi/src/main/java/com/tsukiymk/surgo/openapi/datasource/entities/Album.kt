package com.tsukiymk.surgo.openapi.datasource.entities

data class Album(
    val albumId: Long,
    val name: String,
    val imageUrl: String? = null,
    val songs: List<Song>? = null
)
