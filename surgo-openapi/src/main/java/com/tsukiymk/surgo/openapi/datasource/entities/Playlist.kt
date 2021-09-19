package com.tsukiymk.surgo.openapi.datasource.entities

data class Playlist(
    val playlistId: Long,
    val name: String,
    val imageUrl: String? = null,
    val tracks: List<Song>? = null
)
