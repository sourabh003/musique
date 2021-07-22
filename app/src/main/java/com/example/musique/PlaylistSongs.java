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
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;
import com.example.musique.services.PlayerService;
import com.example.musique.utility.Constants;
import com.example.musique.utility.Functions;

import java.util.ArrayList;

import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class PlaylistSongs extends AppCompatActivity {

    private static final String TAG = "PlaylistSongs";

    Playlist playlist;
    TextView txtPlaylistName, txtPlaylistCreatedDate, txtPlaylistSongCount;
    SwipeRefreshLayout playlistTracksContainer;
    RecyclerView playlistTracksList;

    ArrayList<Song> playlistTracks = new ArrayList<>();
    TrackListAdapter adapter;
    Database database;
    ProgressBar loading;

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Thread miniPlayerThread;

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
        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
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
                if (!playlistTracks.isEmpty()){
                    PlayerService.playAllSongs(this, playlistTracks);
                } else {
                    Toast.makeText(this, "No Songs in this Playlist!", Toast.LENGTH_SHORT).show();
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