package app.surgo.data.daos

import androidx.room.Dao
import androidx.room.Query
import app.surgo.data.entities.LastRequestEntity
import app.surgo.data.entities.Request

@Dao
abstract class LastRequestsDao : EntityDao<LastRequestEntity>() {
    @Query("SELECT * FROM last_requests WHERE request = :request AND entity_id = :entityId")
    abstract suspend fun getLastRequest(request: Request, entityId: Long): LastRequestEntity?
}
