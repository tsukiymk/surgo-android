package app.surgo.domain.interactors

import app.surgo.data.repositories.search.SearchRepository
import app.surgo.data.repositories.songs.SongsRepository
import app.surgo.data.resultentities.SearchResults
import app.surgo.domain.SuspendingWorkInteractor
import com.tsukiymk.surgo.openapi.datasource.enumerations.SearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchSongs @Inject constructor(
    private val searchRepository: SearchRepository,
    private val songsRepository: SongsRepository
) : SuspendingWorkInteractor<SearchSongs.Parameters, SearchResults>() {
    override suspend fun doWork(params: Parameters): SearchResults {
        return withContext(Dispatchers.IO) {
            searchRepository.search(params.query, params.types.toTypedArray())
        }
    }

    data class Parameters(
        val query: String,
        val types: List<SearchType>
    )
}

/*
return@withContext if (
    params.types.contains(SearchType.SONGS) &&
    searchResults.songs.isNotEmpty()
) {
    val songs = songsRepository.getSongs(searchResults.songs.map { it.originId }.toTypedArray())

    searchResults.copy(songs = songs)
} else {
    searchResults
}
 */
