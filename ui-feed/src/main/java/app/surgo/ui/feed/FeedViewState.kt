package app.surgo.ui.feed

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.PlaylistEntity

@Immutable
internal data class FeedViewState(
    val refreshing: Boolean = false,
    val recentlyPlaylists: List<PlaylistEntity> = emptyList(),
    val recommendedPlaylists: List<PlaylistEntity> = emptyList(),
    val popularPlaylists: List<PlaylistEntity> = emptyList()
)
