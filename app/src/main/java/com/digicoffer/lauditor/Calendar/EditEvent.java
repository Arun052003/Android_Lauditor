package com.digicoffer.lauditor.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class EditEvent extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    private static String ADAPTER_TAG = "";
    private AlertDialog progressDialog;
    MultiAutoCompleteTextView at_family_members;
    CardView cv_meeting_details;
    Meetings meetings;
    String recurring_edit_choice;
    AlertDialog ad_dialog;
    final ArrayList<String> Repetetions = new ArrayList<>();
    JSONArray notification = new JSONArray();
    LinearLayout ll_client_team_members, ll_documents_list, ll_attach_document, ll_selected_entites, selected_individual, ll_individual_list, ll_selected_team_members, selected_tm, selected_groups;
    final int[] mHour = new int[1];
    final int[] mMinute = new int[1];
    private ArrayList<String> selectedValues = new ArrayList<>();
    String selected_hour_type = "";
    private boolean timeZonesTaskCompleted = false;
    CheckBox cb_all_day, cb_add_to_timesheet;
    boolean isAllDay;
    boolean isAddTimesheet;
    private boolean matterTaskCompleted = false;
    private boolean tmTaskCompleted = false;
    final String[] AM_PM = new String[1];
    int offset;
    private Button btn_add_groups, btn_attach_document, btn_add_clients, btn_assigned_team_members, btn_individual;
    String team_member_id;
    int adapter_position = 0;
    ArrayList<RelationshipsDO> entities_list = new ArrayList<>();
    ArrayList<RelationshipsDO> individual_list = new ArrayList<>();
    ArrayList<RelationshipsDO> selected_individual_list = new ArrayList<>();
    ArrayList<DocumentsDo> selected_documents_list = new ArrayList<>();
    ArrayList<RelationshipsDO> selected_entity_client_list = new ArrayList<>();
    ArrayList<RelationshipsDO> new_selected_client_list = new ArrayList<>();
    ArrayList<RelationshipsDO> entity_client_list = new ArrayList<>();
    ArrayList<TeamDo> selected_tm_list = new ArrayList<>();
    private TextView at_add_groups, tv_message, at_attach_document, at_add_clients, at_assigned_team_members, at_individual;
    private TextInputEditText tv_meeting_link, tv_dialing_number, tv_location, tv_description;
    private AppCompatButton add_notification, btn_cancel_timesheet,btn_create_event, btn_save_timesheet;

    TextView tv_project_name, tv_matter_name, tv_task_name, tv_date_name, tv_time_zone, tv_repetetion, tv_meeting_link_name, tv_dial_in_number, tv_location_name, tv_description_name, tv_message_name, add_groups, add_entities, tv_assigned_client, tv_individual, tv_selected_individual, tv_selected_tm, tv_selected_document, tv_name, tv_selected_clients;
    TextView tv_sp_project, tv_sp_matter_name, tv_sp_task_name, tv_sp_time_zone, tv_sp_repetetion, Time_duration, tv_sp_entities, tv_attach_document, at_assigned_client;
    ListView sp_project, sp_matter_name, sp_task, sp_time_zone, sp_repetetion, sp_entities;
    boolean ischecked_project = true, ischecked_matter = true, ischecked_task = true, ischecked_time = true, ischecked_repetetion = true, is_notify = true, is_entities = true, is_clicked_team = true, is_clicked_clients = true, is_clicked_individuals = true, is_clicked_documents = true;
    AppCompatButton tv_event_creation_date, tv_event_start_time, tv_event_end_time, btn_assigned_clients, btn_cancel_event;
    NestedScrollView scrollView;
    private String selected_project;
    private String selected_matter;
    private String selected_task;
    private String entity_id;
    Hashtable<String, Integer> timesPosHash = new Hashtable<>();
    ArrayList<TimeZonesDO> timeZonesList = new ArrayList<TimeZonesDO>();
    private LinearLayout ll_project, selected_attached_documents, ll_matter_name, ll_message, ll_task, ll_add_notification, ll_repetetion;
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
    private String existing_location;
    private String existing_description;
    private String event_id;
    private ArrayList<Event_Details_DO> existing_events_list = new ArrayList<>();
    String Calendar_type = "";
    ArrayList<Event_Details_DO> eventDetailsList1;
    RecyclerView rv_groups_view, rv_clients_view, rv_individuals_view, rv_documents_view;

    public EditEvent(ArrayList<Event_Details_DO> eventDetailsList) {
        eventDetailsList1=eventDetailsList;
    }

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
        //..
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
        sp_repetetion = view.findViewById(R.id.sp_repetetion);
        sp_repetetion.setVisibility(View.GONE);
        at_add_groups = view.findViewById(R.id.at_add_groups);
        add_groups = view.findViewById(R.id.add_groups);
        add_groups.setText("Add Team Members");
        btn_cancel_timesheet = view.findViewById(R.id.btn_cancel_event);
        btn_create_event = view.findViewById(R.id.btn_create_event);
        ll_message = view.findViewById(R.id.ll_message);
        tv_message = view.findViewById(R.id.tv_message);
        tv_message_name = view.findViewById(R.id.tv_message_name);
        tv_message_name.setText("Message");
        tv_message.setHint("Message");
        at_attach_document = view.findViewById(R.id.at_attach_document);
        cb_all_day = view.findViewById(R.id.chk_select_all);
        cb_all_day.setText("All Day");
        cb_all_day.setBackground(getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        Time_duration = view.findViewById(R.id.Time_duration);
        Time_duration.setVisibility(View.GONE);
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
        cb_add_to_timesheet = view.findViewById(R.id.chk_select_all);
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
        tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
        tv_event_start_time.setOnClickListener(this);
        tv_event_end_time = view.findViewById(R.id.tv_event_end_time);
        tv_event_end_time.setOnClickListener(this);
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
        ll_repetetion = view.findViewById(R.id.ll_repetetion);
        ll_repetetion.setVisibility(View.GONE);


        meetings = (Meetings) getParentFragment();

        for(int i=0;i<eventDetailsList1.size();i++) {
            tv_sp_project.setText(eventDetailsList1.get(i).getTitle());
            tv_sp_matter_name.setText(eventDetailsList1.get(i).getMatter_name());
        }

        //..
        projectList.clear();
        btn_cancel_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetings.loadViewEvent(Calendar_type);
            }
        });
        btn_create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.showToast("Update Event",getContext());
                if (!matter_legal.equals("reminders")) {
                    if (tv_event_creation_date.getText().toString().equals("")) {
                        tv_event_creation_date.setError("Date is required");
                        tv_event_creation_date.requestFocus();
                    } else if (tv_event_start_time.getText().toString().equals("")) {
                        tv_event_start_time.setError("Start Time is required");
                        tv_event_start_time.requestFocus();
                    } else if (tv_event_end_time.getText().toString().equals("")) {
                        tv_event_end_time.setError("End time is required");
                        tv_event_end_time.requestFocus();
                    } else {
                        update_event(event_id);
                    }
                } else {
                    if (tv_event_creation_date.getText().toString().equals("")) {
                        tv_event_creation_date.setError("Date is required");
                        tv_event_creation_date.requestFocus();
                    } else if (tv_event_start_time.getText().toString().equals("")) {
                        tv_event_start_time.setError("Start Time is required");
                        tv_event_start_time.requestFocus();
                    } else if (tv_event_end_time.getText().toString().equals("")) {
                        tv_event_end_time.setError("End time is required");
                        tv_event_end_time.requestFocus();
                    } else if (tv_message.getText().toString().equals("")) {
                        tv_message.setError("Message is required");
                        tv_message.requestFocus();
                    } else {
                        update_event(event_id);
                    }
                }
            }
        });

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
        ll_project.setVisibility(View.VISIBLE);
