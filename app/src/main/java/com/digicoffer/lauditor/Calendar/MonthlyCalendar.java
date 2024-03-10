package com.digicoffer.lauditor.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.Models.Events_Do;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.DrawableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MonthlyCalendar extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, Events_Adapter.EventListener {

    com.applandeo.materialcalendarview.CalendarView calendarView;
    Context thiscontext;
    AlertDialog progress_dialog;
    String filter = "";
    String event_id = "";
    String event_creation_date = "";
    Events_Adapter events_adapter;
    String Currenr_date = "";
    private EventDetailsListener eventDetailsListener;
    AlertDialog ad_dialog;
    String Current_day = "";
    String recurring_edit_choice;
    TextView tv_event_name, tv_event_description, tv_event_time, tv_event_repetetion, tv_event_date;
    Button btn_event_save;
    String Current_month = "";
    RecyclerView rv_displayEvents;

    ArrayList<Events_Do> events_list = new ArrayList<Events_Do>();
    List<EventDay> events = new ArrayList<>();
    Meetings meetings;
    ArrayList<Event_Details_DO> event_details_list = new ArrayList<Event_Details_DO>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.month_view_calendar, container, false);
//        final ImageButton create_event = (ImageButton) v.findViewById(R.id.create_event);
        thiscontext = container.getContext();
//        create_event.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callTimeZoneWebservice();
//            }
//        });
        calendarView = (CalendarView) v.findViewById(R.id.prolificcalendarview);
        calendarView.setSelectionBackground(R.drawable.custom_selector);
        calendarView.setSwipeEnabled(true);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                events_list.clear();
                java.util.Calendar myCalendar = eventDay.getCalendar();
                Updatelabel(myCalendar);

            }

        });
        calendarView.setForwardButtonImage(getResources().getDrawable(com.applandeo.materialcalendarview.R.drawable.ic_arrow_right));
        calendarView.setPreviousButtonImage(getResources().getDrawable(com.applandeo.materialcalendarview.R.drawable.ic_arrow_left));
        final java.util.Calendar mCalendar = Calendar.getInstance();

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                events_list.clear();
                mCalendar.set(mCalendar.MONTH, mCalendar.get(mCalendar.MONTH) - 1);
                String myFormat = "MMM dd, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                filter = sdf.format(mCalendar.getTime());
                callEventListwebservice(filter);
            }
        });
        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                events_list.clear();
                mCalendar.set(mCalendar.MONTH, mCalendar.get(mCalendar.MONTH) + 1);
                String myFormat = "MMM dd, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                filter = sdf.format(mCalendar.getTime());
                callEventListwebservice(filter);
            }
        });
        rv_displayEvents = (RecyclerView) v.findViewById(R.id.rv_events);
        callEventListwebservice(filter);
        meetings = (Meetings) getParentFragment();
        return v;
    }

    @Override
    public void onClick(View view) {

    }

    public interface EventDetailsListener {
        void onEventDetailsPassed(ArrayList<Event_Details_DO> event_details_list, String calendar_Type);
    }

    private void Updatelabel(Calendar myCalendar) {
        String myFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        filter = sdf.format(myCalendar.getTime());
        callEventListwebservice(filter);
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
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/events/" + timezoneoffset + "/" + "M" + event_creation_date, "Events_List", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callTimeZoneWebservice() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "event/timezones", "TIMEZONES", postData.toString());
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
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

            calendarView.setEvents(events);

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
            events_adapter = new Events_Adapter(events_list, this, getContext(), getActivity());
            rv_displayEvents.setAdapter(events_adapter);
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            e.printStackTrace();
        }
    }
