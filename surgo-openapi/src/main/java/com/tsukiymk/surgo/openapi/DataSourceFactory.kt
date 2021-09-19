package com.tsukiymk.surgo.openapi

import com.tsukiymk.surgo.openapi.datasource.*

interface DataSourceFactory {
    val name: String

    fun albumsDataSource(): AlbumsDataSource

    fun artistsDataSource(): ArtistsDataSource

    fun categoriesDataSource(): CategoriesDataSource

    fun playlistsDataSource(): PlaylistsDataSource

    fun searchDataSource(): SearchDataSource

    fun songsDataSource(): SongsDataSource
}
