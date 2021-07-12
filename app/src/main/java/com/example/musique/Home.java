package com.example.musique;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Constants;
import com.example.musique.utility.DialogHandlers;
import com.example.musique.utility.Functions;
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
    Thread miniPlayerThread;

    Database database;
    ImageView btnCreatePlaylist;
    LinearLayout playListView;

    boolean backPressed = false;
    final Handler handler = new Handler(Looper.getMainLooper());

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
        btnCreatePlaylist = findViewById(R.id.btn_create_playlist);
        playListView = findViewById(R.id.playlists_view);

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

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkStoragePermission()) {
            Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                requestStoragePermission();
            }).show();
        }
        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
        }
        new Thread(() -> {
            PlayerService.favouriteSongsList.addAll(database.getFavouriteSongs());
        }).start();
        loadPlaylists();
    }

    private void loadPlaylists() {
        if (checkStoragePermission() && playListView.getChildCount() == 0) {
            handler.postDelayed(() -> {
                for (Playlist playlist : database.getPlaylists()) {
                    addNewPlaylist(playlist);
                }
            }, 1000);
        }
    }

    private void addNewPlaylist(Playlist playlist) {
        if (playListView.getChildCount() <= 2) {
            View view = getLayoutInflater().inflate(R.layout.list_item_playlist, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            int margins = Functions.getValueInDP(10, this);
            params.setMargins(margins, 0, 0, margins);
            view.setLayoutParams(params);
            TextView txtPlaylistName = view.findViewById(R.id.txt_playlist_name);
            LinearLayout layoutParent = view.findViewById(R.id.layout_parent);
            txtPlaylistName.setText(playlist.getName());
            layoutParent.setOnClickListener(v -> {
                startActivity(new Intent(this, PlaylistSongs.class).putExtra(Constants.PLAYLIST_OBJECT, playlist));
            });
            playListView.addView(view);
        }
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

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                loadPlaylists();
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

                case R.id.btn_create_playlist:
                    DialogHandlers.showCreatePlaylistDialog(this, this, false);
                    break;

                case R.id.btn_all_playlists:
                    startActivity(new Intent(this, Playlists.class));
                    break;
            }
        } else {
            Toast.makeText(this, "Please grant Storage Permission first", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (backPressed) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
            backPressed = true;
            handler.postDelayed(() -> {
                backPressed = false;
            }, 2000);
        }
    }
}