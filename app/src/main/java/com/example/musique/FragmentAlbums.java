package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.AlbumListAdapter;
import com.example.musique.helpers.Album;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import java.util.ArrayList;

public class FragmentAlbums extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    GridView albumListView;
    ProgressBar loading;
    ArrayList<Album> albumsList = new ArrayList<>();
    AlbumListAdapter adapter;
    SwipeRefreshLayout albumListContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        loading = view.findViewById(R.id.loading);
        albumListView = view.findViewById(R.id.album_list);
        adapter = new AlbumListAdapter(getActivity(), albumsList);
        albumListView.setAdapter(adapter);
        albumListContainer = view.findViewById(R.id.album_list_container);
        albumListContainer.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (albumsList.isEmpty()){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                Functions.showLoading(true, loading);
                albumsList.clear();
                albumsList.addAll(SongsHandler.getAlbums(getActivity()));
                adapter.notifyDataSetChanged();
                Functions.showLoading(false, loading);
            }, 1500);
        }
    }

    @Override
    public void onRefresh() {
        albumsList.clear();
        adapter.notifyDataSetChanged();
        albumsList.addAll(SongsHandler.getAlbums(getActivity()));
        adapter.notifyDataSetChanged();
        albumListContainer.setRefreshing(false);
        Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
    }
}