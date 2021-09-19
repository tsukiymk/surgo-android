package app.surgo.ui.feed

internal sealed class FeedAction {
    object OpenSettings : FeedAction()
    data class OpenPlaylistDetails(val playlistId: Long) : FeedAction()
}
