package app.surgo.data.mappers

import app.surgo.data.entities.AlbumEntity
import app.surgo.data.entities.SongEntity
import com.tsukiymk.surgo.openapi.datasource.entities.Song

object DataSourceToSongEntity {
    operator fun invoke(from: Song, albumId: Long, source: Long): SongEntity {
        return SongEntity(
            albumId = albumId,
            source = source,
            originId = from.songId,
            name = from.name,
            album = from.album?.name,
            imageUri = from.album?.imageUrl,
            songUri = from.songUrl,
            genre = from.genre,
            duration = from.duration
        )
    }

    operator fun invoke(
        from: Song,
        album: AlbumEntity,
        source: Long
    ): SongEntity {
        return SongEntity(
            albumId = album.id,
            source = source,
            originId = from.songId,
            name = from.name,
            album = album.name,
            imageUri = album.imageUri,
            songUri = from.songUrl,
            genre = from.genre,
            duration = from.duration
        )
    }

    operator fun invoke(
        from: Song,
        source: Long
    ): SongEntity {
        return SongEntity(
            source = source,
            originId = from.songId,
            name = from.name,
            songUri = from.songUrl,
            genre = from.genre,
            duration = from.duration
        )
    }
}
