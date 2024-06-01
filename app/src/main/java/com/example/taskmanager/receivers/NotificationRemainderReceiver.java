package com.example.taskmanager.receivers;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmanager.R;


public class NotificationRemainderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_manager_channel";

    @Override
    public void onReceive(Context ctx, Intent intent) {

        Toast.makeText(ctx, "Notification remainder received!", Toast.LENGTH_SHORT).show();

        int notificationId = intent.getIntExtra("notification_id", 0);
        String notificationTitle = intent.getStringExtra("notification_title");
        String notificationText = intent.getStringExtra("notification_text");
        PendingIntent pendingIntent = intent.getParcelableExtra("pending_intent");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.app)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Task Manager Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);

        try{
            notificationManagerCompat.notify(notificationId, builder.build());
        }catch (SecurityException e)
        {
            Toast.makeText(ctx, "Service Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}