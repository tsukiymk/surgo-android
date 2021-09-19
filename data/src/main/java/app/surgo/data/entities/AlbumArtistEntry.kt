package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "album_artist_entries",
    indices = [
        Index(value = ["album_id"]),
        Index(value = ["artist_id"]),
        Index(value = ["album_id", "artist_id"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_id"]
        ),
        ForeignKey(
            entity = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns = ["artist_id"]
        )
    ]
)
data class AlbumArtistEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "album_id") val albumId: Long,
    @ColumnInfo(name = "artist_id") override val artistId: Long
) : ArtistEntry
