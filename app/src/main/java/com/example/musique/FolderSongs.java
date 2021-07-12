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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.helpers.Song;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Constants;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import java.util.ArrayList;

import static com.example.musique.service.PlayerService.currentSong;
import static com.example.musique.service.PlayerService.mediaPlayer;

public class FolderSongs extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FolderSongs";
    ProgressBar loading;
    SwipeRefreshLayout folderSongsContainer;
    RecyclerView folderSongsView;

    ArrayList<Song> tracksList = new ArrayList<>();
    TrackListAdapter adapter;

    String folder;

    TextView txtFolderName, txtFolderSongCount;
    Button btnPlayAll;

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Thread miniPlayerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_songs);
        folder = getIntent().getExtras().getString(Constants.FOLDER);


        loading = findViewById(R.id.loading);
        txtFolderName = findViewById(R.id.txt_folder_name);
        txtFolderName.setText(Functions.decryptName(folder));
        txtFolderSongCount = findViewById(R.id.txt_folder_songs_count);
        folderSongsContainer = findViewById(R.id.folder_track_list_container);
        folderSongsView = findViewById(R.id.folder_track_list);
        folderSongsView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrackListAdapter(tracksList, this);
        folderSongsView.setAdapter(adapter);
        folderSongsContainer.setOnRefreshListener(this);
        btnPlayAll = findViewById(R.id.btn_play_all);

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
    public void onStart() {
        super.onStart();
        if (tracksList.isEmpty()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            Functions.showLoading(true, loading);
            handler.postDelayed(() -> {
                tracksList.addAll(SongsHandler.getAlbumSongs(Functions.decryptID(folder), this));
                txtFolderSongCount.setText(tracksList.size() + " Songs");
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
            }, 1500);
        }

        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
        }
    }

    @Override
    public void onRefresh() {
        tracksList.clear();
        Functions.showLoading(true, loading);
        tracksList.addAll(SongsHandler.getAlbumSongs(Functions.decryptID(folder), this));
        txtFolderSongCount.setText(tracksList.size() + " Songs");
        adapter.notifyDataSetChanged();
        Functions.showLoading(false, loading);
        folderSongsContainer.setRefreshing(false);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_play_all:
                if (!tracksList.isEmpty()){
                    PlayerService.playAllSongs(this, tracksList);
                } else {
                    Toast.makeText(this, "No Songs in this Folder!", Toast.LENGTH_SHORT).show();
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