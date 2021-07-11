package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.PlaylistsAdapter;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.utility.Functions;

import java.util.ArrayList;

public class Playlists extends AppCompatActivity {

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
        if (playlists.isEmpty()){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                playlists.addAll(database.getPlaylists());
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
            }, 1500);
        }
    }

    public void refreshList(){
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
        }
    }
}