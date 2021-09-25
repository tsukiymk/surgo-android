package app.surgo.data.repositories.albums

import com.tsukiymk.surgo.openapi.datasource.AlbumsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Album
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

class LocalAlbumsDataSource : AlbumsDataSource {
    override suspend fun catalog(
        albumId: Long,
        local: String?,
        views: Array<View>?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun library(albumId: Long, local: String?): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
