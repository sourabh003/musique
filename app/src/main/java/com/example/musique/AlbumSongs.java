package com.example.musique;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.example.musique.service.PlayerService.currentSong;
import static com.example.musique.service.PlayerService.mediaPlayer;


public class AlbumSongs extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "AlbumSongs";
    Album album;
    ImageView btnBack;
    TextView txtAlbumName, txtAlbumArtist, txtSongsCount;
    ProgressBar loading;
    SwipeRefreshLayout albumTrackListContainer;
    RecyclerView albumTrackListView;

    TrackListAdapter adapter;
    ArrayList<Song> tracksList = new ArrayList<>();

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Thread miniPlayerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        album = (Album) getIntent().getSerializableExtra(Constants.ALBUM_OBJECT);

        btnBack = findViewById(R.id.btn_back);
        txtAlbumArtist = findViewById(R.id.txt_playlist_created_date);
        loading = findViewById(R.id.loading);
        txtAlbumArtist.setText(album.getArtist());
        txtAlbumName = findViewById(R.id.txt_album_name);
        txtAlbumName.setText(album.getName());
        txtSongsCount = findViewById(R.id.txt_playlist_songs_count);
        txtSongsCount.setText(album.getSongsCount() + " Songs");
        albumTrackListView = findViewById(R.id.album_track_list);
        albumTrackListView.setLayoutManager(new LinearLayoutManager(this));
        albumTrackListContainer = findViewById(R.id.album_track_list_container);
        albumTrackListContainer.setOnRefreshListener(this);
        adapter = new TrackListAdapter(tracksList, this);
        albumTrackListView.setAdapter(adapter);

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
        tracksList.clear();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            tracksList.addAll(SongsHandler.getAlbumSongs(album.getId(), this));
            adapter.notifyDataSetChanged();
            Functions.showLoading(false, loading);
        }, 1500);

        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_play_all:
                if (!tracksList.isEmpty()){
                    PlayerService.playAllSongs(this, tracksList);
                } else {
                    Toast.makeText(this, "No Songs in this Album!", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onRefresh() {
        tracksList.clear();
        tracksList.addAll(SongsHandler.getAlbumSongs(album.getId(), this));
        adapter.notifyDataSetChanged();
        albumTrackListContainer.setRefreshing(false);
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
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