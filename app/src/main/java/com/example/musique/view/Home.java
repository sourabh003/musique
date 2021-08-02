package com.example.musique.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musique.R;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.services.PlayerService;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class Home extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    String TAG = "Home";
    LinearLayout layoutLibraries, layoutFolders, layoutFavourites;
    LinearLayout layoutParent;

    ProgressBar loading;
    TextView viewNoPlaylists;

    Database database;
    ImageView btnCreatePlaylist;
    LinearLayout playListView;

    boolean backPressed = false;
    final Handler handler = new Handler(Looper.getMainLooper());

    PlayerService musicService;

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
        loading = findViewById(R.id.loading);
        viewNoPlaylists = findViewById(R.id.view_no_playlists);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        if (!checkStoragePermission()) {
            Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                requestStoragePermission();
            }).show();
        }
        new Thread(() -> {
            PlayerService.favouriteSongsList.addAll(database.getFavouriteSongs());
        }).start();
        refreshPlaylists();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        PlayerService.CustomBinder binder = (PlayerService.CustomBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void loadPlaylists() {
        if (checkStoragePermission()) {
            Functions.showLoading(true, loading);
            viewNoPlaylists.setVisibility(View.GONE);
            playListView.setVisibility(View.GONE);
            findViewById(R.id.btn_all_playlists).setVisibility(View.GONE);
            if (playListView.getChildCount() == 0 || playListView.getChildCount() != database.getPlaylists().size()) {
                playListView.removeAllViews();
                handler.postDelayed(() -> {
                    if (database.getPlaylists().size() != 0) {
                        for (Playlist playlist : database.getPlaylists()) {
                            addNewPlaylist(playlist);
                        }
                        Functions.showLoading(false, loading);
                        playListView.setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_all_playlists).setVisibility(View.VISIBLE);
                    } else {
                        Functions.showLoading(false, loading);
                        playListView.setVisibility(View.GONE);
                        viewNoPlaylists.setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_all_playlists).setVisibility(View.GONE);
                    }

                }, 1000);
            } else {
                Functions.showLoading(false, loading);
                playListView.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_all_playlists).setVisibility(View.VISIBLE);
            }
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
            FrameLayout layoutParent = view.findViewById(R.id.layout_parent);
            ImageView viewPlaylistImage = view.findViewById(R.id.view_playlist_image);
            Glide.with(this).load(Functions.getPlaylistImage(this, playlist.getImage())).error(R.drawable.playlist).into(viewPlaylistImage);
            txtPlaylistName.setText(Functions.capitalize(playlist.getName()));
            layoutParent.setOnClickListener(v -> {
                startActivity(new Intent(this, PlaylistSongs.class).putExtra(Constants.PLAYLIST_OBJECT, playlist));
            });
            playListView.addView(view);
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

                case R.id.btn_create_playlist:
                    startActivity(
                            new Intent(this, PlaylistInfo.class)
                                    .putExtra(Constants.PLAYLIST_ACTION, Constants.PLAYLIST_ACTION_CREATE)
                    );
                    break;

                case R.id.btn_all_playlists:
                    startActivity(new Intent(this, Playlists.class));
                    break;

                case R.id.btn_refresh_playlist:
                    refreshPlaylists();
                    break;
            }
        } else {
            Toast.makeText(this, "Please grant Storage Permission first", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshPlaylists() {
        playListView.removeAllViews();
        loadPlaylists();
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