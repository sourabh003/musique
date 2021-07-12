package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class FolderSongs extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ProgressBar loading;
    SwipeRefreshLayout folderSongsContainer;
    RecyclerView folderSongsView;

    ArrayList<Song> tracksList = new ArrayList<>();
    TrackListAdapter adapter;

    String folder;

    TextView txtFolderName, txtFolderSongCount;
    Button btnPlayAll;

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
        btnPlayAll =findViewById(R.id.btn_play_all);

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
        if (view.getId() == R.id.btn_back) {
            onBackPressed();
        } else {
            PlayerService.playAllSongs(this, tracksList);
        }
    }
}