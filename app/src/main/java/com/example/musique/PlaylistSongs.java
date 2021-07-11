package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;
import com.example.musique.utility.Constants;
import com.example.musique.utility.Functions;

import java.util.ArrayList;

public class PlaylistSongs extends AppCompatActivity {

    Playlist playlist;
    TextView txtPlaylistName, txtPlaylistCreatedDate, txtPlaylistSongCount;
    SwipeRefreshLayout playlistTracksContainer;
    RecyclerView playlistTracksList;

    ArrayList<Song> playlistTracks = new ArrayList<>();
    TrackListAdapter adapter;
    Database database;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        playlist = (Playlist) getIntent().getSerializableExtra(Constants.PLAYLIST_OBJECT);

        txtPlaylistCreatedDate = findViewById(R.id.txt_playlist_created_date);
        txtPlaylistCreatedDate.setText("Created on: " + Functions.getModifiedDate(playlist.getCreatedDate()));
        txtPlaylistName = findViewById(R.id.txt_playlist_name);
        txtPlaylistName.setText(playlist.getName());
        txtPlaylistSongCount = findViewById(R.id.txt_playlist_songs_count);
        playlistTracksContainer = findViewById(R.id.playlist_tracks_container);
        playlistTracksContainer.setOnRefreshListener(this::refreshList);
        playlistTracksList = findViewById(R.id.playlist_tracks_list);
        playlistTracksList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackListAdapter(playlistTracks, this);
        playlistTracksList.setAdapter(adapter);
        database = new Database(this);
        loading = findViewById(R.id.loading);
    }

    private void refreshList() {
        playlistTracks.clear();
        playlistTracks.addAll(database.getTracksFromPlaylist(this, playlist));
        playlistTracksContainer.setRefreshing(false);
        txtPlaylistSongCount.setText(playlistTracks.size() + " Songs");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playlistTracks.isEmpty()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                playlistTracks.addAll(database.getTracksFromPlaylist(this, playlist));
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
                txtPlaylistSongCount.setText(playlistTracks.size() + " Songs");
            }, 1000);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_option:
                Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_play_all:
                Toast.makeText(this, "Playing all songs", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}