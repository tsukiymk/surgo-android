package app.surgo.core.plugin

import android.content.Context
import app.surgo.core.datastore.PreferenceStorage
import app.surgo.core.plugin.extensions.SimpleDataSourceManager
import app.surgo.data.DataSourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PluginModule {
    @Singleton
    @Provides
    fun providePluginManager(
        @ApplicationContext context: Context
    ): PluginManager = PluginManager.getInstance(context)
}

@InstallIn(SingletonComponent::class)
@Module
object ExtensionModule {
    @Singleton
    @Provides
    fun provideDataSourceManager(
        @ApplicationContext context: Context,
        pluginManager: PluginManager,
        preferenceStorage: PreferenceStorage
    ): DataSourceManager = SimpleDataSourceManager(context, pluginManager, preferenceStorage)
}
