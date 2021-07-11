package com.example.musique.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "musique.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FAV_SONGS_TABLE = "favourite_songs";
    public static final String SONG_ID = "song_id";
    private static final String CREATE_FAV_SONGS_TABLE = "CREATE TABLE " + FAV_SONGS_TABLE + " (" + SONG_ID + " TEXT NOT NULL UNIQUE)";
    public static final String GET_FAVOURITE_SONGS = "SELECT * FROM " + FAV_SONGS_TABLE;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAV_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FAV_SONGS_TABLE);
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
}
