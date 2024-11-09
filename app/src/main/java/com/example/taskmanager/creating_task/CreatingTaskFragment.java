package com.example.taskmanager.creating_task;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmanager.R;
import com.example.taskmanager.db_model.Task;
import com.example.taskmanager.high_priority_tasks.HighPriorityTasksFragment;
import com.example.taskmanager.low_priority_tasks.LowPriorityTasksFragment;
import com.example.taskmanager.main.CustomNumberPickerFormatter;
import com.example.taskmanager.main.CustomViewModel;
import com.example.taskmanager.searching_tasks.SearchingTasksFragment;
import com.example.taskmanager.util.DefaultFragment;
import com.example.taskmanager.util.DefaultParameters;
import com.example.taskmanager.main.OnButtonAddTaskClickedListener;
import com.example.taskmanager.middle_priority_tasks.MiddlePriorityTasksFragment;
import com.example.taskmanager.services.DueDateService;
import com.example.taskmanager.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class CreatingTaskFragment extends Fragment {


    FloatingActionButton fabAddTask, fabCancelTask;
    Button btnToggleDate, btnToggleTime;
    TextInputLayout tfTitle, tfDetails, tf;
    EditText etTitle, etDetails, etRb1, etRb2, etRb3, etRb4, et;
    TextView tvRemainderNumberResult, tvRemainderPeriodResult, tvRepeatNumberResult, tvRepeatPeriodResult, tvRepeatToggle, tvRemindToggle;
    List<EditText> etRbList;
    RadioButton rb;
    RadioGroup rg;
    SwitchMaterial swt_repeat, swt_remainder;
    CalendarView calendar;
    TimePicker timePicker;
    NumberPicker npNumber, npPeriod;
    LinearLayout llRepeatGroup, llRemainderGroup, llRepeatResult, llRemainderResult;
    Fragment pre_fragment;
    String  currentDate, currentTime;
    String[] periods = {"minute", "hour", "day", "week"};
    private OnButtonAddTaskClickedListener listener;
    private int priorityColorPrimary, priorityColorSecondary;
    private boolean doRepeat = false, doRemind = false;
    SimpleDateFormat dateFormat, timeFormat, dateTimeFormat;
    private int priority;
    private String priorityText;

    CustomViewModel viewModel;

    long task_id;
    int task_priority, task_status;
    String task_title, task_details;
    boolean task_remainder, task_repeating;
    long task_dueDateTime, task_remainderDueDate, task_repeatingDueDate, task_calendarEventId;
    boolean taskEditMode = false;

    boolean[] boolArr;
    String[] strArr;
    long[] longArr;

    int search;
    AppCompatActivity activity;


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
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
        activity = (AppCompatActivity) getActivity();
        viewModel = new ViewModelProvider(this).get(CustomViewModel.class);
        taskEditMode = false;

        boolArr = new boolean[2];
        strArr = new String[2];
        longArr = new long[5];

        Bundle bundle = getArguments();
        priority = (bundle != null) ? bundle.getInt("priority") : 0;
        search = (bundle != null) ? bundle.getInt("search") : 0;
        priorityColorPrimary = ContextCompat.getColor(requireContext(),R.color.colorSuccess);
        priorityColorSecondary = ContextCompat.getColor(requireContext(),R.color.textHandles);


        pre_fragment = new DefaultFragment();
        switch (priority)
        {
            case DefaultParameters.HIGH_PRIORITY_TASK :
                                    priorityColorPrimary = ContextCompat.getColor(requireContext(),R.color.priorityHighPrimary);
                                    priorityColorSecondary = ContextCompat.getColor(requireContext(),R.color.priorityHighSecondary);
                                    pre_fragment = new HighPriorityTasksFragment();
                                    priorityText = "High";
                                    break;
            case DefaultParameters.MIDDLE_PRIORITY_TASK :
                                    priorityColorPrimary = ContextCompat.getColor(requireContext(),R.color.priorityMiddlePrimary);
                                    priorityColorSecondary = ContextCompat.getColor(requireContext(),R.color.priorityMiddleSecondary);
                                    pre_fragment = new MiddlePriorityTasksFragment();
                                    priorityText = "Middle";
                                    break;
            case DefaultParameters.LOW_PRIORITY_TASK :
                                    pre_fragment = new LowPriorityTasksFragment();
                                    priorityColorPrimary = ContextCompat.getColor(requireContext(),R.color.priorityLowPrimary);
                                    priorityColorSecondary = ContextCompat.getColor(requireContext(),R.color.priorityLowSecondary);
                                    priorityText = "Low";
                                    break;
        }

        if (search > 0)
            pre_fragment = new SearchingTasksFragment();

         dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
         timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
         dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        if (bundle != null)
        {
            if(bundle.getLongArray("longArr") != null && bundle.getBooleanArray("boolArr") != null && bundle.getStringArray("strArr") != null)
            {
                longArr = bundle.getLongArray("longArr");
                boolArr = bundle.getBooleanArray("boolArr");
                strArr = bundle.getStringArray("strArr");

                taskEditMode = true;
            }
        }

        getCurrentDateTime();

        OnBackPressedDispatcher onBackPressedDispatcher = activity.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                activity.finishAffinity();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_creating_task, container, false);

        fabAddTask = view.findViewById(R.id.fabAddTask);
        fabCancelTask = view.findViewById(R.id.fabCancelTask);
        tfTitle = view.findViewById(R.id.tfTitle);
        tfDetails = view.findViewById(R.id.tfDetails);
        etTitle = view.findViewById(R.id.etTitle);
        etDetails = view.findViewById(R.id.etDetails);
        etRb1 = view.findViewById(R.id.etRb1);
        etRb2 = view.findViewById(R.id.etRb2);
        etRb3 = view.findViewById(R.id.etRb3);
        etRb4 = view.findViewById(R.id.etRb4);
        etRbList = new ArrayList<>();
        etRbList.add(etRb1); etRbList.add(etRb2); etRbList.add(etRb3); etRbList.add(etRb4);
        rg = view.findViewById(R.id.rg);
        calendar = view.findViewById(R.id.calendar);
        timePicker = view.findViewById(R.id.timePicker);
        btnToggleDate = view.findViewById(R.id.btnToggleDate);
        btnToggleTime = view.findViewById(R.id.btnToggleTime);
        swt_repeat = view.findViewById(R.id.swt_repeat);
        swt_remainder = view.findViewById(R.id.swt_remainder);
        tvRemainderNumberResult = view.findViewById(R.id.tvRemainderNumberResult);
        tvRemainderPeriodResult = view.findViewById(R.id.tvRemainderPeriodResult);
        tvRepeatNumberResult = view.findViewById(R.id.tvRepeatNumberResult);
        tvRepeatPeriodResult = view.findViewById(R.id.tvRepeatPeriodResult);
        tvRemindToggle = view.findViewById(R.id.tvRemindToggle);
        tvRepeatToggle = view.findViewById(R.id.tvRepeatToggle);
        npNumber = view.findViewById(R.id.npNumber);
        npPeriod = view.findViewById(R.id.npPeriod);
        llRepeatGroup = view.findViewById(R.id.llRepeatGroup);
        llRemainderGroup = view.findViewById(R.id.llRemainderGroup);
        llRepeatResult = view.findViewById(R.id.llRepeatResult);
        llRemainderResult = view.findViewById(R.id.llRemainderResult);

        tfTitle.setHint(priorityText + " " + getResources().getString(R.string.title_task_hint));
        tfTitle.setBoxStrokeColorStateList(ColorStateList.valueOf(priorityColorPrimary));
        tfTitle.setHintTextColor(ColorStateList.valueOf(priorityColorPrimary));
        tfTitle.setCursorColor(ColorStateList.valueOf(priorityColorPrimary));

        tfDetails.setBoxStrokeColorStateList(ColorStateList.valueOf(priorityColorPrimary));
        tfDetails.setHintTextColor(ColorStateList.valueOf(priorityColorPrimary));
        tfDetails.setCursorColor(ColorStateList.valueOf(priorityColorPrimary));


        tvRemainderNumberResult.setTextColor(priorityColorPrimary);
        tvRemainderPeriodResult.setTextColor(priorityColorPrimary);


        calendar.setVisibility(View.GONE);

        npPeriod.setMinValue(0);
        npPeriod.setMaxValue(periods.length-1);
        npPeriod.setDisplayedValues(periods);
        npPeriod.setWrapSelectorWheel(true);
        npPeriod.setFormatter(new CustomNumberPickerFormatter(periods));
        npPeriod.setOnValueChangedListener((picker, oldVal, newVal) -> {

            if(llRemainderResult.getVisibility() == View.GONE)
            {
                ObjectAnimator animator = ObjectAnimator.ofFloat(llRemainderResult, "alpha", 0f, 1f);
                llRemainderResult.setVisibility(View.VISIBLE);
                animator.setDuration(300);
                animator.start();
            }

            String value = "";
            if(npPeriod.getValue() == 2 && npNumber.getValue() >= 7)
            {
                npNumber.setValue(7);
                value = String.valueOf(npNumber.getValue());
            }else if(npPeriod.getValue() == 3 && npNumber.getValue() >= 4)
            {
                npNumber.setValue(4);
                value = String.valueOf(npNumber.getValue());
            }else value = String.valueOf(npNumber.getValue());

            tvRemainderNumberResult.setText(value);
            String text = " " + periods[newVal] + " before";
            tvRemainderPeriodResult.setText(text);
        });

        npNumber.setMinValue(1);
        npNumber.setMaxValue(99);
        npNumber.setOnValueChangedListener((picker, oldVal, newVal) -> {

            if(llRemainderResult.getVisibility() == View.GONE)
            {
                ObjectAnimator animator = ObjectAnimator.ofFloat(llRemainderResult, "alpha", 0f, 1f);
                llRemainderResult.setVisibility(View.VISIBLE);
                animator.setDuration(300);
                animator.start();
            }

            String value = "";

            if(npPeriod.getValue() == 2 && npNumber.getValue() >= 7)
            {
                npNumber.setValue(7);
                value = String.valueOf(npNumber.getValue());
            }else if(npPeriod.getValue() == 3 && npNumber.getValue() >= 4)
            {
                npNumber.setValue(4);
                value = String.valueOf(npNumber.getValue());
            }else value = String.valueOf(newVal);


            tvRemainderNumberResult.setText(value);
            String periodValue = " " + periods[npPeriod.getValue()] + " before";
            tvRemainderPeriodResult.setText(periodValue);
        });

        if(taskEditMode)
        {
            setFields();
        }
        else
        {
            if(currentDate != null)
                btnToggleDate.setText(currentDate);

            if(currentTime != null)
                btnToggleTime.setText(currentTime);
        }


        btnToggleDate.setOnClickListener( v -> toggleVisibility(DefaultParameters.CALENDAR_OPTION, true));
        btnToggleTime.setOnClickListener( v -> toggleVisibility(DefaultParameters.TIME_OPTION,true));

         Date currentDateTime = parsingStringToDate(currentDate, "dateFormat");
         if(currentDateTime != null)
         {
             calendar.setMinDate(currentDateTime.getTime());
             calendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {

                 String date = "";
                 if(dayOfMonth <= 9 && month <= 8)
                     date = "0" + dayOfMonth + "." + "0" + (month+1) + "." + year;
                 else if(dayOfMonth > 9 && month <= 8)
                     date = dayOfMonth + "." + "0" + (month+1) + "." + year;
                 else if(dayOfMonth <= 9)
                     date = "0" + dayOfMonth + "." + (month+1) + "." + year;
                 else date = dayOfMonth + "." + (month+1) + "." + year;

                 Date selectedDate = parsingStringToDate(date, "dateFormat");

                 if(selectedDate != null)
                 {
                     if(calendar.getMinDate() == selectedDate.getTime())
                     {
                         getCurrentDateTime();
                         btnToggleTime.setText(currentTime);
                     }
                     btnToggleDate.setText(date);

                 } else Toast.makeText(requireContext(), getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show();

             });
         } else Toast.makeText(requireContext(), getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show();


        timePicker.setOnTimeChangedListener((view12, hourOfDay, minute) -> {

             String selectedTime = hourOfDay + ":" + minute;
             if(minute <= 9) selectedTime = hourOfDay + ":" + "0" + minute;
             Calendar c = Calendar.getInstance();
             int currentHour = c.get(Calendar.HOUR_OF_DAY);
             int currentMinute = c.get(Calendar.MINUTE);


             if(currentDate.equals(btnToggleDate.getText().toString()))
             {
                 if(hourOfDay <= currentHour && minute <= currentMinute)
                 {
                     timePicker.setHour(currentHour);
                     timePicker.setMinute(currentMinute);
                 }else if (hourOfDay < currentHour)
                     timePicker.setHour(currentHour);
                 else btnToggleTime.setText(selectedTime);
             }
             else btnToggleTime.setText(selectedTime);

        });

        swt_repeat.setOnCheckedChangeListener((buttonView, isChecked) -> toggleVisibility(DefaultParameters.REPEAT_OPTION,isChecked));
        swt_remainder.setOnCheckedChangeListener((buttonView, isChecked) -> toggleVisibility(DefaultParameters.REMAINDER_OPTION,isChecked));

        etRb1.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "15")});
        etRb2.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});
        etRb3.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "15")});
        etRb4.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "2")});

        for(final EditText etRb : etRbList)
        {

            etRb.setMaxLines(1);

            etRb.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus)
                {
                    if(llRepeatResult.getVisibility() == View.GONE)
                    {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(llRepeatResult, "alpha", 0f, 1f);
                        llRepeatResult.setVisibility(View.VISIBLE);
                        animator.setDuration(300);
                        animator.start();
                    }

                    String tag = etRb.getTag().toString();
                    int indexTag = Integer.parseInt(tag.substring(2));
                    tf = view.findViewWithTag("tf"+indexTag);
                    tf.setBoxStrokeColorStateList(ColorStateList.valueOf(priorityColorPrimary));
                    tf.setCursorColor(ColorStateList.valueOf(priorityColorPrimary));
                    rb = view.findViewWithTag(""+indexTag);
                    if(rb != null)
                    {
                        etRb.setText("");
                        String period = rb.getText().toString().trim();
                        String[] periodArr = period.split(" ");
                        String result = " " + periodArr[1];
                        rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));
                        rb.setChecked(true);
                        tvRepeatPeriodResult.setTextColor(priorityColorPrimary);
                        tvRepeatPeriodResult.setText(result);
                        clearRepetitionEditTexts();

                    }
                }
            });

            etRb.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String result = "";

                        if(s != null)
                            result = "Every " + s;

                        tvRepeatNumberResult.setTextColor(priorityColorPrimary);
                        tvRepeatNumberResult.setText(result);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {

            rb = view.findViewById(checkedId);
            rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));

            if(llRepeatResult.getVisibility() == View.GONE)
            {
                ObjectAnimator animator = ObjectAnimator.ofFloat(llRepeatResult, "alpha", 0f, 1f);
                llRepeatResult.setVisibility(View.VISIBLE);
                animator.setDuration(300);
                animator.start();
            }

            if(rb != null)
            {
                String period = rb.getText().toString();
                String[] periodArr = period.split(" ");
                String result = " " + periodArr[1];

                int tag = Integer.parseInt(rb.getTag().toString());
                tf = view.findViewWithTag("tf"+tag);
                tf.setBoxStrokeColorStateList(ColorStateList.valueOf(priorityColorPrimary));
                tf.setCursorColor(ColorStateList.valueOf(priorityColorPrimary));
                et = view.findViewWithTag("et"+tag);
                et.requestFocus();
                et.setText("");
                tvRepeatPeriodResult.setTextColor(priorityColorPrimary);
                tvRepeatPeriodResult.setText(result);
                clearRepetitionEditTexts();
            }

        });

        fabAddTask.setBackgroundTintList(ColorStateList.valueOf(priorityColorPrimary));
        fabAddTask.setOnClickListener(v -> {
            createTask();
        });


        fabCancelTask.setOnClickListener( v -> {

            if(listener != null)
                listener.enableChangingTabs();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, pre_fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }


    public void toggleVisibility(int option, boolean isChecked)
    {
        ObjectAnimator animatorO = new ObjectAnimator();
        ObjectAnimator animatorO2 = new ObjectAnimator();

        switch (option) {
            case DefaultParameters.CALENDAR_OPTION:


                 if(calendar.getVisibility() == View.GONE && timePicker.getVisibility() == View.VISIBLE)
                {
                    animatorO = ObjectAnimator.ofFloat(calendar,"alpha",0f, 1f);
                    animatorO2 = ObjectAnimator.ofFloat(timePicker,"alpha", 1f,0f);
                    calendar.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.GONE);
                }
                 else if(calendar.getVisibility() == View.GONE && timePicker.getVisibility() == View.GONE)
                {
                    animatorO = ObjectAnimator.ofFloat(calendar,"alpha",0f, 1f);
                    calendar.setVisibility(View.VISIBLE);
                }
                  else if(calendar.getVisibility() == View.VISIBLE && timePicker.getVisibility() == View.GONE)
                {
                    animatorO = ObjectAnimator.ofFloat(calendar,"alpha",1f, 0f);
                    calendar.setVisibility(View.GONE);
                }

                break;

            case DefaultParameters.TIME_OPTION:

                  if(calendar.getVisibility() == View.VISIBLE && timePicker.getVisibility() == View.GONE)
                 {
                    animatorO = ObjectAnimator.ofFloat(timePicker,"alpha",0f, 1f);
                    animatorO2 = ObjectAnimator.ofFloat(calendar,"alpha", 1f,0f);
                    calendar.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                 }
                  else if(calendar.getVisibility() == View.GONE && timePicker.getVisibility() == View.GONE)
                 {
                    animatorO = ObjectAnimator.ofFloat(timePicker,"alpha",0f, 1f);
                    timePicker.setVisibility(View.VISIBLE);
                 }
                  else if(calendar.getVisibility() == View.GONE && timePicker.getVisibility() == View.VISIBLE)
                 {
                    animatorO = ObjectAnimator.ofFloat(timePicker,"alpha",1f, 0f);
                    timePicker.setVisibility(View.GONE);
                 }

                break;

            case DefaultParameters.REPEAT_OPTION:

                if(isChecked)
                {
                    swt_repeat.setThumbTintList(ColorStateList.valueOf(priorityColorPrimary));
                    swt_repeat.setTrackTintList(ColorStateList.valueOf(priorityColorSecondary));
                    animatorO = ObjectAnimator.ofFloat(llRepeatGroup, "alpha", 0f, 1f);
                    animatorO2 = ObjectAnimator.ofFloat(llRepeatResult, "alpha", 0f, 1f);
                    llRepeatGroup.setVisibility(View.VISIBLE);
                    tvRepeatToggle.setText(R.string.repeat_task);
                    doRepeat = true;
                }
                else
                {
                    swt_repeat.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.switch_thumb_color_unchecked)));
                    swt_repeat.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.switch_track_color_unchecked)));
                    animatorO = ObjectAnimator.ofFloat(llRepeatGroup, "alpha", 1f, 0f);
                    animatorO2 = ObjectAnimator.ofFloat(llRepeatResult, "alpha", 1f, 0f);
                    unsetRadioButtons();
                    llRepeatGroup.setVisibility(View.GONE);
                    llRepeatResult.setVisibility(View.GONE);
                    tvRepeatNumberResult.setText("");
                    tvRepeatPeriodResult.setText("");
                    tvRepeatToggle.setText(R.string.no_repeat);
                    clearRepetitionEditTexts();
                    doRepeat = false;

                }


                break;

            case DefaultParameters.REMAINDER_OPTION:

                if(isChecked)
                {
                    swt_remainder.setThumbTintList(ColorStateList.valueOf(priorityColorPrimary));
                    swt_remainder.setTrackTintList(ColorStateList.valueOf(priorityColorSecondary));
                    animatorO = ObjectAnimator.ofFloat(llRemainderGroup, "alpha", 0f, 1f);
                    animatorO2 = ObjectAnimator.ofFloat(llRemainderResult, "alpha", 0f, 1f);
                    llRemainderGroup.setVisibility(View.VISIBLE);
                    tvRemindToggle.setText(R.string.remainder);
                    doRemind = true;
                }
                else
                {
                    swt_remainder.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.switch_thumb_color_unchecked)));
                    swt_remainder.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.switch_track_color_unchecked)));
                    animatorO = ObjectAnimator.ofFloat(llRemainderGroup, "alpha", 1f, 0f);
                    animatorO2 = ObjectAnimator.ofFloat(llRemainderResult, "alpha", 1f, 0f);
                    setRemaindersDefault();
                    llRemainderGroup.setVisibility(View.GONE);
                    llRemainderResult.setVisibility(View.GONE);
                    tvRemainderNumberResult.setText("");
                    tvRemainderPeriodResult.setText("");
                    tvRemindToggle.setText(R.string.no_remind);
                    doRemind = false;
                }
                break;
        }

        if(Objects.equals(animatorO2.getPropertyName(), "alpha"))
        {
            animatorO2.setDuration(300);
            animatorO2.start();
        }


        animatorO.setDuration(300);
        animatorO.start();
    }

    public boolean checkForErrors()
    {

        if(tfTitle.getError() != null) tfTitle.setError(null);
        else if(tfDetails.getError() != null) tfDetails.setError(null);

        if(etTitle.getText().toString().isEmpty())
            tfTitle.setError("Title field can't be empty");
        else if(etDetails.getText().toString().isEmpty())
            tfDetails.setError("Details field can't be empty");

        return (tfTitle.getError() == null && tfDetails.getError() == null);
    }

    public void getCurrentDateTime()
    {
        Calendar c = Calendar.getInstance();
        currentDate = dateFormat.format(c.getTime());
        currentTime = timeFormat.format(c.getTime());
    }

    public void unsetRadioButtons()
    {
        for (int i = 0; i < rg.getChildCount(); i++) {
            View view = rg.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton rb = (RadioButton) view;
                rb.setChecked(false);
            }
        }
    }

    public void clearRepetitionEditTexts()
    {
        for (int i = 0; i < etRbList.size(); i++)
        {
            EditText et = etRbList.get(i);
            String etTag = String.valueOf(et.getTag().toString().charAt(2));

            View view = requireView().findViewWithTag(etTag);
            if(view instanceof RadioButton)
            {
                RadioButton rb = (RadioButton) view;
                if(!rb.isChecked())
                    et.getText().clear();
            }

        }
    }

    public void setRemaindersDefault()
    {
        npNumber.setValue(1);
        npPeriod.setDisplayedValues(periods);
    }

    public long getRepeatingTime()
    {
        long result = 0;
        String period = "";
        String numOfPeriods = "";
        Calendar c = Calendar.getInstance();
        Date dateOfRepeating;


        Date taskDueDate = parsingStringToDate(btnToggleDate.getText().toString() + " " + btnToggleTime.getText().toString(), "dateTimeFormat");
        c.setTime(taskDueDate);

        if(taskDueDate != null)
        {
            if(doRepeat)
            {
                for (int i = 0; i < rg.getChildCount(); i++)
                {
                    View view = rg.getChildAt(i);
                    if (view instanceof RadioButton)
                    {
                        RadioButton rb = (RadioButton) view;
                        if(rb.isChecked())
                        {
                            String rbText = rb.getText().toString().trim();
                            String[] arrRbText = rbText.split(" ");
                            period = arrRbText[1];

                            View view2 = requireView().findViewWithTag("et"+rb.getTag());
                            if(view2 instanceof EditText)
                            {
                                EditText et = (EditText)view2;
                                numOfPeriods = et.getText().toString();

                                if(numOfPeriods.isEmpty()) numOfPeriods = "1";


                                switch (period)
                                {
                                    case "day":
                                        c.add(Calendar.DAY_OF_YEAR, Integer.parseInt(numOfPeriods));
                                        break;
                                    case "week":
                                        c.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(numOfPeriods));
                                        break;
                                    case "month":
                                        c.add(Calendar.MONTH, Integer.parseInt(numOfPeriods));
                                        break;
                                    case "year":
                                        c.add(Calendar.YEAR, Integer.parseInt(numOfPeriods));
                                        break;
                                    default:
                                        Toast.makeText(requireContext(),getString(R.string.error, "getRepeatingTime() - unknown period"), Toast.LENGTH_SHORT).show();

                                }

                                dateOfRepeating = c.getTime();
                                Timestamp timestamp = new Timestamp(dateOfRepeating.getTime());
                                result = timestamp.getTime();
                            }
                        }
                        else
                        {
                            doRepeat = false;
                        }
                    }

                }
            }
        } else
        {
            Toast.makeText(requireContext(), getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show();
            result = -1;
        }


        return result;

    }

    public long getRemainderTime()
    {
        long result = 0;
        String period = periods[npPeriod.getValue()];
        int numOfPeriods = npNumber.getValue();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Date timeOfReminder;

        Date taskDueDate = parsingStringToDate(btnToggleDate.getText().toString() + " " + btnToggleTime.getText().toString(), "dateTimeFormat");
        c1.setTime(taskDueDate);
        c2.setTime(c2.getTime());

        if(taskDueDate != null)
        {
            if(doRemind)
            {
                switch (period)
                {
                    case "minute":
                        c1.add(Calendar.MINUTE,-numOfPeriods);
                        break;
                    case "hour":
                        c1.add(Calendar.HOUR,-numOfPeriods);
                        break;
                    case "day":
                        c1.add(Calendar.DAY_OF_YEAR,-numOfPeriods);
                        break;
                    case "week":
                        c1.add(Calendar.WEEK_OF_YEAR,-numOfPeriods);
                        break;
                    default:
                        Toast.makeText(requireContext(), getString(R.string.error, "getReminderTime() - unknown period"), Toast.LENGTH_SHORT).show();
                }

                timeOfReminder = c1.getTime();
                Timestamp timestamp = new Timestamp(timeOfReminder.getTime());

                if(c2.compareTo(c1) >= 0)
                    result = -2;
                else result = timestamp.getTime();
            }
        }
        else
        {
            Toast.makeText(requireContext(), getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show();
            result = -1;
        }


        return result;

    }

    public Date parsingStringToDate(String date, String type)
    {
        SimpleDateFormat sdf;
        Date resultDate = null;

        if(type.equals("dateFormat")) sdf = dateFormat;
        else sdf = dateTimeFormat;

        try {

            resultDate =  sdf.parse(date);

        }catch (ParseException e)
        {
            Toast.makeText(requireContext(),  getString(R.string.error, e.getMessage()), Toast.LENGTH_SHORT).show();
        }

        return resultDate;
    }


    public void setFields()
    {
        String[] currentDateTime;

        task_id = longArr[3];
        task_calendarEventId = longArr[4];

        etTitle.setText(strArr[0]);
        etDetails.setText(strArr[1]);

        Date dueDateTime = new Date(longArr[0]);
        currentDateTime = (dateTimeFormat.format(dueDateTime)).split(" ");
        btnToggleDate.setText(currentDateTime[0]);
        btnToggleTime.setText(currentDateTime[1]);

        if(boolArr[1])
        {
            doRemind = true;
            String[] remainderTime;
            String remainderText = "";
            swt_remainder.setChecked(true);

            swt_remainder.setThumbTintList(ColorStateList.valueOf(priorityColorPrimary));
            swt_remainder.setTrackTintList(ColorStateList.valueOf(priorityColorSecondary));
            llRemainderGroup.setVisibility(View.VISIBLE);
            llRemainderResult.setVisibility(View.VISIBLE);
            tvRemindToggle.setText(R.string.remainder);

            remainderTime = Util.getRemainderTime(longArr[2], longArr[0]);
            tvRemainderNumberResult.setTextColor(priorityColorPrimary);
            tvRemainderPeriodResult.setTextColor(priorityColorPrimary);
            tvRemainderNumberResult.setText(remainderTime[0]);
            remainderText = " " + remainderTime[1] + " before";
            tvRemainderPeriodResult.setText(remainderText);

            npNumber.setValue(Integer.parseInt(remainderTime[0]));

            switch (remainderTime[1])
            {
                case "minute": npPeriod.setValue(0);break;
                case "hour": npPeriod.setValue(1);break;
                case "day": npPeriod.setValue(2); break;
                case "week": npPeriod.setValue(3); break;

                default: Toast.makeText(requireContext(),  getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show(); break;
            }
        }

        if(boolArr[0])
        {
            doRepeat = true;
            Calendar c = Calendar.getInstance();
            String[] repeatingTime;
            String repeatingText = "";
            swt_repeat.setChecked(true);

            swt_repeat.setThumbTintList(ColorStateList.valueOf(priorityColorPrimary));
            swt_repeat.setTrackTintList(ColorStateList.valueOf(priorityColorSecondary));
            llRepeatGroup.setVisibility(View.VISIBLE);
            llRepeatResult.setVisibility(View.VISIBLE);
            tvRepeatToggle.setText(R.string.repeat_task);
            doRepeat = true;

            repeatingTime = Util.getRepeatingTime(longArr[1], longArr[0]);

            tvRepeatNumberResult.setTextColor(priorityColorPrimary);
            tvRepeatPeriodResult.setTextColor(priorityColorPrimary);
            repeatingText = "Every " + repeatingTime[0] + " ";
            tvRepeatNumberResult.setText(repeatingText);
            tvRepeatPeriodResult.setText(repeatingTime[1]);

            switch (repeatingTime[1])
            {
                case "day":
                            rb = rg.findViewWithTag("1");
                            rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));
                            rb.setChecked(true);
                            etRb1.setText(repeatingTime[0]);
                            break;
                case "week":
                            rb = rg.findViewWithTag("2");
                            rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));
                            rb.setChecked(true);
                            etRb2.setText(repeatingTime[0]);
                            break;
                case "month":
                            rb = rg.findViewWithTag("3");
                            rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));
                            rb.setChecked(true);
                            etRb3.setText(repeatingTime[0]);
                            break;
                case "year":
                            rb = rg.findViewWithTag("4");
                            rb.setButtonTintList(ColorStateList.valueOf(priorityColorPrimary));
                            rb.setChecked(true);
                            etRb4.setText(repeatingTime[0]);
                            break;

                default: Toast.makeText(requireContext(),  getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show(); break;
            }
        }

        fabAddTask.setImageResource(R.drawable.edit_task);
    }



    public void createTask()
    {
        if(checkForErrors())
        {
            task_status = DefaultParameters.TASK_IN_PROGRESS;
            task_priority = priority;
            task_title = etTitle.getText().toString();
            task_details = etDetails.getText().toString();
            task_remainder = false;
            task_repeating = false;
            task_dueDateTime = 0;
            task_remainderDueDate = 0;
            task_repeatingDueDate = 0;

            Date dueDateTime = parsingStringToDate(btnToggleDate.getText().toString() + " " + btnToggleTime.getText().toString(), "dateTimeFormat");
            Timestamp timestamp = new Timestamp(dueDateTime.getTime());
            task_dueDateTime = timestamp.getTime();

            task_repeating = doRepeat;
            task_repeatingDueDate = getRepeatingTime();

            task_remainder = doRemind;
            task_remainderDueDate = getRemainderTime();


            if(getRepeatingTime() != -1 && getRemainderTime() != -1 && dueDateTime != null && task_calendarEventId != -1)
            {

                if(getRemainderTime() != -2)
                {
                    if(listener != null)
                        listener.enableChangingTabs();

                    Task task = new Task(task_title, task_status, task_priority, task_details, task_dueDateTime, task_remainder, task_repeating, task_remainderDueDate, task_repeatingDueDate, task_calendarEventId);

                    if(!taskEditMode)
                    {
                        Log.d("TASK_MODE", "Task NOT in EDIT mode");
                        task_calendarEventId = Util.addEventToCalendar(requireContext(), task_title, task_details, task_dueDateTime, task_remainderDueDate, task_priority);
                        task.setTask_calendarEventId(task_calendarEventId);
                        Log.d("TEST_CALENDAR", "INSERT - CALENDAR_EVENT_ID: " + task_calendarEventId);
                        task_id = viewModel.insertTask(task);
                    }
                    else{
                        Log.d("TASK_MODE", "Task IN EDIT mode");
                        task.setTask_id(task_id);
                        Log.d("TEST_CALENDAR", "UPDATE - CALENDAR_EVENT_ID: " + task_calendarEventId);
                        task_calendarEventId = Util.updateEventInCalendar(requireContext(), task_title, task_details, task_dueDateTime, task_remainderDueDate, task_priority, task_calendarEventId);
                        task.setTask_calendarEventId(task_calendarEventId);
                        viewModel.updateTask(task);
                    }


                    Intent serviceIntent = new Intent(requireContext(), DueDateService.class);
                    serviceIntent.putExtra("taskId", task_id);
                    serviceIntent.putExtra("due_date", task_dueDateTime);
                    serviceIntent.putExtra("remainder_dueDate", task_remainderDueDate);
                    serviceIntent.putExtra("title", task_title);

                    requireContext().startService(serviceIntent);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, pre_fragment)
                            .addToBackStack(null)
                            .commit();

                } else Toast.makeText(requireContext(), "Please select earlier reminder", Toast.LENGTH_SHORT).show();


            } else Toast.makeText(requireContext(), getString(R.string.error, "Please contact your programmer!"), Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(requireContext(), R.string.error_fields_empty, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onStart() {
        super.onStart();
        Log.d("TESTY", "CreatingTaskFragment onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TESTY", "CreatingTaskFragment onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TESTY", "CreatingTaskFragment onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        taskEditMode = false;
        Log.d("TESTY", "CreatingTaskFragment onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TESTY", "CreatingTaskFragment onDestroy()");
    }

}
