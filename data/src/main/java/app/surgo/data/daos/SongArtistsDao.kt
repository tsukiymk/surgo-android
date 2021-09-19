package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Transaction
import app.surgo.data.entities.SongArtistEntry

@Dao
abstract class SongArtistsDao : EntityDao<SongArtistEntry>() {
    suspend fun insertOrUpdate(entity: SongArtistEntry): Long {
        return if (entity.id == 0L) {
            insert(entity)
        } else {
            update(entity)
            entity.id
        }
    }

    @Transaction
    open suspend fun insertOrUpdate(entities: List<SongArtistEntry>) {
        entities.forEach { insertOrUpdate(it) }
    }
}
