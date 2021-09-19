package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.AlbumArtistEntry

@Dao
abstract class AlbumArtistsDao : EntityDao<AlbumArtistEntry>() {
    @Query("DELETE FROM album_artist_entries WHERE artist_id = :artistId")
    abstract suspend fun deleteByArtistId(artistId: Long)

    suspend fun insertOrUpdate(entity: AlbumArtistEntry): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<AlbumArtistEntry>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
