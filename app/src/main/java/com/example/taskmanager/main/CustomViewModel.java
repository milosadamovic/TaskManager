package com.example.taskmanager.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.taskmanager.db_model.Repository;
import com.example.taskmanager.db_model.Task;

import java.util.List;

public class CustomViewModel extends AndroidViewModel {


    Repository repository;
    LiveData<List<Task>> tasksByPriority;
    LiveData<List<Task>> searchedTasks;
    List<Task> tasksInProgress;

    public CustomViewModel(Application application)
    {
        super(application);
        this.repository = new Repository(application);
    }
    public Long insertTask(Task task) {
        return repository.insertTask(task);
    }
    public void updateTask(Task task) { repository.updateTask(task);}
    public void deleteTask(Task task) { repository.deleteTask(task);}
    public LiveData<List<Task>> getTasksByPriority(int priority) { tasksByPriority = repository.getTasksByPriority(priority); return tasksByPriority;}

    public void updateTaskStatus(long taskId, int taskStatus)
    {
        repository.updateTaskStatus(taskId, taskStatus);
    }

    public LiveData<List<Task>> getSearchedTasks(String text)
    {
        searchedTasks = repository.getSearchedTasks(text);
        return searchedTasks;
    }

    public List<Task> getTasksInProgress()
    {
        tasksInProgress = repository.getTasksInProgress();
        return tasksInProgress;
    }

    public Long getNumOfTasks()
    {
        return repository.getNumOfTasks();
    }

    public void clearAllTables(){ repository.clearAllTables();}

}
