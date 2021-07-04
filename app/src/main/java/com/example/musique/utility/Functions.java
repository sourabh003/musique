package com.example.musique.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.example.musique.helpers.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Functions {
    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showLoading(boolean show, ProgressBar loading){
        if (show){
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.INVISIBLE);
        }
    }

    public static void saveToInternalStorage(String data, String filename, Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        //default mode is PRIVATE, can be APPEND etc.
        fos.write(data.getBytes());
        fos.close();
    }

    public static void updateLatestSong(Context context, Song song) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.CURRENT_PLAYING_SONG, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.SONG_TITLE, song.getTitle());
        editor.putString(Constants.SONG_ID, song.getId());
        editor.putString(Constants.SONG_ARTIST, song.getArtist());
        editor.putString(Constants.SONG_DATA, song.getData());
        editor.putString(Constants.SONG_ALBUM, song.getAlbum());
        editor.apply();
    }

    public static Song getLatestSong(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.CURRENT_PLAYING_SONG, Context.MODE_PRIVATE);
        return new Song(
                sharedPreferences.getString(Constants.SONG_ID, "0"),
                sharedPreferences.getString(Constants.SONG_TITLE, null),
                sharedPreferences.getString(Constants.SONG_ARTIST, null),
                sharedPreferences.getString(Constants.SONG_ALBUM, null),
                sharedPreferences.getString(Constants.SONG_DATA, null)
        );
    }

    @SuppressLint("DefaultLocale")
    public static String getModifiedDuration(int time) {
        String duration;
        if (time > 3600000) {
            duration = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        } else {
            duration = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        }
        return duration;
    }
}