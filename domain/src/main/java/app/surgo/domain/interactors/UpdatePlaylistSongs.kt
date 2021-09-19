package app.surgo.domain.interactors

import app.surgo.data.repositories.playlists.PlaylistsRepository
import app.surgo.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdatePlaylistSongs @Inject constructor(
    private val repository: PlaylistsRepository
) : Interactor<UpdatePlaylistSongs.Parameters>() {
    override suspend fun doWork(params: Parameters) {
        withContext(Dispatchers.IO) {
            repository.fetchPlaylistSongs(
                id = params.playlistId,
                forceRefresh = params.forceRefresh
            )
        }
    }

    data class Parameters(
        val playlistId: Long,
        val forceRefresh: Boolean = false
    )
}
