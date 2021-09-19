package app.surgo.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "music_videos",
    indices = [
        Index(value = ["source", "origin_id"], unique = true)
    ]
)
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "source") val source: Long = 0,
    @ColumnInfo(name = "origin_id") val originId: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image_uri") val imageUri: String? = null,
) : IndexedEntity
