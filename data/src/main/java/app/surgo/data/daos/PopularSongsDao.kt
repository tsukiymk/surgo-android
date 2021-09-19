package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.PopularSongEntry
import app.surgo.data.resultentities.PopularEntryWithSong
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PopularSongsDao : EntityDao<PopularSongEntry>() {
    @Transaction
    @Query("SELECT * FROM popular_song_entries WHERE artist_id = :artistId")
    abstract fun entriesObservable(artistId: Long): Flow<List<PopularEntryWithSong>>

    @Query("DELETE FROM popular_song_entries WHERE artist_id = :artistId")
    abstract suspend fun deleteByArtistId(artistId: Long)

    suspend fun insertOrUpdate(entity: PopularSongEntry): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<PopularSongEntry>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
