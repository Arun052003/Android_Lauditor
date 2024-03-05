package com.digicoffer.lauditor.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Calendar.Models.CalendarDo;
import com.digicoffer.lauditor.Calendar.Models.DocumentsDo;
import com.digicoffer.lauditor.Calendar.Models.Event_Details_DO;
import com.digicoffer.lauditor.Calendar.Models.MinutesDO;
import com.digicoffer.lauditor.Calendar.Models.RelationshipsDO;
import com.digicoffer.lauditor.Calendar.Models.TaskDo;
import com.digicoffer.lauditor.Calendar.Models.TeamDo;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.digicoffer.lauditor.common_objects.TimeZonesDO;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pgpainless.key.selection.key.util.And;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CreateEvent extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    private static String ADAPTER_TAG = "";
    private String Event_name = "";
    private TextView error_msg, error_msg1;
    private TextInputEditText tv_numbers;
    private AlertDialog progressDialog;
    private String existing_task;
    private boolean isTimesheet;
    private String existing_date;
    private String existing_start_time;
    private String existing_end_time;
    private boolean isExistingAllday;
    private String existing_time_zone;
    private String existing_repetetion;
    private String existing_meeting_link;
    private String existing_dialin;
    String Calendar_type = "";
    private String existing_location;
    private String existing_description;
    String recurring_edit_choice;
    private ArrayList<Event_Details_DO> existing_events_list = new ArrayList<>();
    String event_id;
    String time_format = "", time_value = "";
    String event_name;
    MultiAutoCompleteTextView at_family_members;
    CardView cv_meeting_details, cv_add_clients;
    int temp = 0;
    TimePickerDialog timePickerDialog;
    int end = 0;
    private boolean is_notify_clicked = true;
    private boolean is_timenotify;
    int endMinute = 0;
    int startMinute = 0;
    final ArrayList<String> Repetetions = new ArrayList<>();
    JSONArray notification = new JSONArray();
    LinearLayout ll_client_team_members, ll_documents_list, ll_attach_document, ll_selected_entites, selected_individual, ll_individual_list, ll_selected_team_members, selected_tm, selected_groups;
    final int[] mHour = new int[1];
    final int[] mMinute = new int[1];
    private ArrayList<String> selectedValues = new ArrayList<>();
    String selected_hour_type = "";
    //    String selected_notify_value = "";
    private boolean timeZonesTaskCompleted = false;
    CheckBox cb_all_day, cb_add_to_timesheet;
    boolean isAllDay;
    boolean isAddTimesheet;
    private boolean matterTaskCompleted = false;
    private boolean tmTaskCompleted = false;
    final String[] AM_PM = new String[1];
    int offset;
    private Button btn_add_groups, btn_attach_document, btn_create_event, btn_add_clients, btn_assigned_clients, btn_individual;
    String team_member_id;
    int start_time = 0;
    LocalTime end_time;
    LocalTime close_time = LocalTime.ofSecondOfDay(0);
    int adapter_position = 0;
    ArrayList<RelationshipsDO> entities_list = new ArrayList<>();
    ArrayList<RelationshipsDO> individual_list = new ArrayList<>();
    ArrayList<RelationshipsDO> selected_individual_list = new ArrayList<>();
    ArrayList<DocumentsDo> selected_documents_list = new ArrayList<>();
    ArrayList<RelationshipsDO> selected_entity_client_list = new ArrayList<>();
    ArrayList<RelationshipsDO> new_selected_client_list = new ArrayList<>();
    ArrayList<RelationshipsDO> entity_client_list = new ArrayList<>();
    ArrayList<TeamDo> selected_tm_list = new ArrayList<>();
    private TextView at_add_groups, tv_message, at_attach_document, at_add_clients, at_assigned_client, at_individual;
    private Spinner sp_add_team_member, sp_add_entity, sp_client_team_members;
    private TextInputEditText tv_meeting_link, tv_dialing_number, tv_location, tv_description;
    private AppCompatButton add_notification, btn_cancel_event, btn_save_timesheet;
    TextView tv_project_name, tv_matter_name, tv_task_name, tv_date_name, tv_time_zone, tv_repetetion, tv_meeting_link_name, tv_dial_in_number, tv_location_name, tv_description_name, tv_message_name, add_groups, add_entities, tv_assigned_client, tv_individual, tv_selected_individual, tv_selected_tm, tv_selected_document, tv_name, tv_selected_clients;
    TextView tv_sp_project, tv_sp_matter_name, tv_sp_task_name, tv_sp_time_zone, tv_sp_repetetion, Time_duration, tv_sp_entities, tv_attach_document, tv_sp_minutes;
    ListView sp_project, sp_matter_name, sp_task, sp_time_zone, sp_repetetion, sp_entities, sp_minutes;
    boolean ischecked_project = true, ischecked_matter = true, ischecked_task = true, ischecked_time = true, ischecked_repetetion = true, is_notify = true, is_entities = true, is_clicked_team = true, is_clicked_clients = true, is_clicked_individuals = true, is_clicked_documents = true;
    AppCompatButton tv_event_creation_date, tv_event_start_time, tv_event_end_time;
    NestedScrollView scrollView;
    private String selected_project;
    private String selected_task;
    private String entity_id;
    Hashtable<String, Integer> timesPosHash = new Hashtable<>();
    ArrayList<TimeZonesDO> timeZonesList = new ArrayList<TimeZonesDO>();
    private LinearLayout ll_project, selected_attached_documents, ll_matter_name, ll_message, ll_task, ll_repetetion, ll_add_notification, ll_add_groups, ll_add_entities, ll_individual, ll_documents_view, ll_assign_clients;
    ArrayList<CalendarDo> projectList = new ArrayList<>();
    ArrayList<ViewMatterModel> matterList = new ArrayList<>();
    ArrayList<DocumentsDo> documents_list = new ArrayList<>();
    ArrayList<TeamDo> teamList = new ArrayList<>();
    ArrayList<TaskDo> legalTaksList = new ArrayList<>();
    private String repeat_interval;
    private String event_creation_date;
    private String event_starting_date;
    private String event_end_time;
    private String timezone_location;
    private String matter_id;
    private String matter_name;
    private String matter_legal;
    private Meetings meetings;

    RecyclerView rv_groups_view, rv_clients_view, rv_individuals_view, rv_documents_view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        callTimeZoneWebservice();
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event, container, false);
        sp_project = view.findViewById(R.id.sp_project1);
        scrollView = view.findViewById(R.id.scrollView);
        sp_matter_name = view.findViewById(R.id.sp_matter_name);
        at_individual = view.findViewById(R.id.at_individual);
        sp_task = view.findViewById(R.id.sp_task);
        sp_task.setVisibility(View.GONE);
        ll_matter_name = view.findViewById(R.id.ll_matter_name);
        ll_matter_name.setVisibility(View.GONE);
        sp_time_zone = view.findViewById(R.id.sp_time_zone);
        sp_time_zone.setVisibility(View.GONE);


        tv_sp_time_zone = view.findViewById(R.id.tv_sp_time_zone);
        tv_sp_repetetion = view.findViewById(R.id.tv_sp_repetetion);
        tv_sp_repetetion.setText("None");
        sp_repetetion = view.findViewById(R.id.sp_repetetion);
        sp_repetetion.setVisibility(View.GONE);
        at_add_groups = view.findViewById(R.id.at_add_groups);
        add_groups = view.findViewById(R.id.add_groups);
        add_groups.setText("Add Team Members");
        btn_create_event = view.findViewById(R.id.btn_create_event);
        btn_create_event.setOnClickListener(this);
        ll_message = view.findViewById(R.id.ll_message);
        tv_message = view.findViewById(R.id.tv_message);
        tv_message_name = view.findViewById(R.id.tv_message_name);
        tv_message_name.setText("Message");
        tv_message.setHint("Message");
        at_attach_document = view.findViewById(R.id.at_attach_document);
        cb_all_day = view.findViewById(R.id.cb_all_day);
        cb_all_day.setText("All Day");
        cb_all_day.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        btn_cancel_event = view.findViewById(R.id.btn_cancel_event);
        Time_duration = view.findViewById(R.id.Time_duration);
        Time_duration.setText("Duration : ");
//        Time_duration.setVisibility(View.GONE);
        tv_project_name = view.findViewById(R.id.tv_project_name);
        tv_project_name.setText("Event Type");
        tv_sp_project = view.findViewById(R.id.tv_sp_project);
//        list_scroll_project = view.findViewById(R.id.list_scroll_project);
        sp_project.setVisibility(View.GONE);

        tv_matter_name = view.findViewById(R.id.tv_matter_name);
        tv_matter_name.setText(R.string.matter_name);
        tv_sp_matter_name = view.findViewById(R.id.tv_sp_matter_name);
//        list_scroll_matter = view.findViewById(R.id.list_scroll_matter);
        sp_matter_name.setVisibility(View.GONE);
        sp_matter_name.setNestedScrollingEnabled(true);
//        sp_project.setNestedScrollingEnabled(true);
        tv_task_name = view.findViewById(R.id.tv_task_name);
        tv_task_name.setText(R.string.task);
        tv_sp_task_name = view.findViewById(R.id.tv_sp_task_name);
        tv_date_name = view.findViewById(R.id.tv_date_name);
        tv_date_name.setText(R.string.date);
        tv_time_zone = view.findViewById(R.id.tv_time_zone);
        tv_time_zone.setText(R.string.time_zone);
        tv_repetetion = view.findViewById(R.id.tv_repetetion);
        tv_repetetion.setText("Repetition");
        tv_meeting_link_name = view.findViewById(R.id.tv_meeting_link_name);
        tv_meeting_link_name.setText(R.string.meeting_link);
        tv_dial_in_number = view.findViewById(R.id.tv_dial_in_number);
        tv_dial_in_number.setText("Dial-In Number");
        tv_location_name = view.findViewById(R.id.tv_location_name);
        tv_location_name.setText(R.string.location);
        tv_description_name = view.findViewById(R.id.tv_description_name);
        tv_description_name.setText(R.string.description);
        tv_sp_entities = view.findViewById(R.id.tv_sp_entities);
        sp_entities = view.findViewById(R.id.sp_entities);
        sp_entities.setVisibility(View.GONE);
//       isAllDay = cb_all_day.isChecked();
        cv_meeting_details = view.findViewById(R.id.cv_meeting_details);
        cv_add_clients = view.findViewById(R.id.cv_add_clients);
        cv_add_clients.setVisibility(View.GONE);

        cb_add_to_timesheet = view.findViewById(R.id.cb_add_to_timesheet);
        cb_add_to_timesheet.setText("Add To Timesheet");
        cb_add_to_timesheet.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        loadcheckboxData();
        ll_attach_document = view.findViewById(R.id.ll_attach_document);
        btn_attach_document = view.findViewById(R.id.btn_attach_document);
        btn_attach_document.setOnClickListener(this);
        ll_add_notification = view.findViewById(R.id.ll_add_notification);

        rv_groups_view = view.findViewById(R.id.rv_groups_view);
        rv_groups_view.setVisibility(View.GONE);
        rv_groups_view.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        rv_clients_view = view.findViewById(R.id.rv_clients_view);
        rv_clients_view.setVisibility(View.GONE);
        rv_clients_view.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        rv_individuals_view = view.findViewById(R.id.rv_individuals_view);
        rv_individuals_view.setVisibility(View.GONE);
        rv_individuals_view.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        rv_documents_view = view.findViewById(R.id.rv_documents_view);
        rv_documents_view.setVisibility(View.GONE);
        rv_documents_view.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));

        add_entities = view.findViewById(R.id.add_entities);
        add_entities.setText(R.string.add_entity);
        add_notification = view.findViewById(R.id.add_notification);
        add_notification.setOnClickListener(this);
        add_notification.setText("Add Notification");
        ll_documents_list = view.findViewById(R.id.ll_documents_list);
        at_assigned_client = view.findViewById(R.id.at_assigned_client);
        tv_assigned_client = view.findViewById(R.id.tv_assigned_client);
        tv_assigned_client.setText(R.string.add_clients);
        tv_individual = view.findViewById(R.id.tv_individual);
        tv_individual.setText("Add Individuals");
        tv_selected_clients = view.findViewById(R.id.tv_selected_clients);
        tv_selected_clients.setText("Selected Entities");
        tv_selected_individual = view.findViewById(R.id.tv_selected_individual);
        tv_selected_individual.setText("Selected Individuals");
        tv_selected_tm = view.findViewById(R.id.tv_selected_tm);
        tv_selected_tm.setText("Selected Clients");
        tv_selected_document = view.findViewById(R.id.tv_selected_document);
        tv_selected_document.setText("Selected Documents");
        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText("Selected Team Members");
        tv_attach_document = view.findViewById(R.id.tv_attach_document);
        tv_attach_document.setText("Add Documents");

        btn_add_groups = view.findViewById(R.id.btn_add_groups);
        selected_groups = view.findViewById(R.id.selected_groups);
        btn_individual = view.findViewById(R.id.btn_individual);
        selected_individual = view.findViewById(R.id.selected_individual);
        ll_individual_list = view.findViewById(R.id.ll_individual_list);
        btn_individual.setOnClickListener(this);
        btn_add_groups.setOnClickListener(this);
