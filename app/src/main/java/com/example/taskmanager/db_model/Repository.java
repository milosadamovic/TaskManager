package com.example.taskmanager.db_model;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Repository {

    public ExecutorService es;
    public Handler handler;
    private final TaskDAO taskDAO;
    private final TaskDB taskDB;



    public Repository(Application application)
    {
        taskDB = TaskDB.getInstance(application);
        this.taskDAO = taskDB.taskDAO();

        es = Executors.newSingleThreadExecutor();
        handler = new android.os.Handler(Looper.getMainLooper());
    }

    public Long insertTask(final Task task) {

        Future<Long> future = es.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return taskDAO.insertTask(task);
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            Log.d("ERRORY", Objects.requireNonNull(e.getMessage()));
            return 0L;
        }
    }

    public void deleteTask(Task task)
    {
        es.execute( () -> taskDAO.deleteTask(task));
    }

    public void updateTask(Task task)
    {
        es.execute( () -> taskDAO.updateTask(task));
    }

    public LiveData<List<Task>> getTasksByPriority(int priority)
    {
       return taskDAO.getTasksByPriority(priority);
    }

    public void updateTaskStatus(long taskId, int taskStatus)
    {
        es.execute( () -> taskDAO.updateTaskStatus(taskId, taskStatus));
    }

    public LiveData<List<Task>> getSearchedTasks(String text)
    {
        return taskDAO.getSearchedTasks(text);
    }

    public List<Task> getTasksInProgress()
    {
        return taskDAO.getTasksInProgress();
    }

    public Long getNumOfTasks()
    {
        Future<Long> future = es.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return taskDAO.getNumOfTasks();
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            Log.d("ERRORY", Objects.requireNonNull(e.getMessage()));
            return 0L;
        }
    }

     public void clearAllTables()
     {
        es.execute(taskDB::clearAllTables);
     }



}


