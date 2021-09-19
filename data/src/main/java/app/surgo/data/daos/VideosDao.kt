package app.surgo.data.daos

import androidx.room.*
import app.surgo.data.entities.VideoEntity

@Dao
abstract class VideosDao : EntityDao<VideoEntity>() {
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
