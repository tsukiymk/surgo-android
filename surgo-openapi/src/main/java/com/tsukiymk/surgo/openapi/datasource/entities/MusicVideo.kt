package com.tsukiymk.surgo.openapi.datasource.entities

data class MusicVideo(
    val videoId: Long,
    val name: String,
    val imageUrl: String? = null
)
