package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "recommended_playlist_entries",
    indices = [
        Index(value = ["playlist_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecommendedPlaylistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "playlist_id") override val playlistId: Long
) : PlaylistEntry
