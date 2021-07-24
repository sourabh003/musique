package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musique.adapters.AddSongToPlaylistAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;
import com.example.musique.utils.SongsHandler;

import java.util.ArrayList;

public class AddSongToPlaylist extends AppCompatActivity {

    Database database;
    ImageView btnAddToPlaylist;
    RecyclerView viewSongList;
    ProgressBar loading;
    Playlist currentPlaylist;
    AddSongToPlaylistAdapter adapter;
    ArrayList<Song> trackList = new ArrayList<>();
    ArrayList<String> existingSongsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);
        currentPlaylist = (Playlist) getIntent().getExtras().getSerializable(Constants.PLAYLIST_OBJECT);

        database = new Database(this);
        viewSongList = findViewById(R.id.view_songs_list);
        viewSongList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddSongToPlaylistAdapter(trackList, currentPlaylist.getId(), this);
        viewSongList.setAdapter(adapter);
        btnAddToPlaylist = findViewById(R.id.btn_add_to_playlist);
        loading = findViewById(R.id.loading);
    }

    @Override
    protected void onStart() {
        super.onStart();
        existingSongsList = database.getSongsFromPlaylist(currentPlaylist.getId());
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            trackList.addAll(SongsHandler.getSongs(this));
            adapter.notifyDataSetChanged();
            Functions.showLoading(false, loading);
        }, 1000);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_add_to_playlist:
                addSongsToPlaylist(adapter.getSelectedSongsList());
                break;
        }
    }

    private void addSongsToPlaylist(ArrayList<Song> selectedSongsList) {
        if (selectedSongsList.isEmpty()) {
            Toast.makeText(this, "No Songs Selected!", Toast.LENGTH_SHORT).show();
        } else {
            database.bulkAddSongsToPlaylist(selectedSongsList, currentPlaylist.getId());
            Toast.makeText(this, selectedSongsList.size() + " Songs added to (" + currentPlaylist.getName() + ") Playlist", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}