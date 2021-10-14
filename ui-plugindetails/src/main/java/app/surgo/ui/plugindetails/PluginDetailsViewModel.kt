package app.surgo.ui.plugindetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.surgo.common.compose.NavArguments
import app.surgo.core.datastore.PreferenceStorage
import app.surgo.core.plugin.PluginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PluginDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val preferenceStorage: PreferenceStorage,
    private val pluginManager: PluginManager
) : ViewModel() {
    private val _state = MutableStateFlow(PluginDetailsViewState())
    val state: StateFlow<PluginDetailsViewState>
        get() = _state

    private val pluginId = savedStateHandle.get<Long>(NavArguments.PLUGIN_DETAILS_ID_KEY)
        ?: throw NullPointerException()

    private val pendingActions = MutableSharedFlow<PluginDetailsAction>()

    init {
        viewModelScope.launch {
            combine(
                preferenceStorage.selectedDataSource,
                pluginManager.observableInstalled
            ) { selectedDataSource, installedPlugins ->
                val plugin = installedPlugins.find { pluginId == it.id }

                PluginDetailsViewState(
                    plugin = plugin,
                    isDefaultDataSource = selectedDataSource == plugin?.packageName
                )
            }.collect { _state.value = it }
        }

        viewModelScope.launch {
            pendingActions.collect { action ->
                when (action) {
                    is PluginDetailsAction.ChangeDataSource -> {
                        preferenceStorage.selectDataSource(action.dataSource)
                    }
                }
            }
        }
    }

    fun submitAction(action: PluginDetailsAction) {
        viewModelScope.launch {
            pendingActions.emit(action)
        }
    }
}
