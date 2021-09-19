package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "song_artist_entries",
    indices = [
        Index(value = ["song_id"]),
        Index(value = ["artist_id"]),
        Index(value = ["song_id", "artist_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id"]
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"]
        )
    ]
)
data class SongArtistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "song_id") val songId: Long,
    @ColumnInfo(name = "artist_id") override val artistId: Long
) : ArtistEntry
