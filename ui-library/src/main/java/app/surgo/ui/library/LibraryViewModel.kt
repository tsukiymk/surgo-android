package app.surgo.ui.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class LibraryViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(LibraryViewState())
    val state: StateFlow<LibraryViewState>
        get() = _state
}
