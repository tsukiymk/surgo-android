package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType

interface SearchDataSource {
    suspend fun catalog(
        query: String,
        local: String? = null,
        types: Array<SearchType>
    ): Result<Resource>
}