//        btn_add_clients = view.findViewById(R.id.btn_add_clients);
//        btn_add_clients.setOnClickListener(this);
        selected_tm = view.findViewById(R.id.selected_tm);
        ll_client_team_members = view.findViewById(R.id.ll_client_team_members);
        ll_selected_entites = view.findViewById(R.id.ll_selected_entites);
        ll_selected_team_members = view.findViewById(R.id.ll_selected_team_members);
        btn_assigned_clients = view.findViewById(R.id.btn_assigned_clients);
        btn_assigned_clients.setOnClickListener(this);
        tv_event_creation_date = view.findViewById(R.id.tv_event_creation_date);
        tv_event_creation_date.setOnClickListener(this);
        tv_event_creation_date.setHint(R.string.date);
        tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
        tv_event_start_time.setOnClickListener(this);
        tv_event_end_time = view.findViewById(R.id.tv_event_end_time);
        tv_event_end_time.setOnClickListener(this);
        tv_event_end_time.setHint(R.string.to);
        selected_attached_documents = view.findViewById(R.id.selected_attached_documents);
        tv_meeting_link = view.findViewById(R.id.tv_meeting_link1);
        tv_meeting_link.setHint(R.string.meeting_link);
        tv_dialing_number = view.findViewById(R.id.tv_dialing_number);
        tv_dialing_number.setHint("Dial In Number");
        tv_location = view.findViewById(R.id.tv_location1);
        tv_location.setHint(R.string.location);
        tv_description = view.findViewById(R.id.tv_description);
        tv_description.setHint(R.string.description);
        ll_project = view.findViewById(R.id.ll_project);

        ll_task = view.findViewById(R.id.ll_task);
        ll_task.setVisibility(View.GONE);
        ll_repetetion = view.findViewById(R.id.ll_repetetion);
        ll_repetetion.setVisibility(View.GONE);
        ll_add_groups = view.findViewById(R.id.ll_add_groups);
        ll_assign_clients = view.findViewById(R.id.ll_assign_clients);
        ll_assign_clients.setVisibility(View.GONE);
        ll_add_entities = view.findViewById(R.id.ll_add_entities);

//        display_check_list();
//        cv_add_clients.setVisibility(View.VISIBLE);
//        ll_assign_clients = view.findViewById(R.id.ll_assign_team_members);

        ll_documents_view = view.findViewById(R.id.ll_documents_view);
        ll_individual = view.findViewById(R.id.ll_individual);

        tv_sp_task_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ischecked_task) {
                    sp_task.setVisibility(View.VISIBLE);
                } else
                    sp_task.setVisibility(View.GONE);
                ischecked_task = !ischecked_task;
            }
        });
        tv_sp_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_project, sp_project);
                ischecked_project = !ischecked_project;
            }
        });

        tv_sp_time_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_time, sp_time_zone);
                ischecked_time = !ischecked_time;
            }
        });
        tv_sp_repetetion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_repetetion, sp_repetetion);
                ischecked_repetetion = !ischecked_repetetion;
            }
        });
        tv_sp_matter_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_matter, sp_matter_name);
                ischecked_matter = !ischecked_matter;
            }
        });
        tv_sp_entities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_entities) {
                    if (entities_list.size() == 0)
                        AndroidUtils.showToast("No Entities to View", getContext());
                    else
                        sp_entities.setVisibility(View.VISIBLE);
                } else {
                    sp_entities.setVisibility(View.GONE);
                }
                is_entities = !is_entities;
            }
        });
        at_add_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_clicked_team) {
                    if (!(matterList.size() == 0)) {
                        TeamMembersPopup();
                    } else {
                        rv_groups_view.setVisibility(View.VISIBLE);
                    }
                } else {
                    rv_groups_view.setVisibility(View.GONE);
                }
                is_clicked_team = !is_clicked_team;
            }
        });
        btn_cancel_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadClearedLists();
                meetings.loadView();
            }
        });
        at_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_clicked_individuals) {
                    rv_individuals_view.setVisibility(View.VISIBLE);
                    load_individual_Popup();
                } else {
                    rv_individuals_view.setVisibility(View.GONE);
                }
                is_clicked_individuals = !is_clicked_individuals;
            }
        });
        at_assigned_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_clicked_clients) {
                    rv_clients_view.setVisibility(View.VISIBLE);
                    loadEntityClientPopup();
                } else {
                    rv_clients_view.setVisibility(View.GONE);
                }
                is_clicked_clients = !is_clicked_clients;
            }
        });

        at_attach_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_clicked_documents) {
                    rv_documents_view.setVisibility(View.VISIBLE);
                    load_documents_Popup();
                } else {
                    rv_documents_view.setVisibility(View.GONE);
                }
                is_clicked_documents = !is_clicked_documents;
            }
        });
        projectList.clear();
        if (Constants.ROLE.equals("AAM")) {
            projectList.add(new CalendarDo("Overhead"));
            projectList.add(new CalendarDo("Others"));
            projectList.add(new CalendarDo("Reminders"));
        } else {
            projectList.add(new CalendarDo("Legal Matter"));
            projectList.add(new CalendarDo("General Matter"));
            projectList.add(new CalendarDo("Overhead"));
            projectList.add(new CalendarDo("Others"));
            projectList.add(new CalendarDo("Reminders"));
        }
        legalTaksList.clear();

//        callTimeZoneWebservice();
        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), projectList);

        sp_project.setAdapter(spinner_adapter);
        sp_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_project = projectList.get(position).getProjectName();
                tv_sp_project.setText(selected_project);
                ischecked_project = true;
                ll_repetetion.setVisibility(View.VISIBLE);
                ll_task.setVisibility(View.VISIBLE);
                selected_task = "";
                tv_sp_task_name.setText("");
                matter_name = "";
                tv_sp_matter_name.setText("");
                load_clear_list();
                hide_all_list();
                clear_selected_list();
                loadProjectData(selected_project);
            }
        });
        return view;
    }

    private void loadcheckboxData() {

        cb_all_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_all_day.isChecked()) {
                    tv_event_start_time.setVisibility(View.GONE);
                    tv_event_end_time.setVisibility(View.GONE);
                    isAllDay = true;
                } else {
                    tv_event_start_time.setVisibility(View.VISIBLE);
                    tv_event_end_time.setVisibility(View.VISIBLE);
                    isAllDay = false;
                }
            }
        });
        cb_add_to_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_add_to_timesheet.isChecked()) {
                    isAddTimesheet = true;
                } else {
                    isAddTimesheet = false;
                }
            }
        });
        //        cb_all_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // Retrieve the checked status of the checkbox
