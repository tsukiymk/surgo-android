package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Album

interface AlbumsDataSource {
    suspend fun getAlbum(albumId: Long): Result<Album>
}
