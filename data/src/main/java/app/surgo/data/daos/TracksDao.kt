package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.surgo.data.entities.TrackEntity
import app.surgo.data.resultentities.TrackWithSongAndArtists
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TracksDao : EntityDao<TrackEntity>() {
    @Query("SELECT * FROM tracks WHERE song_id = :songId")
    abstract fun getTrackBySongId(songId: Long): TrackEntity?

    @Transaction
    @Query(" SELECT * FROM tracks ORDER BY id, priority ASC")
    abstract fun getTrackWithSongs(): List<TrackWithSongAndArtists>

    @Transaction
    @Query(" SELECT * FROM tracks ORDER BY id, priority ASC")
    abstract fun getObservableTrackWithSongs(): Flow<List<TrackWithSongAndArtists>>

    @Query("DELETE FROM tracks")
    abstract suspend fun deleteAll()
}
