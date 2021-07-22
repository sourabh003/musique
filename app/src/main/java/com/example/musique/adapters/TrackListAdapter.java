package com.example.musique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musique.Player;
import com.example.musique.R;
import com.example.musique.helpers.Song;
import com.example.musique.services.PlayerService;

import java.util.ArrayList;


public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    String TAG = "TrackListAdapter";
    ArrayList<Song> trackList;
    Context context;

    public TrackListAdapter(ArrayList<Song> trackList, Context context) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_tracks, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = trackList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.artistTitle.setText(song.getArtist());
        holder.parentView.setOnClickListener(v -> {
            PlayerService.currentSong = song;
            PlayerService.songIndex = position;
            PlayerService.tracksList.clear();
            PlayerService.tracksList.addAll(trackList);
            context.startActivity(new Intent(context, Player.class));
        });

    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout parentView;
        public TextView songTitle, artistTitle;
        public ImageView btnOption, albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView.findViewById(R.id.parent_view);
            this.songTitle = itemView.findViewById(R.id.song_title);
            this.albumArt = itemView.findViewById(R.id.album_art);
            this.artistTitle = itemView.findViewById(R.id.sont_artist);
            this.btnOption = itemView.findViewById(R.id.btn_option);
        }
    }
}
