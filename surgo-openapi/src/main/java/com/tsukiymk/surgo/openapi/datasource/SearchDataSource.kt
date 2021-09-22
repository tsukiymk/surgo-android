package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType

interface SearchDataSource {
    suspend fun catalog(
        storefront: String,
        query: String,
        local: String? = null,
        types: Array<SearchType>
    ): Result<Catalog>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun search(
        query: String,
        types: Array<SearchType>
    ): Result<SearchResults>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getSuggestion(): Result<List<String>>
}
