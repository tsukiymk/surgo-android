package app.surgo.domain.interactors

import app.surgo.data.repositories.recommendations.RecommendationsStore
import app.surgo.domain.SuspendingWorkInteractor
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRecommendations @Inject constructor(
    private val store: RecommendationsStore
) : SuspendingWorkInteractor<Unit, Resource>() {
    override suspend fun doWork(params: Unit): Resource {
        return withContext(Dispatchers.IO) {
            store()
        }
    }
}
