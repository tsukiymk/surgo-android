package app.surgo.data.repositories.categories

import app.surgo.data.entities.CategoryEntity
import app.surgo.data.mappers.DataSourceToCategoryEntity
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.CategoriesDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesStore @Inject constructor(
    private val sourceManager: DataSourceManager
) {
    private val source
        get() = sourceManager.key

    private val categoriesDataSource: CategoriesDataSource
        get() = sourceManager.factory.categoriesDataSource()

    suspend fun getCategories(): List<CategoryEntity> {
        return categoriesDataSource.getCategories()
            .getOrNull()
            ?.map { DataSourceToCategoryEntity(it, source) } ?: emptyList()
    }
}
