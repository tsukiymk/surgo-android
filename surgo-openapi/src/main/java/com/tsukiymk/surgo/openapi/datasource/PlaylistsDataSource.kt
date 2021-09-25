package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

interface PlaylistsDataSource {
    suspend fun catalog(
        playlistId: Long,
        storefront: String,
        local: String? = null,
        views: Array<View>? = null
    ): Result<Resource>
}
