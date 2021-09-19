package app.surgo.data.entities

import app.surgo.data.resultentities.SongWithArtists

interface ArtistEntry : IndexedEntity {
    val artistId: Long
}

interface EntryWithArtist<E : ArtistEntry> {
    var entry: E
    var artists: ArtistEntity
}

interface SongEntry : IndexedEntity {
    val songId: Long
}

interface EntryWithSong<E : SongEntry> {
    var entry: E
    var songWithArtists: SongWithArtists
}

interface PlaylistEntry : IndexedEntity {
    val playlistId: Long
}

interface EntryWithPlaylist<E : PlaylistEntry> {
    var entry: E
    var playlist: PlaylistEntity
}
