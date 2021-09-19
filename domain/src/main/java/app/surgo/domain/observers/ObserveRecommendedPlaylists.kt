package app.surgo.domain.observers

import app.surgo.data.daos.RecommendedPlaylistsDao
import app.surgo.data.resultentities.RecommendedEntryWithPlaylist
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecommendedPlaylists @Inject constructor(
    private val recommendedPlaylistsDao: RecommendedPlaylistsDao
) : SubjectInteractor<ObserveRecommendedPlaylists.Parameters, List<RecommendedEntryWithPlaylist>>() {
    override fun createObservable(params: Parameters): Flow<List<RecommendedEntryWithPlaylist>> {
        return recommendedPlaylistsDao.entriesObservable(params.count, 0)
    }

    data class Parameters(
        val count: Int = 10
    )
}
