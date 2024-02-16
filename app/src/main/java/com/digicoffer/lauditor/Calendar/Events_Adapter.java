package com.digicoffer.lauditor.Calendar;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.Models.Events_Do;
import com.digicoffer.lauditor.Calendar.Models.TeamDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Events_Adapter extends RecyclerView.Adapter<Events_Adapter.MyViewHolder> implements Filterable, View.OnClickListener, AsyncTaskCompleteListener {

    private static String FLAG = "";
    ArrayList<Events_Do> list_item = new ArrayList<Events_Do>();
    ArrayList<Events_Do> filtered_list = new ArrayList<Events_Do>();
    private Events_Adapter.EventListener context;
    private boolean isDetailsVisible = true;
    AlertDialog progress_dialog;
    ArrayList<Event_Details_DO> event_details_list = new ArrayList<Event_Details_DO>();
    Context mcontext;
    String event_id;
    Activity activity;
    MyViewHolder my_view_holder;

    public Events_Adapter(ArrayList<Events_Do> events_list, EventListener mcontext, Context context, Activity activity) {
        this.list_item = events_list;
        this.filtered_list = events_list;
        this.context = mcontext;
        this.mcontext = context;
        this.activity = activity;
    }

    public interface EventListener {
        void onEvent(ArrayList<Event_Details_DO> event_details_list);

        void delete_events(String id, boolean isrecurring);

        void delete(String event_id, boolean recur);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        try {
            if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
                try {
                    JSONObject result = new JSONObject(httpResult.getResponseContent());
                    if (httpResult.getRequestType().equals("EVENT DETAILS")) {
                        if (!result.getBoolean("error")) {
                            event_details_list.clear();
                            load_event_details(result.getJSONObject("event"), event_id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    private void load_event_details(JSONObject event_details, String event_id) {
        Event_Details_DO event_details_do;
        event_details_list.clear();
//        notification_list.clear();
//        attachments_list.clear();
//        tm_list.clear();
//        clients_list.clear();
        try {
            event_details_do = new Event_Details_DO();
            event_details_do.setEvent_type(event_details.getString("event_type"));
            event_details_do.setId(event_details.getString("id"));
            event_details_do.setTitle(event_details.getString("title"));
            event_details_do.setDescription(event_details.getString("description"));
            event_details_do.setFrom_ts(event_details.getString("from_ts"));
            event_details_do.setAll_day(event_details.getBoolean("allday"));
            String from_ts = event_details_do.getFrom_ts();
            Date event_date = AndroidUtils.stringToDateTimeDefault(from_ts, "yyyy-MM-dd'T'HH:mm:ss");
            String event_start_time = AndroidUtils.getDateToString(event_date, "HH:mm a");
            String event_date_forevents = AndroidUtils.getDateToString(event_date, "MM-dd-yyyy");
            event_details_do.setDate(event_date_forevents);

            event_details_do.setRecurring(event_details.getBoolean("isrecurring"));
            event_details_do.setLocation(event_details.getString("location"));
            event_details_do.setDialin(event_details.getString("dialin"));
            event_details_do.setTo_ts(event_details.getString("to_ts"));
            event_details_do.setOffset(event_details.getString("timezone_offset"));
            event_details_do.setOffset_location(event_details.getString("timezone_location"));
            event_details_do.setConverted_Start_time(event_start_time);
            event_details_do.setOwner(event_details.getBoolean("owner"));
            String to_ts = event_details_do.getTo_ts();
            event_details_do.setMeeting_link(event_details.getString("meeting_link"));
            Date event_date2 = AndroidUtils.stringToDateTimeDefault(to_ts, "yyyy-MM-dd'T'HH:mm:ss");
            String event_end_time = AndroidUtils.getDateToString(event_date2, "HH:mm a");
            event_details_do.setConverted_End_time(event_end_time);

            event_details_do.setRepeat_interval(event_details.getString("repeat_interval"));
            event_details_do.setNotifications(event_details.getJSONArray("notifications"));
            event_details_do.setOwner_name(event_details.getString("owner_name"));
            event_details_do.setAttachments(event_details.getJSONArray("attachments"));
            event_details_do.setTeam_name(event_details.getJSONArray("invitees_internal"));
            event_details_do.setTm_name(event_details.getJSONArray("invitees_external"));
            if (event_details.has("invitees_consumer_external")) {
                Log.d("ArrayListLog", event_details.getJSONArray("invitees_consumer_external").toString());
                event_details_do.setConsumer_external(event_details.getJSONArray("invitees_consumer_external"));
            }
            if (event_details.has("matter_name")) {
                event_details_do.setMatter_name(event_details.getString("matter_name"));
            }
            if (event_details.has("matter_id")) {
                event_details_do.setMatter_id(event_details.getString("matter_id"));
            }
            if (event_details.has("matter_type")) {
                event_details_do.setMatter_type(event_details.getString("matter_type"));
            }
            event_details_list.add(event_details_do);
            // Assuming you have created and populated the list

//            for (Event_Details_DO event : event_details_list) {
//                Log.d("ArrayListLog", event.toString());
//            }
            if ((FLAG == "MORE")) {
                load_more_details();
            } else {
                context.onEvent(event_details_list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void load_more_details() {


        for (int i = 0; i < event_details_list.size(); i++) {
            Event_Details_DO events_do = event_details_list.get(i);
            for (int a = 0; a < events_do.getAttachments().length(); a++) {
                try {

                    Log.d("Attachments", events_do.getAttachments().toString());
                    JSONObject att_obj = events_do.getAttachments().getJSONObject(a);
                    View view = LayoutInflater.from(mcontext).inflate(R.layout.event_details_notifications, null);
                    final TextView attchment_list = (TextView) view.findViewById(R.id.tv_event_notifications);
                    attchment_list.setText(att_obj.getString("name"));
                    my_view_holder.ll_documents.addView(view);
//                    Log.d("View Details",att_obj.getString())
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                for (int a = 0; a < events_do.getTeam_name().length(); a++) {
                    Log.d("Team Members", events_do.getTeam_name().toString());
                    JSONObject att_team_obj = events_do.getTeam_name().getJSONObject(a);
                    View view = LayoutInflater.from(mcontext).inflate(R.layout.event_details_notifications, null);
                    final TextView team_list = (TextView) view.findViewById(R.id.tv_event_notifications);
                    team_list.setText(att_team_obj.getString("name"));
                    my_view_holder.ll_team_members.addView(view);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                for (int c = 0; c < events_do.getTm_name().length(); c++) {
                    Log.d("Tm Name", events_do.getTeam_name().toString());
                    JSONObject att_client_obj = events_do.getTm_name().getJSONObject(c);
                    View view = LayoutInflater.from(mcontext).inflate(R.layout.event_details_notifications, null);
                    final TextView clien_list = (TextView) view.findViewById(R.id.tv_event_notifications);
                    clien_list.setText(att_client_obj.getString("tmName"));
                    my_view_holder.ll_clients.addView(view);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_recyler_list, parent, false);
        return new Events_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Events_Adapter.MyViewHolder holder, int position) {
        my_view_holder = holder;
        for (Events_Do event : filtered_list) {
            Log.d("Event List", event.getEvent_Name());
        }
        final Events_Do events_do = filtered_list.get(position);
//        AndroidUtils.showToast(events_do.toString(),mcontext);

        final String from_ts = events_do.getEvent_start_time();
        Date event_date = AndroidUtils.stringToDateTimeDefault(from_ts, "yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat amPmFormat = new SimpleDateFormat("a", Locale.US);
        String amPm_from_ts = amPmFormat.format(event_date);
        for (int j = 0; j < events_do.getNotifications().length(); j++) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.event_details_notifications, null);
            final TextView tv_notifications = (TextView) view.findViewById(R.id.tv_event_notifications);
            try {
                tv_notifications.setText(events_do.getNotifications().get(j).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.ll_notifications.addView(view);
        }

        final String converted_date = AndroidUtils.getDateToString(event_date, "yyyy-MM-dd");
        String converted_time = AndroidUtils.getDateToString(event_date, "hh:mm");
        final String to_ts = events_do.getEvent_end_time();
        Date end_date = AndroidUtils.stringToDateTimeDefault(to_ts, "yyyy-MM-dd'T'HH:mm:ss");
        String converted_end_time = AndroidUtils.getDateToString(end_date, "hh:mm");
        SimpleDateFormat amPmFormat_to_ts = new SimpleDateFormat("a", Locale.US);
        String amPm_to_ts = amPmFormat_to_ts.format(end_date);
        if (converted_time.equals("00:00")) {
            converted_time = "12:00";
        } else if (converted_end_time.equals("00:00")) {
            converted_end_time = "12:00";
        }
        holder.events_names.setText(converted_date);
        if (events_do.isOwner()) {
            holder.ib_view_events.setVisibility(View.GONE);
            holder.ll_rsvp.setVisibility(View.VISIBLE);
        } else {
            holder.ib_view_events.setVisibility(View.VISIBLE);
            holder.ll_rsvp.setVisibility(View.GONE);
        }
        holder.event_title.setText(events_do.getEvent_Name());
        if (events_do.isAll_day()) {
            holder.event_time.setText("All Day");
        } else {
            holder.event_time.setText(converted_time + amPm_from_ts + "-" + converted_end_time + amPm_to_ts);
        }

        holder.event_timezone.setText(events_do.getTimezone_location());
        holder.event_description.setText(events_do.getDescription());
        holder.tv_meeting_link.setText(events_do.getMeeting_link());
        holder.tv_phone_dialin.setText(events_do.getDialin());
        holder.tv_location.setText(events_do.getLocation());

        holder.bt_hide_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDetailsVisible) {
                    holder.ll_view_more.setVisibility(View.GONE);
                    // Clear the ArrayList here
                    event_details_list.clear();
                    holder.ll_notifications.removeAllViews();
                    holder.ll_documents.removeAllViews();
                    holder.ll_team_members.removeAllViews();
                    holder.ll_clients.removeAllViews();
                    holder.bt_hide_details.setText("View More");
                } else {
                    holder.bt_hide_details.setText("View Less");
                    holder.ll_view_more.setVisibility(View.VISIBLE);
                    FLAG = "MORE";
                    callEventDetailsWebservice(events_do.getEvent_id());
                }
                isDetailsVisible = !isDetailsVisible;
            }
        });
//        holder.ll_view_more.setVisibility(isDetailsVisible ? View.VISIBLE : View.GONE);
//        holder.bt_hide_details.setText(isDetailsVisible ? "View less" : "View More");
        holder.ib_delete_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.delete_events(events_do.getEvent_id(), events_do.isRecurring());
            }
        });
        holder.ib_view_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = "VIEW";
                callEventDetailsWebservice(events_do.getEvent_id());
//                context.onEvent(filtered_list,events_do);
            }
        });
    }

    private void callEventDetailsWebservice(String id) {
        progress_dialog = AndroidUtils.get_progress(activity);
        JSONObject postData = new JSONObject();
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        int offset = timeZone.getRawOffset();
        long hours = TimeUnit.MILLISECONDS.toMinutes(offset);
        long timezoneoffset = (-1) * (hours);
        event_id = id;
        WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.GET, "v3/event/" + event_id + "/" + timezoneoffset, "EVENT DETAILS", postData.toString());

    }

    @Override
    public int getItemCount() {
        return filtered_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView events_names, tv_meeting_link, tv_phone_dialin, tv_location, tv_yes, tv_no, tv_maybe, event_title, event_time, event_description, event_timezone;
        TextView time, meeting_link, dialin, location, notified_before, documents, team_members, clients;
        ImageButton ib_view_events, ib_delete_events;
        ImageButton ib_view;
        ImageButton ib_delete;
        AppCompatButton bt_hide_details;
        LinearLayout ll_notifications, ll_view_more, ll_rsvp, ll_documents, ll_team_members, ll_clients;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            events_names = (TextView) itemView.findViewById(R.id.events_names);
            time = (TextView) itemView.findViewById(R.id.time);
            meeting_link = (TextView) itemView.findViewById(R.id.meeting_name);
            meeting_link.setText(R.string.meeting_link);
            dialin = itemView.findViewById(R.id.dialin);
            dialin.setText("Join by phone");
            location = itemView.findViewById(R.id.location);
            location.setText(R.string.location);
            notified_before = itemView.findViewById(R.id.notified_before);
            notified_before.setText("Notified Before");
            documents = itemView.findViewById(R.id.documents);
            documents.setText(R.string.documents);
            team_members = itemView.findViewById(R.id.team_members);
            team_members.setText(R.string.team_members);
            clients = itemView.findViewById(R.id.clients);
            clients.setText(R.string.client);
            event_title = (TextView) itemView.findViewById(R.id.event_title);
            event_title.setTextColor(Color.BLACK);
            event_title.setTypeface(null, Typeface.BOLD);
            event_title.setTextSize(20);
            ib_view_events = (ImageButton) itemView.findViewById(R.id.ib_view_events);
            tv_yes = (TextView) itemView.findViewById(R.id.tv_yes);
            tv_no = (TextView) itemView.findViewById(R.id.tv_no);
            tv_maybe = (TextView) itemView.findViewById(R.id.tv_maybe);
            event_time = (TextView) itemView.findViewById(R.id.event_time);
            event_time.setTextColor(Color.BLACK);
            event_timezone = (TextView) itemView.findViewById(R.id.event_timezone);
            event_timezone.setTextColor(Color.BLACK);
            ib_delete_events = (ImageButton) itemView.findViewById(R.id.ib_delete_events);
            event_description = (TextView) itemView.findViewById(R.id.event_description);
            event_description.setTextColor(Color.BLACK);
            tv_meeting_link = (TextView) itemView.findViewById(R.id.tv_meeting_link);
            tv_meeting_link.setTextColor(Color.BLACK);
            tv_phone_dialin = (TextView) itemView.findViewById(R.id.tv_phone_dialin);
            tv_phone_dialin.setTextColor(Color.BLACK);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_location.setTextColor(Color.BLACK);
            bt_hide_details = (AppCompatButton) itemView.findViewById(R.id.bt_hide_details);
            ll_notifications = (LinearLayout) itemView.findViewById(R.id.ll_notifications);
            ll_view_more = (LinearLayout) itemView.findViewById(R.id.ll_view_more);
            ll_documents = (LinearLayout) itemView.findViewById(R.id.ll_documents);
            ll_team_members = (LinearLayout) itemView.findViewById(R.id.ll_team_members);
            ll_clients = (LinearLayout) itemView.findViewById(R.id.ll_clients);
            ll_rsvp = (LinearLayout) itemView.findViewById(R.id.ll_rsvp);
        }
    }
}
