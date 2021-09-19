package app.surgo.core.media2

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.cast.CastPlayer
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.gms.cast.framework.CastContext
import kotlinx.coroutines.*

class MediaPlayer constructor(
    context: Context,
    private val mediaSource: MediaSource
) : ContextWrapper(context) {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    lateinit var currentPlayer: Player
        private set

    val mediaSession: MediaSessionCompat = MediaSessionCompat(this, TAG)

    var onPlayerStateChanged: (Boolean, Int) -> Unit = { _, _ -> }

    private val mediaSessionConnector = MediaSessionConnector(mediaSession)

    private val playerListener = PlayerListener()

    private val dataSourceFactory by lazy {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        DefaultDataSourceFactory(this, null, httpDataSourceFactory)
    }

    private val exoPlayer by lazy {
        SimpleExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
            .apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build()

                setAudioAttributes(audioAttributes, true)
                setHandleAudioBecomingNoisy(true)
                addListener(playerListener)
            }
    }

    private val castPlayer: CastPlayer? by lazy {
        try {
            CastPlayer(CastContext.getSharedInstance(this)).apply {
                setSessionAvailabilityListener(object : SessionAvailabilityListener {
                    override fun onCastSessionAvailable() {
                        switchToPlayer(currentPlayer, castPlayer!!)
                    }

                    override fun onCastSessionUnavailable() {
                        switchToPlayer(currentPlayer, exoPlayer)
                    }
                })
                addListener(playerListener)
            }
        } catch (error: Exception) {
            Log.i(TAG, "Cast is not available on this device.")

            null
        }
    }

    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    init {
        mediaSessionConnector.setPlaybackPreparer(MediaSessionPlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(MediaSessionQueueNavigator(mediaSession))

        switchToPlayer(
            null,
            if (castPlayer?.isCastSessionAvailable == true)
                castPlayer!!
            else
                exoPlayer
        )
    }

    fun release() {
        mediaSession.run {
            isActive = false
            release()
        }

        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) return

        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                currentPlayer.stop()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    currentPlaylistItems,
                    currentPlaylistItems[previousPlayer.currentWindowIndex],
                    previousPlayer.playWhenReady,
                    previousPlayer.currentPosition
                )
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop()
        previousPlayer?.clearMediaItems()
    }

    private fun preparePlaylist(
        playlistItems: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        val initialWindowIndex = when {
            itemToPlay == null -> 0
            else -> playlistItems.indexOf(itemToPlay)
        }
        currentPlaylistItems = playlistItems

        currentPlayer.playWhenReady = playWhenReady
        if (currentPlayer == exoPlayer) {
            val concatenatingMediaSource = ConcatenatingMediaSource()
            playlistItems.forEach {
                concatenatingMediaSource.addMediaSource(it.toMediaSource(dataSourceFactory))
            }
            exoPlayer.setMediaSource(concatenatingMediaSource)
            exoPlayer.prepare()
            exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
        } else {
            val items = playlistItems.map {
                it.toMediaItem()
            }.toMutableList()
            castPlayer!!.setMediaItems(
                items,
                initialWindowIndex,
                playbackStartPositionMs
            )
            castPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    private inner class MediaSessionQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return currentPlaylistItems[windowIndex].description
        }
    }

    private inner class MediaSessionPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean = false

        override fun getSupportedPrepareActions(): Long {
            return MediaSessionConnector.PlaybackPreparer.ACTIONS
        }

        override fun onPrepare(playWhenReady: Boolean) {
            return onPrepareFromMediaId("", playWhenReady, null)
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            serviceScope.launch {
                mediaSource.load {
                    val itemToPlay: MediaMetadataCompat? = mediaSource.find { item ->
                        item.mediaId == mediaId
                    }
                    if (itemToPlay == null) {
                        Log.w(TAG, "Content not found: MediaID=$mediaId")
                        // TODO: Notify caller of the error.
                    } else {
                        val playbackStartPositionMs = extras?.getLong("playback_start_position_ms", C.TIME_UNSET) ?: C.TIME_UNSET

                        preparePlaylist(
                            mediaSource.filter { it.album == itemToPlay.album }.sortedBy { it.trackNumber },
                            itemToPlay,
                            playWhenReady,
                            playbackStartPositionMs
                        )
                    }
                }
            }
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        }
    }

    private inner class PlayerListener : Player.Listener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            this@MediaPlayer.onPlayerStateChanged(playWhenReady, playbackState)
        }
    }

    companion object {
        private val TAG = MediaPlayer::class.java.simpleName
    }
}
