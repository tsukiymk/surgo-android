package app.surgo.core.plugin

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class PluginUpdateJob(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
