package app.surgo.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface PreferenceStorage {
    val selectedTheme: Flow<String>
    suspend fun selectTheme(theme: String)

    val selectedDataSource: Flow<String>
    suspend fun selectDataSource(dataSource: String)
}

@Singleton
class AppPreferenceStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceStorage {
    override val selectedTheme: Flow<String>
        get() = dataStore.data.map { it[PREF_SELECTED_THEME] ?: "system" }

    override suspend fun selectTheme(theme: String) {
        dataStore.edit {
            it[PREF_SELECTED_THEME] = theme
        }
    }

    override val selectedDataSource: Flow<String>
        get() = dataStore.data.map { it[PREF_SELECTED_DATASOURCE] ?: "" }

    override suspend fun selectDataSource(dataSource: String) {
        dataStore.edit {
            it[PREF_SELECTED_DATASOURCE] = dataSource
        }
    }

    companion object {
        const val PREFERENCES_NAME: String = "surgo_preferences"

        val PREF_SELECTED_THEME = stringPreferencesKey("pref_dark_mode")
        val PREF_SELECTED_DATASOURCE = stringPreferencesKey("pref_selected_datasource")
    }
}
