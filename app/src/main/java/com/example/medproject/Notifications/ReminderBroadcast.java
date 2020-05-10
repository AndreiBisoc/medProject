package com.example.medproject.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;

import static android.content.Context.ALARM_SERVICE;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int totalTimes = intent.getIntExtra("totalTimes", -1);
        int index = intent.getIntExtra("index", -1);
        long timeToNextAlarm = intent.getLongExtra("timeToNextAlarm",-1);
        String dosage = intent.getStringExtra("dosage");
        String drugName = intent.getStringExtra("drugName");

        int notificationUniqueId = (int) System.currentTimeMillis();

        Intent activityIntent = new Intent(context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                activityIntent, 0);

        String notificationUniqueIdName = Integer.toString(notificationUniqueId);

        //Intent for Accept
        Intent broadcastIntentAccept = new Intent(context, TakenActionBroadcast.class);
            broadcastIntentAccept.putExtra("notificationUniqueIdName", notificationUniqueIdName);
            broadcastIntentAccept.putExtra( notificationUniqueIdName, notificationUniqueId);
        PendingIntent actionIntentAccept = PendingIntent.getBroadcast(context, notificationUniqueId,
                broadcastIntentAccept, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent for Snooze
        Intent broadcastIntentSnooze = new Intent(context, SnoozeActionBroadcast.class);
            broadcastIntentSnooze.putExtra("dosage", dosage);
            broadcastIntentSnooze.putExtra("drugName", drugName);
            broadcastIntentSnooze.putExtra("notificationUniqueIdName", notificationUniqueIdName);
            broadcastIntentSnooze.putExtra( notificationUniqueIdName, notificationUniqueId);
        PendingIntent actionIntentSnooze = PendingIntent.getBroadcast(context, notificationUniqueId,
                broadcastIntentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(R.drawable.doctor)
                .setContentTitle("Reminder")
                .setContentText("Este timpul să luați " + dosage + " de " + drugName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "Gata", actionIntentAccept)
                .addAction(R.mipmap.ic_launcher, "Amână", actionIntentSnooze)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationUniqueId, notification);

        if(index != -1 && totalTimes != -1 && index < totalTimes){
            intent = new Intent(context, ReminderBroadcast.class);
            intent.putExtra("totalTimes", totalTimes);
            intent.putExtra("index", index + 1);
            intent.putExtra("timeToNextAlarm", timeToNextAlarm);
            intent.putExtra("dosage", dosage);
            intent.putExtra("drugName", drugName);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                     index, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC,
                    System.currentTimeMillis() + timeToNextAlarm,
                    pendingIntent);
        }
    }
}
