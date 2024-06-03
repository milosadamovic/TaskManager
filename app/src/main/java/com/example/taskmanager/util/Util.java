package com.example.taskmanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taskmanager.db_model.Task;

import java.util.Objects;
import java.util.TimeZone;

public class Util {


    public static String[] getRepeatingTime(long repeatingTime, long dueDateTime) {

        String[] res = new String[2];

        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final long DAY_MILLIS = 24 * HOUR_MILLIS;
        final long WEEK_MILLIS = 7 * DAY_MILLIS;
        final double MONTH_MILLIS = 31 * DAY_MILLIS;
        final double YEAR_MILLIS = 12 * MONTH_MILLIS;


        final long diff = repeatingTime - dueDateTime;

        if (diff < 2 * DAY_MILLIS) {
            res[0] = "1";
            res[1] = "day";
            return res;
        } else if (diff <= 15 * DAY_MILLIS) {
            long result = (long) Math.ceil((double) diff / DAY_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "day";
            return res;
        } else if (diff <= 5 * WEEK_MILLIS) {
            if(diff > 4 * WEEK_MILLIS && diff < 32 * DAY_MILLIS)
            {
                res[0] = String.valueOf(1);
                res[1] = "month";
                return res;
            }
            long result = (long) Math.ceil((double) diff / WEEK_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "week";
            return res;
        } else if (diff <= 15 * MONTH_MILLIS) {
            long result = (long) Math.ceil((double) diff / MONTH_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "month";
            return res;
        }else {
            long result = (long) Math.ceil((double) diff / YEAR_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "year";
            return res;
        }
    }

    public static String[] getRemainderTime(long remainderTime, long dueDateTime) {
        String[] res = new String[2];

        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final long DAY_MILLIS = 24 * HOUR_MILLIS;
        final long WEEK_MILLIS = 7 * DAY_MILLIS;


        final long diff = dueDateTime - remainderTime;

        if (diff < 2 * MINUTE_MILLIS) {
            res[0] = "1";
            res[1] = "minute";
            return res;
        } else if (diff <= 99 * MINUTE_MILLIS) {
            long result = (long) Math.ceil((double) diff / MINUTE_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "minute";
            return res;
        }else if (diff <= 99 * HOUR_MILLIS) {
            long result = (long) Math.ceil((double) diff / HOUR_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "hour";
            return res;
        } else if (diff < 8 * DAY_MILLIS) {
            long result = (long) Math.ceil((double) diff / DAY_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "day";
            return res;
        } else {
            long result = (long) Math.ceil((double) diff / WEEK_MILLIS);
            res[0] = String.valueOf(result);
            res[1] = "week";
            return res;
        }
    }

    public static String getRemainingTime(long taskDueDate) {

        String res = "";

        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final long DAY_MILLIS = 24 * HOUR_MILLIS;

        final double MONTH_MILLIS = 30.42 * DAY_MILLIS;
        final double YEAR_MILLIS = 12 * MONTH_MILLIS;

        long now = System.currentTimeMillis();

        if (taskDueDate < now || taskDueDate <= 0) return "finished";

        final long diff = taskDueDate - now;

        if (diff < MINUTE_MILLIS)
            return res = "tik tok";

        else if (diff < 2 * MINUTE_MILLIS)
            return res = "1 minute left";
        else if (diff < 59 * MINUTE_MILLIS)
            return res = (int)Math.ceil((double) diff / MINUTE_MILLIS) + " minutes left";
        else if (diff < 90 * MINUTE_MILLIS)
            return res = "1 hour left";
        else if (diff <= 24 * HOUR_MILLIS)
            return res = (int)Math.ceil((double) diff / HOUR_MILLIS) + " hours left";
        else if (diff < 2 * DAY_MILLIS)
            return res = "1 day left";
        else if (diff < 30 * DAY_MILLIS)
            return res = (int)Math.ceil((double) diff / DAY_MILLIS) + " days left";
        else if (diff < 2L * MONTH_MILLIS)
            return res = "1 month left";
        else if (diff < 12L * MONTH_MILLIS)
            return res = (int) (Math.ceil(diff / MONTH_MILLIS)) + " months left";
        else if (diff < 2L * YEAR_MILLIS)
            return res = "1 year left";
        else return res = (int)(Math.ceil(diff / YEAR_MILLIS)) + " years left";

    }

    public static long addEventToCalendar(@NonNull Context ctx, @NonNull String taskTitle, String taskDetails, long taskDueDate, long taskRemainderTime, int taskPriority)
    {

        int eventColor = Color.GREEN;

        if(taskPriority == DefaultParameters.HIGH_PRIORITY_TASK) eventColor = Color.RED;
        else if(taskPriority == DefaultParameters.LOW_PRIORITY_TASK) eventColor = Color.BLUE;


        ContentResolver cr = ctx.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, taskDueDate);
        values.put(CalendarContract.Events.DTEND, taskDueDate);
        values.put(CalendarContract.Events.TITLE, taskTitle);
        values.put(CalendarContract.Events.DESCRIPTION, taskDetails);
        values.put(CalendarContract.Events.CALENDAR_ID, getPrimaryCalendarId(ctx));
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.EVENT_COLOR, eventColor);

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        if (uri != null)
        {
            long eventId = Long.parseLong(Objects.requireNonNull(uri.getLastPathSegment()));

            if(taskRemainderTime > 0)
                insertCalendarRemainder(ctx, cr, taskDueDate, taskRemainderTime, eventId);

            //Toast.makeText(ctx, "Event added to calendar", Toast.LENGTH_SHORT).show();
            return eventId;
        }

        return -1;
    }

    public static long updateEventInCalendar(@NonNull Context ctx, @NonNull String taskTitle, String taskDetails, long taskDueDate, long taskRemainderTime, int taskPriority, long eventId) {


        int eventColor = Color.GREEN;

        if(taskPriority == DefaultParameters.HIGH_PRIORITY_TASK) eventColor = Color.RED;
        else if(taskPriority == DefaultParameters.LOW_PRIORITY_TASK) eventColor = Color.BLUE;

        ContentResolver cr = ctx.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, taskDueDate);
        values.put(CalendarContract.Events.DTEND, taskDueDate);
        values.put(CalendarContract.Events.TITLE, taskTitle);
        values.put(CalendarContract.Events.DESCRIPTION, taskDetails);
        values.put(CalendarContract.Events.EVENT_COLOR, eventColor);

        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.update(updateUri, values, null, null);

        if(rows > 0 && taskRemainderTime > 0)
        {
            if(deleteCalendarRemainder(ctx, cr, eventId) != -1)
            {
                insertCalendarRemainder(ctx, cr, taskDueDate, taskRemainderTime, eventId);
                return eventId;
            }
        }
        else if (rows > 0 && taskRemainderTime == 0)
        {
            if(deleteCalendarRemainder(ctx, cr, eventId) == -1)
                Toast.makeText(ctx, "Error while finding a remainder!", Toast.LENGTH_SHORT).show();
            else return eventId;
        }
        else
        {
            Toast.makeText(ctx, "Failed to update Event so insert new!", Toast.LENGTH_SHORT).show();
            return addEventToCalendar(ctx, taskTitle, taskDetails, taskDueDate, taskRemainderTime, taskPriority);
        }

        return -1;
    }

    public static void deleteEventFromCalendar(Context ctx, long eventId)
    {
        ContentResolver cr = ctx.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.delete(deleteUri, null, null);

        if (rows > 0 && deleteCalendarRemainder(ctx, cr, eventId) != -1)
            Log.d("TEST","Event deleted successfully");
            //Toast.makeText(ctx, "Event deleted successfully", Toast.LENGTH_SHORT).show();
        else Toast.makeText(ctx, "Failed to delete event from Calendar", Toast.LENGTH_SHORT).show();

    }

    public static void insertCalendarRemainder(Context ctx, ContentResolver cr, long taskDueDate, long taskRemainderTime, long eventId)
    {
        long timeDiffInMinutes = (taskDueDate - taskRemainderTime) / (60 * 1000);

        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.MINUTES, timeDiffInMinutes);
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

        if (reminderUri != null)
            Log.d("TEST","Remainder added to calendar");
            //Toast.makeText(ctx, "Remainder added to calendar", Toast.LENGTH_SHORT).show();
        else Toast.makeText(ctx, "Failed to add Remainder to calendar", Toast.LENGTH_SHORT).show();
    }



    public static int deleteCalendarRemainder(Context ctx, ContentResolver cr, long eventId)
    {
        String selection = CalendarContract.Reminders.EVENT_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(eventId)};

        Cursor cursor = cr.query(CalendarContract.Reminders.CONTENT_URI, null, selection, selectionArgs, null);

        if (cursor != null)
        {
            try {
                if (cursor.getCount() > 0)
                {
                    int rows = cr.delete(CalendarContract.Reminders.CONTENT_URI, selection, selectionArgs);
                    if (rows > 0)
                    {
                        //Toast.makeText(ctx, "Reminder removed for event", Toast.LENGTH_SHORT).show();
                        return 1;

                    }  else Toast.makeText(ctx, "Failed removing remainder", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    //Toast.makeText(ctx, "No reminder found for event", Toast.LENGTH_SHORT).show();
                    return 0;
                }

            } finally {
                cursor.close();
            }
        } else Toast.makeText(ctx, "Failed to query reminders", Toast.LENGTH_SHORT).show();

        return  -1;
    }


    public static long getPrimaryCalendarId(@NonNull Context ctx)
    {
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.IS_PRIMARY
        };
        Cursor cursor = null;
        long primaryCalendarId = -1;

        try {
            cursor = ctx.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                    int primaryIndex = cursor.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);

                    if (idIndex != -1 && primaryIndex != -1) {
                        long calendarId = cursor.getLong(idIndex);
                        int isPrimary = cursor.getInt(primaryIndex);

                        if (isPrimary == 1) {
                            primaryCalendarId = calendarId;
                            break;
                        }
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Fallback: if no primary calendar is found, return the first available calendar
        if (primaryCalendarId == -1 && cursor == null) {
            try {
                cursor = ctx.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);

                    if (idIndex != -1) {
                        primaryCalendarId = cursor.getLong(idIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        Log.d("TEST_CALENDAR", "CALENDAR_ID: " + primaryCalendarId);
        return primaryCalendarId;

    }
}

