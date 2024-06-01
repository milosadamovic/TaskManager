package com.example.taskmanager.high_priority_tasks;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.R;
import com.example.taskmanager.creating_task.CreatingTaskFragment;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.main.CustomViewModel;
import com.example.taskmanager.services.DueDateService;
import com.example.taskmanager.util.DefaultParameters;
import com.example.taskmanager.main.OnButtonAddTaskClickedListener;
import com.example.taskmanager.receivers.NotificationRemainderReceiver;
import com.example.taskmanager.receivers.TaskFinishedReceiver;
import com.example.taskmanager.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class HighPriorityTasksFragment extends Fragment{


    private FloatingActionButton fabCreateTask;
    private TextView tvEmptyListMsg;
    private RecyclerView recyclerView;
    private OnButtonAddTaskClickedListener listener;
    private HighPriorityTasksAdapter adapter;
    private List<Task> taskList = new ArrayList<>();
    private List<Boolean> collapsableTaskList = new ArrayList<>();
    private CustomViewModel viewModel;

    long newDueDate, newRemainderDueDate, newRepeatDueDate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("TESTY", "HighPriorityFragment onAttach()");

        try {
            listener = (OnButtonAddTaskClickedListener) context;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement OnButtonAddTaskClickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TESTY", "HighPriorityFragment onCreate()");

        viewModel = new ViewModelProvider(this).get(CustomViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TESTY", "HighPriorityFragment onCreateView()");

        View view = inflater.inflate(R.layout.fragment_high_priority_tasks, container, false);
        fabCreateTask = view.findViewById(R.id.fabCreateTask);
        tvEmptyListMsg = view.findViewById(R.id.tvEmptyListMsg);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getTasksByPriority(DefaultParameters.HIGH_PRIORITY_TASK).observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {

                if(tasks.isEmpty())
                    tvEmptyListMsg.setVisibility(View.VISIBLE);
                else tvEmptyListMsg.setVisibility(View.GONE);

                taskList.clear();
                collapsableTaskList.clear();

                taskList.addAll(tasks);
                for(int i = 0; i < taskList.size(); i++)
                    collapsableTaskList.add(Boolean.FALSE);

                adapter.notifyDataSetChanged();
            }
        });

        adapter = new HighPriorityTasksAdapter(requireContext(), taskList, collapsableTaskList, getParentFragmentManager(), listener);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Task t = taskList.get(viewHolder.getAdapterPosition());

                if(direction == ItemTouchHelper.LEFT)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("DELETE THIS TASK ?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Util.deleteEventFromCalendar(requireContext(), t.getTask_calendarEventId());
                                    cancelTaskNotifications(t);
                                    viewModel.deleteTask(t);
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                    builder.create().show();
                }
                else if(direction == ItemTouchHelper.RIGHT)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("SUBMIT THIS TASK ?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if(t.getTask_status() != DefaultParameters.TASK_UNSUCCESSFUL)
                                    {
                                        if(!t.isTask_repeating())
                                        {
                                            cancelTaskNotifications(t);
                                            Util.deleteEventFromCalendar(requireContext(), t.getTask_calendarEventId());
                                            viewModel.updateTaskStatus(t.getTask_id(), DefaultParameters.TASK_SUCCESSFULL);
                                            Toast.makeText(requireContext(), "You successfully submitted " + t.getTask_title() + " task !", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            cancelTaskNotifications(t);
                                            getNewDueDate(t);

                                            if(!t.isTask_remainder())
                                                newRemainderDueDate = 0;
                                            else getNewRemainderDueDate(t);


                                            int task_status = DefaultParameters.TASK_IN_PROGRESS;
                                            int task_priority = DefaultParameters.HIGH_PRIORITY_TASK;
                                            String task_title = t.getTask_title();
                                            String task_details = t.getTask_details();
                                            boolean task_remainder = t.isTask_remainder();
                                            boolean task_repeating = t.isTask_repeating();
                                            long task_dueDateTime = newDueDate;
                                            long task_remainderDueDate = newRemainderDueDate;
                                            long task_repeatingDueDate = newRepeatDueDate;
                                            long task_calendarEventId = t.getTask_calendarEventId();


                                            Task newTask = new Task(task_title, task_status, task_priority, task_details, task_dueDateTime, task_remainder, task_repeating, task_remainderDueDate, task_repeatingDueDate, task_calendarEventId);
                                            newTask.setTask_id(t.getTask_id());

                                            Util.updateEventInCalendar(requireContext(),task_title, task_details, task_dueDateTime,task_remainderDueDate, task_priority, task_calendarEventId);
                                            viewModel.updateTask(newTask);

                                            Intent serviceIntent = new Intent(requireContext(), DueDateService.class);
                                            serviceIntent.putExtra("taskId", t.getTask_id());
                                            serviceIntent.putExtra("due_date", task_dueDateTime);
                                            serviceIntent.putExtra("remainder_dueDate", task_remainderDueDate);
                                            serviceIntent.putExtra("title", task_title);

                                            requireContext().startService(serviceIntent);

                                            Toast.makeText(requireContext(), "You successfully submitted " + t.getTask_title() + " task !", Toast.LENGTH_SHORT).show();


                                        }

                                    }  else Toast.makeText(requireContext(), "Time for this task is finished!", Toast.LENGTH_SHORT).show();

                                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    });
                    builder.create().show();
                }

            }
        }).attachToRecyclerView(recyclerView);


        fabCreateTask.setOnClickListener(v -> {

            if(listener != null)
                listener.disableChangingTabs();

            Bundle bundle = new Bundle();
            bundle.putInt("priority", DefaultParameters.HIGH_PRIORITY_TASK);
            CreatingTaskFragment createTask = new CreatingTaskFragment();
            createTask.setArguments(bundle);

            //TODO CLEARING DB TABLES
            //viewModel.clearAllTables();

            getParentFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, createTask)
            .addToBackStack(null)
            .commit();
        });

        return view;
    }


    public void cancelTaskNotifications(Task t)
    {

        AlarmManager alarmManager = (AlarmManager) requireContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Canceling remainder and task finished notification
        Intent alarmIntent = new Intent(requireContext().getApplicationContext(), TaskFinishedReceiver.class);
        alarmIntent.setAction("ACTION_NOTIFICATION_" + Integer.hashCode((int)t.getTask_id()));
        alarmIntent.addCategory("/" + Long.valueOf(t.getTask_id()).hashCode());
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(requireContext().getApplicationContext(), Integer.hashCode(Long.valueOf(t.getTask_id()).hashCode()), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent alarmRemainderIntent = new Intent(requireContext().getApplicationContext(), NotificationRemainderReceiver.class);
        alarmRemainderIntent.setAction("ACTION_NOTIFICATION_" + t.getTask_id());
        alarmRemainderIntent.addCategory("/" + Long.valueOf(t.getTask_id()).hashCode());
        PendingIntent pendingRemainderIntent = PendingIntent.getBroadcast(requireContext().getApplicationContext(), Long.valueOf(t.getTask_id()).hashCode(), alarmRemainderIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(alarmPendingIntent);
        alarmManager.cancel(pendingRemainderIntent);
    }

    // TODO ZA TASKOVE KOJI SE PONAVLJAJU NA GODISNJEM NIVOU NISU UZETE U OBZIR (NE)PRESTUPNE GODINE - DOLAZI DO NETACNIH REZULTATA U SLUCAJU POJAVLJIVANJA PRESTUPNIH GODINA
    public void getNewDueDate(Task t)
    {
        newDueDate = t.getTask_repeatingDueDate();
        long timeDiff = t.getTask_repeatingDueDate() - t.getTask_dueDateTime();
        newRepeatDueDate = newDueDate + timeDiff;
    }

    public void getNewRemainderDueDate(Task t)
    {
        long timeDiff = t.getTask_dueDateTime() - t.getTask_remainderDueDate();
        newRemainderDueDate = newDueDate - timeDiff;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("TESTY", "HighPriorityFragment onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TESTY", "HighPriorityFragment onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TESTY", "HighPriorityFragment onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TESTY", "HighPriorityFragment onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TESTY", "HighPriorityFragment onDestroy()");
    }

}