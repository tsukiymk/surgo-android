package app.surgo.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.plugin.PluginManager
import app.surgo.domain.InteractorStatus
import app.surgo.domain.ObservableLoadingCounter
import app.surgo.domain.interactors.UpdatePopularPlaylists
import app.surgo.domain.interactors.UpdateRecommendedPlaylists
import app.surgo.domain.observers.ObservePopularPlaylists
import app.surgo.domain.observers.ObserveRecommendedPlaylists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val pluginManager: PluginManager,
    private val updateRecommendedPlaylists: UpdateRecommendedPlaylists,
    private val observeRecommendedPlaylists: ObserveRecommendedPlaylists,
    private val updatePopularPlaylists: UpdatePopularPlaylists,
    private val observePopularPlaylists: ObservePopularPlaylists
) : ViewModel() {
    private val _state = MutableStateFlow(FeedViewState())
    val state: StateFlow<FeedViewState>
        get() = _state

    private val loadingState = ObservableLoadingCounter()

    init {
        viewModelScope.launch {
            combine(
                observeRecommendedPlaylists.observe(),
                observePopularPlaylists.observe()
            ) { recommendedPlaylists, popularPlaylists ->
                FeedViewState(
                    recommendedPlaylists = recommendedPlaylists.map { it.playlist },
                    popularPlaylists = popularPlaylists.map { it.playlist }
                )
            }.collect { _state.value = it }
        }

        observeRecommendedPlaylists(ObserveRecommendedPlaylists.Parameters(count = 10))
        observePopularPlaylists(ObservePopularPlaylists.Parameters(count = 10))

        viewModelScope.launch {
            pluginManager.messageBus.subscribeDataSource.collect {
                refresh(true)
            }
        }

        refresh(false)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            updateRecommendedPlaylists(
                UpdateRecommendedPlaylists.Parameters(forceRefresh = force)
            ).collectWithStatus()

            updatePopularPlaylists(
                UpdatePopularPlaylists.Parameters(forceRefresh = force)
            ).collectWithStatus()
        }
    }

    private suspend fun Flow<InteractorStatus>.collectWithStatus() = collect { status ->
        when (status) {
            InteractorStatus.Loading -> { loadingState.addLoader() }
            InteractorStatus.Success -> { loadingState.removeLoader() }
            is InteractorStatus.Error -> {
                loadingState.removeLoader()
            }
        }
    }
}
