package app.surgo.data.repositories.songs

import com.tsukiymk.surgo.openapi.datasource.SongsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.entities.Song
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

class LocalSongsDataSource : SongsDataSource {
    override suspend fun catalog(
        songId: Long,
        local: String?,
        views: Array<View>?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
