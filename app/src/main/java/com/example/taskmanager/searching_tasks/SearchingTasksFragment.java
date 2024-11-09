package com.example.taskmanager.searching_tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.R;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.main.CustomViewModel;
import com.example.taskmanager.main.MainActivity;
import com.example.taskmanager.main.OnButtonAddTaskClickedListener;
import com.example.taskmanager.services.DueDateService;
import com.example.taskmanager.util.DefaultParameters;
import com.example.taskmanager.util.Util;

import java.util.ArrayList;
import java.util.List;

public class SearchingTasksFragment extends Fragment {


    private Toolbar toolbar;
    private TextView tvEmptySearchMsg;
    private RecyclerView recyclerView;
    private SearchingTasksAdapter adapter;
    private CustomViewModel viewModel;

    private List<Task> taskList = new ArrayList<>();
    private List<Boolean> collapsableTaskList = new ArrayList<>();
    AppCompatActivity activity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(CustomViewModel.class);


        OnBackPressedDispatcher onBackPressedDispatcher =activity.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(requireContext(), MainActivity.class));
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_searching_tasks, container, false);

        tvEmptySearchMsg = view.findViewById(R.id.tvEmptySearchMsg);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SearchingTasksAdapter(requireContext(), taskList, collapsableTaskList, getParentFragmentManager());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_searching_menu,menu);
        MenuItem actionSearchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) actionSearchItem.getActionView();

        actionSearchItem.expandActionView();
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //searchTasks(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty())
                    searchTasks(newText);
                else
                {
                    taskList.clear();
                    collapsableTaskList.clear();
                    adapter.notifyDataSetChanged();
                    tvEmptySearchMsg.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

    }
    public void searchTasks(String text)
    {
        viewModel.getSearchedTasks("%" + text + "%").observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {

                if(tasks.isEmpty())
                    tvEmptySearchMsg.setVisibility(View.VISIBLE);
                else tvEmptySearchMsg.setVisibility(View.GONE);

                taskList.clear();
                collapsableTaskList.clear();

                taskList.addAll(tasks);
                for(int i = 0; i < taskList.size(); i++)
                    collapsableTaskList.add(Boolean.FALSE);

                adapter.notifyDataSetChanged();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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

                                    if(t.getTask_dueDateTime() == 0)
                                        viewModel.deleteTask(t);
                                    else Toast.makeText(requireContext(), "You can't delete tasks in progress from here!", Toast.LENGTH_SHORT).show();

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

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}