package app.surgo.data.repositories.tracks

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TracksRepository @Inject constructor(
    private val tracksStore: TracksStore
) {
    suspend fun addTracksFromPlaylist(playlistId: Long) {
        tracksStore.addTracksFromPlaylist(playlistId)
    }
}
