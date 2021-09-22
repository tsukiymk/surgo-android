package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "video_artist_entries",
    indices = [
        Index(value = ["video_id"]),
        Index(value = ["artist_id"]),
        Index(value = ["video_id", "artist_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["video_id"]
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"]
        )
    ]
)
data class VideoArtistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "video_id") val videoId: Long,
    @ColumnInfo(name = "artist_id") override val artistId: Long
) : ArtistEntry
