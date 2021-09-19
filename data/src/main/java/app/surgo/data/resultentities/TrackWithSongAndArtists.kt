package app.surgo.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import app.surgo.data.entities.SongEntity
import app.surgo.data.entities.TrackEntity

data class TrackWithSongAndArtists(
    @Embedded val track: TrackEntity
) {
    @Relation(
        parentColumn = "song_id",
        entity = SongEntity::class,
        entityColumn = "id"
    )
    lateinit var songWithArtists: SongWithArtists
}
