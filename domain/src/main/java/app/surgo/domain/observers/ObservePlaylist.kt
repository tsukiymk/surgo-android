package app.surgo.domain.observers

import app.surgo.data.daos.PlaylistsDao
import app.surgo.data.entities.PlaylistEntity
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlaylist @Inject constructor(
    private val playlistsDao: PlaylistsDao
) : SubjectInteractor<ObservePlaylist.Parameters, PlaylistEntity>() {
    override fun createObservable(params: Parameters): Flow<PlaylistEntity> {
        return playlistsDao.getObservablePlaylistById(params.playlistId)
    }

    data class Parameters(
        val playlistId: Long
    )
}
