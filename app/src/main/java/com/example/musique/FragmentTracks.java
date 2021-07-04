package com.example.musique;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.musique.adapters.TrackListAdapter;
import com.example.musique.utility.Functions;
import com.example.musique.utility.SongsHandler;

import static com.example.musique.service.PlayerService.tracksList;

public class FragmentTracks extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    Activity activity;
    RecyclerView tracksListView;
    ProgressBar loading;
    TrackListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        activity = getActivity();
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        tracksListView = view.findViewById(R.id.tracks_list_view);
        tracksListView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new TrackListAdapter(tracksList, activity);
        tracksListView.setAdapter(adapter);
        loading = view.findViewById(R.id.loading);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Functions.showLoading(true, loading);
        if (SongsHandler.getSongs(activity).size() != tracksList.size()) {
            tracksList.addAll(SongsHandler.getSongs(activity));
        }
        adapter.notifyDataSetChanged();
        Functions.showLoading(false, loading);
    }

    @Override
    public void onRefresh() {
        tracksList.clear();
        Functions.showLoading(true, loading);
        tracksList.addAll(SongsHandler.getSongs(activity));
        adapter.notifyDataSetChanged();
        Functions.showLoading(false, loading);
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(activity, "Refreshed", Toast.LENGTH_SHORT).show();
    }
}