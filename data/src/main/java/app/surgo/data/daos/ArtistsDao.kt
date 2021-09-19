package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import app.surgo.data.entities.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistsDao : EntityDao<ArtistEntity>() {
    @Query("SELECT * FROM artists WHERE id = :id")
    abstract fun getObservableArtistById(id: Long): Flow<ArtistEntity>

    @Query("SELECT * FROM artists WHERE id = :id")
    abstract suspend fun getArtistById(id: Long): ArtistEntity?

    @Query("SELECT * FROM artists WHERE source = :source AND origin_id = :originId")
    abstract suspend fun getArtistBySourceAndOriginId(source: Long, originId: Long): ArtistEntity?

    suspend fun getArtistByIdOrThrow(id: Long): ArtistEntity {
        return getArtistById(id) ?: throw IllegalArgumentException("No artist with id $id in database")
    }

    suspend fun insertOrUpdate(entity: ArtistEntity): Long {
        val artist = getArtistBySourceAndOriginId(entity.source, entity.originId)

        return if (artist != null) {
            update(entity)
            artist.id
        } else {
            insert(entity)
        }
    }

    suspend fun insertOrUpdate(entities: List<ArtistEntity>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
