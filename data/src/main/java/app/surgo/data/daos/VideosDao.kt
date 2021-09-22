package app.surgo.data.daos

import androidx.room.*
import app.surgo.data.entities.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class VideosDao : EntityDao<VideoEntity>() {
    @Query(
        """
        SELECT * FROM music_videos AS A
        INNER JOIN video_artist_entries AS B ON B.video_id = A.id
        WHERE artist_id = :artistId
        """
    )
    abstract fun getObservableVideoByArtistId(artistId: Long): Flow<List<VideoEntity>>

    @Query("SELECT * FROM music_videos WHERE source = :source AND origin_id = :originId")
    abstract suspend fun getVideoBySourceAndOriginId(source: Long, originId: Long): VideoEntity?

    suspend fun insertOrUpdate(entity: VideoEntity): Long {
        val video = getVideoBySourceAndOriginId(entity.source, entity.originId)

        return if (video != null) {
            update(entity)
            video.id
        } else {
            insert(entity)
        }
    }

    suspend fun insertOrUpdate(entities: List<VideoEntity>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
