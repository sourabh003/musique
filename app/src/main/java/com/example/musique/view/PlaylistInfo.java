package com.example.musique.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musique.R;
import com.example.musique.database.Database;
import com.example.musique.helpers.Playlist;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;

import java.io.IOException;

public class PlaylistInfo extends AppCompatActivity {

    private static final int PICK_IMAGE = 0;
    private static final String TAG = "EditPlaylist";
    Playlist playlist;
    ImageView viewPlaylistImage;
    EditText inputPlaylistName;

    Database database;
    String oldImage;
    String ACTION;
    TextView txtPlaylistTitle;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_info);
        Intent intent = getIntent();
        ACTION = intent.getExtras().getString(Constants.PLAYLIST_ACTION);

        txtPlaylistTitle = findViewById(R.id.txt_playlist_title);
        database = new Database(this);
        viewPlaylistImage = findViewById(R.id.image_playlist_logo);
        inputPlaylistName = findViewById(R.id.input_playlist_name);

        if (ACTION.equals(Constants.PLAYLIST_ACTION_CREATE)) {
            txtPlaylistTitle.setText("CREATE NEW PLAYLIST");
            playlist = new Playlist();
            playlist.setImage(Constants.DEFAULT_PLAYLIST_IMAGE);
        } else {
            txtPlaylistTitle.setText("EDIT PLAYLIST");
            playlist = (Playlist) getIntent().getSerializableExtra(Constants.PLAYLIST_OBJECT);
            oldImage = playlist.getImage();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ACTION.equals(Constants.PLAYLIST_ACTION_EDIT)) {
            inputPlaylistName.setText(playlist.getName());
            Glide.with(this).load(Functions.getPlaylistImage(this, playlist.getImage())).error(R.drawable.playlist).into(viewPlaylistImage);
        }
    }

    public void onClick(View view) throws IOException {
        switch (view.getId()) {
            case R.id.btn_add_image:
                addImage();
                break;

            case R.id.btn_cancel_edit:
                finish();
                break;

            case R.id.btn_confirm_edit:
                if (inputPlaylistName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Invalid Name!", Toast.LENGTH_SHORT).show();
                } else {
                    String name = Functions.capitalize(inputPlaylistName.getText().toString().trim());
                    playlist.setName(name);
                }
                if (ACTION.equals(Constants.PLAYLIST_ACTION_CREATE)) {
                    createPlaylist();
                } else {
                    updatePlaylist();
                }
                break;

        }
    }

    private void addImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = Functions.getBitmapFromUri(this, imageUri);
                Glide.with(this).load(Functions.compressBitmap(bitmap)).error(R.drawable.playlist).into(viewPlaylistImage);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
    }

    private void createPlaylist() throws IOException {
        String id = Functions.generateID();
        String time = String.valueOf(System.currentTimeMillis());
        playlist.setId(id);
        playlist.setCreatedDate(time);
        if (imageUri != null) {
            playlist.setImage(Functions.saveImageToInternalStorage(this, Functions.getBitmapFromUri(this, imageUri)));
        } else {
            playlist.setImage("null");
        }
        Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();
        database.createPlaylist(playlist);
        finish();
    }

    private void updatePlaylist() throws IOException {
        if (imageUri != null) {
            playlist.setImage(Functions.saveImageToInternalStorage(this, Functions.getBitmapFromUri(this, imageUri)));
        }
        database.updatePlaylist(playlist);
        if (!playlist.getImage().equals(oldImage)){
            Functions.deleteImageFromInternalStorage(this, oldImage);
        }
        Toast.makeText(this, "Playlist Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}