//                if (isChecked) {
//                    tv_event_start_time.setVisibility(View.GONE);
//                    tv_event_end_time.setVisibility(View.GONE);
//                    isAllDay = true;
//                } else {
//                    tv_event_start_time.setVisibility(View.VISIBLE);
//                    tv_event_end_time.setVisibility(View.VISIBLE);
//                    isAllDay = false;
//                }
//            }
//        });
//        cb_add_to_timesheet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // Retrieve the checked status of the checkbox
//                if (isChecked) {
//                    // Checkbox is checked, return true
//                    // Perform any additional actions here
//                    isAddTimesheet = true;
//                } else {
//                    // Checkbox is not checked, return false
//                    // Perform any additional actions here
//                    isAddTimesheet = false;
//                }
//            }
//        });
    }

    private void callTimeZoneWebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "event/timezones", "TIMEZONES", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void Show_Time_picker(boolean is_start, String title) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay < 12) {
                    AM_PM[0] = " AM";
                } else {
                    AM_PM[0] = " PM";
                }
                if (is_start) {
                    start_time = hourOfDay;
                    startMinute = minute;
                    tv_event_start_time.setText(hourOfDay + ":" + minute + AM_PM[0]);
                    if (tv_event_end_time.getText().toString().equals("")) {
                        close_time = LocalTime.of(hourOfDay, minute).plusMinutes(30);
                        tv_event_end_time.setText(close_time.toString() + AM_PM[0]);
                    }
                } else {
                    if (hourOfDay < start_time) {
                        AndroidUtils.showAlert("Please select End Time with in this Date", getContext());
                        tv_event_end_time.setText("");
                        Time_duration.setText("Duration : ");
                    } else {
                        tv_event_end_time.setText(hourOfDay + ":" + minute + AM_PM[0]);
                    }
                }
                display_duration(tv_event_start_time.getText().toString(), tv_event_end_time.getText().toString());
            }
        }, hour, minute, false);
        timePickerDialog.setTitle(title);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_event_creation_date:
                final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                    private void updateLabel() {
                        String myFormat = "MM-dd-yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        tv_event_creation_date.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(java.util.Calendar.YEAR), myCalendar.get(java.util.Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;
            case R.id.tv_event_start_time:
                if (tv_event_creation_date.getText().equals("")) {
                    AndroidUtils.showAlert("Please First Select the Date", getContext());
                } else {
                    Show_Time_picker(true, "Select Start Time");
                }
                break;
            case R.id.tv_event_end_time:
                if (tv_event_creation_date.getText().equals("")) {
                    AndroidUtils.showAlert("Please First Select the Date", getContext());
                } else if (tv_event_start_time.getText().equals("")) {
                    AndroidUtils.showAlert("Please Select the Start Time", getContext());
                } else {
                    Show_Time_picker(false, "Select End Time");
                }
                break;
            case R.id.add_notification:
                is_notify_clicked = false;
                if (!is_notify_clicked) {
                    NotificationPopup();
                }
                break;
//            case R.id.btn_cancel_timesheet:
//                AndroidUtils.showToast("Event is not Created", getContext());
//                loadClearedLists();
//                meetings.loadView();
            case R.id.btn_create_event:
                if (tv_sp_project.getText().toString().equals("")) {
                    AndroidUtils.showAlert("Event Name is required", getContext());
                } else {
                    if (!matter_legal.equals("reminders")) {
                        if (tv_sp_task_name.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Task is required", getContext());
                        } else if (tv_event_creation_date.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Date is required", getContext());
                        } else if (tv_event_start_time.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Start Time is required", getContext());
                        } else if (tv_sp_time_zone.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Time Zone is required", getContext());
//                        } else if (!(selectedValues.size() == 0)) {
//                            if (!is_timenotify)
//                                AndroidUtils.showAlert("Please Check Notification", getContext());
                        } else {
                            if (Constants.is_meeting == "Create")
                                callCreateEventWebservice();
                            else
                                update_event();
                        }
                    } else {
                        if (tv_event_creation_date.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Date is required", getContext());
                        } else if (tv_event_start_time.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Start Time is required", getContext());
                        } else if (tv_sp_time_zone.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Time Zone is required", getContext());
                        } else if (tv_message.getText().toString().equals("")) {
                            AndroidUtils.showAlert("Message is required", getContext());
//                        } else if (!(selectedValues.size() == 0)) {
//                            if (!is_timenotify)
//                                AndroidUtils.showAlert("Please Check Notification", getContext());
                        } else {
                            if (Constants.is_meeting == "Create")
                                callCreateEventWebservice();
                            else
                                update_event();
                        }
                    }
                }
                break;
        }//End of Switch Case...
    }

    private void display_duration(String start_time, String end_time) {
        //start time
        String v1 = "";
        String v2 = "";
        if (start_time.contains("AM")) {
            v1 = start_time.replace(" AM", "");
        } else if (start_time.contains("PM")) {
            v1 = start_time.replace(" PM", "");
        }
        String[] value_1 = v1.split(":");
        int firstString = Integer.parseInt(value_1[0]);
        int secondString = Integer.parseInt(value_1[1]);
        LocalTime st_time = LocalTime.of(firstString, secondString);
        //end time
        if (end_time.contains("AM")) {
            v2 = end_time.replace(" AM", "");
        } else if (end_time.contains("PM")) {
            v2 = end_time.replace(" PM", "");
        }
        String[] value_2 = v2.split(":");
        Log.d("vvvvvv2", "" + value_2);
        Log.d("vvvvvv1", value_1.toString());
        int e_firstString = Integer.parseInt(value_2[0]);
        int e_secondString = Integer.parseInt(value_2[1]);
        LocalTime ed_time = LocalTime.of(e_firstString, e_secondString);
        Duration t_d = Duration.between(st_time, ed_time);
        Log.d("Hours", firstString + ",  " + secondString + ",  " + e_firstString + e_secondString);
        Time_duration.setText("Duration : " + t_d.toHours() + " Hours," + t_d.toMinutes() % 60 + " Minutes");
    }

    private void load_documents_Popup() {
        try {
//            documents_list.clear();
            if (documents_list.size() == 0) {
                for (int i = 0; i < matterList.size(); i++) {
                    if (matter_id.equals(matterList.get(i).getId())) {
                        JSONArray documents = matterList.get(i).getDocuments();
                        for (int j = 0; j < documents.length(); j++) {
                            DocumentsDo documentsDo = new DocumentsDo();
                            JSONObject jsonObject = documents.getJSONObject(j);
                            documentsDo.setDocid(jsonObject.getString("docid"));
                            documentsDo.setDoctype(jsonObject.getString("doctype"));
                            documentsDo.setName(jsonObject.getString("name"));
                            documentsDo.setUser_id(jsonObject.getString("user_id"));
                            documents_list.add(documentsDo);
                        }
                    }
                }
            }
            if (documents_list.size() == 0) {
                rv_documents_view.setVisibility(View.GONE);
                is_clicked_documents = true;
                AndroidUtils.showToast("No document to show", getContext());
            } else {
                rv_documents_view.setVisibility(View.VISIBLE);
                for (int i = 0; i < documents_list.size(); i++) {
                    for (int j = 0; j < selected_documents_list.size(); j++) {
                        if (documents_list.get(i).getDocid().matches(selected_documents_list.get(j).getDocid())) {
                            DocumentsDo documentsDo = documents_list.get(i);
                            documentsDo.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);
                        }
                    }
                }
//            selected_tm_list.clear();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_documents_view.setLayoutManager(layoutManager);
                rv_documents_view.setHasFixedSize(true);
                ADAPTER_TAG = "Documents";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_documents_view.setAdapter(documentsAdapter);

                btn_attach_document.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getDocuments_list().size(); i++) {
                            DocumentsDo documentsDo = documentsAdapter.getDocuments_list().get(i);
                            if (documentsDo.isChecked()) {
                                selected_documents_list.add(documentsDo);
//                        AndroidUtils.showAlert(selected_individual_list.toString(),getContext());
//                        new_selected_individual_list.add(teamModel);
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }

                        loadSelectedDocuments();
                        is_clicked_documents = true;
                        rv_documents_view.setVisibility(View.GONE);
//                    loadSelectedIndividual();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadSelectedDocuments() {
        String[] value = new String[selected_documents_list.size()];
        for (int i = 0; i < selected_documents_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_documents_list.get(i).getName();

        }

        String str = String.join(",", value);
        at_attach_document.setText(str);
        if (selected_documents_list.size() == 0) {
            selected_attached_documents.setVisibility(View.GONE);
        } else {
            selected_attached_documents.setVisibility(View.VISIBLE);
        }

        ll_documents_list.removeAllViews();
        for (int i = 0; i < selected_documents_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_documents_list.get(i).getName());
            ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
            ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
            iv_remove_opponent.setTag(i);
            iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = 0;
                        if (v.getTag() instanceof Integer) {
                            position = (Integer) v.getTag();
                            v = ll_documents_list.getChildAt(position);
                            ll_documents_list.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            DocumentsDo teamModel = selected_documents_list.get(position);
                            teamModel.setChecked(false);
                            selected_documents_list.remove(position);
                            if (selected_documents_list.size() == 0) {
                                selected_attached_documents.setVisibility(View.GONE);
                                at_attach_document.setText("Select Document");
                            }
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_documents_list.size()];
                            for (int i = 0; i < selected_documents_list.size(); i++) {
                                value[i] = selected_documents_list.get(i).getName();
                            }

                            String str = String.join(",", value);
                            at_attach_document.setText(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_documents_list.addView(view_opponents);
        }
    }

    private void callCreateEventWebservice() {
        try {
            String doctype = "doctype";
            String docid = "docid";
            JSONObject postData = new JSONObject();
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONArray selected_team_member = new JSONArray();
            JSONArray selected_clients_list = new JSONArray();
            JSONArray individual_array = new JSONArray();
            JSONArray added_notification_list = new JSONArray();
            JSONArray time_sheets = new JSONArray();
            JSONArray existing_attachments = new JSONArray();
            for (int i = 0; i < selectedValues.size(); i++) {
//            NotifyMeDo notifyMeDo = notifyme_list.get(i);
                added_notification_list.put(selectedValues.get(i));
            }

            JSONObject selected_docs;
            for (int j = 0; j < selected_documents_list.size(); j++) {
                selected_docs = new JSONObject();
                DocumentsDo attachDocumentsDO = selected_documents_list.get(j);
                selected_docs.put(doctype, attachDocumentsDO.getDoctype());
                selected_docs.put(docid, attachDocumentsDO.getDocid());
                existing_attachments.put(selected_docs);
            }

            for (int i = 0; i < selected_tm_list.size(); i++) {
                TeamDo addTeamMembersDo = selected_tm_list.get(i);
                selected_team_member.put(addTeamMembersDo.getId());
            }
            for (int i = 0; i < selected_entity_client_list.size(); i++) {
                RelationshipsDO addClientsDo = selected_entity_client_list.get(i);
                selected_clients_list.put(addClientsDo.getId());
            }
            for (int i = 0; i < selected_individual_list.size(); i++) {
                RelationshipsDO relationshipsDO = selected_individual_list.get(i);
                individual_array.put(relationshipsDO.getId());
            }

            Date event_date = AndroidUtils.stringToDateTimeDefault(tv_event_creation_date.getText().toString(), "MM-dd-yyyy");
            event_creation_date = AndroidUtils.getDateToString(event_date, "yyyy-MM-dd");
            Date event_start_date = null;
            Date event_date2 = null;
            String start_time = tv_event_start_time.getText().toString();
            if (start_time.equals("") || start_time == null) {
                start_time = "00:00";
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "HH:mm");

            } else {
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "HH:mm");
            }
            event_starting_date = AndroidUtils.getDateToString(event_start_date, event_creation_date + "'T'HH:mm:ss");
            String end_time = tv_event_end_time.getText().toString();
            if (end_time.equals("") || end_time == null) {
                end_time = "00:00";
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "HH:mm");
            } else {
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "HH:mm");
            }
            String duration_timesheet = "";
            if (event_start_date != null && event_date2 != null) {

                long differenceInMilliSeconds = Math.abs(event_start_date.getTime() - event_date2.getTime());
                long differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;
                long differenceInMinutes
                        = (differenceInMilliSeconds / (60 * 1000)) % 60;
                long differenceInSeconds
                        = (differenceInMilliSeconds / 1000) % 60;
                String duration = differenceInHours + ":" + differenceInMinutes;
                Date hours = AndroidUtils.stringToDateTimeDefault(duration, "HH:mm");
                duration_timesheet = AndroidUtils.getDateToString(hours, "HH:mm");
            }
            event_end_time = AndroidUtils.getDateToString(event_date2, event_creation_date + "'T'HH:mm:ss");

            if (isAddTimesheet) {
                JSONObject time_sheet_obj;
//                        for (int i = 0; i < time_sheets.length(); i++) {
                time_sheet_obj = new JSONObject();
                time_sheet_obj.put("date", event_creation_date);
                if (duration_timesheet.equals("") || duration_timesheet == null) {
                    time_sheet_obj.put("duration", "30:00");
                } else {
                    time_sheet_obj.put("duration", duration_timesheet);
                }
                time_sheet_obj.put("eventtitle", selected_task);
                time_sheet_obj.put("addedby", Constants.NAME);
                time_sheet_obj.put("user_id", Constants.USER_ID);
                time_sheets.put(time_sheet_obj);
            }

            int multiplied_offset = (-1) * (offset);
            String originalDateString = event_creation_date;

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


            Date originalDate = originalFormat.parse(originalDateString);

            desiredFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String newDateString = desiredFormat.format(originalDate);
            postData.put("date", newDateString);
            postData.put("attachments", existing_attachments);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            postData.put("invitees_internal", selected_team_member);
            postData.put("invitees_external", selected_clients_list);
            postData.put("invitees_consumer_external", individual_array);
            if (matter_legal.equals("legal") || matter_legal.equals("general")) {
                postData.put("title", matter_name + " - " + tv_sp_task_name.getText().toString());
            } else if (matter_legal.equals("overhead") || (matter_legal.equals("others"))) {
                postData.put("title", tv_sp_task_name.getText().toString());
            } else {
                postData.put("title", matter_legal);
            }
            if (!matter_legal.equals("reminders")) {
                postData.put("dialin", tv_dialing_number.getText().toString());
                postData.put("location", tv_location.getText().toString());
                postData.put("addtimesheet", isAddTimesheet);
                postData.put("timesheets", time_sheets);
                postData.put("description", tv_description.getText().toString());
            } else {
                postData.put("description", tv_message.getText().toString());
            }

            postData.put("notifications", added_notification_list);
//            postData.put("description", tv_description.getText().toString());
            postData.put("timezone_location", timezone_location);
            postData.put("timezone_offset", multiplied_offset);
            postData.put("repeat_interval", tv_sp_repetetion.getText().toString().toLowerCase(Locale.ROOT));
            postData.put("meeting_link", tv_meeting_link.getText().toString());
            postData.put("allday", isAllDay);
            postData.put("from_ts", event_starting_date);
            postData.put("to_ts", event_end_time);
            if (matter_legal.equals("legal") || matter_legal.equals("general")) {
                postData.put("matter_type", matter_legal);
                postData.put("matter_id", matter_id);
            }
            postData.put("event_type", matter_legal);

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/events", "CREATE_EVENT", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NotificationPopup() {
        View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.add_calendar_notification, null);
        TextView tv_sp_minutes = view_opponents.findViewById(R.id.tv_sp_minutes);
        ListView sp_minutes = view_opponents.findViewById(R.id.sp_minutes);
        TextInputEditText tv_numbers = view_opponents.findViewById(R.id.tv_numbers);
        ArrayList<MinutesDO> minutes_list = new ArrayList<>();
        TextView error_msg = view_opponents.findViewById(R.id.error_msg);
        TextView error_msg1 = view_opponents.findViewById(R.id.error_msg1);
        error_msg.setTextColor(getContext().getResources().getColor(R.color.Red));
        error_msg.setTextSize(15);
        error_msg1.setTextColor(getContext().getResources().getColor(R.color.Red));
        error_msg1.setTextSize(15);
        error_msg1.setVisibility(View.GONE);
