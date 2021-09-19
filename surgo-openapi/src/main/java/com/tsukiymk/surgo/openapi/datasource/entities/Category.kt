package com.tsukiymk.surgo.openapi.datasource.entities

data class Category(
    val categoryId: Long,
    val name: String,
    val imageUrl: String? = null
)
