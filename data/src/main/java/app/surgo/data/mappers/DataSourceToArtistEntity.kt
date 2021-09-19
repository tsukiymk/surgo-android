package app.surgo.data.mappers

import app.surgo.data.entities.ArtistEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Artist

object DataSourceToArtistEntity {
    operator fun invoke(from: Artist, source: Long): ArtistEntity {
        return ArtistEntity(
            source = source,
            originId = from.artistId,
            name = from.name,
            imageUri = from.imageUrl
        )
    }
}
