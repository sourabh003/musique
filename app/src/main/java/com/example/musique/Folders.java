package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.FolderListAdapter;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import java.util.ArrayList;

public class Folders extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ImageView btnBack;
    ProgressBar loading;
    SwipeRefreshLayout folderListContainer;
    RecyclerView folderListView;
    ArrayList<String> folderList = new ArrayList<>();
    FolderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);

        btnBack = findViewById(R.id.btn_back);
        folderListContainer = findViewById(R.id.folder_list_container);
        folderListView = findViewById(R.id.folder_list_view);
        loading = findViewById(R.id.loading);
        folderListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FolderListAdapter(folderList, this);
        folderListView.setAdapter(adapter);
        folderListContainer.setOnRefreshListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (folderList.isEmpty()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            Functions.showLoading(true, loading);
            handler.postDelayed(() -> {
                folderList.addAll(SongsHandler.getFolders(this));
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
            }, 1500);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onRefresh() {
        folderList.clear();
        Functions.showLoading(true, loading);
        folderList.addAll(SongsHandler.getFolders(this));
        adapter.notifyDataSetChanged();
        Functions.showLoading(false, loading);
        folderListContainer.setRefreshing(false);
    }
}