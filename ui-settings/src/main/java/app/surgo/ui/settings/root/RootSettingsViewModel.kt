package app.surgo.ui.settings.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.datastore.PreferenceStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RootSettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : ViewModel() {
    private val _state = MutableStateFlow(RootSettingsViewState())
    val state: StateFlow<RootSettingsViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<RootSettingsAction>()

    init {
        viewModelScope.launch {
            combine(
                preferenceStorage.selectedDataSource
            ) { (selectedDataSource) ->
                RootSettingsViewState(
                    selectedDataSource = selectedDataSource
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is RootSettingsAction.ChangeDataSource -> {
                        preferenceStorage.selectDataSource(action.dataSource)
                    }
                }
            }
        }
    }

    fun submitAction(action: RootSettingsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }
}
