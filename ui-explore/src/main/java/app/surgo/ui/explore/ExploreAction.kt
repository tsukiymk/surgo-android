package app.surgo.ui.explore

internal sealed class ExploreAction {
    data class OpenArtistDetails(val artistId: Long) : ExploreAction()
    data class Search(val query: String = "") : ExploreAction()
}
