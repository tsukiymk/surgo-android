package app.surgo.domain.observers

import app.surgo.data.daos.PlaylistSongsDao
import app.surgo.data.resultentities.PlaylistEntryWithSong
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlaylistSongs @Inject constructor(
    private val playlistSongsDao: PlaylistSongsDao
) : SubjectInteractor<ObservePlaylistSongs.Parameters, List<PlaylistEntryWithSong>>() {
    override fun createObservable(params: Parameters): Flow<List<PlaylistEntryWithSong>> {
        return playlistSongsDao.entriesObservable(params.playlistId)
    }

    data class Parameters(
        val playlistId: Long
    )
}
