package app.surgo.core.room

import androidx.room.withTransaction
import app.surgo.data.DatabaseTransactionRunner
import javax.inject.Inject

class RoomTransactionRunner @Inject constructor(
    private val database: AppDatabase
) : DatabaseTransactionRunner {
    override suspend operator fun <T> invoke(block: suspend () -> T): T {
        return database.withTransaction {
            block()
        }
    }
}
