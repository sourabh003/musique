package com.example.musique;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.PlaylistsAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Functions;

import java.util.ArrayList;

import static com.example.musique.service.PlayerService.currentSong;
import static com.example.musique.service.PlayerService.mediaPlayer;

public class Playlists extends AppCompatActivity {

    public static final String TAG = "Playlists";

    ProgressBar loading;
    GridView playlistsView;
    Database database;

    ArrayList<Playlist> playlists = new ArrayList<>();
    PlaylistsAdapter adapter;
    SwipeRefreshLayout playlistsViewContainer;

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Thread miniPlayerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        loading = findViewById(R.id.loading);
        playlistsView = findViewById(R.id.playlists_view);
        database = new Database(this);
        adapter = new PlaylistsAdapter(this, playlists);
        playlistsView.setAdapter(adapter);
        playlistsViewContainer = findViewById(R.id.playlists_view_container);
        playlistsViewContainer.setOnRefreshListener(this::refreshList);

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
        if (playlists.isEmpty()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                playlists.addAll(database.getPlaylists());
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
            }, 1500);
        }
        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
        }
    }

    public void refreshList() {
        playlists.clear();
        playlists.addAll(database.getPlaylists());
        adapter.notifyDataSetChanged();
        playlistsViewContainer.setRefreshing(false);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_option:
                Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_play_miniplayer:
                updateMiniPlayerAction();
                break;

            case R.id.layout_miniplayer:
                startActivity(new Intent(this, Player.class));
                break;

            case R.id.btn_next_miniplayer:
                PlayerService.nextSong();
                PlayerService.loadMediaPlayer(this);
                initMediaPlayer();
                break;
        }
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