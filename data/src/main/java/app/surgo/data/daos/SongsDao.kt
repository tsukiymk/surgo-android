package app.surgo.data.daos

import androidx.room.*
import app.surgo.data.entities.SongArtistEntry
import app.surgo.data.entities.SongEntity
import app.surgo.data.resultentities.SongWithArtists
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongsDao : EntityDao<SongEntity>() {
    @Query("SELECT * FROM songs WHERE id = :id")
    abstract suspend fun getSongById(id: Long): SongEntity?

    @Query("SELECT * FROM songs WHERE source = :source AND origin_id = :originId")
    abstract fun getSongBySourceAndOriginId(source: Long, originId: Long): SongEntity?

    @Transaction
    @Query("SELECT * FROM songs WHERE origin_id = :originId")
    abstract fun getObservableSongWithArtistsByOriginId(originId: Long): Flow<SongWithArtists>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRelatedArtists(relates: List<SongArtistEntry>)

    suspend fun getSongByIdOrThrow(id: Long): SongEntity {
        return getSongById(id) ?: throw IllegalArgumentException("No song with id $id in database")
    }

    suspend fun insertOrUpdate(entity: SongEntity): Long {
        val song = getSongBySourceAndOriginId(entity.source, entity.originId)

        return if (song != null) {
            update(entity)
            song.id
        } else {
            insert(entity)
        }
    }

    suspend fun insertOrUpdate(entities: List<SongEntity>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