//        error_msg.setVisibility(View.GONE);
        minutes_list.add(new MinutesDO("Minutes"));
        minutes_list.add(new MinutesDO("Hours"));
        minutes_list.add(new MinutesDO("Days"));
        minutes_list.add(new MinutesDO("Weeks"));
        sp_minutes.setVisibility(View.GONE);
        tv_sp_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_notify)
                    sp_minutes.setVisibility(View.VISIBLE);
                else sp_minutes.setVisibility(View.GONE);
                is_notify = !is_notify;
            }
        });
        ImageView iv_delete_notification = view_opponents.findViewById(R.id.iv_delete_notification);
        tv_numbers.setText("10");
        if (Constants.is_meeting == "Edit") {
            tv_numbers.setText(time_format);
            tv_sp_minutes.setText(time_value);
        }
        final int position = ll_add_notification.getChildCount();
// Set the position as a tag to the view
        view_opponents.setTag(position);

        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), minutes_list);
        sp_minutes.setAdapter(spinner_adapter);
        sp_minutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                selected_hour_type = minutes_list.get(i).getName();
//                selectedValues.clear();
                loadnewInput(tv_numbers, view);

                try {
                    if (position >= 0 && position < ll_add_notification.getChildCount()) {
                        View selectedView = ll_add_notification.getChildAt(position);
                        if (selectedView != null) {
                            Spinner selectedSpinnerTextView = selectedView.findViewById(R.id.sp_minutes);
                            selectedSpinnerTextView.setSelection(i);
                        }
                    }

                    if (position >= 0 && position < selectedValues.size()) {
                        selectedValues.set(position, tv_numbers.getText().toString() + "- " + selected_hour_type.toLowerCase(Locale.ROOT));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is_notify = true;
                sp_minutes.setVisibility(View.GONE);
                tv_sp_minutes.setText(selected_hour_type);
            }
        });
