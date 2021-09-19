package app.surgo.data.mappers

import app.surgo.data.entities.SongEntity
import com.tsukiymk.surgo.openapi.datasource.Catalog

object CatalogToSongEntity {
    operator fun invoke(from: Catalog, source: Long): SongEntity {
        return SongEntity(
            source = source,
            originId = from.id!!,
            name = from.attributes?.name!!,
            album = from.attributes?.albumName,
            imageUri = from.attributes?.artwork,
            songUri = from.attributes?.url,
            duration = from.attributes?.durationInMillis
        )
    }
}
