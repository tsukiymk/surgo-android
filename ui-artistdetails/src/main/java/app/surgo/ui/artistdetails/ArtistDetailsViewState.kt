package app.surgo.ui.artistdetails

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.AlbumEntity
import app.surgo.data.entities.ArtistEntity
import app.surgo.data.entities.SongEntity
import app.surgo.data.entities.VideoEntity

@Immutable
internal class ArtistDetailsViewState(
    val isRefreshing: Boolean = false,
    val artist: ArtistEntity = ArtistEntity(name = ""),
    val songs: List<SongEntity> = emptyList(),
    val albums: List<AlbumEntity> = emptyList(),
    val videos: List<VideoEntity> = emptyList()
)