//        selected_notify_value = tv_sp_minutes.getText().toString();
        if (tv_sp_minutes.getText().toString().equals("")) {
            error_msg1.setVisibility(View.VISIBLE);
            error_msg1.setText("This field is required");
            is_timenotify = false;
        }
        tv_sp_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    error_msg1.setVisibility(View.GONE);
                    is_timenotify = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tv_numbers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (position >= 0 && position < selectedValues.size()) {
                    selectedValues.set(position, editable.toString() + "- " + tv_sp_minutes.getText().toString().toLowerCase(Locale.ROOT));
                }
                if (!(tv_numbers.getText().toString().equals(""))) {
                    is_timenotify = false;
                    int notify_number = Integer.parseInt(tv_numbers.getText().toString());
                    if ((selected_hour_type.equals("Minutes")) && ((notify_number) > 60)) {
                        error_msg.setVisibility(View.VISIBLE);
                        error_msg.setText("Must Between 1 to 60 Minutes");
                    } else if ((selected_hour_type.equals("Hours")) && ((notify_number) > 24)) {
                        error_msg.setVisibility(View.VISIBLE);
                        error_msg.setText("Must Between 1 to 24 Hours");
                    } else if ((selected_hour_type.equals("Days")) && ((notify_number) > 31)) {
                        error_msg.setVisibility(View.VISIBLE);
                        error_msg.setText("Must Between 1 to 31 Days");
                    } else if ((selected_hour_type.equals("Weeks")) && ((notify_number) > 4)) {
                        error_msg.setVisibility(View.VISIBLE);
                        error_msg.setText("Must Between 1 to 4 Weeks");
                    } else {
                        error_msg.setVisibility(View.GONE);
                        is_timenotify = true;
                    }
                } else {
                    is_timenotify = false;
                    error_msg.setVisibility(View.VISIBLE);
                    error_msg.setText("This Field is Required");
                }
            }
        });
        iv_delete_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the position from the clicked view's tag
                int position = (int) view_opponents.getTag();

                // Remove the view from the LinearLayout
                ll_add_notification.removeView(view_opponents);

                // Remove the value from the ArrayList at the specified position
                if (position >= 0 && position < selectedValues.size()) {
                    selectedValues.remove(position);
                }
            }
        });
        ll_add_notification.addView(view_opponents);
        if (!is_notify_clicked) {
            selectedValues.add(tv_numbers.getText().toString() + "- " + selected_hour_type.toLowerCase(Locale.ROOT));
        }
    }

    private void loadnewInput(TextInputEditText tv_numbers, View view) {
        View parentView = (View) tv_numbers.getParent();
        TextView inputFieldValueTextView = parentView.findViewById(R.id.tv_numbers);
        inputFieldValueTextView.setText(tv_numbers.getText().toString());
        int position = findPositionInParent(view, ll_add_notification);
        if (position >= 0 && position < selectedValues.size()) {
            // Update the input field value in the ArrayList
            selectedValues.set(position, tv_numbers.getText().toString() + "- " + selected_hour_type.toLowerCase(Locale.ROOT));
        }
    }

    private int findPositionInParent(View view, ViewGroup parent) {
        int position = parent.indexOfChild(view);
        if (position >= 0) {
            return position;
        } else {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    int childPosition = findPositionInParent(view, (ViewGroup) child);
                    if (childPosition >= 0) {
                        return childPosition; // Return the childPosition if it's >= 0
                    }
                }
            }
        }
        return -1; // Return -1 if the view is not found in the parent or its children
    }

    private void loadUpdatedInput(TextInputEditText tv_numbers) {
        tv_numbers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadTextchangedData(s, tv_numbers, selected_hour_type.toLowerCase(Locale.ROOT));
            }
        });
    }

    private void loadTextchangedData(Editable s, TextInputEditText tv_numbers, String selected_hour_type) {
//        if (!s.toString().isEmpty()) {
        int number = Integer.parseInt(tv_numbers.getText().toString());
//            if (number > 4) {
//                tv_numbers.setText("4");
//                tv_numbers.setSelection(tv_numbers.getText().length());
//            } else if (number <= 0) {
//                tv_numbers.setText("1");
//                tv_numbers.setSelection(tv_numbers.getText().length());
//
//            }
        String value = number + "-" + selected_hour_type.toLowerCase(Locale.ROOT);
//            selectedValues.set(position, value);
//                                notification.put(selectedValues);
        StringBuilder sb = new StringBuilder();
        for (String notification : selectedValues) {
            sb.append(notification).append("\n");
        }
        AndroidUtils.showToast(sb.toString(), getContext());
//        }
    }

    private void load_individual_Popup() {
        try {
            if (individual_list.size() == 0) {
                is_clicked_individuals = true;
                rv_individuals_view.setVisibility(View.GONE);
                AndroidUtils.showToast("No Individuals to show", getContext());
            } else {
                rv_individuals_view.setVisibility(View.VISIBLE);
                for (int i = 0; i < individual_list.size(); i++) {
                    for (int j = 0; j < selected_individual_list.size(); j++) {
                        if (individual_list.get(i).getId().matches(selected_individual_list.get(j).getId())) {
                            RelationshipsDO teamModel = individual_list.get(i);
                            teamModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);
                        }
                    }
                }
                selected_individual_list.clear();
//            selected_tm_list.clear();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_individuals_view.setLayoutManager(layoutManager);
                rv_individuals_view.setHasFixedSize(true);
                ADAPTER_TAG = "INDIVIDUAL";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_individuals_view.setAdapter(documentsAdapter);
                btn_individual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getIndividual_List().size(); i++) {
                            RelationshipsDO teamModel = documentsAdapter.getIndividual_List().get(i);
                            if (teamModel.isChecked()) {
                                if (!selected_individual_list.contains(teamModel)) {
                                    selected_individual_list.add(teamModel);
//                        AndroidUtils.showAlert(selected_individual_list.toString(),getContext());
//                        new_selected_individual_list.add(teamModel);
                                    //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                                }
                            }
                        }
                        rv_individuals_view.setVisibility(View.GONE);
                        is_clicked_individuals = true;
                        loadSelectedIndividual();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }

    }

    private void loadSelectedIndividual() {

        String[] value = new String[selected_individual_list.size()];
        for (int i = 0; i < selected_individual_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_individual_list.get(i).getName();

        }

        String str = String.join(",", value);
        at_individual.setText(str);
        selected_individual.setVisibility(View.VISIBLE);
        ll_individual_list.removeAllViews();
        for (int i = 0; i < selected_individual_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_individual_list.get(i).getName());
            ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
            ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
            iv_remove_opponent.setTag(i);
            iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //..
                        int position = (int) v.getTag();
                        // Remove the view at the specified position
                        ll_individual_list.removeViewAt(position);
                        // Remove the corresponding item from the list
                        RelationshipsDO clientsModel = selected_individual_list.remove(position);
                        clientsModel.setChecked(false);
                        // Update the tags of the remaining views
                        for (int j = 0; j < ll_individual_list.getChildCount(); j++) {
                            ImageView iv_remove = ll_individual_list.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                            if (iv_remove != null) {
                                iv_remove.setTag(j);
                            }
                        }

                        String str = String.join(",", value);
                        at_individual.setText(str);
                        // Update at_add_groups text
                        StringBuilder stringBuilder = new StringBuilder();
                        for (RelationshipsDO model : selected_individual_list) {
                            stringBuilder.append(model.getName()).append(",");
                        }
                        if (stringBuilder.length() > 0) {
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }
                        String strn = stringBuilder.toString();
                        at_individual.setText(strn);
                        if (selected_individual_list.size() == 0) {
                            selected_individual.setVisibility(View.GONE);
                        } else {
                            selected_individual.setVisibility(View.VISIBLE);
                        }
                        //..
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_individual_list.addView(view_opponents);
        }
    }


    private void callClientsWebservice() {
        try {
            entities_list.clear();
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v2/relationship/client/list", "Client List", postdata.toString());
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void TeamMembersPopup() {
        try {
//            teamList.clear();
            if (teamList.size() == 0) {
                for (int i = 0; i < matterList.size(); i++) {
                    if (matter_id.equals(matterList.get(i).getId())) {
                        JSONArray team_members = matterList.get(i).getMembers();
                        for (int j = 0; j < team_members.length(); j++) {
                            TeamDo teamModel = new TeamDo();
                            JSONObject jsonObject = team_members.getJSONObject(j);
                            teamModel.setId(jsonObject.getString("id"));
                            teamModel.setName(jsonObject.getString("name"));
                            teamList.add(teamModel);
                        }
                    }
                }
            }
            for (int i = 0; i < teamList.size(); i++) {
                for (int j = 0; j < selected_tm_list.size(); j++) {
                    if (teamList.get(i).getId().matches(selected_tm_list.get(j).getId())) {
                        TeamDo teamModel = teamList.get(i);
                        teamModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);
                    }
                }
            }
            if (teamList.size() == 0) {
                rv_groups_view.setVisibility(View.GONE);
                AndroidUtils.showToast("No Team Members to View", getContext());
                is_clicked_team = true;
            } else {
//                selected_tm_list.clear();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_groups_view.setLayoutManager(layoutManager);
                rv_groups_view.setHasFixedSize(true);
                ADAPTER_TAG = "TM";

                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_groups_view.setAdapter(documentsAdapter);
                btn_add_groups.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = 0;
                        for (int i = 0; i < documentsAdapter.getTmList().size(); i++) {
                            TeamDo teamModel = documentsAdapter.getTmList().get(i);
                            if (teamModel.isChecked()) {
                                if (!selected_tm_list.contains(teamModel)) {
                                    //selected_tm_list.add(teamModel);
                                    selected_tm_list.add(index, teamModel);
                                    index++;
//                        selected_tm_list.add(teamModel);
                                    //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                                }
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }
                        for (int i = 0; i < selected_tm_list.size(); i++) {
                            Log.d("List_Item", selected_tm_list.get(i).getName().toString());
                        }
                        if (selected_tm_list.size() == 0) {
                            selected_groups.setVisibility(View.GONE);
                            at_add_groups.setText("");
                        } else {
                            loadSelectedTM();
                        }
                        rv_groups_view.setVisibility(View.GONE);
                        is_clicked_team = true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }

    }


    private void loadSelectedClients() {

        String[] value = new String[selected_entity_client_list.size()];
        for (int i = 0; i < selected_entity_client_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_entity_client_list.get(i).getName();
        }
        String str = String.join(",", value);
        at_assigned_client.setText(str);
        selected_tm.setVisibility(View.VISIBLE);
        ll_client_team_members.removeAllViews();
        for (int i = 0; i < selected_entity_client_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_entity_client_list.get(i).getName());
            ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
            ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
            iv_remove_opponent.setTag(i);
            iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        int position = (int) v.getTag();
//                        // Remove the view at the specified position
//                        ll_client_team_members.removeViewAt(position);
//                        // Remove the corresponding item from the list
//                        Relation clientsModel = selected_clients_list.remove(position);
//                        clientsModel.setChecked(false);

                        int position = (Integer) v.getTag();
                        ll_client_team_members.removeViewAt(position);
//                            ll_selected_groups.addView(view_opponents,position);
                        RelationshipsDO teamModel = selected_entity_client_list.remove(position);
                        teamModel.setChecked(false);
//                            selected_entity_client_list.remove(position);
                        //..
                        for (int j = 0; j < ll_client_team_members.getChildCount(); j++) {
                            ImageView iv_remove = ll_client_team_members.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                            if (iv_remove != null) {
                                iv_remove.setTag(j);
                            }
                        }

                        String str = String.join(",", value);
                        at_assigned_client.setText(str);
                        // Update at_add_groups text
                        StringBuilder stringBuilder = new StringBuilder();
                        for (RelationshipsDO model : selected_entity_client_list) {
                            stringBuilder.append(model.getName()).append(",");
                        }

                        if (stringBuilder.length() > 0) {
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }
                        String strn = stringBuilder.toString();
                        at_assigned_client.setText(strn);
                        if (selected_entity_client_list.size() > 0) {
                            selected_tm.setVisibility(View.VISIBLE);
                        } else {
                            selected_tm.setVisibility(View.GONE);
                        }
                        //..
//                            selected_groups_list.set(position, groupsModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_client_team_members.addView(view_opponents);
        }
    }

    private void loadSelectedTM() {

        String[] value = new String[selected_tm_list.size()];
        for (int i = 0; i < selected_tm_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_tm_list.get(i).getName();

        }

        String str = String.join(",", value);
        at_add_groups.setText(str);
        selected_groups.setVisibility(View.VISIBLE);
        ll_selected_team_members.removeAllViews();
        for (int i = 0; i < selected_tm_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_tm_list.get(i).getName());
            ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
            ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
            iv_remove_opponent.setTag(i);
            iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //..
                        int position = (Integer) v.getTag();
//                            ll_selected_groups.addView(view_opponents,position);
                        ll_selected_team_members.removeViewAt(position);
//                            ll_selected_groups.addView(view_opponents,position);
                        TeamDo teamModel = selected_tm_list.remove(position);
                        teamModel.setChecked(false);
//                            selected_entity_client_list.remove(position);
                        //..
                        for (int j = 0; j < ll_selected_team_members.getChildCount(); j++) {
                            ImageView iv_remove = ll_selected_team_members.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                            if (iv_remove != null) {
                                iv_remove.setTag(j);
                            }
                        }

                        String str = String.join(",", value);
                        at_add_groups.setText(str);
                        // Update at_add_groups text
                        StringBuilder stringBuilder = new StringBuilder();
                        for (TeamDo model : selected_tm_list) {
                            stringBuilder.append(model.getName()).append(",");
                        }

                        if (stringBuilder.length() > 0) {
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }
                        String strn = stringBuilder.toString();
                        at_add_groups.setText(strn);
                        //..
                        if (selected_tm_list.size() > 0) {
                            selected_groups.setVisibility(View.VISIBLE);
                        } else {
                            selected_groups.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_selected_team_members.addView(view_opponents);
        }
    }

    private void loadProjectData_edit(String selected_project) {
        switch (selected_project) {
            case "legal":
                callProjectWebservice(matter_legal);
                loadRepetetions();
                try {
                    loadEntitiesSpinnerData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                legalTaksList.add(new TaskDo("Case Filling"));
                legalTaksList.add(new TaskDo("Consultation"));
                legalTaksList.add(new TaskDo("Creating Legal Breifs"));
                legalTaksList.add(new TaskDo("Meeting with client"));
                legalTaksList.add(new TaskDo("Hearing"));
                loadTaskList(legalTaksList);
                break;
            case "general":
                legalTaksList.clear();
                loadRepetetions();
                callProjectWebservice(matter_legal);
                try {
                    loadEntitiesSpinnerData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                TaskDo consultation_general = new TaskDo("Consultation");
//                TaskDo draft_agreements = new TaskDo("Draft agreements");
//                TaskDo fwa = new TaskDo("Filling with authorities");
//                TaskDo mwc_general = new TaskDo("Meeting with client");
//                TaskDo paf = new TaskDo("Prepare annual fillings");
                legalTaksList.add(new TaskDo("Consultation"));
                legalTaksList.add(new TaskDo("Draft agreements"));
                legalTaksList.add(new TaskDo("Filling with authorities"));
                legalTaksList.add(new TaskDo("Meeting with client"));
                legalTaksList.add(new TaskDo("Prepare annual fillings"));
                loadTaskList(legalTaksList);
                break;
            case "overhead":
                legalTaksList.clear();
                loadRepetetions();
                callClientsWebservice();
//                matter_legal = ""
//                TaskDo conference = new TaskDo("Conference");
//                TaskDo holidays = new TaskDo("Holidays");
//                TaskDo research = new TaskDo("Research");
//                TaskDo training = new TaskDo("Training");
//                TaskDo vacation = new TaskDo("Vacation");
                legalTaksList.add(new TaskDo("Conference"));
                legalTaksList.add(new TaskDo("Holidays"));
                legalTaksList.add(new TaskDo("Research"));
                legalTaksList.add(new TaskDo("Training"));
                legalTaksList.add(new TaskDo("Vacation"));
                loadTaskList(legalTaksList);
                break;
            case "others":
                callClientsWebservice();
                loadRepetetions();
                legalTaksList.clear();
                legalTaksList.add(new TaskDo("Business Development"));
                legalTaksList.add(new TaskDo("Personal"));
                legalTaksList.add(new TaskDo("Doctor Appointment"));
                legalTaksList.add(new TaskDo("Lunch/Dinner"));
                legalTaksList.add(new TaskDo("Misc"));
                loadTaskList(legalTaksList);
                break;
            case "reminders":
                legalTaksList.clear();
                hideAlldetails();
                callClientsWebservice();
                loadRepetetions();
                break;
        }
    }

    private void loadProjectData(String selected_project) {
        switch (selected_project) {
            case "Legal Matter":

                loadClearedLists();

                unHideMatterDetails();
                loadRepetetions();
                matter_legal = "legal";
                callProjectWebservice(matter_legal);
                hide_documents();
//                callClientsWebservice();
//                TaskDo caseFilling = new TaskDo("Case Filling");
//                TaskDo consultation = new TaskDo("Consultation");
//                TaskDo clb = new TaskDo("Creating Legal Breifs");
//                TaskDo mwc = new TaskDo("Meeting with client");
//                TaskDo hearing = new TaskDo("Hearing");
                legalTaksList.add(new TaskDo("Case Filling"));
                legalTaksList.add(new TaskDo("Consultation"));
                legalTaksList.add(new TaskDo("Creating Legal Briefs"));
                legalTaksList.add(new TaskDo("Meeting with client"));
                legalTaksList.add(new TaskDo("Hearing"));
                loadTaskList(legalTaksList);
                break;
            case "General Matter":
                legalTaksList.clear();
                unHideMatterDetails();
                loadClearedLists();
                loadRepetetions();
                matter_legal = "general";
                callProjectWebservice(matter_legal);
                hide_documents();
//                TaskDo consultation_general = new TaskDo("Consultation");
//                TaskDo draft_agreements = new TaskDo("Draft agreements");
//                TaskDo fwa = new TaskDo("Filling with authorities");
//                TaskDo mwc_general = new TaskDo("Meeting with client");
//                TaskDo paf = new TaskDo("Prepare annual fillings");
                legalTaksList.add(new TaskDo("Consultation"));
                legalTaksList.add(new TaskDo("Draft agreements"));
                legalTaksList.add(new TaskDo("Filling with authorities"));
                legalTaksList.add(new TaskDo("Meeting with client"));
                legalTaksList.add(new TaskDo("Prepare annual fillings"));
                loadTaskList(legalTaksList);
                break;
            case "Overhead":
                legalTaksList.clear();
                hideMatterDetails();
                loadClearedLists();
                loadRepetetions();
                callTeamMemberWebservice();
                display_members();
                matter_legal = "overhead";
                hide_documents();
//                matter_legal = ""
//                TaskDo conference = new TaskDo("Conference");
//                TaskDo holidays = new TaskDo("Holidays");
//                TaskDo research = new TaskDo("Research");
//                TaskDo training = new TaskDo("Training");
//                TaskDo vacation = new TaskDo("Vacation");
                legalTaksList.add(new TaskDo("Conference"));
                legalTaksList.add(new TaskDo("Holidays"));
                legalTaksList.add(new TaskDo("Research"));
                legalTaksList.add(new TaskDo("Training"));
                legalTaksList.add(new TaskDo("Vacation"));
                loadTaskList(legalTaksList);
                break;
            case "Others":
                legalTaksList.clear();
                hideMatterDetails();
                loadClearedLists();
                loadRepetetions();
                callTeamMemberWebservice();
                display_members();
                ll_documents_view.setVisibility(View.GONE);
//                display_check_list();
                matter_legal = "others";
                hide_documents();
                TaskDo business_development = new TaskDo("Business Development");
                TaskDo personal = new TaskDo("Personal");
                TaskDo doctor_appointment = new TaskDo("Doctor Appointment");
                TaskDo lunch_dinner = new TaskDo("Lunch/Dinner");
                TaskDo misc = new TaskDo("Misc");
                legalTaksList.add(new TaskDo("Business Development"));
                legalTaksList.add(new TaskDo("Personal"));
                legalTaksList.add(new TaskDo("Doctor Appointment"));
                legalTaksList.add(new TaskDo("Lunch/Dinner"));
                legalTaksList.add(new TaskDo("Misc"));
                loadTaskList(legalTaksList);
                break;
            case "Reminders":
                cb_add_to_timesheet.setVisibility(View.GONE);
                legalTaksList.clear();
                hideAlldetails();
                loadRepetetions();
                loadClearedLists();
                callTeamMemberWebservice();
                display_members();
//                display_check_list();
                matter_legal = "reminders";
                break;
        }
    }

    private void loadClearedLists() {
        legalTaksList.clear();
        matterList.clear();
        entity_client_list.clear();
        individual_list.clear();
        documents_list.clear();
        entities_list.clear();
        selectedValues.clear();
        teamList.clear();
    }

    private void load_clear_list() {
        at_add_groups.setText("");
        at_assigned_client.setText("");
        at_individual.setText("");
        at_attach_document.setText("");
        tv_sp_entities.setText("");

        entity_client_list.clear();
        individual_list.clear();
        documents_list.clear();
        entities_list.clear();
        selectedValues.clear();
        teamList.clear();
    }

    private void clear_selected_list() {
        selected_individual_list.clear();
        selected_tm_list.clear();
        selected_entity_client_list.clear();
        selected_documents_list.clear();
    }

    private void display_members() {
        cv_add_clients.setVisibility(View.VISIBLE);
        ll_add_groups.setVisibility(View.VISIBLE);
        ll_add_entities.setVisibility(View.VISIBLE);
        ll_individual.setVisibility(View.VISIBLE);
    }

    private void loadRepetetions() {
        Repetetions.clear();
        Repetetions.add("Daily");
        Repetetions.add("Weekly");
        Repetetions.add("Bi- Weekly");
        Repetetions.add("Monthly");
        Repetetions.add("Yearly");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Repetetions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_repetetion.setAdapter(adapter1);
        sp_repetetion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                repeat_interval = Repetetions.get(position);

                tv_sp_repetetion.setText(repeat_interval);
                sp_repetetion.setVisibility(View.GONE);
                ischecked_repetetion = true;
            }
        });
    }

    private void hideAlldetails() {
        ll_matter_name.setVisibility(View.GONE);
        ll_message.setVisibility(View.VISIBLE);
        cv_meeting_details.setVisibility(View.GONE);
        ll_task.setVisibility(View.GONE);
        ll_documents_view.setVisibility(View.GONE);
    }

    private void hideMatterDetails() {
        cv_meeting_details.setVisibility(View.VISIBLE);
        ll_matter_name.setVisibility(View.GONE);
        ll_message.setVisibility(View.GONE);
        cb_add_to_timesheet.setVisibility(View.VISIBLE);
        ll_task.setVisibility(View.VISIBLE);
        ll_documents_view.setVisibility(View.GONE);
    }

    private void unHideMatterDetails() {
        cv_meeting_details.setVisibility(View.VISIBLE);
        ll_matter_name.setVisibility(View.VISIBLE);
        ll_message.setVisibility(View.GONE);
        ll_task.setVisibility(View.VISIBLE);
        ll_documents_view.setVisibility(View.VISIBLE);
        cv_add_clients.setVisibility(View.GONE);
    }

    private void loadTaskList(ArrayList<TaskDo> legalTaksList) {
        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), legalTaksList);

        sp_task.setAdapter(spinner_adapter);
        sp_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_task = legalTaksList.get(position).getTaskName();
                tv_sp_task_name.setText(selected_task);
                sp_task.setVisibility(View.GONE);
                ischecked_task = true;
            }
        });
    }

    private void callProjectWebservice(String selected_project) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/matter/" + selected_project, "Matter List", postdata.toString());
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callTeamMemberWebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/event/tms", "Team List", postdata.toString());
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog.isShowing() && progressDialog != null) {
            AndroidUtils.dismiss_dialog(progressDialog);
        }
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
//                boolean error = result.getBoolean("error");
                if (httpResult.getRequestType().equals("Matter List")) {
                    if (!timeZonesTaskCompleted) {

                        return;
                    }
                    JSONArray matters = result.getJSONArray("matters");
                    try {
                        matterTaskCompleted = true;
                        loadMattersList(matters);

                    } catch (Exception e) {
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                        e.printStackTrace();
                    }
                } else if (httpResult.getRequestType().equals("TIMEZONES")) {
                    if (!result.getBoolean("error")) {

                        JSONArray jsonArray = result.getJSONArray("timezones");
                        load_timezones(jsonArray);
                        timeZonesTaskCompleted = true;
//                        Thre
                        if (Constants.is_meeting == "Edit") {
                            for (int i = 0; i < existing_events_list.size(); i++) {
                                Event_Details_DO event_details_do = existing_events_list.get(i);
                                matter_legal = event_details_do.getEvent_type();
                                AndroidUtils.showToast(matter_legal, getContext());
                                existing_description = event_details_do.getDescription();
                                existing_date = event_details_do.getDate();
                                existing_dialin = event_details_do.getDialin();
                                existing_location = event_details_do.getLocation();
                                existing_end_time = event_details_do.getConverted_End_time();
                                existing_start_time = event_details_do.getConverted_Start_time();
                                existing_meeting_link = event_details_do.getMeeting_link();
                                existing_repetetion = event_details_do.getRepeat_interval();
                                event_id = event_details_do.getId();
                                matter_id = event_details_do.getMatter_id();
                                matter_name = event_details_do.getMatter_name();
//                                AndroidUtils.showToast(event_id + "..+.." + event_name, getContext());
                                String title = event_details_do.getTitle();
                                if (title.contains("-")) {
                                    String[] splitStrings = title.split(" - ");
                                    String firstString = splitStrings[0];
                                    String secondString = splitStrings[1];
                                    existing_task = secondString;
                                } else {
                                    existing_task = title;
                                }
                                existing_time_zone = event_details_do.getOffset_location();
                                isAddTimesheet = event_details_do.isRecurring();
                                isExistingAllday = event_details_do.isAll_day();
                            }
                            check_event();
                            loadProjectData(Event_name);
                            loadExistingData();
                        }
                        //..
                    } else {
                        AndroidUtils.showAlert("Something went wrong", getContext());
                    }
                } else if (httpResult.getRequestType().equals("Team List")) {
//                    if (!matterTaskCompleted) {
//                        return;
//                    }
                    if (!result.getBoolean("error")) {
                        JSONArray jsonArray = result.getJSONArray("users");
                        loadTeamList(jsonArray);
                        tmTaskCompleted = true;
//                        Thre
                        callClientsWebservice();
                    } else {
                        AndroidUtils.showAlert("Something went wrong", getContext());
                    }
                } else if (httpResult.getRequestType().equals("Client List")) {
                    if (Constants.ROLE.equals("AAM")) {
                        if (!timeZonesTaskCompleted) {

                            return;
                        }
                        if (!result.getBoolean("error")) {

                            JSONObject jsonObject = result.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("relationships");
                            loadRelationshipsList(jsonArray);
                        } else {
                            AndroidUtils.showAlert("Something went wrong", getContext());
                        }
                    } else {
                        if (!result.getBoolean("error")) {

                            JSONObject jsonObject = result.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("relationships");
                            loadRelationshipsList(jsonArray);
                        } else {
                            AndroidUtils.showAlert("Something went wrong", getContext());
                        }
                    }
//                    if (!matterTaskCompleted) {
////                        AndroidUtils.showToast("Please add team members",getContext());
//                        return;
//                    }
//                    callTeamMemberWebservice();
                } else if (httpResult.getRequestType().equals("Entity Client List")) {
                    if (!result.getBoolean("error")) {
                        JSONArray jsonArray = result.getJSONArray("users");
                        loadEntity_Clients(jsonArray);
                    } else {
                        AndroidUtils.showAlert("Something went wrong", getContext());
                    }
                } else if (httpResult.getRequestType().equals("EDIT_EVENT")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    loadClearedLists();
                    progressDialog.dismiss();
                    meetings.loadView();
                } else if (httpResult.getRequestType().equals("CREATE_EVENT")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    loadClearedLists();
                    meetings.loadView();//Navigating to View Meetings page after meeting/event is created...
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AndroidUtils.showAlert("Something went wrong", getContext());
        }
    }

    private void loadCreatEvent() {
        try {
//            Fragment fragment = new com.digicoffer.lauditor.Calendar.Calendar();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.id_framelayout, fragment);
//            ft.addToBackStack("current_fragment").commit();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Meetings nonSubmittedTimesheets = new Meetings();
            ft.replace(R.id.id_framelayout, nonSubmittedTimesheets);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEntity_Clients(JSONArray jsonArray) throws JSONException {
        entity_client_list.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            RelationshipsDO relationshipsDO = new RelationshipsDO();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            relationshipsDO.setId(entity_id + "_" + jsonObject.getString("id"));
            relationshipsDO.setName(jsonObject.getString("name"));
            entity_client_list.add(relationshipsDO);
        }
        if (entity_client_list.size() == 0) {
            ll_assign_clients.setVisibility(View.GONE);
        } else {
            ll_assign_clients.setVisibility(View.VISIBLE);
        }
//        loadSelectedClients();
    }

    private void loadEntityClientPopup() {
        try {
            if (entity_client_list.size() == 0) {
                rv_clients_view.setVisibility(View.GONE);
                is_clicked_clients = true;
                AndroidUtils.showToast("No Clients to show", getContext());
            } else {
                for (int i = 0; i < entity_client_list.size(); i++) {
                    for (int j = 0; j < selected_entity_client_list.size(); j++) {
                        if (entity_client_list.get(i).getId().matches(selected_entity_client_list.get(j).getId())) {
                            RelationshipsDO teamModel = entity_client_list.get(i);
                            teamModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);
                        }
                    }
                }
                selected_entity_client_list.clear();
//            selected_tm_list.clear();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_clients_view.setLayoutManager(layoutManager);
                rv_clients_view.setHasFixedSize(true);
                ADAPTER_TAG = "ENTITY";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_clients_view.setAdapter(documentsAdapter);
                btn_assigned_clients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getEntity_client_list().size(); i++) {
                            RelationshipsDO teamModel = documentsAdapter.getEntity_client_list().get(i);
                            if (teamModel.isChecked()) {
                                if (!selected_entity_client_list.contains(teamModel)) {
                                    selected_entity_client_list.add(teamModel);
//                        AndroidUtils.showAlert(selected_individual_list.toString(),getContext());
//                        new_selected_client_list.add(teamModel);
                                    //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                                }
                            }
                        }
                        loadSelectedClients();
                        rv_clients_view.setVisibility(View.GONE);
                        is_clicked_clients = true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }

    }

    private void loadRelationshipsList(JSONArray jsonArray) throws JSONException {
        individual_list.clear();
        entities_list.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            RelationshipsDO relationshipsDO = new RelationshipsDO();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String type =   ;

            if (jsonObject.getString("type").equals("consumer")) {
                relationshipsDO.setId(jsonObject.getString("id"));
                relationshipsDO.setName(jsonObject.getString("name"));
                relationshipsDO.setType(jsonObject.getString("type"));
                individual_list.add(relationshipsDO);
            } else {
                relationshipsDO.setId(jsonObject.getString("id"));
                relationshipsDO.setName(jsonObject.getString("name"));
                relationshipsDO.setType(jsonObject.getString("type"));
                entities_list.add(relationshipsDO);
            }
        }
        loadEntitiesSpinnerData();
    }

    private void loadEntitiesSpinnerData() throws JSONException {
        if (entities_list.size() == 0) {
            for (int i = 0; i < matterList.size(); i++) {
                if (matter_id.equals(matterList.get(i).getId())) {
                    JSONArray entities = matterList.get(i).getClients();
                    for (int j = 0; j < entities.length(); j++) {
                        RelationshipsDO relationshipsDO = new RelationshipsDO();
                        JSONObject jsonObject = entities.getJSONObject(j);
//                String type =   ;

                        if (jsonObject.getString("type").equals("consumer")) {
                            relationshipsDO.setId(jsonObject.getString("id"));
                            relationshipsDO.setName(jsonObject.getString("name"));
                            relationshipsDO.setType(jsonObject.getString("type"));
                            individual_list.add(relationshipsDO);
                        } else {
                            relationshipsDO.setId(jsonObject.getString("id"));
                            relationshipsDO.setName(jsonObject.getString("name"));
                            relationshipsDO.setType(jsonObject.getString("type"));
                            entities_list.add(relationshipsDO);
                        }
                    }
                }
            }
        }
//        display_check_list();
//        if(!(Constants.is_meeting =="Edit")) {
//        }

        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), entities_list);
        sp_entities.setAdapter(adapter);
