package com.example.taskmanager.receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.taskmanager.db_model.Repository;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.main.CustomViewModel;
import com.example.taskmanager.services.DueDateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    List<Task> taskList = new ArrayList<>();
    Repository repository;

    @Override
    public void onReceive(Context ctx, Intent intent) {

        repository = new Repository((Application) ctx.getApplicationContext());
        fetchTasksInProgress(ctx);
    }

    private void fetchTasksInProgress(Context ctx) {

        repository.es.execute(() -> {

            taskList = repository.getTasksInProgress();

            repository.handler.post(() -> {
                if (taskList != null && !taskList.isEmpty()) {

                    for(Task t: taskList)
                    {
                        Intent serviceIntent = new Intent(ctx, DueDateService.class);
                        serviceIntent.putExtra("taskId", t.getTask_id());
                        serviceIntent.putExtra("due_date", t.getTask_dueDateTime());
                        serviceIntent.putExtra("remainder_dueDate", t.getTask_remainderDueDate());
                        serviceIntent.putExtra("title", t.getTask_title());
                        ctx.startService(serviceIntent);
                    }
                } else {
                    Toast.makeText(ctx, "No tasks in progress found.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}