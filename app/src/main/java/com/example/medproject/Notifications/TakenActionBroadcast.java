package com.example.medproject.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class TakenActionBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationUniqueIdName = intent.getStringExtra("notificationUniqueIdName");
        int notificationUniqueId = intent.getIntExtra( notificationUniqueIdName, 0);

        Toast.makeText(context, "Vă mulțumim că sunteți responsabil!", Toast.LENGTH_SHORT).show();
        if(notificationUniqueId != 0) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationUniqueId);
        }
    }
}
