package com.example.medproject;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medproject.Notifications.ReminderBroadcast;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        Button button = findViewById(R.id.remindButton);

        createNotificationChannels();

        button.setOnClickListener(v -> {
            Toast.makeText(this, "Reminder set!",Toast.LENGTH_LONG).show();

            long timeAtButtonClick = System.currentTimeMillis();
            long fiveSecondsInMillis = 5 * 1000;

            Intent broadcastIntent = new Intent(this, ReminderBroadcast.class);
            broadcastIntent.putExtra("totalTimes", 5);
            broadcastIntent.putExtra("index",  1);
            broadcastIntent.putExtra("timeToNextAlarm", 5);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    (int) timeAtButtonClick, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


            alarmManager.set(AlarmManager.RTC,
                    timeAtButtonClick + fiveSecondsInMillis,
                    pendingIntent);
        });
    }

    private void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PillReminderChannel";
            String description = "Channel for pill reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
