package com.example.taskmanager.high_priority_tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanager.R;
import com.example.taskmanager.creating_task.CreatingTaskFragment;


public class HighPriorityTasksFragmentParent extends Fragment {

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TESTY", "HighPriorityFragmentParent onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TESTY", "HighPriorityFragmentParent onCreateView()");
        return inflater.inflate(R.layout.fragment_high_priority_tasks_parent, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TESTY", "HighPriorityFragmentParent onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TESTY", "HighPriorityFragmentParent onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TESTY", "HighPriorityFragmentParent onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TESTY", "HighPriorityFragmentParent onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TESTY", "HighPriorityFragmentParent onDestroy()");
    }
}