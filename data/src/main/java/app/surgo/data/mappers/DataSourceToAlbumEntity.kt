package app.surgo.data.mappers

import app.surgo.data.entities.AlbumEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Album

object DataSourceToAlbumEntity {
    operator fun invoke(from: Album, source: Long): AlbumEntity {
        return AlbumEntity(
            source = source,
            originId = from.albumId,
            name = from.name,
            imageUri = from.imageUrl
        )
    }
}
