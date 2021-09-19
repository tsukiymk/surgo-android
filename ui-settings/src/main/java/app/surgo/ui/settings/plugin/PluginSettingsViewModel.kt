package app.surgo.ui.settings.plugin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.core.datastore.PreferenceStorage
import app.surgo.core.plugin.PluginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PluginSettingsViewModel @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val pluginManager: PluginManager
) : ViewModel() {
    private val _state = MutableStateFlow(PluginSettingsViewState())
    val state: StateFlow<PluginSettingsViewState>
        get() = _state

    private val pendingActions = MutableSharedFlow<PluginSettingsAction>()

    init {
        viewModelScope.launch {
            combine(
                preferenceStorage.selectedDataSource,
                pluginManager.observableInstalled
            ) { selectedDataSource, installedExtensions ->
                PluginSettingsViewState(
                    selectedDataSource = selectedDataSource,
                    installedPlugins = installedExtensions
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is PluginSettingsAction.ChangeDataSource -> {
                        preferenceStorage.selectDataSource(action.dataSource)
                    }
                }
            }
        }
    }

    fun submitAction(action: PluginSettingsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }
}
