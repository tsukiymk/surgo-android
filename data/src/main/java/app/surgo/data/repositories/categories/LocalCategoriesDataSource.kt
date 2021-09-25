package app.surgo.data.repositories.categories

import com.tsukiymk.surgo.openapi.datasource.CategoriesDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

class LocalCategoriesDataSource : CategoriesDataSource {
    override suspend fun categories(
        local: String?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
