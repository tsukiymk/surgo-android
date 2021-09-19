package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.PlaylistSongEntry
import app.surgo.data.resultentities.PlaylistEntryWithSong
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistSongsDao : EntityDao<PlaylistSongEntry>() {
    @Transaction
    @Query("SELECT * FROM playlist_song_entries WHERE playlist_id = :playlistId")
    abstract fun entriesForStore(playlistId: Long): Flow<List<PlaylistSongEntry>>

    @Transaction
    @Query("SELECT * FROM playlist_song_entries WHERE playlist_id = :playlistId")
    abstract fun entriesInternal(playlistId: Long): List<PlaylistEntryWithSong>

    @Transaction
    @Query("SELECT * FROM playlist_song_entries WHERE playlist_id = :playlistId")
    abstract fun entriesObservable(playlistId: Long): Flow<List<PlaylistEntryWithSong>>

    @Query("DELETE FROM playlist_song_entries WHERE playlist_id = :playlistId")
    abstract suspend fun deleteByPlaylistId(playlistId: Long)

    @Query("DELETE FROM playlist_song_entries")
    abstract suspend fun deleteAll()

    suspend fun insertOrUpdate(entity: PlaylistSongEntry): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<PlaylistSongEntry>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
