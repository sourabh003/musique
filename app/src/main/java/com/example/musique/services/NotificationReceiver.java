package com.example.musique.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.musique.utils.Constants;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(Constants.BROADCAST_ACTION).putExtra(Constants.NOTIFICATION_ACTION, intent.getAction()));
    }
}
