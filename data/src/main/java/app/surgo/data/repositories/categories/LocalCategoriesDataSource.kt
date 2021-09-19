package app.surgo.data.repositories.categories

import com.tsukiymk.surgo.openapi.datasource.CategoriesDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Category

class LocalCategoriesDataSource : CategoriesDataSource {
    override suspend fun getCategories(): Result<List<Category>> {
        return Result.failure(NotImplementedError())
    }
}
