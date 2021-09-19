package app.surgo.ui.playback

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.media2.PlaybackConnection
import app.surgo.core.media2.isPlayEnabled
import app.surgo.core.media2.isPlaying
import app.surgo.core.media2.isPrepared
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class PlaybackViewModel @Inject constructor(
    private val playbackConnection: PlaybackConnection
) : ViewModel() {
    private val _state = MutableStateFlow(PlaybackViewState())
    val state: StateFlow<PlaybackViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<PlaybackAction>()

    init {
        viewModelScope.launch {
            combine(
                playbackConnection.playbackState,
                playbackConnection.currentPlay,
                playbackConnection.progressState
            ) { playbackState, mediaMetadata, progressState ->
                PlaybackViewState(
                    playbackState = playbackState,
                    nowPlaying = mediaMetadata,
                    progressState = progressState
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is PlaybackAction.PlayOrPause -> {
                        playMedia("1")
                    }
                    is PlaybackAction.SkipToPrevious -> skipToPrevious()
                    is PlaybackAction.SkipToNext -> skipToNext()
                }
            }
        }
    }

    fun submitAction(action: PlaybackAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }

    private fun playMedia(mediaId: String, pauseAllowed: Boolean = true) {
        val playbackState = playbackConnection.playbackState.value
        val transportControls = playbackConnection.transportControls ?: return

        val isPrepared = playbackState.isPrepared
        if (isPrepared) {
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
            transportControls.playFromMediaId(mediaId, null)
        }
    }

    private fun skipToNext() {
        val transportControls = playbackConnection.transportControls ?: return

        transportControls.skipToNext()
    }

    private fun skipToPrevious() {
        val transportControls = playbackConnection.transportControls ?: return

        transportControls.skipToPrevious()
    }

    companion object {
        private val TAG = PlaybackViewModel::class.java.simpleName
    }
}
