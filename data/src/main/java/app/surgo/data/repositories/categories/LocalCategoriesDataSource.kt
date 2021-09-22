package app.surgo.data.repositories.categories

import com.tsukiymk.surgo.openapi.datasource.Catalog
import com.tsukiymk.surgo.openapi.datasource.CategoriesDataSource
import com.tsukiymk.surgo.openapi.datasource.ViewType
import com.tsukiymk.surgo.openapi.datasource.entities.Category

class LocalCategoriesDataSource : CategoriesDataSource {
    override suspend fun catalog(
        storefront: String,
        local: String?
    ): Result<Catalog> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return Result.failure(NotImplementedError())
    }
}
