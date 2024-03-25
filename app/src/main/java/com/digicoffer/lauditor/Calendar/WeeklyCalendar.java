package com.digicoffer.lauditor.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.EventDay;
import com.digicoffer.lauditor.Calendar.Models.Day;
import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.Models.Events_Do;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.WeekDateInfo;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common.DrawableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeeklyCalendar extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener, Events_Adapter.EventListener, WeeklyCalendarAdapter.OnDaySelectedListener {
    Calendar calendar = Calendar.getInstance();
    WeekDateInfo weekDateInfo;
    String start_date;
    String end_date;
    String filter = "";
    Calendar cal;
    String recurring_edit_choice;
    private WeeklyCalendarAdapter adapter;
    private List<Day> days = new ArrayList<>();
    AlertDialog progress_dialog;
    String Current_month = "";
    RecyclerView rv_week_dates;
    private EventDetailsListener eventDetailsListener;
    AlertDialog ad_dialog;
    String event_creation_date = "";
    String event_end_date = "";
    String Currenr_date = "";
    RecyclerView rv_displayEvents;
    String Current_day = "";
    Events_Adapter events_adapter;
    TextView tv_from_date_timesheet, tv_to_date_timesheet;
    ArrayList<Event_Details_DO> event_details_list = new ArrayList<Event_Details_DO>();
    final java.util.Calendar mCalendar = Calendar.getInstance();
    ArrayList<Events_Do> events_list = new ArrayList<Events_Do>();
    List<EventDay> events = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weekly_calendar, container, false);
        ImageButton iv_next_week = v.findViewById(R.id.iv_next_week);
        iv_next_week.setImageDrawable(getContext().getDrawable(R.drawable.baseline_arrow_forward_ios_24));
        ImageButton iv_previous_week = v.findViewById(R.id.iv_previous_week);
        rv_displayEvents = (RecyclerView) v.findViewById(R.id.rv_events);
        rv_week_dates = (RecyclerView) v.findViewById(R.id.rv_week_dates);
        tv_from_date_timesheet = (TextView) v.findViewById(R.id.tv_from_date_timesheet);
        tv_to_date_timesheet = (TextView) v.findViewById(R.id.tv_to_date_timesheet);
        tv_to_date_timesheet.setTextColor(getContext().getColor(R.color.Blue_text_color));
        tv_from_date_timesheet.setTextColor(getContext().getColor(R.color.Blue_text_color));

