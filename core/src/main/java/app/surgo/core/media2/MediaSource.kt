package app.surgo.core.media2

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat

interface MediaSource : Iterable<MediaMetadataCompat> {
    suspend fun load(whenReady: () -> Unit)

    fun whenReady(performAction: (Boolean) -> Unit): Boolean

    fun search(query: String, extras: Bundle): List<MediaMetadataCompat>
}
