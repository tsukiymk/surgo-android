package app.surgo.domain.observers

import app.surgo.data.daos.ArtistsDao
import app.surgo.data.entities.ArtistEntity
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveArtist @Inject constructor(
    private val artistsDao: ArtistsDao
) : SubjectInteractor<ObserveArtist.Parameters, ArtistEntity>() {
    override fun createObservable(params: Parameters): Flow<ArtistEntity> {
        return artistsDao.getObservableArtistById(params.artistId)
    }

    data class Parameters(
        val artistId: Long
    )
}
