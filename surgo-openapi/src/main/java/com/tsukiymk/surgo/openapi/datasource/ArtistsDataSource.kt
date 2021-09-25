package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

interface ArtistsDataSource {
    /**
     * Get a [Resource] artist.
     *
     * @param id The unique identifier for the artist.
     * @param views The views to activate for the albums resource.
     */
    suspend fun catalog(
        artistId: Long,
        local: String? = null,
        views: Array<View>? = null
    ): Result<Resource>
}
