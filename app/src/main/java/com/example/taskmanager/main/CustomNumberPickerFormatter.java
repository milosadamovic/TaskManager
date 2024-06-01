package com.example.taskmanager.main;

import android.widget.NumberPicker;

public class CustomNumberPickerFormatter implements NumberPicker.Formatter {
        private String[] values;

        public CustomNumberPickerFormatter(String[] values) {
            this.values = values;
        }

        @Override
        public String format(int value) {
            if (value >= 0 && value < values.length) {
                return values[value];
            }
            return "";
        }
    }
