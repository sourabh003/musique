package com.example.musique.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musique.Playlists;
import com.example.musique.R;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;

import java.util.ArrayList;

import static com.example.musique.service.PlayerService.currentSong;

public class DialogHandlers {

    public static void showAddToPlaylistDialog(Context context, Activity activity) {
        Database database = new Database(context);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_to_playlist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        ArrayList<String> addToPlaylistList = new ArrayList<>();
        addToPlaylistList.clear();
        LinearLayout playlistList = dialog.findViewById(R.id.playlist_list);
        if (database.getPlaylists().isEmpty()) {
            dialog.findViewById(R.id.txt_no_playlist).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.playlist_list_container).setVisibility(View.GONE);
        } else {
            for (Playlist playlist : database.getPlaylists()) {
                View view = activity.getLayoutInflater().inflate(R.layout.list_item_playlist_list, null);
                CheckBox checkBox = view.findViewById(R.id.playlist_checkbox);
                checkBox.setText(playlist.getName());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        addToPlaylistList.add(playlist.getId());
                    } else {
                        addToPlaylistList.remove(playlist.getId());
                    }
                });
                playlistList.addView(view);
            }
        }
        ImageView btnClosePlaylistDialog = dialog.findViewById(R.id.btn_close_playlist_dialog);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnCreate = dialog.findViewById(R.id.btn_create_playlist);
        btnAdd.setOnClickListener(v -> {
            if (addToPlaylistList.isEmpty()) {
                Toast.makeText(context, "No Playlists Selected!", Toast.LENGTH_SHORT).show();
            } else {
                database.addSongToPlaylist(addToPlaylistList, currentSong.getId());
                Toast.makeText(context, "Song added to Playlist(s)", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCreate.setOnClickListener(v -> {
            dialog.dismiss();
            showCreatePlaylistDialog(context, activity, true);
        });
        btnClosePlaylistDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public static void showCreatePlaylistDialog(Context context, Activity activity, boolean fromPlayer) {
        Database database = new Database(context);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_playlist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText inputPlaylistName = dialog.findViewById(R.id.input_playlist_name);
        ImageView btnClosePlaylistDialog = dialog.findViewById(R.id.btn_close_playlist_dialog);
        Button btnCreatePlaylist = dialog.findViewById(R.id.btn_create_playlist);
        btnCreatePlaylist.setOnClickListener(v -> {
            String name = inputPlaylistName.getText().toString().trim();
            if (!name.isEmpty()) {
                dialog.dismiss();
                Functions.closeKeyboard(activity);
                new Thread(() -> {
                    String time = String.valueOf(System.currentTimeMillis());
                    database.createPlaylist(name, time);
                }).start();
                if (fromPlayer){
                    showAddToPlaylistDialog(context, activity);
                } else {
                    context.startActivity(new Intent(context, Playlists.class));
                }
                Toast.makeText(context, name + " Playlist Created!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Invalid Name!", Toast.LENGTH_SHORT).show();
            }
        });
        btnClosePlaylistDialog.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
