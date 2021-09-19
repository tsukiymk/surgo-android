package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.VideoArtistEntry

@Dao
abstract class VideoArtistsDao : EntityDao<VideoArtistEntry>() {
    @Query("DELETE FROM video_artist_entries WHERE artist_id = :artistId")
    abstract suspend fun deleteByArtistId(artistId: Long)

    suspend fun insertOrUpdate(entity: VideoArtistEntry): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<VideoArtistEntry>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
