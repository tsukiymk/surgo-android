package app.surgo.core.media2

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackConnection(
    context: Context,
    serviceComponent: ComponentName
) {
    private val coroutineJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val playbackState = MutableStateFlow(EMPTY_PLAYBACK_STATE)
    val currentPlay = MutableStateFlow(NOTHING_PLAYING)

    val transportControls: MediaControllerCompat.TransportControls?
        get() = mediaController?.transportControls

    private var playbackProgressInterval: Job = Job()
    val progressState = MutableStateFlow(PlaybackProgressState())

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }
    private var mediaController: MediaControllerCompat? = null

    init {
        coroutineScope.launch {
            combine(playbackState, currentPlay, ::Pair).collect { (state, current) ->
                playbackProgressInterval.cancel()

                val progress = PlaybackProgressState(
                    total = current.duration,
                    position = state.position
                )

                if (state.isPlaying) {
                    startPlaybackProgressInterval(progress)
                }
            }
        }
    }

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    private fun startPlaybackProgressInterval(initialState: PlaybackProgressState) {
        val delayMillis = TimeUnit.MILLISECONDS.toMillis(1000L)

        playbackProgressInterval = coroutineScope.launch {
            channelFlow {
                var tick = 0
                send(tick)
                while (true) {
                    delay(delayMillis)
                    send(++tick)
                }
            }.collect { ticks ->
                val elapsed = 1000L * (ticks + 1)
                progressState.value = initialState.copy(elapsed = elapsed)
            }
        }
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            this@PlaybackConnection.playbackState.tryEmit(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            currentPlay.tryEmit(
                if (metadata?.mediaId == null) {
                    NOTHING_PLAYING
                } else {
                    metadata
                }
            )
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>) {
            super.onQueueChanged(queue)
        }
    }

    companion object {
        val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build()

        val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build()
    }
}
