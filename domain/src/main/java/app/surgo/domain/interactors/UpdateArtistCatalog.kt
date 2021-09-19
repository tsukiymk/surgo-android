package app.surgo.domain.interactors

import app.surgo.data.repositories.artists.ArtistsRepository
import app.surgo.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateArtistCatalog @Inject constructor(
    private val repository: ArtistsRepository
) : Interactor<UpdateArtistCatalog.Parameters>() {
    override suspend fun doWork(params: Parameters) {
        withContext(Dispatchers.IO) {
            repository.fetchCatalog(params.artistId)
        }
    }

    data class Parameters(
        val artistId: Long,
        val forceRefresh: Boolean = false
    )
}
