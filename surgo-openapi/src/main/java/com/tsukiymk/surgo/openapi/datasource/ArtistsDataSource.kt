package com.tsukiymk.surgo.openapi.datasource

interface ArtistsDataSource {
    suspend fun catalog(
        artistId: Long,
        storefront: String,
        local: String? = null,
        views: Array<ViewType>? = null
    ): Result<Catalog>
}
