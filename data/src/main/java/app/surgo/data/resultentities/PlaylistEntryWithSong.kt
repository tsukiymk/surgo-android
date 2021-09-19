package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.EntryWithSong
import app.surgo.data.entities.PlaylistSongEntry
import app.surgo.data.entities.SongEntity

data class PlaylistEntryWithSong(
    @Embedded override var entry: PlaylistSongEntry
) : EntryWithSong<PlaylistSongEntry> {
    @Relation(
        parentColumn = "song_id",
        entity = SongEntity::class,
        entityColumn = "id"
    )
    override lateinit var songWithArtists: SongWithArtists
}
