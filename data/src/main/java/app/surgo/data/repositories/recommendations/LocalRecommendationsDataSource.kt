package app.surgo.data.repositories.recommendations

import com.tsukiymk.surgo.openapi.datasource.RecommendationsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

class LocalRecommendationsDataSource : RecommendationsDataSource {
    override suspend fun recommendations(
        local: String?,
        limit: Int?,
        offset: Int?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
