package app.surgo.domain.interactors

import app.surgo.data.repositories.categories.CategoriesStore
import app.surgo.domain.SuspendingWorkInteractor
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCategories @Inject constructor(
    private val store: CategoriesStore
) : SuspendingWorkInteractor<Unit, Resource>() {
    override suspend fun doWork(params: Unit): Resource {
        return withContext(Dispatchers.IO) {
            store()
        }
    }
}
