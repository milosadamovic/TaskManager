package com.example.taskmanager.high_priority_tasks;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.creating_task.CreatingTaskFragment;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.main.OnButtonAddTaskClickedListener;
import com.example.taskmanager.util.DefaultParameters;
import com.example.taskmanager.util.Util;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HighPriorityTasksAdapter extends RecyclerView.Adapter<HighPriorityTasksAdapter.CustomViewHolder>{


    // TODO ZASTO CONTEXT MORA BITI OVDE ?!!
    Context ctx;
    List<Task> taskList;
    List<Boolean> collapsableTaskList;
    FragmentManager fm;
    OnButtonAddTaskClickedListener listener;



    public HighPriorityTasksAdapter(Context ctx, List<Task> taskList, List<Boolean> collapsableTaskList, FragmentManager fm, OnButtonAddTaskClickedListener listener)
    {
        this.ctx = ctx;
        this.taskList = taskList;
        this.collapsableTaskList = collapsableTaskList;
        this.fm = fm;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.high_priority_task_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTask_title());
        holder.tvTimeLeft.setText(Util.getRemainingTime(task.getTask_dueDateTime()));
        holder.tvTaskDetails.setText(task.getTask_details());

        holder.clCollapsableContent.setVisibility(collapsableTaskList.get(position) ? View.VISIBLE : View.GONE);

        holder.clMainContent.setOnClickListener( v -> {
            collapsableTaskList.set(position, !collapsableTaskList.get(position));
            notifyItemChanged(position);
        });

        holder.ivEditTask.setOnClickListener( v -> {

            if(task.getTask_status() != DefaultParameters.TASK_UNSUCCESSFUL)
            {

                Bundle bundle = new Bundle();
                long[] longArr = new long[]{task.getTask_dueDateTime(), task.getTask_repeatingDueDate(), task.getTask_remainderDueDate(), task.getTask_id(), task.getTask_calendarEventId()};
                boolean[] boolArr = new boolean[]{task.isTask_repeating(), task.isTask_remainder()};
                String[] strArr = new String[]{task.getTask_title(), task.getTask_details()};

                bundle.putLongArray("longArr", longArr);
                bundle.putBooleanArray("boolArr", boolArr);
                bundle.putStringArray("strArr", strArr);

                bundle.putInt("priority", DefaultParameters.HIGH_PRIORITY_TASK);
                CreatingTaskFragment createTask = new CreatingTaskFragment();
                createTask.setArguments(bundle);

                if(listener != null)
                    listener.disableChangingTabs();

                fm.beginTransaction()
                        .replace(R.id.fragment_container, createTask)
                        .addToBackStack(null)
                        .commit();

            }
            else Toast.makeText(ctx, "You can't edit finished task !", Toast.LENGTH_SHORT).show();

        });


    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvTimeLeft, tvTaskDetails;
        ConstraintLayout  clMainContent, clCollapsableContent;
        ImageView ivEditTask;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            tvTaskDetails = itemView.findViewById(R.id.tvTaskDetails);
            clMainContent = itemView.findViewById(R.id.clMainContent);
            clCollapsableContent = itemView.findViewById(R.id.clCollapsableContent);
            ivEditTask = itemView.findViewById(R.id.ivEditTask);
        }
    }
}
