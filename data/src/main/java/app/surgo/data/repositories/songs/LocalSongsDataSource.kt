package app.surgo.data.repositories.songs

import com.tsukiymk.surgo.openapi.datasource.SongsDataSource
import com.tsukiymk.surgo.openapi.datasource.entities.Song

class LocalSongsDataSource : SongsDataSource {
    override suspend fun getSong(songId: Long): Result<Song> {
        return Result.failure(NotImplementedError())
    }

    override suspend fun getSongs(songIds: Array<Long>): Result<List<Song>> {
        return Result.failure(NotImplementedError())
    }
}
