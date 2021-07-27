package com.example.musique.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.musique.R;
import com.example.musique.adapters.PlaylistTracksAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;
import com.example.musique.services.PlayerService;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;

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
    PlaylistTracksAdapter adapter;
    Database database;
    ProgressBar loading;

    LinearLayout layoutMiniPlayer;
    TextView txtSongNameMiniPlayer, txtSongArtistMiniPlayer;
    ImageView btnPlayMiniPlayer, btnNextMiniPlayer, viewPlaylistImage;
    Thread miniPlayerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        playlist = (Playlist) getIntent().getSerializableExtra(Constants.PLAYLIST_OBJECT);

        txtPlaylistCreatedDate = findViewById(R.id.txt_playlist_created_date);
        txtPlaylistCreatedDate.setText("Created on: " + Functions.getModifiedDate(playlist.getCreatedDate()));
        viewPlaylistImage = findViewById(R.id.view_playlist_image);
        txtPlaylistName = findViewById(R.id.txt_playlist_name);
        txtPlaylistName.setText(Functions.capitalize(playlist.getName()));
        txtPlaylistSongCount = findViewById(R.id.txt_playlist_songs_count);
        playlistTracksContainer = findViewById(R.id.playlist_tracks_container);
        playlistTracksContainer.setOnRefreshListener(this::refreshList);
        playlistTracksList = findViewById(R.id.playlist_tracks_list);
        playlistTracksList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistTracksAdapter(playlistTracks, playlist, this);
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

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                txtPlaylistSongCount.setText(playlistTracks.size() + " Songs");
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
        if (playlistTracks.isEmpty() || playlistTracks.size() != database.getSongsFromPlaylist(playlist.getId()).size()) {
            playlistTracks.clear();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                playlistTracks.addAll(database.getTracksFromPlaylist(this, playlist));
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
                txtPlaylistSongCount.setText(playlistTracks.size() + " Songs");
                Glide.with(this).load(Functions.getPlaylistImage(this, playlist.getImage())).error(R.drawable.playlist).into(viewPlaylistImage);
            }, 1000);
        }
        if (!miniPlayerThread.isAlive()) {
            miniPlayerThread.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlaylist();
    }

    private void updatePlaylist() {
        playlist = database.getUpdatedPlaylist(playlist);
        Glide.with(this).load(Functions.getPlaylistImage(this, playlist.getImage())).error(R.drawable.playlist).into(viewPlaylistImage);
        txtPlaylistName.setText(playlist.getName());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_play_all:
                if (!playlistTracks.isEmpty()) {
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
                PlayerService.nextSong(this);
                initMediaPlayer();
                break;

            case R.id.btn_option:
                PopupMenu menu = new PopupMenu(this, view);
                menu.inflate(R.menu.playlists_menu);
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.item_delete:
                            openPlaylistDeleteDialog();
                            break;

                        case R.id.item_add_song:
                            startActivity(new Intent(this, AddSongToPlaylist.class).putExtra(Constants.PLAYLIST_OBJECT, playlist));
                            break;

                        case R.id.item_edit_playlist:
                            startActivity(
                                    new Intent(this, PlaylistInfo.class)
                                            .putExtra(Constants.PLAYLIST_OBJECT, playlist)
                                            .putExtra(Constants.PLAYLIST_ACTION, Constants.PLAYLIST_ACTION_EDIT)
                            );
                            break;

                    }
                    return false;
                });
                menu.show();
                break;
        }
    }

    public void openPlaylistDeleteDialog() {
        Database database = new Database(this);
        Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog_MinWidth);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_playlist, null);
        dialog.setContentView(view);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_delete);
        Button btnDelete = dialog.findViewById(R.id.btn_confirm_delete);
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Playlist Deleted", Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                database.deletePlaylist(this, playlist);
            }).start();
            finish();
        });
        dialog.show();
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