package app.surgo.ui.explore

import androidx.compose.runtime.Immutable
import app.surgo.data.entities.CategoryEntity
import app.surgo.data.resultentities.SearchResults

@Immutable
internal data class ExploreViewState(
    val categories: List<CategoryEntity> = emptyList(),
    val searchResults: SearchResults = SearchResults()
)
