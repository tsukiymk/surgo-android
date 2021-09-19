package app.surgo.data.repositories.tracks

import app.surgo.data.daos.PlaylistSongsDao
import app.surgo.data.daos.TracksDao
import app.surgo.data.entities.TrackEntity
import app.surgo.data.resultentities.TrackWithSongAndArtists
import javax.inject.Inject

class TracksStore @Inject constructor(
    private val playlistSongsDao: PlaylistSongsDao,
    private val tracksDao: TracksDao
) {
    suspend fun addTracksFromPlaylist(playlistId: Long) {
        val tracks = playlistSongsDao.entriesInternal(playlistId)
            .mapIndexed { index, track ->
                TrackEntity(
                    id = (index + 1).toLong(),
                    songId = track.entry.songId
                )
            }

        tracksDao.deleteAll()
        tracksDao.insertAll(tracks)
    }

    fun getTrackDetails(): List<TrackWithSongAndArtists> {
        return tracksDao.getTrackWithSongs()
    }
}
