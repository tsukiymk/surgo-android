package app.surgo.data.daos

import androidx.room.*
import app.surgo.data.entities.PopularPlaylistEntry
import app.surgo.data.resultentities.PopularEntryWithPlaylist
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PopularPlaylistsDao {
    @Transaction
    @Query("SELECT * FROM popular_playlist_entries ORDER BY id ASC LIMIT :count OFFSET :offset")
    abstract fun entriesObservable(count: Int, offset: Int): Flow<List<PopularEntryWithPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<PopularPlaylistEntry>)

    @Query("DELETE FROM popular_playlist_entries")
    abstract suspend fun deleteAll()
}
