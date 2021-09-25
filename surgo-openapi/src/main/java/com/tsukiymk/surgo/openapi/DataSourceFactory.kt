package com.tsukiymk.surgo.openapi

import com.tsukiymk.surgo.openapi.datasource.*

interface DataSourceFactory {
    val name: String

    // Albums, Artists, Songs, and Videos
    fun albumsDataSource(): AlbumsDataSource

    fun artistsDataSource(): ArtistsDataSource

    fun songsDataSource(): SongsDataSource

    // Playlists
    fun playlistsDataSource(): PlaylistsDataSource

    // Search
    fun searchDataSource(): SearchDataSource

    // Recommendations and Categories
    fun recommendationsDataSource(): RecommendationsDataSource

    fun categoriesDataSource(): CategoriesDataSource
}
