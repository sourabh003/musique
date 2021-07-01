package com.example.musique;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    ImageView imageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageLogo = findViewById(R.id.image_logo);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageLogo.setVisibility(View.VISIBLE);
        imageLogo.startAnimation(animation);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(() -> {
            startActivity(new Intent(this, Home.class));
            finish();
        }, 1500);
    }
}