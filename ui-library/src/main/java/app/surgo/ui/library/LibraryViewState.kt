package app.surgo.ui.library

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.PlaylistEntity

@Immutable
internal data class LibraryViewState(
    val playlists: List<PlaylistEntity> = emptyList()
)
