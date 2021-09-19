package app.surgo.data.repositories.lastrequests

import app.surgo.data.daos.LastRequestsDao
import app.surgo.data.entities.LastRequestEntity
import app.surgo.data.entities.Request
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.TemporalAmount
import javax.inject.Inject

class LastRequestsStore @Inject constructor(
    private val lastRequestsDao: LastRequestsDao
) {
    suspend fun isRequestExpired(
        request: Request,
        entityId: Long,
        threshold: TemporalAmount
    ): Boolean {
        val timestamp = lastRequestsDao.getLastRequest(request, entityId)?.timestamp
        val instant = Instant.now().minus(threshold)

        return timestamp?.toInstant()?.isBefore(instant) ?: true
    }

    suspend fun updateLastRequest(
        request: Request,
        entityId: Long,
        timestamp: OffsetDateTime = OffsetDateTime.now()
    ) {
        lastRequestsDao.insert(
            LastRequestEntity(request = request, entityId = entityId, timestamp = timestamp)
        )
    }
}
