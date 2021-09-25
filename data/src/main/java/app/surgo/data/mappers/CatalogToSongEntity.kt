package app.surgo.data.mappers

import app.surgo.data.entities.SongEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

object CatalogToSongEntity {
    operator fun invoke(from: Resource, source: Long): SongEntity {
        return SongEntity(
            source = source,
            originId = from.id!!,
            name = from.attributes?.name!!,
            album = from.attributes?.albumName,
            imageUri = from.attributes?.artwork?.url,
            songUri = from.attributes?.url,
            duration = from.attributes?.durationInMillis
        )
    }
}
