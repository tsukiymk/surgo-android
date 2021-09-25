package app.surgo.data.repositories.search

import com.tsukiymk.surgo.openapi.datasource.SearchDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.entities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType

class LocalSearchDataSource : SearchDataSource {
    override suspend fun catalog(
        query: String,
        local: String?,
        types: Array<SearchType>
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun search(query: String, types: Array<SearchType>): Result<SearchResults> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getSuggestion(): Result<List<String>> {
        return Result.failure(NotImplementedError())
    }
}
