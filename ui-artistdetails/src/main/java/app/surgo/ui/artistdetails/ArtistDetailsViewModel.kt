package app.surgo.ui.artistdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.common.compose.NavArguments
import app.surgo.domain.ObservableLoadingCounter
import app.surgo.domain.collectWithStatus
import app.surgo.domain.interactors.UpdateArtistCatalog
import app.surgo.domain.observers.ObserveArtist
import app.surgo.domain.observers.ObserveArtistAlbums
import app.surgo.domain.observers.ObservePopularSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ArtistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateArtistCatalog: UpdateArtistCatalog,
    private val observeArtist: ObserveArtist,
    private val observePopularSongs: ObservePopularSongs,
    private val observeArtistAlbums: ObserveArtistAlbums
) : ViewModel() {
    private val _state = MutableStateFlow(ArtistDetailsViewState())
    val state: StateFlow<ArtistDetailsViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<ArtistDetailsAction>()

    private val loadingState = ObservableLoadingCounter()

    private val artistId = savedStateHandle.get<Long>(NavArguments.ARTIST_DETAILS_ID_KEY)
        ?: throw NullPointerException()

    init {
        viewModelScope.launch {
            combine(
                loadingState.observe,
                observeArtist.observe(),
                observePopularSongs.observe(),
                observeArtistAlbums.observe()
            ) { isRefreshing, artist, popularSongs, albums ->
                ArtistDetailsViewState(
                    isRefreshing = isRefreshing,
                    artist = artist,
                    songs = popularSongs.map { it.songWithArtists.song },
                    albums = albums
                )
            }.collect { _state.value = it }
        }

        observeArtist(ObserveArtist.Parameters(artistId))
        observePopularSongs(ObservePopularSongs.Parameters(artistId))
        observeArtistAlbums(ObserveArtistAlbums.Parameters(artistId))

        refresh(false)
    }

    fun submitAction(action: ArtistDetailsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            updateArtistCatalog(
                UpdateArtistCatalog.Parameters(
                    artistId = artistId,
                    forceRefresh = force
                )
            ).collectWithStatus(loadingState)
        }
    }
}