//        callTimeZoneWebservice();
//        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), projectList);
//
//        sp_project.setAdapter(spinner_adapter);
//        sp_project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selected_project = projectList.get(adapterView.getSelectedItemPosition()).getProjectName();
//                loadProjectData(selected_project);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        return view;
    }

    private void loadcheckboxData() {
        cb_all_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Retrieve the checked status of the checkbox
                if (isChecked) {
                    // Checkbox is checked, return true
                    // Perform any additional actions here
                    isAllDay = true;
                } else {
                    // Checkbox is not checked, return false
                    // Perform any additional actions here
                    isAllDay = false;
                }
            }
        });
        cb_add_to_timesheet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Retrieve the checked status of the checkbox
                if (isChecked) {
                    // Checkbox is checked, return true
                    // Perform any additional actions here
                    isAddTimesheet = true;
                } else {
                    // Checkbox is not checked, return false
                    // Perform any additional actions here
                    isAddTimesheet = false;
                }
            }
        });
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
            e.fillInStackTrace();
        }
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.BlueDatePickerDialogTheme, date, myCalendar.get(java.util.Calendar.YEAR), myCalendar.get(java.util.Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;
            case R.id.tv_event_start_time:

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay < 12) {
                                    AM_PM[0] = "AM";
                                } else {
                                    AM_PM[0] = "PM";
                                }

                                tv_event_start_time.setText(hourOfDay + ":" + minute + "" + AM_PM[0]);

                            }
                        }, hour, minute, true);
                timePickerDialog.show();
                break;
            case R.id.tv_event_end_time:
                final Calendar calendar = Calendar.getInstance();
                mHour[0] = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute[0] = calendar.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                final TimePickerDialog timePickerDialog_end_time = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                final Calendar c = Calendar.getInstance();
                                mHour[0] = c.get(Calendar.HOUR_OF_DAY);
                                mMinute[0] = c.get(Calendar.MINUTE);
                                if (hourOfDay < 12) {
                                    AM_PM[0] = "AM";
                                } else {
                                    AM_PM[0] = "PM";
                                }

