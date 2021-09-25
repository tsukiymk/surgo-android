package app.surgo.core.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.surgo.data.DatabaseDaos
import app.surgo.data.entities.*

@Database(
    entities = [
        LastRequestEntity::class,
        TrackEntity::class,
        ArtistEntity::class,
        SongEntity::class,
        SongArtistEntry::class,
        AlbumEntity::class,
        AlbumArtistEntry::class,
        VideoEntity::class,
        VideoArtistEntry::class,
        PlaylistEntity::class,
        PlaylistSongEntry::class,
        PopularSongEntry::class,
        //PopularVideoEntry::class
    ],
    views = [],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase(), DatabaseDaos {
    companion object {
        private const val DATABASE_NAME = "surgo_db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
