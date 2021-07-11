package com.example.musique.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.musique.helpers.Playlist;
import com.example.musique.utility.Functions;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "musique.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FAV_SONGS_TABLE = "favourite_songs";
    public static final String SONG_ID = "song_id";
    private static final String CREATE_FAV_SONGS_TABLE = "CREATE TABLE " + FAV_SONGS_TABLE + " (" + SONG_ID + " TEXT NOT NULL UNIQUE)";
    public static final String GET_FAVOURITE_SONGS = "SELECT * FROM " + FAV_SONGS_TABLE;
    private static final String PLAYLISTS_TABLE = "playlists";
    private static final String PLAYLIST_ID = "playlist_id";
    private static final String PLAYLIST_NAME = "playlist_name";
    private static final String PLAYLISTS_SONGS_TABLE = "playlist_songs";
    public static final String CREATE_PLAYLISTS_TABLE = "CREATE TABLE " + PLAYLISTS_TABLE + " (" + PLAYLIST_ID + " TEXT NOT NULL UNIQUE, " + PLAYLIST_NAME + " TEXT)";
    public static final String GET_PLAYLISTS = "SELECT * FROM " + PLAYLISTS_TABLE;
    public static final String CREATE_PLAYLISTS_SONGS_TABLE = "CREATE TABLE " + PLAYLISTS_SONGS_TABLE + " (" + PLAYLIST_ID + " TEXT NOT NULL UNIQUE, " + SONG_ID + " TEXT)";
    public static final String GET_PLAYLISTS_SONGS = "SELECT * FROM " + PLAYLISTS_SONGS_TABLE;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAV_SONGS_TABLE);
        db.execSQL(CREATE_PLAYLISTS_TABLE);
        db.execSQL(CREATE_PLAYLISTS_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_SONGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_SONGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE);
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

    public String createPlaylist(String name) {
        String id = Functions.generateID();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAYLIST_ID, id);
        contentValues.put(PLAYLIST_NAME, name.toLowerCase());
        if (getDatabase().insert(PLAYLISTS_TABLE, null, contentValues) != -1){
            return id;
        } else {
            return null;
        }
    }

    public ArrayList<Playlist> getPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Cursor cursor = getDatabase().rawQuery(GET_PLAYLISTS, null);
        while (cursor.moveToNext()) {
            Playlist playlist = new Playlist(
                    cursor.getString(0),
                    cursor.getString(1)
            );
            playlists.add(playlist);
        }
        cursor.close();
        return playlists;
    }

    public void addSongToPlaylist(String playlistID, String songID) {
        new Thread(() -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PLAYLIST_ID, playlistID);
            contentValues.put(SONG_ID, songID);
            getDatabase().insert(PLAYLISTS_SONGS_TABLE, null, contentValues);
        }).start();
    }

    public ArrayList<String> getSongsFromPlaylist(String playlistID) {
        ArrayList<String> playlistSongs = new ArrayList<>();
        Cursor cursor = getDatabase().query(GET_PLAYLISTS_SONGS, null, PLAYLIST_ID + "=?", new String[]{playlistID}, null, null, null);
        while (cursor.moveToNext()) {
            playlistSongs.add(cursor.getString(1));
        }
        cursor.close();
        return playlistSongs;
    }
}