//    private void load_event_details(JSONObject event_details, String event_id) {
//        Event_Details_DO event_details_do;
//        event_details_list.clear();
//        notification_list.clear();
//        attachments_list.clear();
//        tm_list.clear();
//        clients_list.clear();
//        try {
//            event_details_do = new Event_Details_DO();
//            event_details_do.setTitle(event_details.getString("title"));
//            event_details_do.setDescription(event_details.getString("description"));
//            event_details_do.setFrom_ts(event_details.getString("from_ts"));
//            event_details_do.setAll_day(event_details.getBoolean("allday"));
//            String from_ts = event_details_do.getFrom_ts();
//            Date event_date = AndroidUtils.stringToDateTimeDefault(from_ts, "yyyy-MM-dd'T'HH:mm:ss");
//            String event_start_time = AndroidUtils.getDateToString(event_date, "HH:mm a");
//            String event_date_forevents = AndroidUtils.getDateToString(event_date, "EEEE, MMM dd,yyyy");
//            event_details_do.setDate(event_date_forevents);
//            event_details_do.setTo_ts(event_details.getString("to_ts"));
//            event_details_do.setOffset(event_details.getString("timezone_offset"));
//            event_details_do.setOffset_location(event_details.getString("timezone_location"));
//            event_details_do.setConverted_Start_time(event_start_time);
//            event_details_do.setOwner(event_details.getBoolean("owner"));
//            String to_ts = event_details_do.getTo_ts();
//            Date event_date2 = AndroidUtils.stringToDateTimeDefault(to_ts, "yyyy-MM-dd'T'HH:mm:ss");
//            String event_end_time = AndroidUtils.getDateToString(event_date2, "HH:mm a");
//            event_details_do.setConverted_End_time(event_end_time);
//            event_details_do.setRepeat_interval(event_details.getString("repeat_interval"));
//            event_details_do.setNotifications(event_details.getJSONArray("notifications"));
//            event_details_do.setOwner_name(event_details.getString("owner_name"));
//            event_details_do.setAttachments(event_details.getJSONArray("attachments"));
//            event_details_do.setTeam_name(event_details.getJSONArray("invitees_internal"));
//            event_details_do.setTm_name(event_details.getJSONArray("invitees_external"));
//            event_details_list.add(event_details_do);
////            if (event_details_do.isOwner()) {
////                Edit_events(event_details_do, event_id);
////            } else {
//                edit_participant_events(event_details_do, event_id);
////            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void edit_participant_events(Event_Details_DO event_details_do, final String event_id) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.view_rsvp_events, null);
            tv_event_name = (TextView) dialogLayout.findViewById(R.id.event_title);
            tv_event_description = (TextView) dialogLayout.findViewById(R.id.event_description);
            tv_event_date = (TextView) dialogLayout.findViewById(R.id.tv_event_date);
            tv_event_time = (TextView) dialogLayout.findViewById(R.id.event_time);
            btn_event_save = (Button) dialogLayout.findViewById(R.id.save);
            tv_event_repetetion = (TextView) dialogLayout.findViewById(R.id.event_repetetion_number);
            final TextView yes = dialogLayout.findViewById(R.id.tv_yes);
            final TextView no = dialogLayout.findViewById(R.id.tv_no);
            final TextView maybe = dialogLayout.findViewById(R.id.tv_maybe);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        yes.setEnabled(false);
                        no.setEnabled(true);
                        maybe.setEnabled(true);

                        String rsvp = "yes";
//                        callRSVPwebservice(rsvp, event_id);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    try {
//                        no.setEnabled(false);
//                        maybe.setEnabled(true);
//                        yes.setEnabled(true);
//
//                        String rsvp = "no";
////                        callRSVPwebservice(rsvp, event_id);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                }
            });

            maybe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        maybe.setEnabled(false);
                        yes.setEnabled(true);
                        no.setEnabled(true);


                        String rsvp = "maybe";
//                        callRSVPwebservice(rsvp, event_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            final LinearLayout ll_notify_list = (LinearLayout) dialogLayout.findViewById(R.id.ll_notify);
            final LinearLayout ll_attched_list = (LinearLayout) dialogLayout.findViewById(R.id.ll_attachments);
            final LinearLayout ll_tm = (LinearLayout) dialogLayout.findViewById(R.id.ll_tm);

