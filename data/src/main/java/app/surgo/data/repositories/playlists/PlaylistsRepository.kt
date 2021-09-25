package app.surgo.data.repositories.playlists

import app.surgo.shared.fetch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistsRepository @Inject constructor(
    private val playlistsStore: PlaylistsStore
) {
    suspend fun fetchPlaylistSongs(
        id: Long,
        forceRefresh: Boolean = true
    ) {
        playlistsStore.catalog().fetch(id, forceRefresh)
    }
}