//                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.ENGLISH);
                                tv_event_end_time.setText(hourOfDay + ":" + minute + "" + AM_PM[0]);

                            }
                        }, mHour[0], mMinute[0], true);
                timePickerDialog_end_time.show();
                break;
            case R.id.btn_add_groups:
                if (teamList.size() == 0) {
                    callTeamMemberWebservice();
                } else {
                    TeamMembersPopup();
                }


//
                break;
            case R.id.btn_individual:
                if (individual_list.size() == 0) {
                    callClientsWebservice();
                } else {
                    load_individual_Popup();
                }
                break;
            case R.id.btn_assigned_team_members:

                loadEntityClientPopup();


                break;
            case R.id.add_notification:
                NotificationPopup();
                break;
//            case R.id.btn_create_event:
//                break;
            case R.id.btn_attach_document:
                load_documents_Popup();
        }

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
                AndroidUtils.showToast("No document to show", getContext());
            } else {
                for (int i = 0; i < documents_list.size(); i++) {
                    for (int j = 0; j < selected_documents_list.size(); j++) {
                        if (documents_list.get(i).getDocid().matches(selected_documents_list.get(j).getDocid())) {
                            DocumentsDo documentsDo = documents_list.get(i);
                            documentsDo.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);
                        }
                    }
                }
                selected_documents_list.clear();
