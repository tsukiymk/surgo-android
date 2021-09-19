package app.surgo.domain.observers

import app.surgo.data.daos.PopularSongsDao
import app.surgo.data.resultentities.PopularEntryWithSong
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePopularSongs @Inject constructor(
    private val popularSongsDao: PopularSongsDao
) : SubjectInteractor<ObservePopularSongs.Parameters, List<PopularEntryWithSong>>() {
    override fun createObservable(params: Parameters): Flow<List<PopularEntryWithSong>> {
        return popularSongsDao.entriesObservable(params.artistId)
    }

    data class Parameters(
        val artistId: Long
    )
}
