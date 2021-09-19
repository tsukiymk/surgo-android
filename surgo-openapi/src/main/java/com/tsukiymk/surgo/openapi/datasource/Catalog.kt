package com.tsukiymk.surgo.openapi.datasource

data class Catalog(
    val id: Long? = null,
    val type: CatalogType? = null,
    val attributes: Attributes? = null,
    val data: List<Catalog>? = null,
    val relationships: List<Catalog>? = null,
    val views: Map<ViewType, Catalog>? = null
)

data class Attributes(
    val name: String? = null,
    val artwork: String? = null,
    val albumName: String? = null,
    val artistName: String? = null,
    val genreNames: List<String>? = null,
    val url: String? = null,
    val durationInMillis: Long? = null
)
