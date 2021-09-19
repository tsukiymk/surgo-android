package app.surgo.data.repositories.albums

import com.tsukiymk.surgo.openapi.datasource.AlbumsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Album

class LocalAlbumsDataSource : AlbumsDataSource {
    override suspend fun getAlbum(albumId: Long): Result<Album> {
        return Result.failure(NotImplementedError())
    }
}
