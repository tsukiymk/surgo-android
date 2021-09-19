package app.surgo.ui.playlistdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.common.compose.NavArguments
import app.surgo.core.media2.*
import app.surgo.domain.InteractorStatus
import app.surgo.domain.ObservableLoadingCounter
import app.surgo.domain.collectWithStatus
import app.surgo.domain.interactors.AddPlayTracks
import app.surgo.domain.interactors.UpdatePlaylistSongs
import app.surgo.domain.observers.ObservePlaylist
import app.surgo.domain.observers.ObservePlaylistSongs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PlaylistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playbackConnection: PlaybackConnection,
    private val observePlaylist: ObservePlaylist,
    private val updatePlaylistSongs: UpdatePlaylistSongs,
    private val observePlaylistSongs: ObservePlaylistSongs,
    private val addPlayTracks: AddPlayTracks
) : ViewModel() {
    private val _state = MutableStateFlow(PlaylistDetailsViewState())
    val state: StateFlow<PlaylistDetailsViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<PlaylistDetailsAction>()

    private val loadingState = ObservableLoadingCounter()

    private val playlistId = savedStateHandle.get<Long>(NavArguments.PLAYLIST_DETAILS_ID_KEY)
        ?: throw NullPointerException()

    init {
        viewModelScope.launch {
            combine(
                loadingState.observe,
                observePlaylist.observe(),
                observePlaylistSongs.observe()
            ) { isRefreshing, playlist, playlistSongs ->
                PlaylistDetailsViewState(
                    isRefreshing = isRefreshing,
                    playlist = playlist,
                    songWithArtists = playlistSongs.map { it.songWithArtists }
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is PlaylistDetailsAction.Refresh -> refresh(true)
                    is PlaylistDetailsAction.ShufflePlay -> prepareShufflePlay()
                    is PlaylistDetailsAction.Play -> {
                        preparePlayTracks()
                        playMedia("${action.songId}")
                    }
                }
            }
        }

        observePlaylist(ObservePlaylist.Parameters(playlistId))
        observePlaylistSongs(ObservePlaylistSongs.Parameters(playlistId))

        refresh(false)
    }

    fun submitAction(action: PlaylistDetailsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            updatePlaylistSongs(
                UpdatePlaylistSongs.Parameters(
                    playlistId = playlistId,
                    forceRefresh = force
                )
            ).collectWithStatus(loadingState)
        }
    }

    private fun prepareShufflePlay() {
        viewModelScope.launch {

        }
    }

    private fun preparePlayTracks() {
        viewModelScope.launch {
            addPlayTracks(
                AddPlayTracks.Parameters(playlistId = playlistId)
            ).collect()
        }
    }

    private fun playMedia(mediaId: String, pauseAllowed: Boolean = true) {
        val playbackState = playbackConnection.playbackState.value
        val nowPlaying = playbackConnection.currentPlay.value
        val transportControls = playbackConnection.transportControls ?: return

        val isPrepared = playbackState.isPrepared
        if (isPrepared && nowPlaying.mediaId == mediaId) {
            playbackState.let { state ->
                when {
                    state.isPlaying -> if (pauseAllowed) transportControls.pause() else Unit
                    state.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(TAG, "Playable item clicked but neither play nor pause are enabled! (mediaId=$mediaId)")
                    }
                }
            }
        } else {
            transportControls.prepareFromMediaId(mediaId, null)
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    companion object {
        private val TAG = PlaylistDetailsViewModel::class.java.simpleName
    }
}
