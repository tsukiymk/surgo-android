package app.surgo.data.repositories.categories

import app.surgo.data.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.CategoriesDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesStore @Inject constructor(
    private val sourceManager: DataSourceManager
) {
    private val source
        get() = sourceManager.selectedSource

    private val categoriesDataSource: CategoriesDataSource
        get() = sourceManager[source].categoriesDataSource()

    suspend operator fun invoke(): Resource {
        return categoriesDataSource.categories()
            .getOrNull() ?: Resource()
    }
}
