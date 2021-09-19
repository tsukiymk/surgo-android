package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.AlbumArtistEntry
import app.surgo.data.entities.ArtistEntity
import app.surgo.data.entities.EntryWithArtist

class AlbumEntryWithArtist(
    @Embedded override var entry: AlbumArtistEntry
) : EntryWithArtist<AlbumArtistEntry> {
    @Relation(
        parentColumn = "artist_id",
        entity = ArtistEntity::class,
        entityColumn = "id"
    )
    override lateinit var artists: ArtistEntity
}
