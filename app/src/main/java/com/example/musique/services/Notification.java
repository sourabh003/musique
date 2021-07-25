package com.example.musique.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

import com.example.musique.Home;
import com.example.musique.R;
import com.example.musique.utils.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.musique.services.ApplicationClass.ACTION_NEXT;
import static com.example.musique.services.ApplicationClass.ACTION_PLAY;
import static com.example.musique.services.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musique.services.ApplicationClass.CHANNEL_ID_2;
import static com.example.musique.services.PlayerService.currentSong;
import static com.example.musique.services.PlayerService.mediaPlayer;

public class Notification {

    public void showNotification(Context context) {
        NotificationManager manager = getNotificationManager(context);
        manager.notify(0, getNotificationBuilder(context).build());
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context) {
        Intent intent = new Intent(context, Home.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent playPauseIntent = new Intent(context, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(context, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(context, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int playIcon;
        String action;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            action = Constants.PLAY;
            playIcon = R.drawable.ic_baseline_pause_24;
        } else {
            action = Constants.PAUSE;
            playIcon = R.drawable.ic_baseline_play_arrow_24;
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art_2))
                .setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getArtist())
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", previousPendingIntent)
                .addAction(playIcon, action, playPausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(new MediaSessionCompat(context, "Audio").getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setContentIntent(contentIntent);
    }

    public NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }
}