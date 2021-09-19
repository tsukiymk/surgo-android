package app.surgo.domain.interactors

import app.surgo.data.entities.CategoryEntity
import app.surgo.data.repositories.categories.CategoriesRepository
import app.surgo.domain.SuspendingWorkInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCategories @Inject constructor(
    private val repository: CategoriesRepository
) : SuspendingWorkInteractor<Unit, List<CategoryEntity>>() {
    override suspend fun doWork(params: Unit): List<CategoryEntity> {
        return withContext(Dispatchers.IO) {
            repository.getCategories()
        }
    }
}
