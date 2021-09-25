package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource

interface CategoriesDataSource {
    suspend fun categories(
        local: String? = null
    ): Result<Resource>
}
