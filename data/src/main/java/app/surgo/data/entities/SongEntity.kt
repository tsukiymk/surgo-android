package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "songs",
    indices = [
        Index(value = ["source", "origin_id"], unique = true),
        Index(value = ["album_id"])
    ],
    /*
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_id"]
        )
    ]
     */
)
data class SongEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "album_id") val albumId: Long = 0,
    @ColumnInfo(name = "source") val source: Long = 0,
    @ColumnInfo(name = "origin_id") val originId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "album") val album: String? = null,
    @ColumnInfo(name = "image_uri") val imageUri: String? = null,
    @ColumnInfo(name = "song_uri") val songUri: String? = null,
    @ColumnInfo(name = "genre") val genre: String? = null,
    @ColumnInfo(name = "duration") val duration: Long? = null
) : IndexedEntity
