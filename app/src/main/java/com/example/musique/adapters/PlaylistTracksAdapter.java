package com.example.musique.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musique.R;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.helpers.Song;

import java.util.ArrayList;


public class PlaylistTracksAdapter extends RecyclerView.Adapter<PlaylistTracksAdapter.ViewHolder> {

    String TAG = "TrackListAdapter";
    ArrayList<Song> trackList;
    Context context;
    Database database;
    Playlist playlist;

    public PlaylistTracksAdapter(ArrayList<Song> trackList, Playlist playlist, Context context) {
        this.context = context;
        this.trackList = trackList;
        this.playlist = playlist;
        database = new Database(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_playlist_songs, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = trackList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.artistTitle.setText(song.getArtist());
        holder.btnDeleteSong.setOnClickListener(v -> {
            database.deleteSongFromPlaylist(song.getId(), playlist.getId());
            trackList.remove(position);
            Toast.makeText(context, "Song removed from Playlist", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(position);
            notifyDataSetChanged();
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
        public ImageView btnDeleteSong, albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView.findViewById(R.id.parent_view);
            this.songTitle = itemView.findViewById(R.id.song_title);
            this.albumArt = itemView.findViewById(R.id.album_art);
            this.artistTitle = itemView.findViewById(R.id.sont_artist);
            this.btnDeleteSong = itemView.findViewById(R.id.btn_delete_song);
        }
    }
}
