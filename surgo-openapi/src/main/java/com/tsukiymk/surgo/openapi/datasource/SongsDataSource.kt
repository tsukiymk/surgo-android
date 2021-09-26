package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

interface SongsDataSource {
    suspend fun catalog(
        songId: Long,
        local: String? = null,
        views: Array<View>? = null
    ): Result<Resource>
}