//        iv_next_week.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
////
//                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
//                    weekDateInfo = getWeekDateRange(calendar);
////                    if (weekDateInfo != null && weekDateInfo.getWeekDates() != null && !weekDateInfo.getWeekDates().isEmpty()) {
////                        tv_from_date_timesheet.setText(weekDateInfo.getWeekDates().get(0));
////                        tv_to_date_timesheet.setText(weekDateInfo.getWeekDates().get(weekDateInfo.getWeekDates().size() - 1));
////                    }
////                    loadFragment(tv_from_date_timesheet.getText().toString(),weekDateInfo);
//                    events_list.clear();
//                    mCalendar.set(mCalendar.MONTH, mCalendar.get(mCalendar.MONTH) + 1);
//                    String myFormat = "MMM dd, yyyy";
//                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                    filter = sdf.format(mCalendar.getTime());
//                    callEventListwebservice(filter);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        iv_previous_week.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar.add(Calendar.WEEK_OF_YEAR, -1);
//                weekDateInfo = getWeekDateRange(calendar);
////                if (weekDateInfo != null && weekDateInfo.getWeekDates() != null && !weekDateInfo.getWeekDates().isEmpty()) {
////                    tv_from_date_timesheet.setText(weekDateInfo.getWeekDates().get(0));
////                    tv_to_date_timesheet.setText(weekDateInfo.getWeekDates().get(weekDateInfo.getWeekDates().size() - 1));
////                }
////                loadFragment(tv_from_date_timesheet.getText().toString(),weekDateInfo);
//                events_list.clear();
//                mCalendar.set(mCalendar.MONTH, mCalendar.get(mCalendar.MONTH) - 1);
//                String myFormat = "MMM dd, yyyy";
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                filter = sdf.format(mCalendar.getTime());
//                callEventListwebservice(filter);
//            }
//        });
        CurrentWeek();
        callEventListwebservice(filter);
        adapter = new WeeklyCalendarAdapter(days, this::onDaySelected);
        rv_week_dates.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_week_dates.setAdapter(adapter);
        iv_previous_week.setOnClickListener(view -> showPreviousWeek());
        iv_next_week.setOnClickListener(view -> showNextWeek());
        return v;
    }

    private void CurrentWeek() {
        cal = Calendar.getInstance();
        int firstDayOfWeek = cal.getFirstDayOfWeek();

        // Find the start date (first day) of the current week
        while (cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cal.add(Calendar.DATE, -1);
        }

        // Update the 'days' list with dates of the current week
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String fromDate = dateFormat.format(cal.getTime());

        //Getting first date and last date for current week
        SimpleDateFormat d1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
        start_date = d1.format(cal.getTime());
        Log.d("F_date", "" + start_date);

        days.clear();
        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(cal.getTime());
            days.add(new Day(date));
            cal.add(Calendar.DATE, 1);
        }

        // Find the end date (last day) of the current week
        cal.add(Calendar.DATE, -1);
        String toDate = dateFormat.format(cal.getTime());
        end_date = d1.format(cal.getTime());
        Log.d("L_date", "" + end_date);

        // Update the 'tv_from_date_timesheet' and 'tv_to_date_timesheet' TextViews
        tv_from_date_timesheet.setText(fromDate);
        tv_to_date_timesheet.setText(toDate);
    }

    @Override
    public void onClick(View v) {

    }

    private void showPreviousWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat d1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
        cal = Calendar.getInstance();
        String fromDate;
        String toDate;


        try {
            Date currentDate = dateFormat.parse(days.get(0).getDate());
            cal.setTime(currentDate);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            start_date = d1.format(cal.getTime());
            Log.d("pf_date", "" + start_date);

            // Update the 'days' list with dates of the previous week
            days.clear();
            for (int i = 0; i < 7; i++) {
                String date = dateFormat.format(cal.getTime());
                days.add(new Day(date));
                cal.add(Calendar.DATE, 1);
            }
            end_date = d1.format(cal.getTime());
            Log.d("pl_date", "" + end_date);

            // Calculate the new 'fromDate' and 'toDate' for the TextViews
            fromDate = days.get(0).getDate();
            toDate = days.get(days.size() - 1).getDate();
//            String st_date= d1.format(days.get(0).getDate());
            Log.d("Frommmm", "" + start_date);

//                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");
//                String date_of_filling = outputFormat.format(fromDate);


            // Notify the adapter of the data changes
            adapter.notifyDataSetChanged();
            events_list.clear();

            // Fetch events for the current week from the REST API
            String myFormat = "MMM dd, yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            filter = sdf.format(cal.getTime());
            Log.d("Filterrr", filter);
            callEventListwebservice(filter);

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Update the 'tv_from_date_timesheet' and 'tv_to_date_timesheet' TextViews
        tv_from_date_timesheet.setText(fromDate);
        tv_to_date_timesheet.setText(toDate);
    }


    private void showNextWeek() {
        // Calculate the first date of the next week
        SimpleDateFormat d1 = new SimpleDateFormat("ddMMyyyy", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        cal = Calendar.getInstance();
        String fromDate;
        String toDate;
        try {
            Date currentDate = dateFormat.parse(days.get(0).getDate());
            cal.setTime(currentDate);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            start_date = d1.format(cal.getTime());
            Log.d("ff_date", "" + start_date);

            // Update the 'days' list with dates of the next week
            days.clear();
            for (int i = 0; i < 7; i++) {
                String date = dateFormat.format(cal.getTime());
                days.add(new Day(date));
                cal.add(Calendar.DATE, 1);
            }
            end_date = d1.format(cal.getTime());
            Log.d("pf_date", "" + end_date);
            // Calculate the new 'fromDate' and 'toDate' for the TextViews
            fromDate = days.get(0).getDate();
            toDate = days.get(days.size() - 1).getDate();

            // Notify the adapter of the data changes
            adapter.notifyDataSetChanged();
            events_list.clear();

            // Fetch events for the current week from the REST API
            String myFormat = "MMM dd, yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            filter = sdf.format(cal.getTime());
            callEventListwebservice(filter);

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Update the 'tv_from_date_timesheet' and 'tv_to_date_timesheet' TextViews
        tv_from_date_timesheet.setText(fromDate);
        tv_to_date_timesheet.setText(toDate);
//        from_date=fromDate;
//        to_date=toDate;
    }

    public interface EventDetailsListener {
        void onEventDetailsPassed(ArrayList<Event_Details_DO> event_details_list, String calendar_Type);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof EventDetailsListener) {
        try {
            Log.d("Interface", "Interface Called");
            eventDetailsListener = (EventDetailsListener) getParentFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement EventDetailsListener");
//        }
    }

    private void callEventListwebservice(String filter) {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        try {
            Date event_date = AndroidUtils.stringToDateTimeDefault(filter, "MMM dd, yyyy");
            event_creation_date = AndroidUtils.getDateToString(event_date, "MMyyyy");
            Currenr_date = AndroidUtils.getDateToString(event_date, "yyyy-MM-dd");
            Current_day = AndroidUtils.getDateToString(event_date, "dd");
            Current_month = AndroidUtils.getDateToString(event_date, "MM");
            Calendar calendar = new GregorianCalendar();
            TimeZone timeZone = calendar.getTimeZone();
            int offset = timeZone.getRawOffset();
            long hours = TimeUnit.MILLISECONDS.toMinutes(offset);
            long timezoneoffset = (-1) * (hours);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/events/" + timezoneoffset + "/" + "W" + start_date + "-" + end_date, "Events_List", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        try {
            if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
                try {
                    JSONObject result = new JSONObject(httpResult.getResponseContent());
                    if (httpResult.getRequestType().equals("Events_List")) {
                        if (!result.getBoolean("error")) {
                            JSONArray jsonArray = result.getJSONArray("events");
                            loadEvents(jsonArray);
                        } else {
                            AndroidUtils.showAlert(result.getString("msg"), getContext());
                        }
                    } else if (httpResult.getRequestType().equals("EVENT DETAILS")) {
                        if (!result.getBoolean("error")) {
                            event_details_list.clear();
//                            load_event_details(result.getJSONObject("event"), event_id);
                        }
                    } else if (httpResult.getRequestType().equals("EVENT_DELETE")) {
//                        if (!result.getBoolean("error")) {
                        AndroidUtils.showToast("Event Deleted Successfully", getContext());
//                            if (!(ad_dialog == null)) {
                        ad_dialog.dismiss();
                        progress_dialog.dismiss();

//                            }
                        event_details_list.clear();
                        events.clear();
//                            rv_displayEvents.clea/
                        callEventListwebservice(filter);
//                        } else {
//                            AndroidUtils.showValidationALert("Alert", result.getString("msg"), getContext());
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEvents(JSONArray jsonArray) {
        try {
            Events_Do events_do;
            events_list.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                events_do = new Events_Do();
//                events_do.setDescription(jsonObject.getString("description"));
                events_do.setEvent_Name(jsonObject.getString("title"));
                events_do.setDialin(jsonObject.getString("dialin"));
                events_do.setEvent_type(jsonObject.getString("event_type"));
                events_do.setLocation(jsonObject.getString("location"));
                if (jsonObject.has("matter_id")) {
                    events_do.setMatter_id(jsonObject.getString("matter_id"));
                }
                if (jsonObject.has("matter_name")) {
                    events_do.setMatter_name(jsonObject.getString("matter_name"));
                }
                if (jsonObject.has("matter_type")) {
                    events_do.setMatter_type(jsonObject.getString("matter_type"));
                }
                events_do.setRecurring(jsonObject.getBoolean("isrecurring"));
                events_do.setMeeting_link(jsonObject.getString("meeting_link"));
                events_do.setNotes(jsonObject.getString("notes"));
                events_do.setRepeat_interval(jsonObject.getString("repeat_interval"));
                events_do.setTimezone_location(jsonObject.getString("timezone_location"));
                events_do.setTimezone_offset(jsonObject.getString("timezone_offset"));
                events_do.setAttachments(jsonObject.getJSONArray("attachments"));
                events_do.setInvitees_internal(jsonObject.getJSONArray("invitees_external"));
                events_do.setInvitees_internal(jsonObject.getJSONArray("invitees_internal"));
                events_do.setNotifications(jsonObject.getJSONArray("notifications"));
                events_do.setEvent_start_time(jsonObject.getString("from_ts"));
                events_do.setEvent_end_time(jsonObject.getString("to_ts"));
                events_do.setAll_day(jsonObject.getBoolean("allday"));
                events_do.setEvent_id(jsonObject.getString("id"));
                events_do.setOwner(jsonObject.getBoolean("owner"));
                String from_ts = events_do.getEvent_start_time();
                String to_ts = events_do.getEvent_end_time();
                Date event_date = AndroidUtils.stringToDateTimeDefault(from_ts, "yyyy-MM-dd'T'HH:mm:ss");
                String event_start_time = AndroidUtils.getDateToString(event_date, "HH:mm a");
                events_do.setConverted_Start_time(event_start_time);
                Date event_date2 = AndroidUtils.stringToDateTimeDefault(to_ts, "yyyy-MM-dd'T'HH:mm:ss");
                String event_end_time = AndroidUtils.getDateToString(event_date2, "HH:mm a");
                events_do.setCOnverted_End_time(event_end_time);
                String converted_from_ts = AndroidUtils.getDateToString(event_date, "yyyy-MM-dd");
                String converted_to_ts = AndroidUtils.getDateToString(event_date2, "yyyy-MM-dd");
                String converted_day = AndroidUtils.getDateToString(event_date, "dd");
                int year = Integer.parseInt(AndroidUtils.getDateToString(event_date, "yyyy"));
//                System.out.println(events_do.getEvent_Name() + ";" + event_date.getDate() + "-" + event_date.getMonth() + "-" +year);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, event_date.getMonth(), event_date.getDate());
                events.add(new EventDay(calendar, DrawableUtils.getThreeDots(getContext())));
//                events.add(new EventDay(calendar,DrawableUtils.getDayCircle(getContext(), R.color.blue,R.color.green )));
//                Log.d("From Start Date", converted_from_ts);
//                Log.d("Current Date",Currenr_date);

                if (converted_from_ts.toString().contains(Currenr_date) || converted_to_ts.toString().contains(Currenr_date)) {
//                    events_do.setRecurring(jsonObject.getBoolean("isrecurring"));
//                    if (events_do.isRecurring()) {
//                        events_list.add(events_do);
//                    } else {
                    events_list.add(events_do);

//                    }
                }
            }

            Collections.sort(events_list, new Comparator<Events_Do>() {
                @Override
                public int compare(Events_Do eventDay, Events_Do t1) {
                    if (eventDay.getConverted_Start_time() == null || t1.getConverted_Start_time() == null)
                        return 0;
                    return eventDay.getConverted_Start_time().compareTo(t1.getConverted_Start_time());
                }
            });
            for (Day day : days) {
                if (day.getDate().equals(Currenr_date)) {
                    day.setEvents(events);
                    break;
                }
            }

            //Check for Event Status
            for (int i = 1; i <= days.size(); i++) {
                if (events_list.size() > 0) {
                    days.get(i).setHasEvents(true);
                } else {
                    days.get(i).setHasEvents(false);
                }
                adapter.notifyDataSetChanged();
                break;
            }
//            for(Day day:days) {
//                if (events_list.size() > 0) {
//                    day.setHasEvents(true);
//                } else {
//                    day.setHasEvents(false);
//                }
//                break;
//            }
            loadRecyclerView();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage().toString(), getContext());
            e.printStackTrace();
        }
    }

    private void loadRecyclerView() throws Exception {
        try {
            rv_displayEvents.setLayoutManager(new GridLayoutManager(getContext(), 1));
            for (Events_Do event : events_list) {
                Log.d("Event List", event.getEvent_Name());
            }
            Log.d("Event_size.", "" + events_list.size());
            events_adapter = new Events_Adapter(events_list, this, getContext(), getActivity());
            rv_displayEvents.setAdapter(events_adapter);
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(ArrayList<Event_Details_DO> event_details_list) {
        if (eventDetailsListener != null) {
            Log.d("EventsList", event_details_list.toString());
            String Calendar_Type = "Weekly";
            eventDetailsListener.onEventDetailsPassed(event_details_list, Calendar_Type);
        }
    }

    @Override
    public void delete_events(String event_id, boolean recur, String event_name) {
        if (recur) {
            delete_event(event_id, event_name);
        } else {
            callDeleteEventwebservice(event_id, recurring_edit_choice);
        }
    }

    private void callDeleteEventwebservice(String id, String recurring_choice) {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        try {
            if (!(recurring_choice == null)) {
                postData.put("choice", recurring_choice);
            }
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.DELETE, "v3/event/" + id, "EVENT_DELETE", postData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void delete_event(final String event_id, String event_name) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.alert_dialog_delete, null);
            final TextView delete_event_msg = dialogLayout.findViewById(R.id.delete_event_msg);
            delete_event_msg.setText("Are you Sure do you want to delete " + event_name + " ?");
            delete_event_msg.setTextSize(20);
            delete_event_msg.setTextColor(Color.BLACK);
            delete_event_msg.setTypeface(null, Typeface.NORMAL);
            final Button delete = dialogLayout.findViewById(R.id.delete_event);
            final Button btn_close_event = dialogLayout.findViewById(R.id.btn_close_event);
            btn_close_event.setTextColor(Color.RED);
            btn_close_event.setText(R.string.cancel);

            final AlertDialog dialog = builder.create();
            ad_dialog = dialog;
            btn_close_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad_dialog.dismiss();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recurring_edit_choice = "this";
                    callDeleteEventwebservice(event_id, recurring_edit_choice);
                }
            });

            dialog.setView(dialogLayout);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String event_id, boolean recur) {

    }

    @Override
    public void onDaySelected(Day day) {
        String selectedDate = day.getDate();
        Log.d("TRUE_VALUE", "" + day.hasEvents());

        // Update the calendar's date with the selected date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            cal = Calendar.getInstance();
            Date selectedDateObj = dateFormat.parse(selectedDate);
            cal.setTime(selectedDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        events_list.clear();
//        cal = day.get
//        cal = Calendar.getInstance();
//        mCalendar.set(mCalendar.MONTH, mCalendar.get(mCalendar.MONTH) - 1);
        String myFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        filter = sdf.format(cal.getTime());
        try {
            callEventListwebservice(filter);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
