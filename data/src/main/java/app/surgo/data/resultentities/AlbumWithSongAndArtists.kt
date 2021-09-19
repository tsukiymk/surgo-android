package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.AlbumEntity
import app.surgo.data.entities.SongEntity

data class AlbumWithSongAndArtists(
    @Embedded val album: AlbumEntity
) {
    @Relation(
        parentColumn = "id",
        entity = SongEntity::class,
        entityColumn = "album_id"
    )
    lateinit var songWithArtists: List<SongWithArtists>
}
