package app.surgo.data.repositories.recommendations

import app.surgo.data.DatabaseTransactionRunner
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.RecommendationsDataSource
import javax.inject.Inject

class RecommendationsStore @Inject constructor(
    private val sourceManager: DataSourceManager,
    private val transactionRunner: DatabaseTransactionRunner
) {
    private val source: Long
        get() = sourceManager.key

    private val recommendationsDataSource: RecommendationsDataSource
        get() = sourceManager.factory.recommendationsDataSource()

    suspend operator fun invoke(): List<Any> {
        val catalog = recommendationsDataSource.catalog()
            .getOrNull() ?: return emptyList()

        return catalog.data.orEmpty()
    }
}
