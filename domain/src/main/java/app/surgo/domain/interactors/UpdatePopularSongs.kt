package app.surgo.domain.interactors

import app.surgo.data.repositories.artists.ArtistsRepository
import app.surgo.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatePopularSongs @Inject constructor(
    private val repository: ArtistsRepository
) : Interactor<UpdatePopularSongs.Parameters>() {
    override suspend fun doWork(params: Parameters) {
        withContext(Dispatchers.IO) {
            repository.fetchPopularSongs(
                id = params.artistId,
                forceRefresh = params.forceRefresh
            )
        }
    }

    data class Parameters(
        val artistId: Long,
        val forceRefresh: Boolean = false
    )
}
