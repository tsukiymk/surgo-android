package app.surgo.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.plugin.PluginManager
import app.surgo.domain.ObservableLoadingCounter
import app.surgo.domain.interactors.GetCategories
import app.surgo.domain.interactors.SearchSongs
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class ExploreViewModel @Inject constructor(
    private val pluginManager: PluginManager,
    private val searchSongs: SearchSongs,
    private val getCategories: GetCategories
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreViewState())
    val state: StateFlow<ExploreViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<ExploreAction>()

    private val loadingState = ObservableLoadingCounter()

    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                getCategories.observe(),
                searchSongs.observe(),
                //getSearchSuggestions.observe()
            ) { categories, results ->
                ExploreViewState(
                    categories = categories,
                    searchResults = results
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            searchQuery.debounce(300)
                .collectLatest { query ->
                    val job = launch {
                        loadingState.addLoader()
                        val types = listOf(
                            SearchType.ARTISTS,
                            SearchType.ALBUMS,
                            SearchType.SONGS
                        )
                        searchSongs(SearchSongs.Parameters(query, types))
                    }
                    job.invokeOnCompletion { loadingState.removeLoader() }
                    job.join()
                }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is ExploreAction.Search -> {
                        searchQuery.value = action.query
                    }
                    else -> {}
                }
            }
        }

        viewModelScope.launch {
            pluginManager.messageBus.subscribeDataSource.collect {
                refresh()
            }
        }

        refresh()
    }

    fun submitAction(action: ExploreAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun refresh() {
        getCategories(Unit)

        //getSearchSuggestions(Unit)
    }
}
