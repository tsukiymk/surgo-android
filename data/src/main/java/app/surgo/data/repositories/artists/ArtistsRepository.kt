package app.surgo.data.repositories.artists

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistsRepository @Inject constructor(
    private val artistsStore: ArtistsStore
) {
    suspend fun fetchCatalog(
        id: Long,
        forceRefresh: Boolean = true
    ) {
        artistsStore.catalog(id)
    }

    suspend fun fetchPopularSongs(
        id: Long,
        forceRefresh: Boolean = true
    ) {
    }
}
