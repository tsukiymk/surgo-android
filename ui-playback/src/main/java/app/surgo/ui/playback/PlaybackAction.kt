package app.surgo.ui.playback

internal sealed class PlaybackAction {
    object Close : PlaybackAction()
    data class OpenArtistDetails(val artistId: Long) : PlaybackAction()
    data class PlayOrPause(val songUri: String) : PlaybackAction()
    object SkipToPrevious : PlaybackAction()
    object SkipToNext : PlaybackAction()
}
