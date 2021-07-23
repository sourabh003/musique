package com.example.musique.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musique.helpers.Song;

public class CacheHandlers {

    public static final String LATEST_SONG = "LATEST_SONG";
    public static final String SONG_TITLE = "SONG_TITLE";
    public static final String SONG_ID = "SONG_ID";
    public static final String SONG_ARTIST = "SONG_ARTIST";
    public static final String SONG_ALBUM = "SONG_ALBUM";
    public static final String SONG_DATA = "SONG_DATA";
    public static final String SONG_INDEX = "SONG_INDEX";

    public static void updateLatestSong(Context context, Song song, int songIndex) {
        new Thread(() -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(LATEST_SONG, Context.MODE_PRIVATE).edit();
            editor.putString(SONG_TITLE, song.getTitle());
            editor.putString(SONG_ID, song.getId());
            editor.putString(SONG_ARTIST, song.getArtist());
            editor.putString(SONG_DATA, song.getData());
            editor.putString(SONG_ALBUM, song.getAlbum());
            editor.putInt(SONG_INDEX, songIndex);
            editor.apply();
        }).start();
    }

    public static Song getLatestSong(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LATEST_SONG, Context.MODE_PRIVATE);
        return new Song(
                sharedPreferences.getString(SONG_ID, "0"),
                sharedPreferences.getString(SONG_TITLE, null),
                sharedPreferences.getString(SONG_ARTIST, null),
                sharedPreferences.getString(SONG_ALBUM, null),
                sharedPreferences.getString(SONG_DATA, null)
        );
    }

    public static int getLatestSongIndex(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LATEST_SONG, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(SONG_INDEX, 0);
    }
}
