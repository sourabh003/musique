package com.example.musique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musique.AlbumSongs;
import com.example.musique.R;
import com.example.musique.helpers.Album;
import com.example.musique.utils.Constants;

import java.util.ArrayList;

public class AlbumListAdapter extends ArrayAdapter<Album> {
    public AlbumListAdapter(@NonNull Context context, ArrayList<Album> albumsList) {
        super(context, 0, albumsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_albums, parent, false);
        }
        Album album = getItem(position);
        FrameLayout parentLayout = listItemView.findViewById(R.id.layout_parent);
        TextView txtAlbumName = listItemView.findViewById(R.id.txt_album_name);
        TextView txtAlbumSongsCount = listItemView.findViewById(R.id.txt_album_count);
        txtAlbumSongsCount.setText(album.getSongsCount() + " Songs");
        txtAlbumName.setText(album.getName());
        parentLayout.setOnClickListener(v -> {
            getContext().startActivity(new Intent(getContext(), AlbumSongs.class).putExtra(Constants.ALBUM_OBJECT, album));
        });
        return listItemView;
    }
}