//            selected_tm_list.clear();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.groups_list_adapter, null);
                TextView tv_header = view.findViewById(R.id.header_name);
                tv_header.setText("Select Documents");
                RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
                ImageView iv_cancel = view.findViewById(R.id.close_groups);
                AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
                AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_groups.setLayoutManager(layoutManager);
                rv_groups.setHasFixedSize(true);
                ADAPTER_TAG = "Documents";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_groups.setAdapter(documentsAdapter);
                AlertDialog dialog = dialogBuilder.create();
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_groups_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_save_group.setOnClickListener(new View.OnClickListener() {
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
//                    loadSelectedIndividual();
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.setView(view);
                dialog.show();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
                        e.fillInStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_documents_list.addView(view_opponents);
        }
    }

    private void update_event(final String event_id) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.edit_recurring_choice, null);
            final RadioGroup radioGroup = (RadioGroup) dialogLayout.findViewById(R.id.radioGroup);
            final RadioButton delete_only_this = (RadioButton) dialogLayout.findViewById(R.id.radio_delete_only_this);
            final RadioButton delete_all = (RadioButton) dialogLayout.findViewById(R.id.radio_delete_all);
            final RadioButton delete_following = (RadioButton) dialogLayout.findViewById(R.id.radio_delete_ts_fe);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (delete_only_this.isChecked()) {
                        recurring_edit_choice = "this";
                    } else if (delete_all.isChecked()) {
                        recurring_edit_choice = String.valueOf("all");
                    } else if (delete_following.isChecked()) {
                        recurring_edit_choice = String.valueOf("forward");
                    }
                }
            });
            final AlertDialog dialog = builder.create();
            ad_dialog = dialog;
            Button delete = (Button) dialogLayout.findViewById(R.id.delete_event);

            ImageButton btn_close_event = (ImageButton) dialogLayout.findViewById(R.id.btn_close_event);
            btn_close_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad_dialog.dismiss();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callCreateEventWebservice(event_id, recurring_edit_choice);
                }
            });

            dialog.setView(dialogLayout);
            dialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void callCreateEventWebservice(String event_id, String recurring_edit_choice) {
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
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "hh:mm");

            } else {
                event_start_date = AndroidUtils.stringToDateTimeDefault(start_time, "hh:mm");
            }
            event_starting_date = AndroidUtils.getDateToString(event_start_date, event_creation_date + "'T'HH:mm:ss");
            String end_time = tv_event_end_time.getText().toString();
            if (end_time.equals("") || end_time == null) {
                end_time = "00:00";
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "hh:mm");
            } else {
                event_date2 = AndroidUtils.stringToDateTimeDefault(end_time, "hh:mm");
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
//                e.fillInStackTrace();
//            } catch (JSONException e) {
//                e.fillInStackTrace();
//            }
            postData.put("invitees_internal", selected_team_member);
            postData.put("invitees_external", selected_clients_list);
            postData.put("invitees_consumer_external", individual_array);
            postData.put("title", matter_name + " - " + selected_task);
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
            postData.put("repeat_interval", repeat_interval.toLowerCase(Locale.ROOT));
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

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/event/" + this.event_id + "/" + multiplied_offset, "CREATE_EVENT", postData.toString());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void NotificationPopup() {
        View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.add_calendar_notification, null);
        Spinner sp_minutes = view_opponents.findViewById(R.id.sp_minutes);
        ArrayList<MinutesDO> minutes_list = new ArrayList<>();
        minutes_list.add(new MinutesDO("Minutes"));
        minutes_list.add(new MinutesDO("Hours"));
        minutes_list.add(new MinutesDO("Days"));
        minutes_list.add(new MinutesDO("Weeks"));
        TextInputEditText tv_numbers = view_opponents.findViewById(R.id.tv_numbers);
        ImageView iv_delete_notification = view_opponents.findViewById(R.id.iv_delete_notification);
        tv_numbers.setText("1");
        final int position = ll_add_notification.getChildCount();
