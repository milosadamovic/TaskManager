package com.example.taskmanager.main;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanager.R;
import com.example.taskmanager.receivers.BootReceiver;
import com.example.taskmanager.searching_tasks.SearchingTasksActivityParent;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.security.Permission;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnButtonAddTaskClickedListener {

    private Toolbar mainToolbar;
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private TabLayoutMediator tabMediator;
    private MainActivityAdapter adapter;

    private PowerManager powerManager;
    private AlarmManager alarmManager;


    private static final String NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS;
    private static final String CALENDAR_READ_PERMISSION = Manifest.permission.READ_CALENDAR;
    private static final String CALENDAR_WRITE_PERMISSION = Manifest.permission.WRITE_CALENDAR;
    private static final String RECEIVE_BOOT_COMPLETED_PERMISSION = Manifest.permission.RECEIVE_BOOT_COMPLETED;


    private final int REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        alarmManager = ContextCompat.getSystemService(this, AlarmManager.class);

        showPermissionDialog();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appBarColorLight));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.appBarColorLight));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        mainToolbar = findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("");
        setSupportActionBar(mainToolbar);

        pager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new MainActivityAdapter(getSupportFragmentManager(), getLifecycle());
        pager.setAdapter(adapter);

        tabMediator = new TabLayoutMediator(tabLayout, pager, (tab, position) -> {

             switch(position)
             {
                 case 0:  tab.setText("HIGH"); break;
                 case 1:  tab.setText("MIDDLE"); break;
                 case 2:  tab.setText("LOW"); break;
             }
         });
        tabMediator.attach();

        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.priorityHighPrimary));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition())
                {
                    case 0:  tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.priorityHighPrimary)); break;
                    case 1 : tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.priorityMiddlePrimary)); break;
                    case 2 : tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.priorityLowPrimary)); break;
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
         });

        pager.setUserInputEnabled(false);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
       }

    @Override
    public void disableChangingTabs() {

        Objects.requireNonNull(tabLayout.getTabAt(0)).view.setClickable(false);
        Objects.requireNonNull(tabLayout.getTabAt(1)).view.setClickable(false);
        Objects.requireNonNull(tabLayout.getTabAt(2)).view.setClickable(false);
    }

    @Override
    public void enableChangingTabs() {

        Objects.requireNonNull(tabLayout.getTabAt(0)).view.setClickable(true);
        Objects.requireNonNull(tabLayout.getTabAt(1)).view.setClickable(true);
        Objects.requireNonNull(tabLayout.getTabAt(2)).view.setClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(R.id.action_search == itemId) {
            Intent searchTasks= new Intent(this, SearchingTasksActivityParent.class);
            startActivity(searchTasks);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }



    private final ActivityResultLauncher<Intent> batteryOptimizationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                            Toast.makeText(this, "Without OPTIMIZATION permission you will not be able to use the app!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
            });

    private final ActivityResultLauncher<Intent> scheduleExactAlarmPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (!alarmManager.canScheduleExactAlarms())
                        {
                            Toast.makeText(this, "Without ALARM permission you will not be able to use the app!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            if(!powerManager.isIgnoringBatteryOptimizations(getPackageName()))
                            {
                                Intent pmIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
                                batteryOptimizationLauncher.launch(pmIntent);
                            }
                        }

                    });


    public void showPermissionDialog()
    {
       if( ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
               && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
               && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
               && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED
               && alarmManager.canScheduleExactAlarms() && powerManager.isIgnoringBatteryOptimizations(getPackageName()))
       {
       }
       else ActivityCompat.requestPermissions(this, new String[] {NOTIFICATION_PERMISSION, CALENDAR_READ_PERMISSION, CALENDAR_WRITE_PERMISSION, RECEIVE_BOOT_COMPLETED_PERMISSION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length > 0)
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED)
                {
                    if(alarmManager.canScheduleExactAlarms())
                    {
                        if(powerManager.isIgnoringBatteryOptimizations(getPackageName()))
                        {
                            //Toast.makeText(this, "Permissions Granted 2 !", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent pmIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
                            batteryOptimizationLauncher.launch(pmIntent);
                        }
                    }
                    else
                    {
                        Intent amIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()));
                        scheduleExactAlarmPermissionLauncher.launch(amIntent);
                    }
                }
                else
                {
                    Toast.makeText(this, "All permissions must be granted for app to work!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else
            {
                Toast.makeText(this, "All permissions must be granted for app to work!", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else showPermissionDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}