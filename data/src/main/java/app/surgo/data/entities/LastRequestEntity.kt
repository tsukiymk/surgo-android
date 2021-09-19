package app.surgo.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

enum class Request(val tag: String) {
    ALBUM_SONGS("album_songs"),
    PLAYLIST_SONGS("playlist_songs")
}

@Entity(
    tableName = "last_requests",
    indices = [
        Index(value = ["request", "entity_id"], unique = true)
    ]
)
data class LastRequestEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "request") val request: Request,
    @ColumnInfo(name = "entity_id") val entityId: Long,
    @ColumnInfo(name = "timestamp") val timestamp: OffsetDateTime
) : IndexedEntity
