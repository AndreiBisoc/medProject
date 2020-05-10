package com.example.medproject.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;
public class SnoozeActionBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String dosage = intent.getStringExtra("dosage");
        String drugName = intent.getStringExtra("drugName");
            String notificationUniqueIdName = intent.getStringExtra("notificationUniqueIdName");
            int notificationUniqueId = intent.getIntExtra( notificationUniqueIdName, 0);

        Intent broadcastIntent = new Intent(context, ReminderBroadcast.class);
        broadcastIntent.putExtra("drugName", drugName);
        broadcastIntent.putExtra("dosage", dosage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                (int) System.currentTimeMillis(), broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long fiveMinutes = 5 * 60 * 1000;
        alarmManager.set(AlarmManager.RTC,
                System.currentTimeMillis() + fiveMinutes,
                pendingIntent);

        Toast.makeText(context, "Notificarea a fost amânată cu " + fiveMinutes/60/1000 + " minute", Toast.LENGTH_SHORT).show();

        if(notificationUniqueId != 0) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationUniqueId);
        }
    }
}
