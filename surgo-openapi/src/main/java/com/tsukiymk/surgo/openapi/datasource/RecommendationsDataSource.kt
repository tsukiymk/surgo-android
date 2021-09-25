package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource

interface RecommendationsDataSource {
    suspend fun recommendations(
        local: String? = null,
        limit: Int? = null,
        offset: Int? = null
    ): Result<Resource>
}
