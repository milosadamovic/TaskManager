package com.example.taskmanager.receivers;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.R;
import com.example.taskmanager.main.CustomViewModel;
import com.example.taskmanager.util.DefaultParameters;

public class TaskFinishedReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_manager_channel";

    @Override
    public void onReceive(Context ctx, Intent intent) {

        CustomViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(((Application) ctx.getApplicationContext())).create(CustomViewModel.class);

        Toast.makeText(ctx, "Notification task finished received!", Toast.LENGTH_SHORT).show();

        int notificationId = intent.getIntExtra("notification_id", 0);
        String notificationTitle = intent.getStringExtra("notification_title");
        String notificationText = intent.getStringExtra("notification_text");
        PendingIntent pendingIntent = intent.getParcelableExtra("pending_intent");

        viewModel.updateTaskStatus(notificationId, DefaultParameters.TASK_UNSUCCESSFUL);

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
