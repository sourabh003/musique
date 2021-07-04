package com.example.musique;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        try {
            Functions.saveToInternalStorage(SongsHandler.getAlbumArts(this).toString(), "albums.json", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void getSongsFromStorage() throws JSONException {
//        ContentResolver contentResolver = getContentResolver();
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        JSONArray array = new JSONArray();
//        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                JSONObject songObject = new JSONObject();
//                for (int i = 0; i < cursor.getColumnCount(); i++) {
//                    songObject.put(cursor.getColumnName(i), cursor.getString(i));
//                }
//                array.put(songObject);
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//        try {
//            Functions.saveToInternalStorage(array.toString(), "test.json", this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}