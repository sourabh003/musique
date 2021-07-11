package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.helpers.Album;
import com.example.musique.helpers.Song;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Constants;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import java.util.ArrayList;


public class AlbumSongs extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Album album;
    ImageView btnBack;
    TextView txtAlbumName, txtAlbumArtist, txtSongsCount;
    ProgressBar loading;
    SwipeRefreshLayout albumTrackListContainer;
    RecyclerView albumTrackListView;

    TrackListAdapter adapter;
    ArrayList<Song> tracksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        album = (Album) getIntent().getSerializableExtra(Constants.ALBUM_OBJECT);

        btnBack = findViewById(R.id.btn_back);
        txtAlbumArtist = findViewById(R.id.txt_album_artist);
        loading = findViewById(R.id.loading);
        txtAlbumArtist.setText(album.getArtist());
        txtAlbumName = findViewById(R.id.txt_album_name);
        txtAlbumName.setText(album.getName());
        txtSongsCount = findViewById(R.id.txt_album_songs_count);
        txtSongsCount.setText(album.getSongsCount() + " Songs");
        albumTrackListView = findViewById(R.id.album_track_list);
        albumTrackListView.setLayoutManager(new LinearLayoutManager(this));
        albumTrackListContainer = findViewById(R.id.album_track_list_container);
        albumTrackListContainer.setOnRefreshListener(this);
        adapter = new TrackListAdapter(tracksList, this);
        albumTrackListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tracksList.clear();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            tracksList.addAll(SongsHandler.getAlbumSongs(album.getId(), this));
            adapter.notifyDataSetChanged();
            Functions.showLoading(false, loading);
        }, 1500);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_play_all:
                playAllSongs();
                break;
        }
    }

    private void playAllSongs() {
        PlayerService.playAllSongs(this, tracksList);
    }

    @Override
    public void onRefresh() {
        tracksList.clear();
        tracksList.addAll(SongsHandler.getAlbumSongs(album.getId(), this));
        adapter.notifyDataSetChanged();
        albumTrackListContainer.setRefreshing(false);
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
    }
}