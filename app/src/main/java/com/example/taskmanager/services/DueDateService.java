package com.example.taskmanager.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.taskmanager.main.MainActivity;
import com.example.taskmanager.receivers.NotificationRemainderReceiver;
import com.example.taskmanager.receivers.TaskFinishedReceiver;

import java.util.concurrent.TimeUnit;

public class DueDateService extends Service {


    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {


            Context ctx = getApplicationContext();
            Bundle bundle = msg.getData();
            int startId = bundle.getInt("startId",0);
            String title = bundle.getString("title");
            long taskId = bundle.getLong("taskId",0);
            long notificationFinishedTime = bundle.getLong("notificationFinished_time", 0);
            long notificationRemainderTime = bundle.getLong("notificationRemainder_time", 0);

            try {
                Thread.sleep(1000);

                final int NOTIFICATION_ID = (int)taskId;
                Intent taskFinishedIntent = new Intent(ctx, MainActivity.class);
                taskFinishedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                taskFinishedIntent.addCategory("/" + Integer.hashCode(NOTIFICATION_ID));
                PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, taskFinishedIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

                Intent alarmIntent = new Intent(ctx, TaskFinishedReceiver.class);
                alarmIntent.setAction("ACTION_NOTIFICATION_" + Integer.hashCode(NOTIFICATION_ID));
                alarmIntent.addCategory("/" + Long.valueOf(taskId).hashCode());
                alarmIntent.putExtra("notification_id", NOTIFICATION_ID);
                alarmIntent.putExtra("notification_title", "Time for Task " + title + " has expired");
                alarmIntent.putExtra("notification_text", "Plan your tasks wisely!!");
                alarmIntent.putExtra("pending_intent", pendingIntent);

                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(ctx, Integer.hashCode(Long.valueOf(taskId).hashCode()), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                try {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationFinishedTime, alarmPendingIntent);
                } catch (SecurityException e) {
                    Toast.makeText(ctx, "Service Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Setting remainder notification if remainder exists
                if(notificationRemainderTime > 0)
                {
                    Intent notificationRemainderIntent = new Intent(ctx, MainActivity.class);
                    notificationRemainderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    notificationRemainderIntent.addCategory("/" + NOTIFICATION_ID);
                    PendingIntent pendingRemainderIntent = PendingIntent.getActivity(ctx, 0, notificationRemainderIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    Intent alarmRemainderIntent = new Intent(ctx, NotificationRemainderReceiver.class);
                    alarmRemainderIntent.setAction("ACTION_NOTIFICATION_" + taskId);
                    alarmRemainderIntent.addCategory("/" + Long.valueOf(taskId).hashCode());
                    alarmRemainderIntent.putExtra("notification_id", NOTIFICATION_ID);
                    alarmRemainderIntent.putExtra("notification_title", "Task " + title + " Remainder");
                    alarmRemainderIntent.putExtra("notification_text", "Task " + title + " is waiting to be checked !");
                    alarmRemainderIntent.putExtra("pending_intent", pendingRemainderIntent);

                    PendingIntent alarmRemainderPendingIntent = PendingIntent.getBroadcast(ctx, Long.valueOf(taskId).hashCode(), alarmRemainderIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    try {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationRemainderTime, alarmRemainderPendingIntent);
                    } catch (SecurityException e) {
                        Toast.makeText(ctx, "Service Exception " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            stopSelf(startId);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        long taskId = intent.getLongExtra("taskId",-1);
        long dueDate = intent.getLongExtra("due_date", 0);
        long remainderDueDate = intent.getLongExtra("remainder_dueDate", 0);
        String title = intent.getStringExtra("title");
        long currentTime = System.currentTimeMillis();
        long timeDifference = dueDate - currentTime;


        //Toast.makeText(this, "service starting...", Toast.LENGTH_SHORT).show();


        if (timeDifference > 0 && dueDate > currentTime)
        {
            Message msg = serviceHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("startId", startId);
            bundle.putLong("taskId", taskId);
            bundle.putString("title", title);
            bundle.putLong("notificationRemainder_time", remainderDueDate);
            bundle.putLong("notificationFinished_time", dueDate);
            msg.setData(bundle);
            serviceHandler.sendMessage(msg);

        } else stopSelf(startId);


        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "service done...", Toast.LENGTH_SHORT).show();
    }

}
