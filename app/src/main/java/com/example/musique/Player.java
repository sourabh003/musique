package com.example.musique;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musique.database.Database;
import com.example.musique.service.PlayerService;
import com.example.musique.utility.Constants;
import com.example.musique.utility.Functions;

import static com.example.musique.service.PlayerService.currentSong;
import static com.example.musique.service.PlayerService.isLibraryRepeating;
import static com.example.musique.service.PlayerService.mediaPlayer;

public class Player extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Player";

    TextView txtSongName, txtArtistName;
    ImageView imgAlbumArt, btnBack, btnPlay, btnNext, btnPrevious, btnRepeat, btnAddToPlaylist, btnLike;
    TextView songStartTimeStamp, songEndTimeStamp;
    SeekBar playerSeekBar;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        database = new Database(this);
        btnLike = findViewById(R.id.btn_like);
        btnLike.setOnClickListener(this);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnRepeat.setOnClickListener(this);
        btnPlay = findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(this);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        txtSongName = findViewById(R.id.txt_song_title);
        txtArtistName = findViewById(R.id.txt_song_artist);
        imgAlbumArt = findViewById(R.id.default_album_art);
        songStartTimeStamp = findViewById(R.id.txt_time_start);
        songEndTimeStamp = findViewById(R.id.txt_time_end);
        playerSeekBar = findViewById(R.id.player_seekbar);
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                songStartTimeStamp.setText(Functions.getModifiedDuration(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        loadMediaPlayer();
        new Thread(() -> {
            try {
                int start = 0;
                int end = mediaPlayer.getDuration();
                while (start < end) {
                    playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    start = mediaPlayer.getCurrentPosition();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "onCreate: SeekBar Thread: ", e);
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void loadMediaPlayer() {
        PlayerService.loadMediaPlayer(this);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::updateUI, 100);
        Functions.updateLatestSong(this, currentSong);
        playerSeekBar.setMax(mediaPlayer.getDuration());
        songEndTimeStamp.setText(Functions.getModifiedDuration(mediaPlayer.getDuration()));
    }

    private void updatePlayButton() {
        if (mediaPlayer.isPlaying()) {
            txtSongName.setSelected(true);
            btnPlay.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_circle_24));
        } else {
            txtSongName.setSelected(false);
            btnPlay.setImageDrawable(getDrawable(R.drawable.ic_baseline_play_circle_24));
        }
    }


    private void updateUI() {
        adjustAlbumArtSize();
        txtSongName.setText(currentSong.getTitle());
        txtArtistName.setText(currentSong.getArtist());
        songEndTimeStamp.setText(Functions.getModifiedDuration(mediaPlayer.getDuration()));
        updatePlayButton();
        updateLoopButton();
        updateLikeButton();
    }

    private void updateRepeatAction() {
        if (isLibraryRepeating) {
            isLibraryRepeating = false;
            mediaPlayer.setLooping(true);
            Toast.makeText(this, "Repeating current song", Toast.LENGTH_SHORT).show();
        } else if (mediaPlayer.isLooping()) {
            mediaPlayer.setLooping(false);
            isLibraryRepeating = false;
            Toast.makeText(this, "Repeating off", Toast.LENGTH_SHORT).show();
        } else {
            mediaPlayer.setLooping(false);
            isLibraryRepeating = true;
            Toast.makeText(this, "Repeating current library", Toast.LENGTH_SHORT).show();
        }
        updateLoopButton();
    }

    private void updateLoopButton() {
        if (isLibraryRepeating) {
            btnRepeat.setImageDrawable(getDrawable(R.drawable.ic_baseline_repeat_active_24));
        } else if (mediaPlayer.isLooping()) {
            btnRepeat.setImageDrawable(getDrawable(R.drawable.ic_baseline_repeat_one_24));
        } else {
            btnRepeat.setImageDrawable(getDrawable(R.drawable.ic_baseline_repeat_24));
        }
    }

    private void changeSong(String which) {
        playerSeekBar.setProgress(0);
        if (which.equals(Constants.NEXT_SONG)) {
            PlayerService.nextSong();
        } else {
            PlayerService.previousSong();
        }
        loadMediaPlayer();
        updateUI();
    }

    private void updateSongFavourite() {
        if (PlayerService.favouriteSongsList.contains(currentSong.getId())) {
            PlayerService.favouriteSongsList.remove(currentSong.getId());
            database.removeSongFromFavourites(currentSong.getId());
            Toast.makeText(this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
        } else {
            database.addSongToFavourites(currentSong.getId());
            PlayerService.favouriteSongsList.add(currentSong.getId());
            Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        }
        updateLikeButton();
    }

    private void updateLikeButton() {
        if (PlayerService.favouriteSongsList.contains(currentSong.getId())) {
            btnLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_red_24));
        } else {
            btnLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_play:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                updatePlayButton();
                break;

            case R.id.btn_repeat:
                updateRepeatAction();
                break;

            case R.id.btn_next:
                changeSong(Constants.NEXT_SONG);
                break;

            case R.id.btn_previous:
                changeSong(Constants.PREVIOUS_SONG);
                break;

            case R.id.btn_like:
                updateSongFavourite();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void adjustAlbumArtSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams imageLayoutParams = new RelativeLayout.LayoutParams(width, width);
        imageLayoutParams.setMargins(20, 20, 20, 10);
        imgAlbumArt.setLayoutParams(imageLayoutParams);
    }
}