package app.surgo.data.mappers

import app.surgo.data.entities.VideoEntity
import com.tsukiymk.surgo.openapi.datasource.entities.MusicVideo

object DataSourceToMusicVideoEntity {
    operator fun invoke(from: MusicVideo, source: Long): VideoEntity {
        return VideoEntity(
            source = source,
            originId = from.videoId,
            name = from.name,
            imageUri = from.imageUrl
        )
    }
}
