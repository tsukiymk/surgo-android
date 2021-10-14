package app.surgo.data.repositories.songs

import app.surgo.data.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.SongsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsStore @Inject constructor(
    private val sourceManager: DataSourceManager
) {
    private val source
        get() = sourceManager.selectedSource

    private val songsDataSource: SongsDataSource
        get() = sourceManager[source].songsDataSource()
}
