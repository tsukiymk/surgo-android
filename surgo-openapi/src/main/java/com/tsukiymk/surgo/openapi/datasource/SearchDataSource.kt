package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.entities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType

interface SearchDataSource {
    suspend fun catalog(
        query: String,
        local: String? = null,
        types: Array<SearchType>
    ): Result<Resource>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun search(
        query: String,
        types: Array<SearchType>
    ): Result<SearchResults>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getSuggestion(): Result<List<String>>
}
