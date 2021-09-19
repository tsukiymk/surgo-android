package app.surgo.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppPreferenceStorage.PREFERENCES_NAME
)

@InstallIn(SingletonComponent::class)
@Module
object PreferenceStorageModule {
    @Singleton
    @Provides
    fun providePreferenceStorage(
        @ApplicationContext context: Context
    ): PreferenceStorage = AppPreferenceStorage(context.dataStore)
}
