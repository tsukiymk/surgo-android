package app.surgo.data.repositories.songs

import app.surgo.data.entities.SongEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    private val songsStore: SongsStore
) {
}