// Set the position as a tag to the view
        view_opponents.setTag(position);

        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), minutes_list);
        sp_minutes.setAdapter(spinner_adapter);
        sp_minutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_hour_type = minutes_list.get(adapterView.getSelectedItemPosition()).getName();
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
                    e.fillInStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tv_numbers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Update the corresponding value in the ArrayList

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (position >= 0 && position < selectedValues.size()) {
                    selectedValues.set(position, editable.toString() + "- " + selected_hour_type.toLowerCase(Locale.ROOT));
                }
                // Do nothing
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
        selectedValues.add(tv_numbers.getText().toString() + "- " + selected_hour_type.toLowerCase(Locale.ROOT));
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
                AndroidUtils.showToast("No Individuals to show", getContext());
            } else {
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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.groups_list_adapter, null);
                RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
                ImageView iv_cancel = view.findViewById(R.id.close_groups);
                TextView tv_header = view.findViewById(R.id.header_name);
                tv_header.setText("Select Individuals");
                AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
                AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_groups.setLayoutManager(layoutManager);
                rv_groups.setHasFixedSize(true);
                ADAPTER_TAG = "INDIVIDUAL";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_groups.setAdapter(documentsAdapter);
                AlertDialog dialog = dialogBuilder.create();
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_groups_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_save_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getIndividual_List().size(); i++) {
                            RelationshipsDO teamModel = documentsAdapter.getIndividual_List().get(i);
                            if (teamModel.isChecked()) {
                                selected_individual_list.add(teamModel);
//                        AndroidUtils.showAlert(selected_individual_list.toString(),getContext());
//                        new_selected_individual_list.add(teamModel);
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }


                        loadSelectedIndividual();
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.setView(view);
                dialog.show();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
        if (selected_individual_list.size() == 0) {
            selected_individual.setVisibility(View.GONE);
        } else {
            selected_individual.setVisibility(View.VISIBLE);
        }

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
                        int position = 0;
                        if (v.getTag() instanceof Integer) {
                            position = (Integer) v.getTag();
                            v = ll_individual_list.getChildAt(position);
                            ll_individual_list.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            RelationshipsDO teamModel = selected_individual_list.get(position);
                            teamModel.setChecked(false);
                            selected_individual_list.remove(position);
                            if (selected_individual_list.size() == 0) {
                                selected_individual.setVisibility(View.GONE);
                            }
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_individual_list.size()];
                            for (int i = 0; i < selected_individual_list.size(); i++) {
                                value[i] = selected_individual_list.get(i).getName();
                            }

                            String str = String.join(",", value);
                            at_individual.setText(str);
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
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
            e.fillInStackTrace();
        }
    }

    public void setEventDetailsList(ArrayList<Event_Details_DO> eventDetailsList, String calendar_Type) {
        this.existing_events_list = eventDetailsList;
        this.Calendar_type = calendar_Type;
//        for (int i=0;i< existing_events_list.size();i++){
//            Event_Details_DO event_details_do = existing_events_list.get(i);
////            JSONObject jsonObject = existing_events_list.get(i);
//
//        }
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
            selected_tm_list.clear();
            if (teamList.size() == 0) {
                AndroidUtils.showToast("No Team Members to View", getContext());
            } else {
//            selected_tm_list.clear();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.groups_list_adapter, null);
                RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
                ImageView iv_cancel = view.findViewById(R.id.close_groups);
                TextView tv_header = view.findViewById(R.id.header_name);
                tv_header.setText("Select Team Members");
                AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
                AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_groups.setLayoutManager(layoutManager);
                rv_groups.setHasFixedSize(true);
                ADAPTER_TAG = "TM";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_groups.setAdapter(documentsAdapter);
                AlertDialog dialog = dialogBuilder.create();
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_groups_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_save_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (int i = 0; i < documentsAdapter.getTmList().size(); i++) {
                            TeamDo teamModel = documentsAdapter.getTmList().get(i);
                            if (teamModel.isChecked()) {
                                selected_tm_list.add(teamModel);

                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }

                        if (selected_tm_list.size() == 0) {
                            selected_groups.setVisibility(View.GONE);
                            at_add_groups.setText("Select Team Members");

                        } else {
                            loadSelectedTM();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.setView(view);
                dialog.show();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
        at_assigned_team_members.setText(str);
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
                        int position = 0;
                        if (v.getTag() instanceof Integer) {
                            position = (Integer) v.getTag();
                            v = ll_client_team_members.getChildAt(position);
                            ll_client_team_members.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            RelationshipsDO teamModel = selected_entity_client_list.get(position);
                            teamModel.setChecked(false);
                            if (selected_entity_client_list.size() == 0) {
                                selected_tm.setVisibility(View.GONE);
                                ll_client_team_members.removeAllViews();
                                at_assigned_team_members.setText("Select Client");
                            } else {
                                selected_tm.setVisibility(View.VISIBLE);
                            }
                            selected_entity_client_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_entity_client_list.size()];
                            for (int i = 0; i < selected_entity_client_list.size(); i++) {
                                value[i] = selected_entity_client_list.get(i).getName();
                            }

                            String str = String.join(",", value);
                            at_assigned_team_members.setText(str);
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
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
                        int position = 0;
                        if (v.getTag() instanceof Integer) {
                            position = (Integer) v.getTag();
                            v = ll_selected_team_members.getChildAt(position);
                            ll_selected_team_members.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            TeamDo teamModel = selected_tm_list.get(position);
                            teamModel.setChecked(false);

                            selected_tm_list.remove(position);

//                                ll_selected_team_members.setVisibility(View.GONE);
                            if (selected_tm_list.size() == 0) {
                                selected_groups.setVisibility(View.GONE);
                                at_add_groups.setText("Select Team Members");
                            } else {
//                            selected_groups_list.set(position, groupsModel);
                                String[] value = new String[selected_tm_list.size()];
                                for (int i = 0; i < selected_tm_list.size(); i++) {
                                    value[i] = selected_tm_list.get(i).getName();
                                }

                                String str = String.join(",", value);
                                at_add_groups.setText(str);
                            }

                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_selected_team_members.addView(view_opponents);
        }
    }

    private void loadProjectData(String selected_project) {
        switch (selected_project) {
            case "legal":
//                callProjectWebservice(matter_legal);
//                loadExistingData();
//                callClientsWebservice();
//                TaskDo caseFilling = new TaskDo("Case Filling");
//                TaskDo consultation = new TaskDo("Consultation");
//                TaskDo clb = new TaskDo("Creating Legal Breifs");
//                TaskDo mwc = new TaskDo("Meeting with client");
//                TaskDo hearing = new TaskDo("Hearing");
                legalTaksList.add(new TaskDo("Case Filling"));
                legalTaksList.add(new TaskDo("Consultation"));
                legalTaksList.add(new TaskDo("Creating Legal Breifs"));
                legalTaksList.add(new TaskDo("Meeting with client"));
                legalTaksList.add(new TaskDo("Hearing"));
                loadTaskList(legalTaksList);
                break;
            case "general":
                legalTaksList.clear();
//                callProjectWebservice(matter_legal);
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
                break;
        }
    }

    private void loadExistingData() {
        if (isExistingAllday) {
            cb_add_to_timesheet.setChecked(true);
        }
        if (isAllDay) {
            cb_all_day.setChecked(true);
        }
        for (int i = 0; i < legalTaksList.size(); i++) {
            if (existing_task.equalsIgnoreCase(legalTaksList.get(i).getTaskName())) {
                sp_task.setSelection(i);
            }
        }
        tv_event_creation_date.setText(existing_date);
        tv_event_start_time.setText(existing_start_time);
        tv_event_end_time.setText(existing_end_time);
        for (int i = 0; i < timeZonesList.size(); i++) {
            if (existing_time_zone.equals(timeZonesList.get(i).getGMT())) {
                sp_time_zone.setSelection(i);
            }
        }
        for (int i = 0; i < Repetetions.size(); i++) {
            if (existing_repetetion.equals(Repetetions.get(i).toLowerCase(Locale.ROOT))) {
                sp_repetetion.setSelection(i);
            }
        }
        tv_meeting_link.setText(existing_meeting_link);
        tv_description.setText(existing_description);
        tv_dialing_number.setText(existing_dialin);
        tv_location.setText(existing_location);
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
            e.fillInStackTrace();
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
            e.fillInStackTrace();
        }

        try {
            for (int i = 0; i < existing_events_list.size(); i++) {
                JSONArray clients = existing_events_list.get(i).getTm_name();

                for (int j = 0; j < clients.length(); j++) {
                    RelationshipsDO relationshipsDO = new RelationshipsDO();
                    JSONObject jsonObject = clients.getJSONObject(j);
                    relationshipsDO.setId(jsonObject.getString("entityId") + "_" + jsonObject.getString("tmId"));
                    relationshipsDO.setName(jsonObject.getString("tmName"));
                    selected_entity_client_list.add(relationshipsDO);
                }
            }

            if (selected_entity_client_list.size() != 0) {
                loadSelectedClients();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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
            e.fillInStackTrace();
        }
        try {
            for (int i = 0; i < existing_events_list.size(); i++) {
                JSONArray notification = existing_events_list.get(i).getNotifications();
                for (int j = 0; j < notification.length(); j++) {
                    selectedValues.add(notification.get(j).toString());
                    NotificationPopup();
                }

            }
//    NotificationPopup();
        } catch (Exception e) {
            e.fillInStackTrace();
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
//            e.fillInStackTrace();
//        }
//
//        loadSelectedClients();
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

    private void loadRepetetions() {
        Repetetions.clear();
        Repetetions.add("None");
        Repetetions.add("Weekly");
        Repetetions.add("Bi- Weekly");
        Repetetions.add("Monthly");
        Repetetions.add("Yearly");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Repetetions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_repetetion.setAdapter(adapter1);
        sp_repetetion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                repeat_interval = Repetetions.get(sp_repetetion.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadExistingData();
    }

    private void hideAlldetails() {
        ll_matter_name.setVisibility(View.GONE);
        ll_message.setVisibility(View.VISIBLE);
        cv_meeting_details.setVisibility(View.GONE);
        ll_task.setVisibility(View.GONE);
        ll_attach_document.setVisibility(View.GONE);
    }

    private void hideMatterDetails() {
        cv_meeting_details.setVisibility(View.VISIBLE);
        ll_matter_name.setVisibility(View.GONE);
        ll_message.setVisibility(View.GONE);
        ll_task.setVisibility(View.VISIBLE);
        ll_attach_document.setVisibility(View.GONE);
    }

    private void unHideMatterDetails() {
        cv_meeting_details.setVisibility(View.VISIBLE);
        ll_matter_name.setVisibility(View.VISIBLE);
        ll_message.setVisibility(View.GONE);
        ll_task.setVisibility(View.VISIBLE);
        ll_attach_document.setVisibility(View.VISIBLE);
    }

    private void loadTaskList(ArrayList<TaskDo> legalTaksList) {
        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), legalTaksList);

        sp_task.setAdapter(spinner_adapter);
        sp_task.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_task = legalTaksList.get(adapterView.getSelectedItemPosition()).getTaskName();

//                loadProjectData(selected_project);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            e.fillInStackTrace();
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
            e.fillInStackTrace();
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
                        e.fillInStackTrace();
                    }
                } else if (httpResult.getRequestType().equals("TIMEZONES")) {
                    if (!result.getBoolean("error")) {

                        JSONArray jsonArray = result.getJSONArray("timezones");
                        load_timezones(jsonArray);
//                        Thre
                        timeZonesTaskCompleted = true;
                        if (timeZonesTaskCompleted) {
                            for (int i = 0; i < existing_events_list.size(); i++) {
                                Event_Details_DO event_details_do = existing_events_list.get(i);
                                matter_legal = event_details_do.getMatter_type();
                                existing_description = event_details_do.getDescription();
                                existing_date = event_details_do.getDate();
                                existing_dialin = event_details_do.getDialin();
//                                existing_date = event_details_do.getDate();
                                existing_location = event_details_do.getLocation();
                                existing_end_time = event_details_do.getConverted_End_time();
                                existing_start_time = event_details_do.getConverted_Start_time();
                                existing_meeting_link = event_details_do.getMeeting_link();
                                existing_repetetion = event_details_do.getRepeat_interval();
                                event_id = event_details_do.getId();
                                String event_name=event_details_do.getTitle();
                                AndroidUtils.showToast(event_id+"..+.."+event_name,getContext());
                                String title = event_details_do.getTitle();
                                String[] splitStrings = title.split(" - ");
                                String firstString = splitStrings[0];
                                String secondString = splitStrings[1];
                                existing_task = secondString;
                                existing_time_zone = event_details_do.getOffset_location();
                                isAddTimesheet = event_details_do.isRecurring();
                                isExistingAllday = event_details_do.isAll_day();
//                                loadExistingData();
                            }
                            loadProjectData(matter_legal);
//                            callProjectWebservice(matter_legal);
                        }
                    } else {
                        AndroidUtils.showAlert("Something went wrong", getContext());
                    }
                } else if (httpResult.getRequestType().equals("Team List")) {
                    if (!matterTaskCompleted) {

                        return;
                    }
                    if (!result.getBoolean("error")) {
                        JSONArray jsonArray = result.getJSONArray("users");
                        loadTeamList(jsonArray);
                        tmTaskCompleted = true;
//                        Thre

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

                } else if (httpResult.getRequestType().equals("Entity Client List")) {
                    if (!result.getBoolean("error")) {
                        JSONArray jsonArray = result.getJSONArray("users");
                        loadEntity_Clients(jsonArray);
                    } else {
                        AndroidUtils.showAlert("Something went wrong", getContext());
                    }
                } else if (httpResult.getRequestType().equals("CREATE_EVENT")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    loadClearedLists();
                    ad_dialog.dismiss();
                    meetings.loadViewEvent(Calendar_type);
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
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
            e.fillInStackTrace();
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

//        loadSelectedClients();
    }

    private void loadEntityClientPopup() {
        try {
            if (entity_client_list.size() == 0) {
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
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.groups_list_adapter, null);
                TextView tv_header = view.findViewById(R.id.header_name);
                tv_header.setText("Select Clients");
                RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
                ImageView iv_cancel = view.findViewById(R.id.close_groups);
                AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
                AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_groups.setLayoutManager(layoutManager);
                rv_groups.setHasFixedSize(true);
                ADAPTER_TAG = "ENTITY";
                CommonRelationshipsAdapter documentsAdapter = new CommonRelationshipsAdapter(teamList, ADAPTER_TAG, individual_list, entity_client_list, documents_list);
                rv_groups.setAdapter(documentsAdapter);
                AlertDialog dialog = dialogBuilder.create();
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_groups_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_save_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getEntity_client_list().size(); i++) {
                            RelationshipsDO teamModel = documentsAdapter.getEntity_client_list().get(i);
                            if (teamModel.isChecked()) {
                                selected_entity_client_list.add(teamModel);
//                        AndroidUtils.showAlert(selected_individual_list.toString(),getContext());
//                        new_selected_client_list.add(teamModel);
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }
                        loadSelectedClients();
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.setView(view);
                dialog.show();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
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

        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), entities_list);
        sp_entities.setAdapter(adapter);
//        callTimeZoneWebservice();
        sp_entities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                entity_id = entities_list.get(sp_entities.getSelectedItemPosition()).getId();
                callEntityClientWebservice(entity_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            e.fillInStackTrace();
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
        String Message = "";
        for (int i = 0; i < teamList.size(); i++) {
            Message = teamList.get(i).getName();
        }
        Log.d("Team_List", Message);
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
        sp_time_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<TimeZonesDO> timezones = new ArrayList<>();
                for (int j = 0; j < timeZonesList.size(); j++) {
                    String timezone_offset = timeZonesList.get(j).getGMT();

                    if (timezone_offset.matches(String.valueOf((hours)))) {
                        sp_time_zone.setSelection(j);
                        offset = Integer.parseInt(timeZonesList.get(sp_time_zone.getSelectedItemPosition()).getGMT());

                        timezone_location = (timeZonesList.get(sp_time_zone.getSelectedItemPosition()).getNAME());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
//        AndroidUtils.showAlert(matters.toString(),getContext());
        loadMatterSpinnerList(matterList);
    }

    private void loadMatterSpinnerList(ArrayList<ViewMatterModel> matterList) {

        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), matterList);

        sp_matter_name.setAdapter(spinner_adapter);
        sp_matter_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selected_matter = matterList.get(adapterView.getSelectedItemPosition()).getCasetype();
                matter_id = matterList.get(adapterView.getSelectedItemPosition()).getId();
                matter_name = matterList.get(adapterView.getSelectedItemPosition()).getTitle();
                try {
                    loadEntitiesSpinnerData();
                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
//                loadProjectData(selected_project);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {

        } catch (Exception e) {
            e.fillInStackTrace();
        }
//        callClientsWebservice();
//            callTeamMemberWebservice();
    }


}
