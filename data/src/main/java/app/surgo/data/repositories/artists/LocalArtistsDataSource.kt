package app.surgo.data.repositories.artists

import com.tsukiymk.surgo.openapi.datasource.ArtistsDataSource
import com.tsukiymk.surgo.openapi.datasource.Catalog
import com.tsukiymk.surgo.openapi.datasource.ViewType

class LocalArtistsDataSource : ArtistsDataSource {
    override suspend fun catalog(
        artistId: Long,
        storefront: String,
        local: String?,
        views: Array<ViewType>?
    ): Result<Catalog> {
        return Result.failure(NotImplementedError())
    }
}
