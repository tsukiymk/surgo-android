package app.surgo.data.mappers

import app.surgo.data.entities.AlbumEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

object CatalogToAlbumEntity {
    operator fun invoke(from: Resource, source: Long): AlbumEntity {
        return AlbumEntity(
            source = source,
            originId = from.id!!,
            name = from.attributes?.name!!,
            imageUri = from.attributes?.artwork?.url
        )
    }
}
