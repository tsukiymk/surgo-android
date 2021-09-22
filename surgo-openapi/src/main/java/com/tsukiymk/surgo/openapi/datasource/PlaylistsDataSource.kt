package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Playlist

interface PlaylistsDataSource {
    suspend fun catalog(
        playlistId: Long,
        storefront: String,
        local: String? = null,
        views: Array<ViewType>? = null
    ): Result<Catalog>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getPopularPlaylists(
        pageSize: Int
    ): Result<List<Playlist>>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getRecommendedPlaylists(
        pageSize: Int
    ): Result<List<Playlist>>
}
