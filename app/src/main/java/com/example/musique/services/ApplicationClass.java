package com.example.musique.services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "CHANNEL-1";
    public static final String CHANNEL_ID_2 = "CHANNEL-2";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel1 = new NotificationChannel(CHANNEL_ID_1, "CHANNEL(1)", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel1.setDescription("Channel 1 Description");


        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_ID_2, "CHANNEL(2)", NotificationManager.IMPORTANCE_LOW);
        notificationChannel2.setDescription("Channel 2 Description");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel1);
        notificationManager.createNotificationChannel(notificationChannel2);
    }
}
