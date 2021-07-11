package com.example.musique;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.database.Database;
import com.example.musique.service.PlayerService;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static com.example.musique.service.PlayerService.currentSong;
import static com.example.musique.service.PlayerService.mediaPlayer;

public class Home extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Home";
    LinearLayout layoutLibraries, layoutFolders, layoutFavourites;
    LinearLayout layoutParent;
    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = new Database(this);
        layoutLibraries = findViewById(R.id.layout_libraries);
        layoutLibraries.setOnClickListener(this);
        layoutFavourites = findViewById(R.id.layout_favourites);
        layoutFavourites.setOnClickListener(this);
        layoutFolders = findViewById(R.id.layout_folders);
        layoutFolders.setOnClickListener(this);
        layoutParent = findViewById(R.id.layout_parent);
        layoutMiniPlayer = findViewById(R.id.layout_miniplayer);
        txtSongNameMiniPlayer = findViewById(R.id.song_title_miniplayer);
        txtSongArtistMiniPlayer = findViewById(R.id.song_artist_miniplayer);
        btnPlayMiniPlayer = findViewById(R.id.btn_play_miniplayer);
        btnPlayMiniPlayer.setOnClickListener(this);
        btnNextMiniPlayer = findViewById(R.id.btn_next_miniplayer);
        btnNextMiniPlayer.setOnClickListener(this);

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
    protected void onStart() {
        super.onStart();
        if (!checkStoragePermission()) {
            Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                requestStoragePermission();
            }).show();
        }
        if (mediaPlayer != null) {
            initMediaPlayer();
        } else {
            layoutMiniPlayer.setVisibility(View.GONE);
        }
        new Thread(() -> {
            PlayerService.favouriteSongsList.addAll(database.getFavouriteSongs());
        }).start();
    }

    private void initMediaPlayer() {
        txtSongNameMiniPlayer.setText(currentSong.getTitle());
        txtSongArtistMiniPlayer.setText(currentSong.getArtist());
        layoutMiniPlayer.setVisibility(View.VISIBLE);
        updateMiniPlayerPlayButton();
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                    requestStoragePermission();
                }).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    private boolean checkStoragePermission() {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        if (checkStoragePermission()) {
            switch (v.getId()) {
                case R.id.layout_libraries:
                    startActivity(new Intent(getApplicationContext(), Library.class));
                    break;

                case R.id.layout_folders:
                    startActivity(new Intent(this, Folders.class));
                    break;

                case R.id.layout_favourites:
                    startActivity(new Intent(this, Favorites.class));
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
        } else {
            Toast.makeText(this, "Please grant Storage Permission first", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMiniPlayerAction() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        updateMiniPlayerPlayButton();
    }
}