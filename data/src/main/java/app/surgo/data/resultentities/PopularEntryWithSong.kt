package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.EntryWithSong
import app.surgo.data.entities.PopularSongEntry
import app.surgo.data.entities.SongEntity

class PopularEntryWithSong(
    @Embedded override var entry: PopularSongEntry
) : EntryWithSong<PopularSongEntry> {
    @Relation(
        parentColumn = "song_id",
        entity = SongEntity::class,
        entityColumn = "id"
    )
    override lateinit var songWithArtists: SongWithArtists
}
