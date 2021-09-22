package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Song

interface SongsDataSource {
    suspend fun catalog(
        songId: Long,
        storefront: String,
        local: String? = null,
        views: Array<ViewType>? = null
    ): Result<Catalog>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getSong(songId: Long): Result<Song>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getSongs(songIds: Array<Long>): Result<List<Song>>
}