//        callTimeZoneWebservice();
        sp_entities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                entity_id = entities_list.get(position).getId();
                String entity_name = entities_list.get(position).getName();
                callEntityClientWebservice(entity_id);

                tv_sp_entities.setText(entity_name);
                sp_entities.setVisibility(View.GONE);
                is_entities = true;
            }
        });
    }

    private void callEntityClientWebservice(String id) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "related/entities/tms/" + id, "Entity Client List", postdata.toString());
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void loadTeamList(JSONArray jsonArray) throws JSONException {
        teamList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            TeamDo teamDo = new TeamDo();
            teamDo.setId(jsonObject.getString("id"));
            teamDo.setName(jsonObject.getString("name"));
            teamList.add(teamDo);
        }
        TeamMembersPopup();
    }

    private void loadTeamSpinner() {
//        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), teamList);
//        sp_add_team_member.setAdapter(adapter);
////        callTimeZoneWebservice();
//        sp_add_team_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                team_member_id = teamList.get(sp_add_team_member.getSelectedItemPosition()).getId();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        final AutocompleteAdapter matterAutocompleteAdapter = new AutocompleteAdapter(getContext(), 1, teamList);
//        callLegalwebservice();
        at_family_members.setInputType(InputType.TYPE_NULL);
        at_family_members.setThreshold(0);
        at_family_members.setAdapter(matterAutocompleteAdapter);
        at_family_members.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        at_family_members.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamDo selectedItem = matterAutocompleteAdapter.getItem(position);
                matterAutocompleteAdapter.remove(selectedItem);
                matterAutocompleteAdapter.notifyDataSetChanged();
            }
        });
    }


    private void load_timezones(JSONArray jsonArray) throws JSONException {
        TimeZonesDO timeZonesDO;
        timeZonesList.clear();
        timesPosHash.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            timeZonesDO = new TimeZonesDO();
            timeZonesDO.setNAME(String.valueOf(jsonArray.getJSONArray(i).get(1)));
            timeZonesDO.setGMT(String.valueOf(jsonArray.getJSONArray(i).get(0)));
            timeZonesList.add(timeZonesDO);
            timesPosHash.put(String.valueOf(jsonArray.getJSONArray(i).get(0)), i);
        }
        loadTimeZonespinner();
