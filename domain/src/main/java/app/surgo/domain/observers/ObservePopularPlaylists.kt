package app.surgo.domain.observers

import app.surgo.data.daos.PopularPlaylistsDao
import app.surgo.data.resultentities.PopularEntryWithPlaylist
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePopularPlaylists @Inject constructor(
    private val popularPlaylistsDao: PopularPlaylistsDao
) : SubjectInteractor<ObservePopularPlaylists.Parameters, List<PopularEntryWithPlaylist>>() {
    override fun createObservable(params: Parameters): Flow<List<PopularEntryWithPlaylist>> {
        return popularPlaylistsDao.entriesObservable(params.count, 0)
    }

    data class Parameters(
        val count: Int = 10
    )
}
