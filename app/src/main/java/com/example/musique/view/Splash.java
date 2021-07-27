package com.example.musique.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.R;
import com.example.musique.services.Notification;
import com.example.musique.services.PlayerService;
import com.example.musique.utils.Constants;

public class Splash extends AppCompatActivity {

    ImageView imageLogo;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notification = new Notification();
        setContentView(R.layout.activity_splash);
        imageLogo = findViewById(R.id.image_logo);
        registerReceiver(PlayerService.broadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        startService(new Intent(getBaseContext(), PlayerService.class));

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