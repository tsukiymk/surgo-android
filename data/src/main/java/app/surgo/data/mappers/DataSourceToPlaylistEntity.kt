package app.surgo.data.mappers

import app.surgo.data.entities.PlaylistEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Playlist

object DataSourceToPlaylistEntity {
    operator fun invoke(from: Playlist, source: Long): PlaylistEntity {
        return PlaylistEntity(
            source = source,
            originId = from.playlistId,
            name = from.name,
            imageUri = from.imageUrl
        )
    }
}
