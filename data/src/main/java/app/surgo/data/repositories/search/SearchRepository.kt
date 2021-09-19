package app.surgo.data.repositories.search

import app.surgo.data.resultentities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val searchStore: SearchStore
) {
    suspend fun search(query: String, types: Array<SearchType>): SearchResults {
        if (query.isBlank()) {
            return SearchResults()
        }

        return searchStore.search(query, types)
    }
}
