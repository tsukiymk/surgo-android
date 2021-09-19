package app.surgo.core.media2

import kotlin.math.floor

data class PlaybackProgressState(
    val total: Long = 0L,
    val position: Long = 0L,
    val elapsed: Long = 0L
) {
    val progress: Float
        get() = (position.toFloat() + elapsed) / (total + 1).toFloat()

    val currentDuration: String
        get() = (position + elapsed).timestampToMSS()

    val totalDuration: String
        get() = total.timestampToMSS()

    private fun Long.timestampToMSS(): String {
        val totalSeconds = floor(this / 1e3).toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = (totalSeconds % 60).toString().padStart(2, '0')

        return "$minutes:$remainingSeconds"
    }
}
