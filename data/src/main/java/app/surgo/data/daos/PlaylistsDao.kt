package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import app.surgo.data.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistsDao : EntityDao<PlaylistEntity>() {
    @Query("SELECT * FROM playlists WHERE id = :id")
    abstract fun getObservablePlaylistById(id: Long): Flow<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE id = :id")
    abstract suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE source = :source AND origin_id = :originId")
    abstract suspend fun getPlaylistBySourceAndOriginId(source: Long, originId: Long): PlaylistEntity?

    @Query("DELETE FROM playlists")
    abstract suspend fun deleteAll()

    suspend fun getPlaylistByIdOrThrow(id: Long): PlaylistEntity {
        return getPlaylistById(id) ?: throw IllegalArgumentException("No playlist with id $id in database")
    }

    suspend fun insertOrUpdate(entity: PlaylistEntity): Long {
        val playlist = getPlaylistBySourceAndOriginId(entity.source, entity.originId)

        return if (playlist != null) {
            update(entity)
            playlist.id
        } else {
            insert(entity)
        }
    }

    suspend fun insertOrUpdate(entities: List<PlaylistEntity>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
