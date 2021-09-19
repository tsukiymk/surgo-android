package app.surgo.domain.observers

import app.surgo.data.daos.AlbumsDao
import app.surgo.data.entities.AlbumEntity
import app.surgo.domain.SubjectInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveArtistAlbums @Inject constructor(
    private val albumsDao: AlbumsDao
) : SubjectInteractor<ObserveArtistAlbums.Parameters, List<AlbumEntity>>() {
    override fun createObservable(params: Parameters): Flow<List<AlbumEntity>> {
        return albumsDao.getObservableAlbumByArtistId(params.artistId)
    }

    data class Parameters(
        val artistId: Long
    )
}
