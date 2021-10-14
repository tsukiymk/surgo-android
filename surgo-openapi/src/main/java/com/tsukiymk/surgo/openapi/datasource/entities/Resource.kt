package com.tsukiymk.surgo.openapi.datasource.entities

import com.tsukiymk.surgo.openapi.datasource.enumerations.Type
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

data class Resource(
    val id: Long? = null,
    val type: Type? = null,
    val attributes: Attributes? = null,
    val data: List<Resource>? = null,
    val relationships: Relationships? = null,
    val results: Results? = null,
    val views: Map<View, Resource>? = null
)
