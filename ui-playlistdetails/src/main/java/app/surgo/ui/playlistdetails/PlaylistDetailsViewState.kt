package app.surgo.ui.playlistdetails

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.PlaylistEntity
import app.surgo.data.resultentities.SongWithArtists

@Immutable
internal data class PlaylistDetailsViewState(
    val isRefreshing: Boolean = false,
    val playlist: PlaylistEntity = PlaylistEntity(name = ""),
    val songWithArtists: List<SongWithArtists> = emptyList(),
)
