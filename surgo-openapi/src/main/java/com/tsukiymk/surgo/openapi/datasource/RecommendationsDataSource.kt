package com.tsukiymk.surgo.openapi.datasource

interface RecommendationsDataSource {
    suspend fun catalog(
        local: String? = null,
        limit: Int? = null,
        offset: Int? = null
    ): Result<Catalog>
}
