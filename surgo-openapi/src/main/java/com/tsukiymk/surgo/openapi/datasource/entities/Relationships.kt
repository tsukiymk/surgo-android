package com.tsukiymk.surgo.openapi.datasource.entities

data class Relationships(
    val contents: Resource? = null,
    val albums: Resource? = null,
    val artists: Resource? = null,
    val tracks: Resource? = null
)
