package app.surgo.domain.observers

import app.surgo.data.daos.VideosDao
import app.surgo.data.entities.VideoEntity
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveArtistVideos @Inject constructor(
    private val videosDao: VideosDao
) : SubjectInteractor<ObserveArtistVideos.Parameters, List<VideoEntity>>() {
    override fun createObservable(params: Parameters): Flow<List<VideoEntity>> {
        return videosDao.getObservableVideoByArtistId(params.artistId)
    }

    data class Parameters(
        val artistId: Long
    )
}
