package com.example.taskmanager.db_model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    long task_id;
    @ColumnInfo(name="task_priority")
    int task_priority;
    @ColumnInfo(name="task_status")
    int task_status;
    @ColumnInfo(name="task_title")
    String task_title;
    @ColumnInfo(name="task_details")
    String task_details;
    @ColumnInfo(name="task_dueDateTime")
    long task_dueDateTime;
    @ColumnInfo(name="task_remainder")
    boolean task_remainder;
    @ColumnInfo(name="task_repeating")
    boolean task_repeating;
    @ColumnInfo(name="task_remainderDueDate")
    long task_remainderDueDate;
    @ColumnInfo(name="task_repeatingDueDate")
    long task_repeatingDueDate;
    @ColumnInfo(name="task_calendarEventId")
    long task_calendarEventId;



    public Task(long task_id, String task_title, int task_status, int task_priority, String task_details, long task_dueDateTime, boolean task_remainder, boolean task_repeating, long task_remainderDueDate, long task_repeatingDueDate, long task_calendarEventId) {
        this.task_id = task_id;
        this.task_priority = task_priority;
        this.task_status = task_status;
        this.task_title = task_title;
        this.task_details = task_details;
        this.task_dueDateTime = task_dueDateTime;
        this.task_remainder = task_remainder;
        this.task_repeating = task_repeating;
        this.task_remainderDueDate = task_remainderDueDate;
        this.task_repeatingDueDate = task_repeatingDueDate;
        this.task_calendarEventId = task_calendarEventId;
    }

    public Task(String task_title, int task_status,  int task_priority, String task_details, long task_dueDateTime, boolean task_remainder, boolean task_repeating, long task_remainderDueDate, long task_repeatingDueDate, long task_calendarEventId) {
        this.task_status = task_status;
        this.task_priority = task_priority;
        this.task_title = task_title;
        this.task_details = task_details;
        this.task_dueDateTime = task_dueDateTime;
        this.task_remainder = task_remainder;
        this.task_repeating = task_repeating;
        this.task_remainderDueDate = task_remainderDueDate;
        this.task_repeatingDueDate = task_repeatingDueDate;
        this.task_calendarEventId = task_calendarEventId;
    }

    public Task()
    {

    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }
    public int getTask_status() {
        return task_status;
    }

    public void setTask_status(int task_status) {
        this.task_status = task_status;
    }

    public int getTask_priority() {
        return task_priority;
    }

    public void setTask_priority(int task_priority) {
        this.task_priority = task_priority;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_details() {
        return task_details;
    }

    public void setTask_details(String task_details) {
        this.task_details = task_details;
    }

    public long getTask_dueDateTime() {
        return task_dueDateTime;
    }

    public void setTask_dueDateTime(long task_dueDateTime) {
        this.task_dueDateTime = task_dueDateTime;
    }

    public boolean isTask_remainder() {
        return task_remainder;
    }

    public void setTask_remainder(boolean task_remainder) {
        this.task_remainder = task_remainder;
    }

    public boolean isTask_repeating() {
        return task_repeating;
    }

    public void setTask_repeating(boolean task_repeating) {
        this.task_repeating = task_repeating;
    }

    public long getTask_remainderDueDate() {
        return task_remainderDueDate;
    }

    public void setTask_remainderDueDate(long task_remainderDueDate) {
        this.task_remainderDueDate = task_remainderDueDate;
    }

    public long getTask_repeatingDueDate() {
        return task_repeatingDueDate;
    }

    public void setTask_repeatingDueDate(long task_repeatingDueDate) {
        this.task_repeatingDueDate = task_repeatingDueDate;
    }

    public long getTask_calendarEventId() {
        return task_calendarEventId;
    }

    public void setTask_calendarEventId(long task_calendarEventId) {
        this.task_calendarEventId = task_calendarEventId;
    }
}
