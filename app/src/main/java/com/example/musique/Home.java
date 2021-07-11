package com.example.musique;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Constants;
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
    Database database;

    ImageView btnCreatePlaylist;
    LinearLayout playListView;

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
        btnCreatePlaylist = findViewById(R.id.btn_create_playlist);

        playListView = findViewById(R.id.playlists_view);

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
        loadPlaylists();
    }

    private void loadPlaylists() {
        if (checkStoragePermission() && playListView.getChildCount() == 0) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                for (Playlist playlist : database.getPlaylists()) {
                    addNewPlaylist(playlist);
                }
            }, 1500);
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
        txtSongNameMiniPlayer.setText(currentSong.getTitle());
        txtSongArtistMiniPlayer.setText(currentSong.getArtist());
        layoutMiniPlayer.setVisibility(View.VISIBLE);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::updateMiniPlayerPlayButton, 100);
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
                    showCreatePlaylistDialog();
                    break;

                case R.id.btn_all_playlists:
                    startActivity(new Intent(this, Playlists.class));
                    break;
            }
        } else {
            Toast.makeText(this, "Please grant Storage Permission first", Toast.LENGTH_SHORT).show();
        }
    }

    public void showCreatePlaylistDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_playlist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText inputPlaylistName = dialog.findViewById(R.id.input_playlist_name);
        ImageView btnClosePlaylistDialog = dialog.findViewById(R.id.btn_close_playlist_dialog);
        Button btnCreatePlaylist = dialog.findViewById(R.id.btn_create_playlist);
        btnCreatePlaylist.setOnClickListener(v -> {
            String name = inputPlaylistName.getText().toString().trim();
            if (!name.isEmpty()) {
                dialog.dismiss();
                Functions.closeKeyboard(this);
                String time = String.valueOf(System.currentTimeMillis());
                String id = database.createPlaylist(name, time);
                addNewPlaylist(new Playlist(id, name, time));
                Toast.makeText(this, "Playlist Created!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show();
            }
        });
        btnClosePlaylistDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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