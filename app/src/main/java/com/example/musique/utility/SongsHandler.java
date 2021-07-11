package com.example.musique.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musique.FolderSongs;
import com.example.musique.database.Database;
import com.example.musique.helpers.Album;
import com.example.musique.helpers.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SongsHandler {

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
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String album = cursor.getString(1);
                String title = cursor.getString(2);
                String data = cursor.getString(3);
                String artist = cursor.getString(4);
                if (artist.equals(Constants.UNKNOWN_ARTIST)) {
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

    public static List<Album> getAlbums(Context context) {
        List<Album> albums = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, new String[]{
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        }, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String artist = cursor.getString(2);
            String count = cursor.getString(3);
            if (artist.equals(Constants.UNKNOWN_ARTIST)) {
                artist = "Unknown Artist";
            }
            Album album = new Album(
                    id, name, artist, count
            );
            albums.add(album);
        }
        cursor.close();
        return albums;
    }

    public static ArrayList<Song> getAlbumSongs(String albumID, Context context) {
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
                MediaStore.Audio.Media.ALBUM_ID
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(5).equals(albumID)) {
                    String id = cursor.getString(0);
                    String album = cursor.getString(1);
                    String title = cursor.getString(2);
                    String data = cursor.getString(3);
                    String artist = cursor.getString(4);
                    if (artist.equals(Constants.UNKNOWN_ARTIST)) {
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
            }
            cursor.close();
        }
        return trackList;
    }

    public static ArrayList<Song> getSongsByID(Context context, ArrayList<String> songIDList){
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
                MediaStore.Audio.Media.ALBUM_ID
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                if (songIDList.contains(id)) {
                    String album = cursor.getString(1);
                    String title = cursor.getString(2);
                    String data = cursor.getString(3);
                    String artist = cursor.getString(4);
                    if (artist.equals(Constants.UNKNOWN_ARTIST)) {
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
            }
            cursor.close();
        }
        return trackList;
    }

    public static ArrayList<Song> getFavouriteSongs(Context context) {
        String TAG = "SongsHandler";
        ArrayList<Song> trackList = new ArrayList<>();
        List<String> favorites = new Database(context).getFavouriteSongs();
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
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                if (favorites.contains(id)) {
                    String album = cursor.getString(1);
                    String title = cursor.getString(2);
                    String data = cursor.getString(3);
                    String artist = cursor.getString(4);
                    if (artist.equals(Constants.UNKNOWN_ARTIST)) {
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
            }
            cursor.close();
        }
        return trackList;
    }

    public static ArrayList<String> getFolders(Context context) {
        String TAG = "SongsHandler";
        ArrayList<String> trackList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection =
                MediaStore.Audio.Media.IS_MUSIC + "=? AND "
                        + MediaStore.Audio.Media.IS_RINGTONE + "=? AND "
                        + MediaStore.Audio.Media.IS_ALARM + "=? AND "
                        + MediaStore.Audio.Media.IS_NOTIFICATION + "=?";
        String[] selectionArgs = new String[]{"1", "0", "0", "0"};
        Cursor cursor = context.getContentResolver().query(uri, new String[]{
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
        }, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String album = cursor.getString(1);
                if (!album.toLowerCase().equals("ringtones")
                        || !album.toLowerCase().equals("ringtone")
                        || !album.toLowerCase().equals("call_rec")) {
                    if (!trackList.contains(Functions.encryptName(id, album))) {
                        trackList.add(Functions.encryptName(id, album));
                    }
                }
            }
            cursor.close();
        }
        return trackList;
    }

//    public static ArrayList<Song> getSongsFromFolders() {
//
//    }


    public static JSONArray test(Context context) throws JSONException {
        JSONArray albums = new JSONArray();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null);
        while (cursor.moveToNext()) {
            JSONObject album = new JSONObject();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                album.put(cursor.getColumnName(i), cursor.getString(i));
            }
            albums.put(album);
        }
        cursor.close();
        return albums;
    }
}
