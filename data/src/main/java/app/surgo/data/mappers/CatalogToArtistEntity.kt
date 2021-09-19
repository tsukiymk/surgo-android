package app.surgo.data.mappers

import app.surgo.data.entities.ArtistEntity
import com.tsukiymk.surgo.openapi.datasource.Catalog

object CatalogToArtistEntity {
    operator fun invoke(from: Catalog, source: Long): ArtistEntity {
        return ArtistEntity(
            source = source,
            originId = from.id!!,
            name = from.attributes?.name!!,
            imageUri = from.attributes?.artwork!!
        )
    }
}
