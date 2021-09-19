package com.tsukiymk.surgo

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import app.surgo.core.media2.*
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {
    @Inject
    lateinit var trackSource: TrackSource

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaController: MediaControllerCompat

    private lateinit var notificationManager: PlayerNotificationManager

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer(this, trackSource)
        sessionToken = mediaPlayer.mediaSession.sessionToken

        notificationManager = PlayerNotificationManager.Builder(
            applicationContext,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        ).apply {
            setMediaDescriptionAdapter(DescriptionAdapter())

            setChannelNameResourceId(R.string.notification_channel_now_playing)
            setChannelDescriptionResourceId(R.string.notification_channel_now_playing_description)

            setNotificationListener(PlayerNotificationListener())
        }.build()

        notificationManager.apply {
            setUseNextAction(true)

            setMediaSessionToken(mediaPlayer.mediaSession.sessionToken)

            setUseRewindAction(false)
            setUseFastForwardAction(false)
            setUseNextActionInCompactView(true)
        }

        mediaController = MediaControllerCompat(this@MusicService, mediaPlayer.mediaSession.sessionToken)

        mediaPlayer.onPlayerStateChanged = { playWhenReady, playbackState ->
            when (playbackState) {
                Player.STATE_BUFFERING, Player.STATE_READY -> {
                    notificationManager.setPlayer(mediaPlayer.currentPlayer)

                    if (playbackState == Player.STATE_READY) {
                        //saveRecentSongToStorage

                        if (!playWhenReady) {
                            stopForeground(false)
                            //isForegroundService = false
                        }
                    }
                }
                else -> {
                    notificationManager.setPlayer(null)
                }
            }
        }

        notificationManager.setPlayer(mediaPlayer.currentPlayer)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaPlayer.mediaSession, intent)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        serviceJob.cancel()

        mediaPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on download artwork.
                val request = ImageRequest.Builder(this@MusicService)
                    .data(uri)
                    .allowHardware(false)
                    .build()

                when (val result = Coil.execute(request)) {
                    is SuccessResult -> result.drawable.toBitmap()
                    else -> null
                }
            }
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = mediaController.metadata.description.iconUri

            return if (currentIconUri != iconUri || currentBitmap == null) {
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }

                null
            } else {
                currentBitmap
            }
        }
    }

    /*
    private inner class PlayerCustomActionReceiver : PlayerNotificationManager.CustomActionReceiver {
        private fun shouldShowPauseButton(
            player: Player
        ) = player.playbackState != Player.STATE_ENDED &&
            player.playbackState != Player.STATE_IDLE &&
            player.playWhenReady

        override fun createCustomActions(
            context: Context,
            instanceId: Int
        ): MutableMap<String, NotificationCompat.Action> {
            val favoriteAction = NotificationCompat.Action.Builder(
                R.drawable.ic_favorite_border_outlined,
                "Favorite",
                PendingIntent.getBroadcast(
                    context,
                    instanceId,
                    Intent(ACTION_FAVORITE).setPackage(context.packageName),
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).build()

            val shuffleAction = NotificationCompat.Action.Builder(
                R.drawable.ic_shuffle_outlined,
                "Shuffle",
                PendingIntent.getBroadcast(
                    context,
                    instanceId,
                    Intent(ACTION_FAVORITE).setPackage(context.packageName),
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).build()

            return mutableMapOf(
                Pair(ACTION_FAVORITE, favoriteAction),
                Pair(ACTION_SHUFFLE, shuffleAction)
            )
        }

        override fun getCustomActions(player: Player) = mutableListOf(
            ACTION_FAVORITE,
            PlayerNotificationManager.ACTION_PREVIOUS,
            if (shouldShowPauseButton(player)) {
                PlayerNotificationManager.ACTION_PAUSE
            } else {
                PlayerNotificationManager.ACTION_PLAY
            },
            PlayerNotificationManager.ACTION_NEXT,
            ACTION_SHUFFLE
        )

        override fun onCustomAction(player: Player, action: String, intent: Intent) {
        }
    }
     */

    private inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(
            notificationId: Int,
            dismissedByUser: Boolean
        ) {
            stopForeground(true)
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )

                startForeground(notificationId, notification)
            }
        }
    }

    companion object {
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"

        private const val NOW_PLAYING_CHANNEL_ID = "now_playing_channel"
        private const val NOW_PLAYING_NOTIFICATION_ID = 0xb339
    }
}
