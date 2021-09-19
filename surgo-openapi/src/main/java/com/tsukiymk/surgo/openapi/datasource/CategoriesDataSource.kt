package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Category

interface CategoriesDataSource {
    suspend fun getCategories(): Result<List<Category>>
}
