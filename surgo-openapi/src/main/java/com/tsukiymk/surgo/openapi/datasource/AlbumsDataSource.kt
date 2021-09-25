package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

interface AlbumsDataSource {
    suspend fun catalog(
        albumId: Long,
        local: String? = null,
        views: Array<View>? = null
    ): Result<Resource>

    // Reserved
    suspend fun library(
        albumId: Long,
        local: String? = null
    ): Result<Resource>
}
