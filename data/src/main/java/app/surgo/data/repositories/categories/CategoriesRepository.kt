package app.surgo.data.repositories.categories

import app.surgo.data.entities.CategoryEntity
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoriesStore: CategoriesStore
) {
    suspend fun getCategories(): List<CategoryEntity> {
        return categoriesStore.getCategories()
    }
}
