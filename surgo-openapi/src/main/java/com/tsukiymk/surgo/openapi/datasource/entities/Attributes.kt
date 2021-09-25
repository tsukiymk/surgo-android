package com.tsukiymk.surgo.openapi.datasource.entities

data class Attributes(
    val display: Display? = null,
    val title: Title? = null,
    val name: String? = null,
    val artwork: Artwork? = null,
    val albumName: String? = null,
    val artistName: String? = null,
    val genreNames: List<String>? = null,
    val url: String? = null,
    val durationInMillis: Long? = null
)
