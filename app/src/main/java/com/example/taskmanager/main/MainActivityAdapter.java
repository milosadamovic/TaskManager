package com.example.taskmanager.main;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.taskmanager.high_priority_tasks.HighPriorityTasksFragmentParent;
import com.example.taskmanager.low_priority_tasks.LowPriorityTasksFragmentParent;
import com.example.taskmanager.middle_priority_tasks.MiddlePriorityTasksFragmentParent;
import com.example.taskmanager.util.DefaultFragment;
import com.example.taskmanager.util.DefaultParameters;

public class MainActivityAdapter extends FragmentStateAdapter {

    public MainActivityAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d("TAGY", "MainActivityAdapter, position: " + position);
        Fragment fragment;

       switch (position)
        {
            case 0:
                    fragment = new HighPriorityTasksFragmentParent();
                    Log.d("TAGY", "MainActivityAdapter, HighPriorityTasksFragmentParent added");
                    break;
            case 1:
                    fragment = new MiddlePriorityTasksFragmentParent();
                    Log.d("TAGY", "MainActivityAdapter, MiddlePriorityTasksFragmentParent added");
                    break;
            case 2:
                    fragment = new LowPriorityTasksFragmentParent();
                    Log.d("TAGY", "MainActivityAdapter, LowPriorityTasksFragmentParent added");
                    break;

            default: fragment = new DefaultFragment(); break;

        }

        return fragment;
    }


    @Override
    public int getItemCount() {

        return DefaultParameters.TAB_COUNT;

    }
}
