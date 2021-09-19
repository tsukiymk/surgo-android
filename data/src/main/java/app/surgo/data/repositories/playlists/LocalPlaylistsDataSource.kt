package app.surgo.data.repositories.playlists

import com.tsukiymk.surgo.openapi.datasource.PlaylistsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Playlist

class LocalPlaylistsDataSource : PlaylistsDataSource {
    override suspend fun getPlaylist(originId: Long): Result<Playlist> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getPopularPlaylists(pageSize: Int): Result<List<Playlist>> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getRecommendedPlaylists(pageSize: Int): Result<List<Playlist>> {
        return Result.failure(NotImplementedError())
    }
}
