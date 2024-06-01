package com.example.taskmanager.low_priority_tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanager.R;


public class LowPriorityTasksFragmentParent extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TESTY", "LowPriorityTasksFragmentParent onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TESTY", "LowPriorityTasksFragmentParent onCreateView()");
        return inflater.inflate(R.layout.fragment_low_priority_tasks_parent, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("TESTY", "LowPriorityTasksFragmentParent onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TESTY", "LowPriorityTasksFragmentParent onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TESTY", "LowPriorityTasksFragmentParent onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TESTY", "LowPriorityTasksFragmentParent onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TESTY", "LowPriorityTasksFragmentParent onDestroy()");
    }

}