package app.surgo.data.repositories.songs

import app.surgo.data.entities.SongEntity
import app.surgo.data.mappers.DataSourceToSongEntity
import app.surgo.shared.plugin.DataSourceManager
import com.tsukiymk.surgo.openapi.datasource.SongsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsStore @Inject constructor(
    private val sourceManager: DataSourceManager
) {
    private val source
        get() = sourceManager.key

    private val songsDataSource: SongsDataSource
        get() = sourceManager.factory.songsDataSource()
}
