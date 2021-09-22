package app.surgo.data.repositories.playlists

import com.tsukiymk.surgo.openapi.datasource.Catalog
import com.tsukiymk.surgo.openapi.datasource.PlaylistsDataSource
import com.tsukiymk.surgo.openapi.datasource.ViewType
import com.tsukiymk.surgo.openapi.datasource.entities.Playlist

class LocalPlaylistsDataSource : PlaylistsDataSource {
    override suspend fun catalog(
        playlistId: Long,
        storefront: String,
        local: String?,
        views: Array<ViewType>?
    ): Result<Catalog> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getPopularPlaylists(pageSize: Int): Result<List<Playlist>> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getRecommendedPlaylists(pageSize: Int): Result<List<Playlist>> {
        return Result.failure(NotImplementedError())
    }
}
