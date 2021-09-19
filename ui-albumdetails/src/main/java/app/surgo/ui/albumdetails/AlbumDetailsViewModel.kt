package app.surgo.ui.albumdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.common.compose.NavArguments
import app.surgo.domain.ObservableLoadingCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AlbumDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(AlbumDetailsViewState())
    val state: StateFlow<AlbumDetailsViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<AlbumDetailsAction>()

    private val loadingState = ObservableLoadingCounter()

    private val artistId = savedStateHandle.get<Long>(NavArguments.ALBUM_DETAILS_ID_KEY)
        ?: throw NullPointerException()

    init {
        viewModelScope.launch {
            combine(
                loadingState.observe
            ) { (isRefreshing) ->
                AlbumDetailsViewState(
                    isRefreshing = isRefreshing
                )
            }.collect { _state.value = it }
        }
    }

    fun submitAction(action: AlbumDetailsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }
}
