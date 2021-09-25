package app.surgo.data.repositories.recommendations

import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.ArtistsDataSource
import com.tsukiymk.surgo.openapi.datasource.RecommendationsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import javax.inject.Inject

class RecommendationsStore @Inject constructor(
    private val sourceManager: DataSourceManager
) {
    private val source: Long
        get() = sourceManager.key

    private val recommendationsDataSource: RecommendationsDataSource
        get() = sourceManager.factory.recommendationsDataSource()

    suspend operator fun invoke(): Resource {
        return recommendationsDataSource.recommendations()
            .getOrNull() ?: Resource()
    }
}
