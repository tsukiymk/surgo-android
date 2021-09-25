package app.surgo.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.plugin.PluginManager
import app.surgo.domain.ObservableLoadingCounter
import app.surgo.domain.interactors.GetRecommendations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val pluginManager: PluginManager,
    private val getRecommendations: GetRecommendations
) : ViewModel() {
    private val _state = MutableStateFlow(FeedViewState())
    val state: StateFlow<FeedViewState>
        get() = _state

    private val loadingState = ObservableLoadingCounter()

    init {
        viewModelScope.launch {
            combine(
                loadingState.observe,
                getRecommendations.observe()
            ) { isRefreshing, recommendations ->
                FeedViewState(
                    isRefreshing = isRefreshing,
                    recommendations = recommendations
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pluginManager.messageBus.subscribeDataSource.collect {
                refresh(true)
            }
        }

        viewModelScope.launch {
            refresh(false)
        }
    }

    private fun refresh(force: Boolean) {
        getRecommendations(Unit)
    }
}
