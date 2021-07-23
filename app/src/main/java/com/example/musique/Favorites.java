package com.example.musique;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.helpers.Song;
import com.example.musique.services.PlayerService;
import com.example.musique.utils.Functions;
import com.example.musique.utils.SongsHandler;

import java.util.ArrayList;

import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class Favorites extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Favorites";
    ImageView btnBack;
    Button playAll;
    TextView txtFavSongsCounter;
    SwipeRefreshLayout favSongsListContainer;
    RecyclerView favSongsListView;
    ProgressBar loading;

    ArrayList<Song> tracksList = new ArrayList<>();
    TrackListAdapter adapter;

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Thread miniPlayerThread;

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

        layoutMiniPlayer = findViewById(R.id.layout_miniplayer);
        txtSongNameMiniPlayer = findViewById(R.id.song_title_miniplayer);
        txtSongArtistMiniPlayer = findViewById(R.id.song_artist_miniplayer);
        btnPlayMiniPlayer = findViewById(R.id.btn_play_miniplayer);
        btnNextMiniPlayer = findViewById(R.id.btn_next_miniplayer);

        miniPlayerThread = new Thread(() -> {
            try {
                while (true) {
                    runOnUiThread(this::initMediaPlayer);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "onCreate: Mini Player Thread Exception", e);
                e.printStackTrace();
            }
        });
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
        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
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

            case R.id.btn_play_miniplayer:
                updateMiniPlayerAction();
                break;

            case R.id.layout_miniplayer:
                startActivity(new Intent(this, Player.class));
                break;

            case R.id.btn_next_miniplayer:
                PlayerService.nextSong(this);
                initMediaPlayer();
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateMiniPlayerPlayButton() {
        if (mediaPlayer.isPlaying()) {
            btnPlayMiniPlayer.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
        } else {
            btnPlayMiniPlayer.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_arrow_24));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        miniPlayerThread.interrupt();
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            layoutMiniPlayer.setVisibility(View.VISIBLE);
            txtSongNameMiniPlayer.setText(currentSong.getTitle());
            txtSongArtistMiniPlayer.setText(currentSong.getArtist());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::updateMiniPlayerPlayButton, 100);
        } else {
            layoutMiniPlayer.setVisibility(View.GONE);
        }
    }

    private void updateMiniPlayerAction() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        updateMiniPlayerPlayButton();
    }
}