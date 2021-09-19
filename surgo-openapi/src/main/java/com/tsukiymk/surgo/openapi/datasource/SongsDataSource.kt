package com.tsukiymk.surgo.openapi.datasource

import com.tsukiymk.surgo.openapi.datasource.entities.Song

interface SongsDataSource {
    suspend fun getSong(songId: Long): Result<Song>

    suspend fun getSongs(songIds: Array<Long>): Result<List<Song>>
}
