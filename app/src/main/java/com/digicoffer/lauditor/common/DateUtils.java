package com.digicoffer.lauditor.common;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import com.digicoffer.lauditor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static void showDatePickerDialog(Context context, final TextView textView, Context context1, final DatePickerDialog.OnDateSetListener listener) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(textView, myCalendar,context1);
                if (listener != null) {
                    listener.onDateSet(textView, myCalendar);
                }
            }
        };


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                R.style.DatePickerStyle,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private static void updateLabel(TextView textView, Calendar calendar,Context context) {
        String myFormat = "MMM dd,YYYY";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(calendar.getTime()));


    }
    public static Date stringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy", Locale.US);
        try {
            // Parse the string into a Date object
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle parsing error if needed
            return null;
        }
    }
}
