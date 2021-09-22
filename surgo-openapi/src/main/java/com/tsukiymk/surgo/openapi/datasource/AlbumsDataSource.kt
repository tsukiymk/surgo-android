package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Album

interface AlbumsDataSource {
    suspend fun catalog(
        albumId: Long,
        storefront: String,
        local: String? = null,
        views: Array<ViewType>? = null
    ): Result<Catalog>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getAlbum(albumId: Long): Result<Album>
}
