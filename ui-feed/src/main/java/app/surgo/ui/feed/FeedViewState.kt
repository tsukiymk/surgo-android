package app.surgo.ui.feed

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.PlaylistEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

@Immutable
internal data class FeedViewState(
    val isRefreshing: Boolean = false,
    val recentlyPlaylists: List<PlaylistEntity> = emptyList(),
    val recommendations: Resource = Resource(),
    val recommendedPlaylists: List<PlaylistEntity> = emptyList(),
    val popularPlaylists: List<PlaylistEntity> = emptyList()
)
