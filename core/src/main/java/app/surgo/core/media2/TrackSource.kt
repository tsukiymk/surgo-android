package app.surgo.core.media2

import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.media2.common.MediaMetadata
import app.surgo.data.repositories.tracks.TracksStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrackSource @Inject constructor(
    private val tracksStore: TracksStore
) : MediaSource {
    private var playlistItems: List<MediaMetadataCompat> = emptyList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZING || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    init {
        state = State.STATE_INITIALIZING
    }

    override suspend fun load(whenReady: () -> Unit) {
        withContext(Dispatchers.IO) {
            val tracks = tracksStore.getTrackDetails()

            playlistItems = tracks.mapIndexed { index, trackDetail ->
                val song = trackDetail.songWithArtists.song
                val artists = trackDetail.songWithArtists.artists.joinToString(" & ") { it.name }

                MediaMetadataCompat.Builder()
                    .apply {
                        // The duration from the JSON is given in seconds, but the rest of the code works in
                        // milliseconds. Here's where we convert to the proper units.
                        val durationMs = TimeUnit.SECONDS.toMillis(song.duration ?: 0)

                        mediaId = "${song.id}"
                        title = song.name
                        artist = artists
                        duration = durationMs
                        genre = song.genre
                        mediaUri = song.songUri
                        albumArtUri = song.imageUri
                        trackNumber = (index + 1).toLong()
                        trackCount = tracks.size.toLong()

                        // To make things easier for *displaying* these, set the display properties as well.
                        displayTitle = song.name
                        displaySubtitle = artists
                        displayIconUri = song.imageUri

                        // Add downloadStatus to force the creation of an "extras" bundle in the resulting
                        // MediaMetadataCompat object. This is needed to send accurate metadata to the
                        // media session during updates.
                        downloadStatus = MediaMetadata.STATUS_NOT_DOWNLOADED
                    }
                    .build()
            }
            state = State.STATE_INITIALIZED
        }

        whenReady()
    }

    override fun whenReady(performAction: (Boolean) -> Unit): Boolean {
        return when (state) {
            State.STATE_CREATED, State.STATE_INITIALIZING -> {
                onReadyListeners += performAction

                false
            }
            else -> {
                performAction(state != State.STATE_ERROR)

                true
            }
        }
    }

    override fun search(query: String, extras: Bundle): List<MediaMetadataCompat> {
        return when (extras[MediaStore.EXTRA_MEDIA_FOCUS]) {
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                val genre = extras[MediaStore.EXTRA_MEDIA_GENRE]
                Log.d(TAG, "Focused genre search: '$genre'")

                filter { song ->
                    song.genre == genre
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused artist search: '$artist'")

                filter { song ->
                    (song.artist == artist || song.albumArtist == artist)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                Log.d(TAG, "Focused album search: album='$album' artist='$artist")

                filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album
                }
            }
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                val title = extras[MediaStore.EXTRA_MEDIA_TITLE]
                val album = extras[MediaStore.EXTRA_MEDIA_ALBUM]
                val artist = extras[MediaStore.EXTRA_MEDIA_ARTIST]
                Log.d(TAG, "Focused media search: title='$title' album='$album' artist='$artist")

                filter { song ->
                    (song.artist == artist || song.albumArtist == artist) && song.album == album && song.title == title
                }
            }
            else -> {
                emptyList()
            }
        }
    }

    override fun iterator(): Iterator<MediaMetadataCompat> = playlistItems.iterator()

    enum class State {
        STATE_CREATED,
        STATE_INITIALIZING,
        STATE_INITIALIZED,
        STATE_ERROR
    }

    companion object {
        private val TAG = TrackSource::class.java.simpleName
    }
}
