package com.example.musique.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musique.R;
import com.example.musique.helpers.Album;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;

import java.io.IOException;

public class EditAlbum extends AppCompatActivity {

    private static final int PICK_IMAGE = 0;
    private static final String TAG = "EditAlbum";
    TextView txtAlbumName;
    Album album;
    ImageView viewAlbumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_album);
        album = (Album) getIntent().getSerializableExtra(Constants.ALBUM_OBJECT);

        viewAlbumArt = findViewById(R.id.view_album_art);
        txtAlbumName = findViewById(R.id.txt_album_name);
    }

    @Override
    protected void onStart() {
        super.onStart();

        txtAlbumName.setText(album.getName());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_album:
                Toast.makeText(this, "Updating album", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_add_image:
                pickImage();
                break;

        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            try {
                Bitmap bitmap = Functions.getBitmapFromUri(this, imageUri);
                Glide.with(this).load(Functions.compressBitmap(bitmap)).error(R.drawable.default_album_art_2).into(viewAlbumArt);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}