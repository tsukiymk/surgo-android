package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Playlist

interface PlaylistsDataSource {
    suspend fun getPlaylist(
        playlistId: Long
    ): Result<Playlist>

    suspend fun getPopularPlaylists(
        pageSize: Int
    ): Result<List<Playlist>>

    suspend fun getRecommendedPlaylists(
        pageSize: Int
    ): Result<List<Playlist>>
}
