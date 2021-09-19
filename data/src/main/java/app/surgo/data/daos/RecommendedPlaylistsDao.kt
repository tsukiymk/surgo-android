package app.surgo.data.daos

import androidx.room.*
import app.surgo.data.entities.RecommendedPlaylistEntry
import app.surgo.data.resultentities.RecommendedEntryWithPlaylist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecommendedPlaylistsDao {
    @Transaction
    @Query("SELECT * FROM recommended_playlist_entries ORDER BY id ASC LIMIT :count OFFSET :offset")
    abstract fun entriesObservable(count: Int, offset: Int): Flow<List<RecommendedEntryWithPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<RecommendedPlaylistEntry>)

    @Query("DELETE FROM recommended_playlist_entries WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM recommended_playlist_entries")
    abstract suspend fun deleteAll()
}
