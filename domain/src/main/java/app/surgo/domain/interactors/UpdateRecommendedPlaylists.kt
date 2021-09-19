package app.surgo.domain.interactors

import app.surgo.data.repositories.playlists.PlaylistsRepository
import app.surgo.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateRecommendedPlaylists @Inject constructor(
    private val repository: PlaylistsRepository
) : Interactor<UpdateRecommendedPlaylists.Parameters>() {
    override suspend fun doWork(params: Parameters) {
        withContext(Dispatchers.IO) {
            repository.fetchRecommended()
        }
    }

    data class Parameters(
        val forceRefresh: Boolean = false
    )
}
