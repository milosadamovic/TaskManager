package com.example.taskmanager.middle_priority_tasks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanager.R;


public class MiddlePriorityTasksFragmentParent extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onCreateView()");
        return inflater.inflate(R.layout.fragment_middle_priority_tasks_parent, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TESTY", "MiddlePriorityTasksFragmentParent onDestroy()");
    }
}