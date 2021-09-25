package app.surgo.data.mappers

import app.surgo.data.entities.VideoEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

object CatalogToVideoEntity {
    operator fun invoke(from: Resource, source: Long): VideoEntity {
        return VideoEntity(
            source = source,
            originId = from.id!!,
            name = from.attributes?.name!!,
            imageUri = from.attributes?.artwork?.url
        )
    }
}
