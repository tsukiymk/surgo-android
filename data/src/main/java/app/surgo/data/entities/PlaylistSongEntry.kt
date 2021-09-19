package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "playlist_song_entries",
    indices = [
        Index(value = ["playlist_id"]),
        Index(value = ["song_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"]
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"]
        )
    ]
)
data class PlaylistSongEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "playlist_id") val playlistId: Long,
    @ColumnInfo(name = "song_id") override val songId: Long
) : SongEntry
