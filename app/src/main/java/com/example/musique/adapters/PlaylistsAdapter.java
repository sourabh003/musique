package com.example.musique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.musique.R;
import com.example.musique.helpers.Playlist;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;
import com.example.musique.view.PlaylistSongs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaylistsAdapter extends ArrayAdapter<Playlist> {

    Context context;
    public PlaylistsAdapter(@NonNull Context context, ArrayList<Playlist> playlists) {
        super(context, 0, playlists);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_playlist, parent, false);
        }
        Playlist playlist = getItem(position);
        FrameLayout parentLayout = listItemView.findViewById(R.id.layout_parent);
        TextView txtPlaylistName = listItemView.findViewById(R.id.txt_playlist_name);
        ImageView viewPlaylistImage = listItemView.findViewById(R.id.view_playlist_image);
        Glide.with(context).load(Functions.getPlaylistImage(context, playlist.getImage())).error(R.drawable.playlist).into(viewPlaylistImage);
        txtPlaylistName.setText(Functions.capitalize(playlist.getName()));
        parentLayout.setOnClickListener(v -> {
            getContext().startActivity(new Intent(getContext(), PlaylistSongs.class).putExtra(Constants.PLAYLIST_OBJECT, playlist));
        });
        return listItemView;
    }
}
