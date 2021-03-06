package com.example.musique.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.musique.helpers.Song;
import com.example.musique.utils.CacheHandlers;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;
import com.example.musique.utils.SongsHandler;

import java.util.ArrayList;
import java.util.List;

import static com.example.musique.services.ApplicationClass.ACTION_NEXT;
import static com.example.musique.services.ApplicationClass.ACTION_PLAY;
import static com.example.musique.services.ApplicationClass.ACTION_PREVIOUS;

public class PlayerService extends Service {

    IBinder mBinder = new CustomBinder();
    public static Song currentSong = null;
    public static MediaPlayer mediaPlayer = null;
    public static boolean isLibraryRepeating = false;
    public static ArrayList<Song> tracksList = new ArrayList<>();
    public static int songIndex = 0;
    public static List<String> favouriteSongsList = new ArrayList<>();
    public static Notification notification = new Notification();

    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString(Constants.NOTIFICATION_ACTION);
            switch (action) {
                case ACTION_PLAY:
                    if (mediaPlayer == null) {
                        loadMediaPlayer(context);
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.start();
                        }
                    }
                    break;
                case ACTION_NEXT:
                    nextSong(context);
                    break;
                case ACTION_PREVIOUS:
                    previousSong(context);
                    break;
            }
        }
    };

    public static void nextSong(Context context) {
        if (songIndex == tracksList.size() - 1) {
            songIndex = 0;
        } else {
            songIndex++;
        }
        currentSong = tracksList.get(songIndex);
        loadMediaPlayer(context);
    }

    public static void previousSong(Context context) {
        if (songIndex == 0) {
            songIndex = tracksList.size() - 1;
        } else {
            songIndex--;
        }
        currentSong = tracksList.get(songIndex);
        loadMediaPlayer(context);
    }

    public static void loadMediaPlayer(Context ctx) {
        if (currentSong == null) {
            currentSong = CacheHandlers.getLatestSong(ctx);
        }
        if (CacheHandlers.getLatestSong(ctx).getId().equals(currentSong.getId())) {
            if (mediaPlayer == null) {
                initMediaPlayer(ctx, true);
            } else {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            initMediaPlayer(ctx, true);

        }
        CacheHandlers.updateLatestSong(ctx, currentSong, songIndex);
    }

    public static void initMediaPlayer(Context context, boolean withStart) {
        mediaPlayer = MediaPlayer.create(context, Uri.parse(currentSong.getData()));
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isLibraryRepeating) {
                nextSong(context);
                loadMediaPlayer(context);
            } else {
                if (!mediaPlayer.isLooping()) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.pause();
                }
            }
        });
        if (withStart) {
            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        }
    }

    public static void playAllSongs(Context context, ArrayList<Song> list) {
        tracksList.clear();
        tracksList.addAll(list);
        songIndex = 0;
        currentSong = tracksList.get(songIndex);
        if (!isLibraryRepeating) {
            isLibraryRepeating = true;
        }
        loadMediaPlayer(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            while (true) {
                try {
                    if (currentSong != null){
                        notification.showNotification(getApplicationContext());
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class CustomBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (currentSong == null) {
            if (!CacheHandlers.getLatestSong(getApplicationContext()).getId().equals("0")) {
                currentSong = CacheHandlers.getLatestSong(getApplicationContext());
                initMediaPlayer(getApplicationContext(), false);
            }
        }
        if (tracksList.isEmpty()){
            new Thread(() -> tracksList.addAll(SongsHandler.getSongs(getApplicationContext()))).start();
        }
        songIndex = CacheHandlers.getLatestSongIndex(this);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        notification.getNotificationManager(getApplicationContext()).cancelAll();
    }
}