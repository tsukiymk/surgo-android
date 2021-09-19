package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.EntryWithPlaylist
import app.surgo.data.entities.PlaylistEntity
import app.surgo.data.entities.PopularPlaylistEntry

class PopularEntryWithPlaylist(
    @Embedded override var entry: PopularPlaylistEntry
) : EntryWithPlaylist<PopularPlaylistEntry> {
    @Relation(
        parentColumn = "playlist_id",
        entity = PlaylistEntity::class,
        entityColumn = "id"
    )
    override lateinit var playlist: PlaylistEntity
}