//            final LinearLayout ll_clients = (LinearLayout) dialogLayout.findViewById(R.id.ll_clients_list);
//            for (int j = 0; j < event_details_do.getNotifications().length(); j++) {
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.event_details_notifications, null);
//                final TextView tv_notifications = (TextView) view.findViewById(R.id.tv_event_notifications);
//                tv_notifications.setText(event_details_do.getNotifications().get(j).toString() + " " + "before");
//                ll_notify_list.addView(view);
//            }
//
//            JSONObject att_obj;
//            for (int a = 0; a < event_details_do.getAttachments().length(); a++) {
//                try {
//                    att_obj = event_details_do.getAttachments().getJSONObject(a);
//                    View view = LayoutInflater.from(getContext()).inflate(R.layout.event_details_attachments, null);
//                    final TextView attchment_list = (TextView) view.findViewById(R.id.tv_event_attachments);
//                    attchment_list.setText(att_obj.getString("name"));
//                    ll_attched_list.addView(view);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            JSONObject att_team_obj;
//            for (int a = 0; a < event_details_do.getTeam_name().length(); a++) {
//                att_team_obj = event_details_do.getTeam_name().getJSONObject(a);
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.event_details_team, null);
//                final TextView team_list = (TextView) view.findViewById(R.id.tv_event_team);
//                team_list.setText(att_team_obj.getString("name"));
//                ll_tm.addView(view);
//            }
//            JSONObject att_client_obj;
//            for (int c = 0; c < event_details_do.getTm_name().length(); c++) {
//                att_client_obj = event_details_do.getTm_name().getJSONObject(c);
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.event_details_clients, null);
//                final TextView clien_list = (TextView) view.findViewById(R.id.tv_event_clients);
//                clien_list.setText(att_client_obj.getString("tmName"));
//                ll_clients.addView(view);
//            }
//            btn_event_close = (ImageButton) dialogLayout.findViewById(R.id.btn_close_event);
//            tv_event_name.setText(event_details_do.getTitle());
//            tv_event_description.setText(event_details_do.getDescription());
//            tv_event_date.setText(event_details_do.getDate());
//            if (event_details_do.getDescription().toString().equals("")) {
//                tv_event_description.setVisibility(View.GONE);
//            }
//            if (event_details_do.getRepeat_interval().toString().equals("weekly")) {
//                tv_event_repetetion.setText("Repeats every week");
//            } else if (event_details_do.getRepeat_interval().toString().equals("biweekly")) {
//                tv_event_repetetion.setText("Repeats twice a week");
//            } else if (event_details_do.getRepeat_interval().toString().equals("monthly")) {
//                tv_event_repetetion.setText("Repeats every month");
//            } else if (event_details_do.getRepeat_interval().toString().equals("yearly")) {
//                tv_event_repetetion.setText("Repeats every year");
//            } else if (event_details_do.getRepeat_interval().toString().equals("none") || event_details_do.getRepeat_interval().toString().equals("")) {
//                tv_event_repetetion.setVisibility(View.GONE);
//            }
//
//            tv_event_time.setText("from" + " " + event_details_do.getConverted_Start_time() + " " + "to" + " " + event_details_do.getConverted_End_time());
            final AlertDialog dialog = builder.create();
//            back_screen = dialog;
//
//            btn_event_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    event_details_list.clear();
//                    back_screen.dismiss();
//                }
//            });
//            final Button tv_back = (Button) dialogLayout.findViewById(R.id.tv_back);
//            tv_back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    event_details_list.clear();
//                    back_screen.dismiss();
//                }
//            });
            dialog.setView(dialogLayout);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(ArrayList<Event_Details_DO> event_details_list) {

        if (eventDetailsListener != null) {
            String Calendar_Type = "Monthly";
            Log.d("EventsList", event_details_list.toString());
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

    private void delete_event(final String event_id, String event_name) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.alert_dialog_delete, null);
            final TextView delete_event_msg = dialogLayout.findViewById(R.id.delete_event_msg);
            delete_event_msg.setText("Are you Sure do you want to delete " + event_name + " ?");
            delete_event_msg.setTextSize(20);
            delete_event_msg.setTextColor(Color.BLACK);
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

    @Override
    public void delete(String event_id, boolean recur) {

    }

    private void callEventDetailsWebservice(String id) {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        int offset = timeZone.getRawOffset();
        long hours = TimeUnit.MILLISECONDS.toMinutes(offset);
        long timezoneoffset = (-1) * (hours);
        event_id = id;
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "event/details/" + event_id + "/" + timezoneoffset, "EVENT DETAILS", postData.toString());

    }
}
