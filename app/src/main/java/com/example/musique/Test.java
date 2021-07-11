package com.example.musique;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        try {
            Functions.saveToInternalStorage(SongsHandler.test(this).toString(), "albums.json", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}