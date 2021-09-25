package app.surgo.ui.explore

import androidx.compose.runtime.Immutable
import app.surgo.data.resultentities.SearchResults
import com.tsukiymk.surgo.openapi.datasource.entities.Resource

@Immutable
internal data class ExploreViewState(
    val categories: Resource = Resource(),
    val searchResults: SearchResults = SearchResults()
)
