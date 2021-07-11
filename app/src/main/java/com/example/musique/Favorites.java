package com.example.musique;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.helpers.Song;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import java.util.ArrayList;

import static com.example.musique.service.PlayerService.isLibraryRepeating;
import static com.example.musique.service.PlayerService.mediaPlayer;

public class Favorites extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ImageView btnBack;
    Button playAll;
    TextView txtFavSongsCounter;
    SwipeRefreshLayout favSongsListContainer;
    RecyclerView favSongsListView;
    ProgressBar loading;

    ArrayList<Song> tracksList = new ArrayList<>();
    TrackListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favSongsListView = findViewById(R.id.fav_songs_list_view);
        loading = findViewById(R.id.loading);
        favSongsListContainer = findViewById(R.id.fav_song_list_container);
        favSongsListContainer.setOnRefreshListener(this);
        txtFavSongsCounter = findViewById(R.id.txt_fav_songs_count);
        btnBack = findViewById(R.id.btn_back);
        playAll = findViewById(R.id.btn_play_all);
        adapter = new TrackListAdapter(tracksList, this);
        favSongsListView.setLayoutManager(new LinearLayoutManager(this));
        favSongsListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (tracksList.isEmpty()){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                tracksList.addAll(SongsHandler.getFavouriteSongs(this));
                adapter.notifyDataSetChanged();
                txtFavSongsCounter.setText(tracksList.size() + " Songs");
                Functions.showLoading(false, loading);
            }, 1500);
        }
    }

    @Override
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
        tracksList.addAll(SongsHandler.getFavouriteSongs(this));
        adapter.notifyDataSetChanged();
        txtFavSongsCounter.setText(tracksList.size() + " Songs");
        favSongsListContainer.setRefreshing(false);
    }
}