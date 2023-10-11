package com.digicoffer.lauditor.TimeSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.DateModel;
import com.digicoffer.lauditor.TimeSheets.Models.WeekDateInfo;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeSheets extends Fragment {
    private NewModel mViewModel;
    TextView tv_aggregated_ts, tv_my_ts, tv_ns_timesheet, tv_submitted, tv_week, tv_month;
    LinearLayoutCompat ll_timesheet_type, ll_submitted_type;
    private static final int DIRECTION_PREVIOUS = -1;
    String s = "";
    private static final int DIRECTION_NEXT = 1;
    private ArrayList<DateModel> datesList = new ArrayList<>();
    //    private ArrayAdapter<DateModel> weekAdapter = new ArrayAdapter<DateModel>(getContext(), android.R.layout.simple_spinner_item, datesList);
    Calendar calendar = Calendar.getInstance();
    WeekDateInfo weekDateInfo;
    private static String main_button_status = "";
    private static String non_main_button_status="" ;
    //    getWeekDateRange(calendar);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timesheet, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Time Sheets");
        ll_submitted_type = view.findViewById(R.id.ll_submitted_type);
        ll_timesheet_type = view.findViewById(R.id.ll_timesheet_type);
        tv_aggregated_ts = view.findViewById(R.id.tv_aggregated_ts);
        tv_my_ts = view.findViewById(R.id.tv_my_ts);
        tv_ns_timesheet = view.findViewById(R.id.tv_ns_timesheet);
        tv_submitted = view.findViewById(R.id.tv_submitted);
        tv_week = view.findViewById(R.id.tv_week);
        tv_month = view.findViewById(R.id.tv_month);

        weekDateInfo = getWeekDateRange(calendar);

        TextView tv_from_date_timesheet = view.findViewById(R.id.tv_from_date_timesheet);
        TextView tv_to_date_timesheet = view.findViewById(R.id.tv_to_date_timesheet);

        if (weekDateInfo != null && weekDateInfo.getWeekDates() != null && !weekDateInfo.getWeekDates().isEmpty()) {
            tv_from_date_timesheet.setText(weekDateInfo.getWeekDates().get(0));
            tv_to_date_timesheet.setText(weekDateInfo.getWeekDates().get(weekDateInfo.getWeekDates().size() - 1));
        }

        if (Constants.ROLE.equals("SU")) {
            main_button_status = "Aggregated";
            non_main_button_status = "TM";
            ll_timesheet_type.setVisibility(View.VISIBLE);
            loadFragment(s,weekDateInfo);
        } else {
            ll_timesheet_type.setVisibility(View.GONE);
//            main_button_status = "MyTimeSheets";
            non_main_button_status = "NS";
            loadFragment(s,weekDateInfo);
        }
        ImageButton iv_next_week = view.findViewById(R.id.iv_next_week);
        ImageButton iv_previous_week = view.findViewById(R.id.iv_previous_week);

        tv_aggregated_ts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_button_status = "Aggregated";
                weekDateInfo = getWeekDateRange(calendar);
                loadAggregatedTimesheets(s,weekDateInfo);
            }
        });
        tv_my_ts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_button_status = "MyTS";
                loadMyTimeSheets(s, weekDateInfo);
            }
        });
        tv_submitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_submitted.getText().toString().equals("Projects")) {
                    non_main_button_status = "Project";

                }else{
                    non_main_button_status = "Submitted";

                }
               loadSubmittedTimesheets(s,weekDateInfo);
            }
        });
        tv_ns_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_ns_timesheet.getText().toString().equals("Team Members")){
                    non_main_button_status = "TM";
                }else{
                    non_main_button_status = "NS";
                }
                loadNsTimesheets(s, weekDateInfo);
            }
        });
        tv_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWeek();
            }
        });
        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMonth();
            }
        });
        iv_next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                    weekDateInfo = getWeekDateRange(calendar);
                    if (weekDateInfo != null && weekDateInfo.getWeekDates() != null && !weekDateInfo.getWeekDates().isEmpty()) {
                        tv_from_date_timesheet.setText(weekDateInfo.getWeekDates().get(0));
                        tv_to_date_timesheet.setText(weekDateInfo.getWeekDates().get(weekDateInfo.getWeekDates().size() - 1));
                    }
                    loadFragment(tv_from_date_timesheet.getText().toString(),weekDateInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_previous_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                weekDateInfo = getWeekDateRange(calendar);
                if (weekDateInfo != null && weekDateInfo.getWeekDates() != null && !weekDateInfo.getWeekDates().isEmpty()) {
                    tv_from_date_timesheet.setText(weekDateInfo.getWeekDates().get(0));
                    tv_to_date_timesheet.setText(weekDateInfo.getWeekDates().get(weekDateInfo.getWeekDates().size() - 1));
                }
                loadFragment(tv_from_date_timesheet.getText().toString(),weekDateInfo);

            }
        });
        return view;
    }

    private void loadAggregatedTimesheets(String s,WeekDateInfo weekDateInfo) {
        tv_aggregated_ts.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_my_ts.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_ns_timesheet.setText("Team Members");
        tv_submitted.setText("Projects");
        mViewModel.setData("Time Sheets");
        if(tv_ns_timesheet.getText().toString().equals("Not Submitted")) {
            loadNsFragment(s,weekDateInfo);
        }else{
            if (non_main_button_status!=null&&non_main_button_status == "TM") {
                loadTMFragment(s);
            }else{
                loadProjectFragment(s,weekDateInfo);
            }
        }
    }

    private void loadMyTimeSheets(String s, WeekDateInfo weekDateInfo) {
        tv_aggregated_ts.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_my_ts.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_ns_timesheet.setText("Not Submitted");
        tv_submitted.setText("Submitted");
        if (non_main_button_status.equals("NS")){
        loadNsTimesheets(s,weekDateInfo);
        }else {
            loadSubmittedTimesheets(s, weekDateInfo);
        }
//        if (non_main_button_status)
    }

    private void loadNsTimesheets(String s, WeekDateInfo weekDateInfo) {
        tv_ns_timesheet.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_submitted.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
//        loadFragment();
        if(tv_ns_timesheet.getText().toString().equals("Not Submitted")) {
            loadNsFragment(s, weekDateInfo);
        }else{
            loadTMFragment(s);
        }
    }

    private void loadWeek() {
        tv_week.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_month.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));

    }

    private void loadMonth() {
        tv_week.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_month.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));

    }

    private void loadSubmittedTimesheets(String s, WeekDateInfo weekDateInfo) {
        tv_submitted.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_ns_timesheet.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        loadSubmittedFragment(s, weekDateInfo);
    }

    private void loadSubmittedFragment(String s,WeekDateInfo weekDateInfo) {
        if(tv_submitted.getText().toString().equals("Submitted")) {
            Bundle bundle = new Bundle();
            bundle.putString("date", s);
            AndroidUtils.showToast(s,getContext());
            bundle.putStringArrayList("weekDates", weekDateInfo.getWeekDates());
//        AndroidUtils.showToast(s,getContext());
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            SubmittedTimeSheets nonSubmittedTimesheets = new SubmittedTimeSheets();
            nonSubmittedTimesheets.setArguments(bundle);
            ft.replace(R.id.child_container_timesheets, nonSubmittedTimesheets);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
//            loadSubmittedFragment(s,weekDateInfo);
        }
        else{
            loadProjectFragment(s,weekDateInfo);
        }
    }

    private void loadFragment(String s,WeekDateInfo weekDateInfo) {
        if (main_button_status!=null&&main_button_status.equals("Aggregated")) {
            loadAggregatedTimesheets(s, weekDateInfo);
        }else{
            loadMyTimeSheets(s,weekDateInfo);
        }

    }

    private void loadTMFragment(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("date", s);
//        bundle.putStringArrayList("weekDates", weekDateInfo.getWeekDates());

//        AndroidUtils.showToast(s,getContext());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        AGS_TeamMembers nonSubmittedTimesheets = new AGS_TeamMembers();
        nonSubmittedTimesheets.setArguments(bundle);
        ft.replace(R.id.child_container_timesheets, nonSubmittedTimesheets);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void loadNsFragment(String s, WeekDateInfo weekDateInfo) {

        Bundle bundle = new Bundle();
        bundle.putString("date", s);
        bundle.putStringArrayList("weekDates", weekDateInfo.getWeekDates());

//        AndroidUtils.showToast(s,getContext());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        NonSubmittedTimesheets nonSubmittedTimesheets = new NonSubmittedTimesheets();
        nonSubmittedTimesheets.setArguments(bundle);
        ft.replace(R.id.child_container_timesheets, nonSubmittedTimesheets);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void loadProjectFragment(String s,WeekDateInfo weekDateInfo){
        Bundle bundle = new Bundle();
        bundle.putString("date", s);
        bundle.putStringArrayList("weekDates", weekDateInfo.getWeekDates());

//        AndroidUtils.showToast(s,getContext());
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        AGS_Projects nonSubmittedTimesheets = new AGS_Projects();
        nonSubmittedTimesheets.setArguments(bundle);
        ft.replace(R.id.child_container_timesheets, nonSubmittedTimesheets);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    //    private String getWeekDateRange(Calendar calendar) {
////        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
////        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
////        String startDate = format.format(calendar.getTime());
////
////        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//////        calendar.add(Calendar.DATE, 7);
////        calendar.add(Calendar.WEEK_OF_YEAR, 1);
////        String endDate = format.format(calendar.getTime());
////        return startDate + " - " + endDate;
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//
//        // Set the calendar to the current week's Monday
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        int firstDayOfWeek = calendar.getFirstDayOfWeek();
//        calendar.add(Calendar.DATE, firstDayOfWeek - dayOfWeek);
//
//        // Calculate the start and end dates of the week
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        String startDate = format.format(calendar.getTime());
//
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Add a week to get the next Sunday
//        String endDate = format.format(calendar.getTime());
//        return startDate + " - " + endDate;
//    }
//private String getWeekDateRange(Calendar calendar) {
//    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//
//    // Calculate the current week's Monday
//    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
//        calendar.add(Calendar.DATE, -1);
//    }
//    String startDate = format.format(calendar.getTime());
//
//    // Calculate the current week's Sunday
//    calendar.add(Calendar.DATE, 6);
//    String endDate = format.format(calendar.getTime());
//
//    return startDate + " - " + endDate;
//}
    private WeekDateInfo getWeekDateRange(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        String startDate = format.format(calendar.getTime());

        ArrayList<String> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            String date = format.format(calendar.getTime());
            weekDates.add(date);
            calendar.add(Calendar.DATE, 1);
        }
        calendar.add(Calendar.DATE, -1);
        String endDate = format.format(calendar.getTime());
        WeekDateInfo weekDateInfo = new WeekDateInfo(startDate + " - " + endDate, weekDates);
        return weekDateInfo;
    }

    private void loadWeekDates(int direction) {
        // Get the first date of the current week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Adjust the calendar object based on the direction parameter
        if (direction == DIRECTION_PREVIOUS) {
            calendar.add(Calendar.DAY_OF_YEAR, -7); // go back one week
        } else if (direction == DIRECTION_NEXT) {
            calendar.add(Calendar.DAY_OF_YEAR, 7); // go forward one week
        }

        // Create a DateFormat object to format the dates as strings
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Clear the current datesList
        datesList.clear();

        // Add the dates of the week to the datesList (Monday to Friday only)
        for (int i = 0; i < 5; i++) {
            Date date = calendar.getTime();
            String dayOfWeek = dateFormat.format(date);
//            DateModel dateModel = new DateModel()
            if (i != 0 && i != 6) {
                // Exclude Saturday and Sunday

                DateModel dateModel = new DateModel(dayOfWeek);
                datesList.add(dateModel);
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        // Update the spinner adapter
//        weekAdapter.notifyDataSetChanged();
    }
}