//        callLegalwebservice();
    }

    private void loadTimeZonespinner() {
        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), timeZonesList);
        sp_time_zone.setAdapter(adapter);
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        int offset1 = timeZone.getRawOffset();
        final long hours = TimeUnit.MILLISECONDS.toMinutes(offset1);
//        callTimeZoneWebservice();
        sp_time_zone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<TimeZonesDO> timezones = new ArrayList<>();
                for (int j = 0; j < timeZonesList.size(); j++) {
                    String timezone_offset = timeZonesList.get(j).getGMT();

                    if (timezone_offset.matches(String.valueOf((hours)))) {
                        sp_time_zone.setSelection(j);
                        offset = Integer.parseInt(timeZonesList.get(position).getGMT());
                        timezone_location = (timeZonesList.get(position).getNAME());
                        tv_sp_time_zone.setText(timezone_location);
                        sp_time_zone.setVisibility(View.GONE);
                        ischecked_time = true;
                    }
                }
            }
        });
//        if (Constants.ROLE.equals("AAM")) {
//            callClientsWebservice();
//        }
    }

    private void loadMattersList(JSONArray matters) throws JSONException {
        matterList.clear();
//        AndroidUtils.showAlert(matters.toString(),getContext());
        for (int i = 0; i < matters.length(); i++) {
            JSONObject jsonObject = matters.getJSONObject(i);
            ViewMatterModel viewMatterModel = new ViewMatterModel();
            viewMatterModel.setId(jsonObject.getString("id"));
            viewMatterModel.setTitle(jsonObject.getString("title"));
            viewMatterModel.setDocuments(jsonObject.getJSONArray("documents"));
            viewMatterModel.setClients(jsonObject.getJSONArray("clients"));
            viewMatterModel.setMembers(jsonObject.getJSONArray("members"));
//            viewMatterModel.setCasetype(jsonObject.getString("caseType"));
            matterList.add(viewMatterModel);
        }
        if (matterList.size() == 0) {
            ll_matter_name.setVisibility(View.GONE);
        } else {
            ll_matter_name.setVisibility(View.VISIBLE);
        }
//        AndroidUtils.showAlert(matters.toString(),getContext());
        loadMatterSpinnerList(matterList);
    }

    private void loadMatterSpinnerList(ArrayList<ViewMatterModel> matterList) {

        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), matterList);

        sp_matter_name.setAdapter(spinner_adapter);
        sp_matter_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                matter_id = matterList.get(position).getId();
                matter_name = matterList.get(position).getTitle();
                tv_sp_matter_name.setText(matter_name);
                ll_assign_clients.setVisibility(View.GONE);

                load_clear_list();
                display_members();

                selected_groups.setVisibility(View.GONE);//team members
                selected_tm.setVisibility(View.GONE);//clients
                selected_individual.setVisibility(View.GONE);
                selected_attached_documents.setVisibility(View.GONE);
                try {
                    loadEntitiesSpinnerData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hide_all_list();
                ischecked_matter = true;
            }
        });
    }

    public CreateEvent(Meetings meetings1, ArrayList<Event_Details_DO> event_details_list) {
        meetings = meetings1;
        existing_events_list = event_details_list;
    }

    private void callCreateEventWebservice_edit(String recurring_edit_choice) {
        try {
            String doctype = "doctype";
            String docid = "docid";
            JSONObject postData = new JSONObject();
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONArray selected_team_member = new JSONArray();
            JSONArray selected_clients_list = new JSONArray();
            JSONArray individual_array = new JSONArray();
            JSONArray added_notification_list = new JSONArray();
            JSONArray time_sheets = new JSONArray();
            JSONArray existing_attachments = new JSONArray();
            for (int i = 0; i < selectedValues.size(); i++) {
//            NotifyMeDo notifyMeDo = notifyme_list.get(i);
                added_notification_list.put(selectedValues.get(i));
            }

            JSONObject selected_docs;
            for (int j = 0; j < selected_documents_list.size(); j++) {
                selected_docs = new JSONObject();
                DocumentsDo attachDocumentsDO = selected_documents_list.get(j);
                selected_docs.put(doctype, attachDocumentsDO.getDoctype());
                selected_docs.put(docid, attachDocumentsDO.getDocid());
                existing_attachments.put(selected_docs);
            }

            for (int i = 0; i < selected_tm_list.size(); i++) {
                TeamDo addTeamMembersDo = selected_tm_list.get(i);
                selected_team_member.put(addTeamMembersDo.getId());
            }
            for (int i = 0; i < selected_entity_client_list.size(); i++) {
                RelationshipsDO addClientsDo = selected_entity_client_list.get(i);
                selected_clients_list.put(addClientsDo.getId());
            }
            for (int i = 0; i < selected_individual_list.size(); i++) {
                RelationshipsDO relationshipsDO = selected_individual_list.get(i);
                individual_array.put(relationshipsDO.getId());
            }
            //...

            Date event_date = AndroidUtils.stringToDateTimeDefault(tv_event_creation_date.getText().toString(), "MM-dd-yyyy");
            event_creation_date = AndroidUtils.getDateToString(event_date, "yyyy-MM-dd");
            Date event_start_date = null;
            Date event_date2 = null;
            String start_time = tv_event_start_time.getText().toString();
            if (start_time.equals("") || start_time == null) {
                start_time = "00:00";
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "HH:mm");

            } else {
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "HH:mm");
            }
            event_starting_date = AndroidUtils.getDateToString(event_start_date, event_creation_date + "'T'HH:mm:ss");
            String end_time = tv_event_end_time.getText().toString();
            if (end_time.equals("") || end_time == null) {
                end_time = "00:00";
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "HH:mm");
            } else {
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "HH:mm");
            }
            String duration_timesheet = "";
            if (event_start_date != null && event_date2 != null) {

                long differenceInMilliSeconds = Math.abs(event_start_date.getTime() - event_date2.getTime());
                long differenceInHours = (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;
                long differenceInMinutes
                        = (differenceInMilliSeconds / (60 * 1000)) % 60;
                long differenceInSeconds
                        = (differenceInMilliSeconds / 1000) % 60;
                String duration = differenceInHours + ":" + differenceInMinutes;
                Date hours = AndroidUtils.stringToDateTimeDefault(duration, "HH:mm");
                duration_timesheet = AndroidUtils.getDateToString(hours, "HH:mm");
            }
            event_end_time = AndroidUtils.getDateToString(event_date2, event_creation_date + "'T'HH:mm:ss");

            //......

            if (isAddTimesheet) {
                JSONObject time_sheet_obj;
                time_sheet_obj = new JSONObject();
                time_sheet_obj.put("date", event_creation_date);
                if (duration_timesheet.equals("") || duration_timesheet == null) {
                    time_sheet_obj.put("duration", "30:00");
                } else {
                    time_sheet_obj.put("duration", duration_timesheet);
                }
                time_sheet_obj.put("eventtitle", tv_sp_task_name.getText().toString());
                time_sheet_obj.put("addedby", Constants.NAME);
                time_sheet_obj.put("user_id", Constants.USER_ID);
                time_sheets.put(time_sheet_obj);
            }
            int multiplied_offset = (-1) * (offset);
            String originalDateString = event_creation_date;
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date originalDate = originalFormat.parse(originalDateString);
            desiredFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String newDateString = desiredFormat.format(originalDate);
            postData.put("date", newDateString);
            postData.put("attachments", existing_attachments);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            postData.put("invitees_internal", selected_team_member);
            postData.put("invitees_external", selected_clients_list);
            postData.put("invitees_consumer_external", individual_array);
            if (matter_legal.equals("legal") || matter_legal.equals("general")) {
                postData.put("title", matter_name + " - " + tv_sp_task_name.getText().toString());
                postData.put("dialin", tv_dialing_number.getText().toString());
                postData.put("location", tv_location.getText().toString());
                postData.put("addtimesheet", isAddTimesheet);
                postData.put("timesheets", time_sheets);
                postData.put("description", tv_description.getText().toString());
            } else if (matter_legal.equals("overhead") || (matter_legal.equals("others"))) {
                postData.put("dialin", tv_dialing_number.getText().toString());
                postData.put("location", tv_location.getText().toString());
                postData.put("addtimesheet", isAddTimesheet);
                postData.put("timesheets", time_sheets);
                postData.put("description", tv_description.getText().toString());
                postData.put("title", tv_sp_task_name.getText().toString());
            } else {
                postData.put("title", tv_sp_task_name.getText().toString());
                postData.put("description", tv_message.getText().toString());
            }

            postData.put("notifications", added_notification_list);
//            postData.put("description", tv_description.getText().toString());
            postData.put("timezone_location", timezone_location);
            postData.put("timezone_offset", multiplied_offset);
            postData.put("repeat_interval", tv_sp_repetetion.getText().toString().toLowerCase(Locale.ROOT));
            postData.put("meeting_link", tv_meeting_link.getText().toString());
            postData.put("allday", isAllDay);
            postData.put("recurrent_edit_choice", recurring_edit_choice);
            postData.put("from_ts", event_starting_date);
            postData.put("to_ts", event_end_time);
            if (matter_legal.equals("legal") || matter_legal.equals("general")) {
                postData.put("matter_type", matter_legal);
                postData.put("matter_id", matter_id);
            }
            postData.put("event_type", matter_legal);

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/event/" + event_id + "/" + multiplied_offset, "EDIT_EVENT", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update_event() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.edit_recurring_choice, null);
            final CheckBox delete_only_this = dialogLayout.findViewById(R.id.radio_delete_only_this);
            delete_only_this.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            delete_only_this.setText("This event");
            delete_only_this.setTextSize(20);
            final CheckBox delete_all = dialogLayout.findViewById(R.id.radio_delete_all);
            delete_all.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            delete_all.setText("All events");
            delete_all.setTextSize(20);
            final CheckBox delete_following = dialogLayout.findViewById(R.id.radio_delete_ts_fe);
            delete_following.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            delete_following.setText("This and following events");
            delete_following.setTextSize(20);
            final Button delete = dialogLayout.findViewById(R.id.delete_event);
            final Button btn_close_event = dialogLayout.findViewById(R.id.btn_close_event);
            btn_close_event.setTextColor(Color.RED);
            btn_close_event.setText(R.string.cancel);

            delete_only_this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_only_this.setChecked(true);
                    recurring_edit_choice = "this";
                    delete_all.setChecked(false);
                    delete_following.setChecked(false);
                }
            });
            delete_following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_following.setChecked(true);
                    recurring_edit_choice = "forward";
                    delete_all.setChecked(false);
                    delete_only_this.setChecked(false);
                }
            });
            delete_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_all.setChecked(true);
                    recurring_edit_choice = "all";
                    delete_only_this.setChecked(false);
                    delete_following.setChecked(false);
                }
            });

            final AlertDialog dialog = builder.create();
            progressDialog = dialog;

            btn_close_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (delete_only_this.isChecked() || delete_following.isChecked() || delete_all.isChecked()) {
                        progressDialog.dismiss();
                        callCreateEventWebservice_edit(recurring_edit_choice);
                    } else {
                        AndroidUtils.showAlert("Please choose one of the edit recurring event", getContext());
                    }
                }
            });
            dialog.setView(dialogLayout);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExistingData() {

        btn_create_event.setText(R.string.update);
        cv_add_clients.setVisibility(View.VISIBLE);
        ll_add_entities.setVisibility(View.VISIBLE);
        ll_individual.setVisibility(View.VISIBLE);
        ll_add_groups.setVisibility(View.VISIBLE);
        hide_documents();
        ll_project.setVisibility(View.VISIBLE);
        ll_matter_name.setVisibility(View.VISIBLE);
        tv_sp_project.setClickable(false);
        tv_sp_matter_name.setClickable(false);
        tv_sp_matter_name.setText(matter_name);
        tv_sp_project.setText(matter_legal);
        ll_assign_clients.setVisibility(View.VISIBLE);
        if (matter_legal == "reminders")
            tv_message.setText(existing_description);
        ll_task.setVisibility(View.VISIBLE);
        ll_repetetion.setVisibility(View.VISIBLE);
        tv_sp_task_name.setText(existing_task);
        tv_description.setText(existing_description);
        tv_sp_repetetion.setText(existing_repetetion);
        tv_meeting_link.setText(existing_meeting_link);
        tv_event_creation_date.setText(existing_date);
        tv_event_start_time.setText(existing_start_time);
        tv_event_end_time.setText(existing_end_time);
//        Log.d("vaaaaa1",v1);
        display_duration(tv_event_start_time.getText().toString(), tv_event_end_time.getText().toString());
        tv_location.setText(existing_location);
        tv_dialing_number.setText(existing_dialin);
        tv_event_creation_date.setText(existing_date);
        if (isAddTimesheet) {
            cb_add_to_timesheet.setChecked(true);
        }
        if (isExistingAllday) {
            cb_all_day.setChecked(true);
        }
//        for (int i = 0; i < legalTaksList.size(); i++) {
//            if (existing_task.equalsIgnoreCase(legalTaksList.get(i).getTaskName())) {
//                sp_task.setSelection(i);
//            }
//        }
        for (int i = 0; i < timeZonesList.size(); i++) {
            if (existing_time_zone.equals(timeZonesList.get(i).getNAME())) {
                sp_time_zone.setSelection(i);
                timezone_location = (timeZonesList.get(i).getNAME());
                offset = Integer.parseInt(timeZonesList.get(i).getGMT());
            }
        }
//        for(int i=0;i<existing_events_list.size();i++)
//        {
//            if(matter_id.equals(matterList.get(i).getId()))
//            {
//                sp_matter_name.setSelection(i);
//                matter_id= matterList.get(i).getMember_id();
//            }
//        }
        tv_sp_time_zone.setText(timezone_location);

        for (int i = 0; i < Repetetions.size(); i++) {
            if (existing_repetetion.equals(Repetetions.get(i).toLowerCase(Locale.ROOT))) {
                sp_repetetion.setSelection(i);
            }
        }
        try {
            for (int i = 0; i < existing_events_list.size(); i++) {

                JSONArray documents = existing_events_list.get(i).getAttachments();
                for (int j = 0; j < documents.length(); j++) {
                    DocumentsDo documentsDo = new DocumentsDo();
                    JSONObject jsonObject = documents.getJSONObject(j);
                    documentsDo.setDocid(jsonObject.getString("docid"));
                    documentsDo.setDoctype(jsonObject.getString("doctype"));
                    documentsDo.setName(jsonObject.getString("name"));
//                    documentsDo.setUser_id(jsonObject.getString("user_id"));
                    selected_documents_list.add(documentsDo);
                }
            }

            if (selected_documents_list.size() != 0) {
                loadSelectedDocuments();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            for (int i = 0; i < existing_events_list.size(); i++) {
                JSONArray team_members = existing_events_list.get(i).getTeam_name();
                for (int j = 0; j < team_members.length(); j++) {
                    TeamDo teamModel = new TeamDo();
                    JSONObject jsonObject = team_members.getJSONObject(j);
                    teamModel.setId(jsonObject.getString("id"));
                    teamModel.setName(jsonObject.getString("name"));
                    selected_tm_list.add(teamModel);
                }
            }
            if (selected_tm_list.size() != 0) {
                loadSelectedTM();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            for (int i = 0; i < existing_events_list.size(); i++) {
                JSONArray clients = existing_events_list.get(i).getTm_name();

                for (int j = 0; j < clients.length(); j++) {
                    RelationshipsDO relationshipsDO = new RelationshipsDO();
                    JSONObject jsonObject = clients.getJSONObject(j);
                    String entity_id_edit = jsonObject.getString("entityId");
                    relationshipsDO.setId(jsonObject.getString("entityId") + "_" + jsonObject.getString("tmId"));
                    relationshipsDO.setName(jsonObject.getString("tmName"));
                    selected_entity_client_list.add(relationshipsDO);
                }
            }

            if (selected_entity_client_list.size() != 0) {
                loadSelectedClients();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            for (int i = 0; i < existing_events_list.size(); i++) {
                Log.d("Selected_Documents", existing_events_list.get(i).getConsumer_external().toString());
                JSONArray individuals = existing_events_list.get(i).getConsumer_external();

                for (int j = 0; j < individuals.length(); j++) {
                    RelationshipsDO relationshipsDO = new RelationshipsDO();
                    JSONObject jsonObject = individuals.getJSONObject(j);
                    relationshipsDO.setId(jsonObject.getString("entityId"));
                    relationshipsDO.setName(jsonObject.getString("tmName"));
                    relationshipsDO.setType(jsonObject.getString("tmId"));
                    selected_individual_list.add(relationshipsDO);
                }
            }
            loadSelectedIndividual();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (int i = 0; i < existing_events_list.size(); i++) {
                JSONArray notification = existing_events_list.get(i).getNotifications();
                for (int j = 0; j < notification.length(); j++) {
                    selectedValues.add(notification.get(j).toString());
                    String value_notify = notification.getString(j);
                    String[] time = value_notify.split("-");
                    time_format = time[0];
                    time_value = time[1];
                    NotificationPopup();
                }

            }

//    NotificationPopup();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            for (int i = 0; i < existing_events_list.size(); i++) {
//                JSONArray team_members = existing_events_list.get(i).getConsumer_external();
//                for (int j = 0; j < team_members.length(); j++) {
//                    RelationshipsDO relationshipsDO = new RelationshipsDO();
//                    JSONObject jsonObject = team_members.getJSONObject(j);
////                String type =   ;
//
////                    if (jsonObject.getString("type").equals("consumer")) {
//                        relationshipsDO.setId(jsonObject.getString("entityId"));
//                        relationshipsDO.setName(jsonObject.getString("tmName"));
//                        relationshipsDO.setType(jsonObject.getString("tmId"));
//                        individual_list.add(relationshipsDO);
////                    } else {
////                        relationshipsDO.setId(jsonObject.getString("id"));
////                        relationshipsDO.setName(jsonObject.getString("name"));
////                        relationshipsDO.setType(jsonObject.getString("type"));
////                        selected_entity_client_list.add(relationshipsDO);
////                    }
//                }
//            }
//            loadSelectedIndividual();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        loadSelectedClients();
//        selected_groups.setVisibility(View.VISIBLE);//team members
//        selected_tm.setVisibility(View.VISIBLE);//clients
//        selected_individual.setVisibility(View.VISIBLE);
//        selected_attached_documents.setVisibility(View.VISIBLE);
        if (selected_documents_list.size() == 0) {
            selected_attached_documents.setVisibility(View.GONE);
        } else {
            selected_attached_documents.setVisibility(View.VISIBLE);
        }
        if (selected_tm_list.size() == 0) {
            selected_groups.setVisibility(View.GONE);
        } else {
            selected_groups.setVisibility(View.VISIBLE);
        }
        if (selected_entity_client_list.size() == 0) {
            selected_tm.setVisibility(View.GONE);
        } else {
            selected_tm.setVisibility(View.VISIBLE);
        }
        if (selected_individual_list.size() == 0) {
            selected_individual.setVisibility(View.GONE);
        } else {
            selected_individual.setVisibility(View.VISIBLE);
        }
    }

    private void check_event() {
        if (matter_legal.equals("legal"))
            Event_name = "Legal Matter";
        else if (matter_legal.equals("general"))
            Event_name = "General Matter";
        else if (matter_legal.equals("overhead"))
            Event_name = "Overhead";
        else if (matter_legal.equals("others"))
            Event_name = "Others";
        else if (matter_legal.equals("reminders"))
            Event_name = "Reminders";
    }

    private void display_check_list() {
        if ((teamList.size() == 0) && (entities_list.size() == 0) && (individual_list.size()) == 0 && (documents_list.size()) == 0 && (entity_client_list.size() == 0)) {
            cv_add_clients.setVisibility(View.GONE);
        } else {
            cv_add_clients.setVisibility(View.VISIBLE);
        }
        if (documents_list.size() == 0) {
            ll_documents_view.setVisibility(View.GONE);
        } else {
            ll_documents_view.setVisibility(View.VISIBLE);
        }
        if (teamList.size() == 0) {
            ll_add_groups.setVisibility(View.GONE);
        } else {
            ll_add_groups.setVisibility(View.VISIBLE);
        }
        if (entities_list.size() == 0) {
            ll_add_entities.setVisibility(View.GONE);
        } else {
            ll_add_entities.setVisibility(View.VISIBLE);
        }
        if (individual_list.size() == 0) {
            ll_individual.setVisibility(View.GONE);
        } else {
            ll_individual.setVisibility(View.VISIBLE);
        }
    }

    private void hide_documents() {
        if ((matter_legal.equals("legal")) || (matter_legal.equals("general"))) {
            ll_documents_view.setVisibility(View.VISIBLE);
        } else {
            ll_documents_view.setVisibility(View.GONE);
        }
    }

    private void hide_all_list() {
        ischecked_project = true;
        ischecked_matter = true;
        ischecked_task = true;
        ischecked_repetetion = true;
        ischecked_time = true;
        is_clicked_team = true;
        is_clicked_clients = true;
        is_clicked_documents = true;
        is_clicked_individuals = true;
        is_entities = true;

        sp_time_zone.setVisibility(View.GONE);
        sp_repetetion.setVisibility(View.GONE);
        sp_project.setVisibility(View.GONE);
        sp_task.setVisibility(View.GONE);
        sp_matter_name.setVisibility(View.GONE);
        rv_groups_view.setVisibility(View.GONE);
        rv_clients_view.setVisibility(View.GONE);
        sp_entities.setVisibility(View.GONE);
        rv_individuals_view.setVisibility(View.GONE);
        rv_documents_view.setVisibility(View.GONE);
    }
}