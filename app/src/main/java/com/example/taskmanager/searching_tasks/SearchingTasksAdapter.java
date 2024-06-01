package com.example.taskmanager.searching_tasks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.creating_task.CreatingTaskFragment;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.high_priority_tasks.HighPriorityTasksAdapter;
import com.example.taskmanager.high_priority_tasks.HighPriorityTasksFragmentParent;
import com.example.taskmanager.middle_priority_tasks.MiddlePriorityTasksFragmentParent;
import com.example.taskmanager.util.DefaultParameters;
import com.example.taskmanager.util.Util;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SearchingTasksAdapter extends RecyclerView.Adapter<SearchingTasksAdapter.CustomViewHolder> {


    Context ctx;
    List<Task> taskList;
    List<Boolean> collapsableTaskList;
    FragmentManager fm;
    int cardColour;


    public SearchingTasksAdapter(Context ctx, List<Task> taskList, List<Boolean> collapsableTaskList, FragmentManager fm)
    {
        this.ctx = ctx;
        this.taskList = taskList;
        this.collapsableTaskList = collapsableTaskList;
        this.fm = fm;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_task_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTask_title());
        holder.tvTimeLeft.setText(Util.getRemainingTime(task.getTask_dueDateTime()));
        holder.tvTaskDetails.setText(task.getTask_details());

        cardColour = setCardColour(task.getTask_priority());
        holder.materialCardView.setBackgroundTintList(ColorStateList.valueOf(cardColour));

        holder.clCollapsableContent.setVisibility(collapsableTaskList.get(position) ? View.VISIBLE : View.GONE);

        holder.clMainContent.setOnClickListener( v -> {
            collapsableTaskList.set(position, !collapsableTaskList.get(position));
            notifyItemChanged(position);
        });

        holder.ivEditTask.setOnClickListener( v -> {

            if(task.getTask_status() != DefaultParameters.TASK_UNSUCCESSFUL && task.getTask_status() != DefaultParameters.TASK_SUCCESSFULL)
            {

                Bundle bundle = new Bundle();
                long[] longArr = new long[]{task.getTask_dueDateTime(), task.getTask_repeatingDueDate(), task.getTask_remainderDueDate(), task.getTask_id(), task.getTask_calendarEventId()};
                boolean[] boolArr = new boolean[]{task.isTask_repeating(), task.isTask_remainder()};
                String[] strArr = new String[]{task.getTask_title(), task.getTask_details()};

                bundle.putLongArray("longArr", longArr);
                bundle.putBooleanArray("boolArr", boolArr);
                bundle.putStringArray("strArr", strArr);

                bundle.putInt("priority", task.getTask_priority());
                bundle.putInt("search",DefaultParameters.SEARCH);
                CreatingTaskFragment createTask = new CreatingTaskFragment();
                createTask.setArguments(bundle);

                fm.beginTransaction()
                        .replace(R.id.fragment_container, createTask)
                        .addToBackStack(null)
                        .commit();
            }
            else Toast.makeText(ctx, "You can only edit tasks in progress! !", Toast.LENGTH_SHORT).show();

        });
    }

    public int setCardColour(int taskPriority)
    {
        switch (taskPriority)
        {
            case DefaultParameters.HIGH_PRIORITY_TASK: cardColour = ContextCompat.getColor(ctx, R.color.priorityHighSecondary); break;
            case DefaultParameters.MIDDLE_PRIORITY_TASK: cardColour = ContextCompat.getColor(ctx, R.color.priorityMiddleSecondary); break;
            case DefaultParameters.LOW_PRIORITY_TASK: cardColour = ContextCompat.getColor(ctx,R.color.priorityLowSecondary); break;

            default: ContextCompat.getColor(ctx, R.color.priorityHighPrimary); break;
        }
        return cardColour;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvTimeLeft, tvTaskDetails;
        ConstraintLayout clMainContent, clCollapsableContent;
        ImageView ivEditTask;
        MaterialCardView materialCardView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            tvTaskDetails = itemView.findViewById(R.id.tvTaskDetails);
            clMainContent = itemView.findViewById(R.id.clMainContent);
            clCollapsableContent = itemView.findViewById(R.id.clCollapsableContent);
            ivEditTask = itemView.findViewById(R.id.ivEditTask);
            materialCardView = itemView.findViewById(R.id.materialCardView);
        }
    }
}
