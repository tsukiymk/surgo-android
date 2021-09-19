package app.surgo.domain.interactors

import app.surgo.data.repositories.tracks.TracksRepository
import app.surgo.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddPlayTracks @Inject constructor(
    private val repository: TracksRepository,
) : Interactor<AddPlayTracks.Parameters>() {
    override suspend fun doWork(params: Parameters) {
        withContext(Dispatchers.IO) {
            repository.addTracksFromPlaylist(params.playlistId)
        }
    }

    data class Parameters(
        val playlistId: Long
    )
}
