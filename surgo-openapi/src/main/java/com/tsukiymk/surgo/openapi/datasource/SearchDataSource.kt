package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType

interface SearchDataSource {
    suspend fun search(
        query: String,
        types: Array<SearchType>
    ): Result<SearchResults>

    suspend fun getSuggestion(): Result<List<String>>
}
