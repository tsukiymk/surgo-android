package app.surgo.ui.playlistdetails

internal sealed class PlaylistDetailsAction {
    object Refresh : PlaylistDetailsAction()
    object ShufflePlay : PlaylistDetailsAction()
    data class Play(val songId: Long) : PlaylistDetailsAction()
}
