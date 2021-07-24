package com.example.musique.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musique.R;
import com.example.musique.database.Database;
import com.example.musique.helpers.Song;

import java.util.ArrayList;


public class AddSongToPlaylistAdapter extends RecyclerView.Adapter<AddSongToPlaylistAdapter.ViewHolder> {

    String TAG = "AddSongToPlaylistAdapter";
    ArrayList<Song> trackList;
    Context context;
    ArrayList<Song> selectedSongsList = new ArrayList<>();
    String playlistID;
    Database database;

    public AddSongToPlaylistAdapter(ArrayList<Song> trackList, String playlistID, Context context) {
        this.context = context;
        this.trackList = trackList;
        this.playlistID = playlistID;
        database = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_add_songs_to_playlist, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = trackList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.artistTitle.setText(song.getArtist());
        holder.parentView.setOnClickListener(v -> holder.checkBoxSelected.setChecked(!holder.checkBoxSelected.isChecked()));
        holder.checkBoxSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedSongsList.add(song);
            } else {
                selectedSongsList.remove(song);
            }
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
        public ImageView albumArt;
        public CheckBox checkBoxSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView.findViewById(R.id.parent_view);
            this.songTitle = itemView.findViewById(R.id.song_title);
            this.albumArt = itemView.findViewById(R.id.album_art);
            this.artistTitle = itemView.findViewById(R.id.song_artist);
            this.checkBoxSelected = itemView.findViewById(R.id.checkbox_selected);
        }
    }

    public ArrayList<Song> getSelectedSongsList() {
        return selectedSongsList;
    }
}
