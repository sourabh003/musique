package com.example.musique.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.musique.helpers.Album;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;
import com.example.musique.utils.Functions;
import com.example.musique.utils.SongsHandler;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public static final String TAG = "Database";
    private static final String DATABASE_NAME = "musique.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FAV_SONGS_TABLE = "favourite_songs";
    public static final String SONG_ID = "song_id";
    private static final String CREATE_FAV_SONGS_TABLE = "CREATE TABLE " + FAV_SONGS_TABLE + " (" + SONG_ID + " TEXT NOT NULL UNIQUE)";
    public static final String GET_FAVOURITE_SONGS = "SELECT * FROM " + FAV_SONGS_TABLE;
    private static final String PLAYLISTS_TABLE = "playlists";
    private static final String PLAYLIST_ID = "playlist_id";
    private static final String PLAYLIST_ENTRY_ID = "entry_id";
    private static final String PLAYLIST_NAME = "playlist_name";
    private static final String PLAYLISTS_SONGS_TABLE = "playlist_songs";
    private static final String PLAYLIST_CREATED = "playlist_created_on";
    private static final String PLAYLIST_IMAGE = "playlist_image";
    public static final String CREATE_PLAYLISTS_TABLE = "CREATE TABLE " + PLAYLISTS_TABLE + " (" + PLAYLIST_ID + " TEXT NOT NULL UNIQUE, " + PLAYLIST_NAME + " TEXT, " + PLAYLIST_CREATED + " TEXT, " + PLAYLIST_IMAGE + " TEXT)";
    public static final String GET_PLAYLISTS = "SELECT * FROM " + PLAYLISTS_TABLE;
    public static final String CREATE_PLAYLISTS_SONGS_TABLE = "CREATE TABLE " + PLAYLISTS_SONGS_TABLE + " (" + PLAYLIST_ENTRY_ID + " TEXT NOT NULL UNIQUE , " + PLAYLIST_ID + " TEXT , " + SONG_ID + " TEXT)";
    public static final String GET_PLAYLISTS_SONGS = "SELECT * FROM " + PLAYLISTS_SONGS_TABLE;
    public static final String ALBUMS_TABLE = "albums";
    public static final String ALBUM_ID = "album_id";
    public static final String ALBUM_ART = "album_art";
    public static final String CREATE_ALBUMS_TABLE = "CREATE TABLE " + ALBUMS_TABLE + " (" + ALBUM_ID + " TEXT NOT NULL UNIQUE, " + ALBUM_ART + " TEXT)";
    public static final String GET_ALBUM_ART = "SELECT * FROM " + ALBUMS_TABLE;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAV_SONGS_TABLE);
        db.execSQL(CREATE_PLAYLISTS_TABLE);
        db.execSQL(CREATE_PLAYLISTS_SONGS_TABLE);
        db.execSQL(CREATE_ALBUMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_SONGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_SONGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALBUMS_TABLE);
        onCreate(db);
    }

    SQLiteDatabase getDatabase() {
        return this.getWritableDatabase();
    }

    public void addSongToFavourites(String songID) {
        new Thread(() -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SONG_ID, songID);
            getDatabase().insert(FAV_SONGS_TABLE, null, contentValues);
        }).start();
    }

    public List<String> getFavouriteSongs() {
        List<String> favSongs = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery(GET_FAVOURITE_SONGS, null);
        while (cursor.moveToNext()) {
            favSongs.add(cursor.getString(0));
        }
        cursor.close();
        return favSongs;
    }

    public void removeSongFromFavourites(String songID) {
        new Thread(() -> getDatabase().delete(FAV_SONGS_TABLE, SONG_ID + "=?", new String[]{songID})).start();
    }

    public void createPlaylist(Playlist playlist) {
        new Thread(() -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLAYLIST_ID, playlist.getId());
            contentValues.put(PLAYLIST_NAME, playlist.getName().toLowerCase());
            contentValues.put(PLAYLIST_CREATED, playlist.getCreatedDate());
            contentValues.put(PLAYLIST_IMAGE, playlist.getImage());
            getDatabase().insert(PLAYLISTS_TABLE, null, contentValues);
        }).start();
    }

    public void deletePlaylist(Context context, Playlist playlist) {
        new Thread(() -> {
            Functions.deleteImageFromInternalStorage(context, playlist.getImage());
            getDatabase().delete(PLAYLISTS_TABLE, PLAYLIST_ID + "=?", new String[]{playlist.getId()});
            getDatabase().delete(PLAYLISTS_SONGS_TABLE, PLAYLIST_ID + "=?", new String[]{playlist.getId()});
        }).start();
    }

    public void updatePlaylist(Playlist playlist) {
        new Thread(() -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLAYLIST_NAME, playlist.getName());
            contentValues.put(PLAYLIST_IMAGE, playlist.getImage());
            getDatabase().update(PLAYLISTS_TABLE, contentValues, PLAYLIST_ID + "=?", new String[]{playlist.getId()});
        }).start();
    }

    public ArrayList<Playlist> getPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery(GET_PLAYLISTS, null);
        while (cursor.moveToNext()) {
            Playlist playlist = new Playlist(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            playlists.add(playlist);
        }
        cursor.close();
        return playlists;
    }

    public void bulkAddSongsToPlaylist(ArrayList<Song> trackList, String playlistID) {
        new Thread(() -> {
            for (Song song : trackList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PLAYLIST_ENTRY_ID, getPlaylistEntryId(song.getId(), playlistID));
                contentValues.put(PLAYLIST_ID, playlistID);
                contentValues.put(SONG_ID, song.getId());
                getDatabase().insert(PLAYLISTS_SONGS_TABLE, null, contentValues);
            }
        }).start();
    }

    public Playlist getUpdatedPlaylist(Playlist playlist) {
        Playlist updatedPlaylist = null;
        Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + PLAYLISTS_TABLE + " WHERE " + PLAYLIST_ID + "=?", new String[]{playlist.getId()});
        while (cursor.moveToNext()) {
            updatedPlaylist = new Playlist(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
        }
        cursor.close();
        return updatedPlaylist;
    }

    public void addSongToPlaylist(ArrayList<String> playlistIDs, String songID) {
        new Thread(() -> {
            for (String playlistID : playlistIDs) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PLAYLIST_ENTRY_ID, getPlaylistEntryId(songID, playlistID));
                contentValues.put(PLAYLIST_ID, playlistID);
                contentValues.put(SONG_ID, songID);
                getDatabase().insert(PLAYLISTS_SONGS_TABLE, null, contentValues);
            }
        }).start();
    }

    public void deleteSongFromPlaylist(String songID, String playlistID) {
        new Thread(() -> getDatabase().delete(PLAYLISTS_SONGS_TABLE, PLAYLIST_ID + "=? AND " + SONG_ID + "=?", new String[]{playlistID, songID})).start();
    }

    public ArrayList<String> getSongsFromPlaylist(String playlistID) {
        ArrayList<String> playlistSongs = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery("SELECT " + SONG_ID + " FROM " + PLAYLISTS_SONGS_TABLE + " WHERE " + PLAYLIST_ID + "=?", new String[]{playlistID});
        while (cursor.moveToNext()) {
            Log.d(TAG, "getSongsFromPlaylist: " + cursor.getString(0));
            playlistSongs.add(cursor.getString(0));
        }
        cursor.close();
        return playlistSongs;
    }

    public ArrayList<Song> getTracksFromPlaylist(Context context, Playlist playlist) {
        ArrayList<String> list = getSongsFromPlaylist(playlist.getId());
        return SongsHandler.getSongsByID(context, list);
    }

    public String getPlaylistEntryId(String songID, String playlistID) {
        return playlistID + songID;
    }

    public void updateAlbumArt(Context context, Album album) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALBUM_ID, album.getId());
        contentValues.put(ALBUM_ART, album.getArt());
        Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + ALBUMS_TABLE + " WHERE " + ALBUM_ID + "=?", new String[]{album.getId()});
        if (cursor.getCount() == 0) {
            getDatabase().insert(ALBUMS_TABLE, null, contentValues);
            Toast.makeText(context, "Inserting...", Toast.LENGTH_SHORT).show();
        } else {
            getDatabase().update(ALBUMS_TABLE, contentValues, ALBUM_ID + "=?", new String[]{album.getId()});
            Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}
