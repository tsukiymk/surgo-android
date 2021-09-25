package app.surgo.data.repositories.playlists

import com.tsukiymk.surgo.openapi.datasource.PlaylistsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

class LocalPlaylistsDataSource : PlaylistsDataSource {
    override suspend fun catalog(
        playlistId: Long,
        storefront: String,
        local: String?,
        views: Array<View>?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
