package com.example.musique.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musique.helpers.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SongsHandler {

    public static JSONArray getAlbumArts(Context context) throws JSONException {
        String TAG = "SongsHandler";
        JSONArray jsonArray = new JSONArray();
        HashMap<String, Drawable> albumArtList = new HashMap<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < cursor.getColumnCount(); i++){
                    jsonObject.put(cursor.getColumnName(i), cursor.getString(i));
                }
                jsonArray.put(jsonObject);
            }
            cursor.close();
        }
        return jsonArray;
    }

    public static ArrayList<Song> getSongs(Context context) {
        String TAG = "SongsHandler";
        ArrayList<Song> trackList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection =
                MediaStore.Audio.Media.IS_MUSIC + "=? AND "
                        + MediaStore.Audio.Media.IS_RINGTONE + "=? AND "
                        + MediaStore.Audio.Media.IS_ALARM + "=? AND "
                        + MediaStore.Audio.Media.IS_NOTIFICATION + "=?";
        String[] selectionArgs = new String[]{"1", "0", "0", "0"};
        Cursor cursor = context.getContentResolver().query(uri, new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.OWNER_PACKAGE_NAME,
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String album = cursor.getString(1);
                String title = cursor.getString(2);
                String data = cursor.getString(3);
                String artist = cursor.getString(4);
                if (artist.equals("<unknown>")){
                    artist = "Unknown Artist";
                }
                if (!album.toLowerCase().equals("ringtones")
                        || !album.toLowerCase().equals("ringtone")
                        || !album.toLowerCase().equals("call_rec")) {
                    if (!title.contains("ringtone")) {
                        Song song = new Song(id, title, artist, album, data);
                        trackList.add(song);
                    }
                }
            }
            cursor.close();
        }
        return trackList;
    }
}
