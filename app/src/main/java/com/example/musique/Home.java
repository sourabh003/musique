package com.example.musique;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Home extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Home";
    LinearLayout layoutLibraries, layoutFolders, layoutFavourites;
    LinearLayout layoutParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layoutLibraries = findViewById(R.id.layout_libraries);
        layoutLibraries.setOnClickListener(this);
        layoutFavourites = findViewById(R.id.layout_favourites);
        layoutFavourites.setOnClickListener(this);
        layoutFolders = findViewById(R.id.layout_folders);
        layoutFolders.setOnClickListener(this);
        layoutParent = findViewById(R.id.layout_parent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkStoragePermission()) {
            Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                requestStoragePermission();
            }).show();
        }
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(Home.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Snackbar.make(layoutParent, "Please grant storage permission", Snackbar.LENGTH_INDEFINITE).setAction("Grant", v -> {
                    requestStoragePermission();
                }).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    private boolean checkStoragePermission() {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        if (checkStoragePermission()) {
            switch (v.getId()) {
                case R.id.layout_libraries:
                    startActivity(new Intent(getApplicationContext(), Library.class));
                    break;

                case R.id.layout_folders:
                    startActivity(new Intent(this, Test.class));
                    break;

                case R.id.layout_favourites:
                    Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Please grant Storage Permission first", Toast.LENGTH_SHORT).show();
        }
    }
}