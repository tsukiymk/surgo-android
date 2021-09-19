package app.surgo.data.mappers

import app.surgo.data.entities.CategoryEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Category

object DataSourceToCategoryEntity {
    operator fun invoke(from: Category, source: Long): CategoryEntity {
        return CategoryEntity(
            source = source,
            originId = from.categoryId,
            name = from.name,
            imageUri = from.imageUrl
        )
    }
}
