package app.surgo.ui.playback

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.Immutable
import app.surgo.core.media2.PlaybackConnection
import app.surgo.core.media2.PlaybackProgressState

@Immutable
internal data class PlaybackViewState(
    val playbackState: PlaybackStateCompat = PlaybackConnection.EMPTY_PLAYBACK_STATE,
    val nowPlaying: MediaMetadataCompat = PlaybackConnection.NOTHING_PLAYING,
    val progressState: PlaybackProgressState = PlaybackProgressState()
)
