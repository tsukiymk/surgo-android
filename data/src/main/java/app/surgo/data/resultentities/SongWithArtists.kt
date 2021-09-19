package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import app.surgo.data.entities.ArtistEntity
import app.surgo.data.entities.SongArtistEntry
import app.surgo.data.entities.SongEntity

data class SongWithArtists(
    @Embedded val song: SongEntity
) {
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SongArtistEntry::class,
            parentColumn = "song_id",
            entityColumn = "artist_id"
        )
    )
    lateinit var artists: List<ArtistEntity>
}
