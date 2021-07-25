package com.example.musique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musique.PlaylistSongs;
import com.example.musique.R;
import com.example.musique.helpers.Playlist;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;

import java.util.ArrayList;

public class PlaylistsAdapter extends ArrayAdapter<Playlist> {

    public PlaylistsAdapter(@NonNull Context context, ArrayList<Playlist> playlists) {
        super(context, 0, playlists);
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
        txtPlaylistName.setText(Functions.capitalize(playlist.getName()));
        parentLayout.setOnClickListener(v -> {
            getContext().startActivity(new Intent(getContext(), PlaylistSongs.class).putExtra(Constants.PLAYLIST_OBJECT, playlist));
        });
        return listItemView;
    }
}
