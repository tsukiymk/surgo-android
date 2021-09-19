package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "popular_song_entries",
    indices = [
        Index(value = ["artist_id"]),
        Index(value = ["song_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"]
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"]
        )
    ]
)
data class PopularSongEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "artist_id") val artistId: Long,
    @ColumnInfo(name = "song_id") override val songId: Long
) : SongEntry
