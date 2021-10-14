package com.tsukiymk.surgo.openapi.datasource.entities

data class Results(
    val album: Resource? = null,
    val artist: Resource? = null,
    val video: Resource? = null,
    val playlist: Resource? = null,
    val song: Resource? = null,
    val top: Resource? = null
)
