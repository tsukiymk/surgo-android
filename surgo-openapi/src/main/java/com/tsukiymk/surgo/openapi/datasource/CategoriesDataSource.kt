package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Category

interface CategoriesDataSource {
    suspend fun catalog(
        storefront: String,
        local: String? = null
    ): Result<Catalog>

    @Deprecated("Use a {@link Catalog} instead.")
    suspend fun getCategories(): Result<List<Category>>
}
