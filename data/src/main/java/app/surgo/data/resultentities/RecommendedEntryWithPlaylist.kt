package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.EntryWithPlaylist
import app.surgo.data.entities.PlaylistEntity
import app.surgo.data.entities.RecommendedPlaylistEntry

class RecommendedEntryWithPlaylist(
    @Embedded override var entry: RecommendedPlaylistEntry
) : EntryWithPlaylist<RecommendedPlaylistEntry> {
    @Relation(
        parentColumn = "playlist_id",
        entity = PlaylistEntity::class,
        entityColumn = "id"
    )
    override lateinit var playlist: PlaylistEntity
}
