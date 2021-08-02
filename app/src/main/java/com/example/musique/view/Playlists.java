package com.example.musique.view;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.R;
import com.example.musique.adapters.PlaylistsAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.services.PlayerService;
import com.example.musique.utils.DialogHandlers;
import com.example.musique.utils.Functions;

import java.util.ArrayList;

import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class Playlists extends AppCompatActivity {

    public static final String TAG = "Playlists";

    ProgressBar loading;
    GridView playlistsView;
    Database database;

    ArrayList<Playlist> playlists = new ArrayList<>();
    PlaylistsAdapter adapter;
    SwipeRefreshLayout playlistsViewContainer;

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
        } else {
            if (playlists.size() != database.getPlaylists().size()) {
                refreshList();
            }
        }
    }

    public void refreshList() {
        playlistsViewContainer.setRefreshing(true);
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

            case R.id.btn_create_playlist:
//                startActivity();
                break;
        }
    }
}