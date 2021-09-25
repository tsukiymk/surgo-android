package app.surgo.data.repositories.artists

import com.tsukiymk.surgo.openapi.datasource.ArtistsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Resource
import com.tsukiymk.surgo.openapi.datasource.enumerations.View

class LocalArtistsDataSource : ArtistsDataSource {
    override suspend fun catalog(
        artistId: Long,
        local: String?,
        views: Array<View>?
    ): Result<Resource> {
        return Result.failure(NotImplementedError())
    }
}
