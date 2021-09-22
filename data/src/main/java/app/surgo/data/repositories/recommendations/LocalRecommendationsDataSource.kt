package app.surgo.data.repositories.recommendations

import com.tsukiymk.surgo.openapi.datasource.Catalog
import com.tsukiymk.surgo.openapi.datasource.RecommendationsDataSource

class LocalRecommendationsDataSource : RecommendationsDataSource {
    override suspend fun catalog(local: String?, limit: Int?, offset: Int?): Result<Catalog> {
        TODO("Not yet implemented")
    }
}
