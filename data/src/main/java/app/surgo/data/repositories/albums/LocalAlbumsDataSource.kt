package app.surgo.data.repositories.albums

import com.tsukiymk.surgo.openapi.datasource.AlbumsDataSource
import com.tsukiymk.surgo.openapi.datasource.Catalog
import com.tsukiymk.surgo.openapi.datasource.ViewType
import com.tsukiymk.surgo.openapi.datasource.entities.Album

class LocalAlbumsDataSource : AlbumsDataSource {
    override suspend fun catalog(
        albumId: Long,
        storefront: String,
        local: String?,
        views: Array<ViewType>?
    ): Result<Catalog> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getAlbum(albumId: Long): Result<Album> {
        return Result.failure(NotImplementedError())
    }
}
