package com.example.taskmanager.db_model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert
    public long insertTask(Task task);
    @Delete
    public void deleteTask(Task task);
    @Update
    public void updateTask(Task task);
    @Query("SELECT * FROM tasks WHERE task_priority == :priority AND (task_status == 0 OR task_status == 2)")
    public LiveData<List<Task>> getTasksByPriority(int priority);

    @Query("UPDATE tasks SET task_status = :taskStatus, task_dueDateTime = 0 WHERE task_id == :taskId")
    public void updateTaskStatus(long taskId, int taskStatus);

    @Query("SELECT * FROM tasks WHERE task_title LIKE :text OR task_details LIKE :text")
    public LiveData<List<Task>> getSearchedTasks(String text);

    @Query("SELECT COUNT(*) FROM tasks")
    public long getNumOfTasks();

    @Query("SELECT * FROM tasks WHERE task_status == 0")
    public List<Task> getTasksInProgress();



}
