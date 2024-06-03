package com.example.taskmanager.db_model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.CompletableFuture;

@Database(entities = {Task.class}, version = 8)
public abstract class TaskDB extends RoomDatabase {

    public abstract TaskDAO taskDAO();

    public static TaskDB dbInstance;

    public static synchronized TaskDB getInstance(Context ctx)
    {
        if(dbInstance == null)
            dbInstance = Room.databaseBuilder(ctx.getApplicationContext(), TaskDB.class, "tasks_db").fallbackToDestructiveMigration().build();

        return dbInstance;
    }

    @Override
    public void clearAllTables() {
       dbInstance.clearAllTables();
    }
}