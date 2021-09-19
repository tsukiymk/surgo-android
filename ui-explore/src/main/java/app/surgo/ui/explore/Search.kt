package app.surgo.ui.explore

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.surgo.common.compose.theme.Keyline1
import app.surgo.common.compose.theme.Pink200
import app.surgo.data.entities.AlbumEntity
import app.surgo.data.entities.ArtistEntity
import app.surgo.data.entities.SongEntity
import app.surgo.data.resultentities.SearchResults
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation

enum class SearchDisplay {
    Categories,
    //Suggestions,
    Results,
    NoResults
}

enum class SearchTypes(
    @StringRes val label: Int,
    @DrawableRes val drawable: Int,
    val color: Color
) {
    ARTISTS(R.string.text_artists, R.drawable.ic_account_music_outlined, Color.Magenta),
    ALBUMS(R.string.text_albums, R.drawable.ic_album_filled, Color.Red),
    PLAYLISTS(R.string.text_playlists, R.drawable.ic_playlist_music_outlined, Color.Green),
    SONGS(R.string.text_songs, R.drawable.ic_music_note_outlined, Color.Blue),
    MUSIC_VIDEOS(R.string.text_music_videos, R.drawable.ic_video_outlined, Color.Gray)
}

/*
@Composable
fun SearchSuggestions(
    suggestions: SearchSuggestionGroup
) {
    LazyColumn {
        if (suggestions.recentSearches.isNotEmpty()) {
            item {
                SearchSuggestionHeader(stringResource(R.string.text_recent_searches))
            }
            items(suggestions.recentSearches) { suggestion ->
                SearchSuggestionItem(suggestion)
            }
        }
        item {
            SearchSuggestionHeader(stringResource(R.string.text_trending_searches))
        }
        items(suggestions.trendingSearches) { suggestion ->
            SearchSuggestionItem(suggestion)
        }
    }
}
 */

@Composable
internal fun SearchResultsContent(
    results: SearchResults,
    emit: (ExploreAction) -> Unit
) {
    LazyColumn {
        item {
            ArtistsColumn(
                artists = results.artists,
                onItemClicked = { emit(ExploreAction.OpenArtistDetails(it)) }
            )
        }
        item {
            AlbumsColumn(results.albums)
        }
        lazyItemOfSongs(results.songs)
    }
}

@Composable
fun SearchNoResultsContent() {

}

@Composable
private fun SearchSuggestionHeader(
    name: String
) {
    Text(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .wrapContentHeight(),
        text = name,
        color = Pink200,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun SearchSuggestionItem(
    suggestion: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = suggestion,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(start = 24.dp)
            .wrapContentSize(Alignment.CenterStart)
    )
}

@Composable
private fun SearchResult(
    song: SongEntity,
    modifier: Modifier = Modifier
) {
    Text(
        text = song.name,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(start = 24.dp)
            .wrapContentSize(Alignment.CenterStart)
    )
}

@Composable
private fun ArtistsColumn(
    artists: List<ArtistEntity>,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.text_artists),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        LazyRow(
            contentPadding = PaddingValues(
                start = Keyline1,
                top = 8.dp,
                end = Keyline1,
                bottom = 16.dp
            )
        ) {
            val lastIndex = artists.size - 1

            itemsIndexed(artists) { index, artist ->
                ArtistRow(
                    artist = artist,
                    modifier = Modifier
                        .width(72.dp)
                        .clickable { onItemClicked(artist.id) }
                )
                if (index < lastIndex) Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ArtistRow(
    artist: ArtistEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (artist.imageUri != null) {
                val painter = rememberImagePainter(
                    data = artist.imageUri,
                    builder = {
                        transformations(
                            CircleCropTransformation()
                        )
                    }
                )

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = artist.name,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AlbumsColumn(
    albums: List<AlbumEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.text_albums),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        LazyRow(
            contentPadding = PaddingValues(
                start = Keyline1,
                top = 8.dp,
                end = Keyline1,
                bottom = 16.dp
            )
        ) {
            val lastIndex = albums.size - 1

            itemsIndexed(albums) { index, album ->
                AlbumRow(
                    album = album,
                    modifier = Modifier
                        .width(128.dp)
                        .clickable { /* */ }
                )
                if (index < lastIndex) Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun AlbumRow(
    album: AlbumEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (album.imageUri != null) {
                Image(
                    painter = rememberImagePainter(album.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            text = album.name,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2
        )
    }
}

private fun LazyListScope.lazyItemOfSongs(
    songs: List<SongEntity>,
) {
    item {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = stringResource(R.string.text_songs),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    items(songs) { song ->
        SongColumn(
            song = song,
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun SongColumn(
    song: SongEntity,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(55.dp)
                    .aspectRatio(1f)
            ) {
                val painter = rememberImagePainter(song.imageUri)

                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            ) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.h6.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.subtitle2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
