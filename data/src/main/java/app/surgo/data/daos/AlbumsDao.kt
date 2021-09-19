package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.AlbumEntity
import app.surgo.data.resultentities.AlbumWithSongAndArtists
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumsDao : EntityDao<AlbumEntity>() {
    @Query(
        """
        SELECT * FROM albums AS A
        INNER JOIN album_artist_entries AS B ON B.album_id = A.id
        WHERE artist_id = :artistId
        """
    )
    abstract fun getObservableAlbumByArtistId(artistId: Long): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :id")
    abstract fun getAlbumByIdForStore(id: Long): Flow<AlbumEntity>

    @Query("SELECT * FROM albums WHERE id = :id")
    abstract suspend fun getAlbumById(id: Long): AlbumEntity?

    @Query("SELECT * FROM albums WHERE source = :source AND origin_id = :originId")
    abstract suspend fun getAlbumBySourceAndOriginId(source: Long, originId: Long): AlbumEntity?

    @Transaction
    @Query("SELECT * FROM albums WHERE id = :id")
    abstract suspend fun getAlbumWithSongAndArtistsById(id: Long): AlbumWithSongAndArtists

    @Query("DELETE FROM albums WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM albums")
    abstract suspend fun deleteAll()

    suspend fun getAlbumByIdOrThrow(id: Long): AlbumEntity {
        return getAlbumById(id) ?: throw IllegalArgumentException("No album with id $id in database")
    }

    suspend fun insertOrUpdate(entity: AlbumEntity): Long {
        val album = getAlbumBySourceAndOriginId(entity.source, entity.originId)

        return if (album != null) {
            update(entity)
            album.id
        } else {
            insert(entity)
        }
    }

    suspend fun insertOrUpdate(entities: List<AlbumEntity>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
