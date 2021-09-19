package app.surgo.data.entities

import androidx.room.*

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["song_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("song_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "song_id") val songId: Long = 0,
    @ColumnInfo(name = "order") val order: Int = 0,
    @ColumnInfo(name = "priority") val priority: Int = 0
) : IndexedEntity
