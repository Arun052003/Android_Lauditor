package com.digicoffer.lauditor.TimeSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Adapters.TimeSheetsAdapter;
import com.digicoffer.lauditor.TimeSheets.Adapters.WeeklyTSAdapter;
import com.digicoffer.lauditor.TimeSheets.Models.EventsModel;
import com.digicoffer.lauditor.TimeSheets.Models.StatusModel;
import com.digicoffer.lauditor.TimeSheets.Models.TSMatterModel;
import com.digicoffer.lauditor.TimeSheets.Models.TaskModel;
import com.digicoffer.lauditor.TimeSheets.Models.TasksModel;
import com.digicoffer.lauditor.TimeSheets.Models.TimeSheetModel;
import com.digicoffer.lauditor.TimeSheets.Models.WeekModel;
import com.digicoffer.lauditor.TimeSheets.Models.WeekTotalModel;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NonSubmittedTimesheets extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, WeeklyTSAdapter.InterfaceListener {
    private AlertDialog progressDialog;
    private LinearLayout task_layout;
    private ArrayList<TimeSheetModel> timeSheetsList = new ArrayList<>();
    private ArrayList<TSMatterModel> matterList = new ArrayList<>();
    private ArrayList<TasksModel> tasksList = new ArrayList<>();
    private ArrayList<EventsModel> eventsList = new ArrayList<>();
    private ArrayList<StatusModel> statusList = new ArrayList<>();
    private ArrayList<WeekTotalModel> weektotalList = new ArrayList<>();
    private ArrayList<WeekModel> weeksList = new ArrayList<>();
    private String selected_matter = "";
    private String selected_matter_id = "";
    private String selected_matter_type = "";
    String current_date = "";
    String matter_name;
    private boolean isMatterTypeExists = false;
    private String selected_task = "";
    private String selected_status = "";
    private boolean date_status = false;
    private View view;
    private String selected_date = "";
    private ListView sp_project, sp_task, sp_status, sp_date;
    private TextInputEditText tv_hours, tv_total_hours;
    private RecyclerView rv_non_submitted_timesheets;
    private androidx.appcompat.widget.AppCompatButton btn_cancel_timesheet, btn_save_timesheet;
    androidx.appcompat.widget.AppCompatButton btn_submit_timesheet, bt_fifteen_minutes, bt_thirty_minutes, bt_forty_five_minutes;
    private String hoursString;
    private String minutesString;
    private TextView project_id, tv_sp_project, task_id, tv_sp_task, status_id, tv_sp_status, date_id, tv_sp_date, hours_id, minutes_id, tot_hours_id;
    String date = "";
    String event_name = "", status = "", task_name = "", chosen_date = "", hours = "", minutes = "", event_task_id, event_id;
    private boolean issubmitted = false;
    ArrayList<TaskModel> timesheet_eventsModels;
    private boolean ischecked_project = true, ischecked_status = true, ischecked_task = true, ischecked_date = true;

    public NonSubmittedTimesheets(ArrayList<TaskModel> eventsModels) {
        this.timesheet_eventsModels = eventsModels;
        // Required empty public constructor
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.not_submitted_timesheet, container, false);
//        if()

        sp_project = view.findViewById(R.id.sp_project);
        sp_project.setVisibility(View.GONE);
        sp_task = view.findViewById(R.id.sp_task);
        sp_task.setVisibility(View.GONE);
        sp_status = view.findViewById(R.id.sp_status);
        sp_status.setVisibility(View.GONE);
        sp_date = view.findViewById(R.id.sp_date);
        sp_date.setVisibility(View.GONE);
        rv_non_submitted_timesheets = view.findViewById(R.id.rv_non_submitted_timesheets);
        tv_hours = view.findViewById(R.id.tv_hours);
        tv_total_hours = view.findViewById(R.id.tv_total_hours);
        btn_cancel_timesheet = view.findViewById(R.id.btn_cancel_timesheet);
        btn_submit_timesheet = view.findViewById(R.id.btn_submit_timesheet);
        btn_submit_timesheet.setText("Submit");
        project_id = view.findViewById(R.id.project_id);
        project_id.setText(R.string.project);
        tv_sp_project = view.findViewById(R.id.tv_sp_project);
        task_id = view.findViewById(R.id.task_id);
        task_id.setText(R.string.task);
        tv_sp_task = view.findViewById(R.id.tv_sp_task);
        task_layout = view.findViewById(R.id.task_layout);
        task_layout.setVisibility(View.GONE);
        tv_sp_task.setVisibility(View.GONE);
        status_id = view.findViewById(R.id.status_id);
        status_id.setText(R.string.status);
        tv_sp_status = view.findViewById(R.id.tv_sp_status);
        date_id = view.findViewById(R.id.date_id);
        date_id.setText(R.string.date);
        tv_sp_date = view.findViewById(R.id.tv_sp_date);
        hours_id = view.findViewById(R.id.hours_id);
        hours_id.setText(R.string.hours);
        minutes_id = view.findViewById(R.id.minutes_id);
        minutes_id.setText(R.string.minutes);
        tot_hours_id = view.findViewById(R.id.tot_hours_id);
        tot_hours_id.setText(R.string.total_hours);
        btn_save_timesheet = view.findViewById(R.id.btn_save_timesheet);
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        ArrayList<String> weekDates = bundle.getStringArrayList("weekDates");
        current_date = weekDates.get(0);
//        AndroidUtils.showAlert(current_date,getContext());
        bt_thirty_minutes = view.findViewById(R.id.bt_thirty_minutes);
        bt_forty_five_minutes = view.findViewById(R.id.bt_forty_five_minutes);
        bt_fifteen_minutes = view.findViewById(R.id.bt_fifteen_minutes);
        bt_fifteen_minutes.setOnClickListener(minutesButtonClickListener);
        bt_thirty_minutes.setOnClickListener(minutesButtonClickListener);
        bt_forty_five_minutes.setOnClickListener(minutesButtonClickListener);

        tv_sp_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_project, sp_project);
                ischecked_project = !ischecked_project;
            }
        });
        tv_sp_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_task, sp_task);
                ischecked_task = !ischecked_task;
            }
        });
        tv_sp_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_status, sp_status);
                ischecked_status = !ischecked_status;
            }
        });
        tv_sp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_date, sp_date);
                ischecked_date = !ischecked_date;
            }
        });
        btn_save_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_total_hours.getText().toString().equals("")) {
                    tv_hours.setError("Please enter hours");
                    tv_hours.requestFocus();
                } else {
                    try {
                        callSaveTimeSheetWebservice();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        tv_hours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String hoursText = s.toString();
                if (!hoursText.isEmpty()) {
                    int hours = Integer.parseInt(hoursText);
                    if (hours > 24) {
                        tv_hours.removeTextChangedListener(this);
                        tv_hours.setText(hoursText.substring(0, hoursText.length() - 1));
                        tv_hours.setSelection(tv_hours.getText().length());
                        tv_hours.addTextChangedListener(this);
                    }
                }
                updateTotalTime();
            }
        });


//        AndroidUtils.showAlert(date, getContext());
        if (date.isEmpty()) {
//             date_status = false;
            callCurrentDateTimeSheetsWebservice(date, date_status);
        } else if (date == null) {
            callCurrentDateTimeSheetsWebservice(date, date_status);
        } else {
            callTimeSheetsWebservice(date);
        }
        btn_submit_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSubmitTimeSheetWebService();
            }
        });
        final CommonSpinnerAdapter status_adapter = new CommonSpinnerAdapter((Activity) getContext(), weekDates);
        sp_date.setAdapter(status_adapter);
        sp_date.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_date = weekDates.get(position);
                ischecked_date = true;
                tv_sp_date.setText(selected_date);
                sp_date.setVisibility(View.GONE);
//                AndroidUtils.showToast(selected_date,getContext());
//                selected_status = weekDates.get(adapterView.getSelectedItemPosition()).ge;
            }
        });
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMMM d,yyyy", Locale.ENGLISH);

        ArrayList<String> formattedDates = new ArrayList<>();

        for (String dateStr : weekDates) {
            try {
                Date date1 = inputFormat.parse(dateStr);
                String formattedDate = outputFormat.format(date1);
                formattedDates.add(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (String value : formattedDates) {
            weeksList.add(new WeekModel(value));
        }

//        AndroidUtils.showAlert(weeksList.toString(),getContext());


//        AndroidUtils.showToast(String.valueOf(status),getContext());
//        if (status){
//            TextView textView = new TextView(getContext());
////            textView.setText("TimeSheet already submitted");
////            ((LinearLayout) view).addView(textView);
////                OpenPopup();
//            // Add a LinearLayout as the container
//        }


        return view;
    }

    private void callDeleteTimesheetwebservice(String timesheet_id) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            postdata.put("id", timesheet_id);
//            https://apidev2.digicoffer.com/professional/v3/user/timesheets
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.DELETE, "v3/user/timesheets", "Delete TimeSheet", postdata.toString());
        } catch (Exception e) {
            if (progressDialog.isShowing() && progressDialog != null) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
        }
    }

    private void callSubmitTimeSheetWebService() {
        try {


            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date new_date = inputFormat.parse(date);
            String outputDate = outputFormat.format(new_date);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/user/timesheets/freeze/" + outputDate, "Submit TimeSheet", postdata.toString());
        } catch (Exception e) {
            if (progressDialog.isShowing() && progressDialog != null) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
        }

    }

    private void callEditTimesheetwebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
//            action:"hours"
//        billing:"billable"
//        date:"Mar 8, 2024"
//        duration_hours:"2"
//        duration_minutes:"00"
//        id:"65f14ecfa1db7204262d10cd"
//        matter_id:"65cb535ca1db72042a368f7d"
//        matter_type:"general"
//        title:"Consultation"
//            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            postdata.put("id", event_name);
            postdata.put("action", "hours");
            postdata.put("billing", selected_status);
            SimpleDateFormat inputformat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputformat = new SimpleDateFormat("MMM d, yyyy");
            try {
                Date date = inputformat.parse(selected_date);
                String formattedDate = outputformat.format(date);
                postdata.put("date", formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            postdata.put("duration_hours", hoursString);
            if (Objects.equals(minutesString, "0")) {
                postdata.put("duration_minutes", "00");
            } else {
                postdata.put("duration_minutes", minutesString);
            }
            postdata.put("matter_id", event_id);
//            if ((selected_matter_type.equals("legal")) || (selected_matter_type.equals("general"))) {
//                postdata.put("matter_type", selected_matter_type);
//            } else {
//                postdata.put("matter_type", matter_name);
//            }
            postdata.put("matter_type", "general");
            postdata.put("title", task_name);
//            AndroidUtils.showAlert(postdata.toString(),getContext());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/user/timesheets", "Edit Timesheet", postdata.toString());
            Log.d("Edit_tt", postdata.toString());
        } catch (Exception e) {
            if (!progressDialog.equals(null) && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }

    }

    private void callSaveTimeSheetWebservice() {
        try {
//            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            postdata.put("action", "hours");
            postdata.put("billing", selected_status);
            SimpleDateFormat inputformat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputformat = new SimpleDateFormat("MMM d, yyyy");
            try {
                Date date = inputformat.parse(selected_date);
                String formattedDate = outputformat.format(date);
                postdata.put("date", formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            postdata.put("duration_hours", hoursString);
            if (Objects.equals(minutesString, "0")) {
                postdata.put("duration_minutes", "00");
            } else {
                postdata.put("duration_minutes", minutesString);
            }
            postdata.put("matter_id", selected_matter_id);
            if (isMatterTypeExists) {
                postdata.put("matter_type", selected_matter_type);
            } else {
                postdata.put("matter_type", selected_matter);
            }

            postdata.put("title", selected_task);
//            AndroidUtils.showAlert(postdata.toString(),getContext());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/user/timesheets", "SAVE TIMESHEETS", postdata.toString());
        } catch (Exception e) {
            if (!progressDialog.equals(null) && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    View.OnClickListener minutesButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String hoursText = tv_hours.getText().toString();
            int hours = 0;
            if (!hoursText.isEmpty()) {
                hours = Integer.parseInt(hoursText);
            }

            // If hours are equal to 24, do not allow selecting minutes
            if (hours == 24) {
                return;
            }

            AppCompatButton button = (AppCompatButton) v;
            deselectAllMinuteButtons();
            button.setSelected(true);
            updateTotalTime();
        }
    };

    private void deselectAllMinuteButtons() {
        bt_fifteen_minutes.setSelected(false);
        bt_thirty_minutes.setSelected(false);
        bt_forty_five_minutes.setSelected(false);
    }

    private void updateTotalTime() {
//        tv_total_hours.hin
        String hoursText = tv_hours.getText().toString();
        int hours = 0;
        if (!hoursText.isEmpty()) {
            hours = Integer.parseInt(hoursText);
        }

        int minutes = 0;
        if (bt_fifteen_minutes.isSelected()) {
            minutes = 15;
        } else if (bt_thirty_minutes.isSelected()) {
            minutes = 30;
        } else if (bt_forty_five_minutes.isSelected()) {
            minutes = 45;
        }
        hoursString = String.valueOf(hours);
        hours = Integer.parseInt(hoursString);
        minutesString = String.valueOf(minutes);
        String totalTimeText = hours + "h " + minutes + "m";
        tv_total_hours.setText(totalTimeText);
    }

    private void callCurrentDateTimeSheetsWebservice(String date, boolean date_status) {
        try {
            clearList();
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject data = new JSONObject();
//            AndroidUtils.showToast(String.valueOf(date_status), getContext());

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/user/timesheets", "TimeSheets", data.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearList() {
        timeSheetsList.clear();
        weektotalList.clear();
        eventsList.clear();
        matterList.clear();
        statusList.clear();
        tasksList.clear();

//        weeksList.clear();
    }

    private void disableAllViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                disableAllViews(child);
            }
        } else {
            view.setEnabled(false);
        }
    }

    private void OpenPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

// Add dialog actions (e.g. positive/negative buttons) here

        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void callTimeSheetsWebservice(String date) {
        try {
            clearList();
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject data = new JSONObject();
//            AndroidUtils.showToast(String.valueOf(date_status),getContext());
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date new_date = inputFormat.parse(date);
            String outputDate = outputFormat.format(new_date);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/user/timesheets/" + outputDate, "TimeSheets", data.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog.isShowing() && progressDialog != null) {
            AndroidUtils.dismiss_dialog(progressDialog);
        }
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("TimeSheets")) {
                    JSONObject dates = result.getJSONObject("dates");
//                    AndroidUtils.showAlert(data.toString(),getContext());
                    timeSheetsList.clear();
                    loadTimesheetData(dates, result);
                } else if (httpResult.getRequestType().equals("Tasks")) {
                    JSONArray tasks = result.getJSONArray("tasks");
                    tasksList.clear();
                    loadTasks(tasks);
                } else if (httpResult.getRequestType().equals("SAVE TIMESHEETS")) {
//                    AndroidUtils.showToast(result.getString("msg"),getContext());
                    tv_hours.setText("");
                    tv_total_hours.setText("");
                    if (date.isEmpty()) {
                        callCurrentDateTimeSheetsWebservice(date, date_status);
                    } else {
                        callTimeSheetsWebservice(date);
                    }
                } else if (httpResult.getRequestType().equals("Submit TimeSheet")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    if (date.isEmpty()) {
                        callCurrentDateTimeSheetsWebservice(date, date_status);
                    } else {
                        callTimeSheetsWebservice(date);
                    }
                } else if (httpResult.getRequestType().equals("Delete TimeSheet")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    if (date.isEmpty()) {
                        callCurrentDateTimeSheetsWebservice(date, date_status);
                    } else {
                        callTimeSheetsWebservice(date);
                    }
                } else if (httpResult.getRequestType().equals("Edit Timesheet")) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    if (date.isEmpty()) {
                        callCurrentDateTimeSheetsWebservice(date, date_status);
                    } else {
                        callTimeSheetsWebservice(date);
                    }
                    tv_sp_status.setText("");
                    tv_sp_date.setText("");
                    tv_hours.setText("");
                    tv_total_hours.setText("");
                    AndroidUtils.dismiss_dialog(progressDialog);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTasks(JSONArray tasks) throws JSONException {
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject jsonObject = tasks.getJSONObject(i);
            TasksModel tasksModel = new TasksModel();
            tasksModel.setDisplayValue(jsonObject.getString("displayValue"));
            tasksModel.setReturnValue(jsonObject.getString("returnValue"));
            tasksList.add(tasksModel);

        }


        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), tasksList);
        sp_task.setAdapter(spinner_adapter);
        sp_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_task = tasksList.get(position).getDisplayValue();
                ischecked_task = true;
                tv_sp_task.setText(selected_task);
                sp_task.setVisibility(View.GONE);
            }
        });

    }

    private boolean containsMatterType(ArrayList<TSMatterModel> matterList, String
            matterTypeToCheck) {
        for (TSMatterModel matter : matterList) {
            if (matter.getMatter_type() != null && matter.getMatter_type().equals(matterTypeToCheck)) {
                return true;
            }
        }
        return false;
    }

    private void loadTimesheetData(JSONObject dates, JSONObject result) throws JSONException {
        timeSheetsList.clear();
        TimeSheetModel timeSheetModel = new TimeSheetModel();
        timeSheetModel.setFrozen(dates.getBoolean("isFrozen"));
        timeSheetModel.setCurrentWeek(dates.getString("currentWeek"));
        timeSheetModel.setNextWeek(dates.getString("nextWeek"));
        timeSheetModel.setPrevWeek(dates.getString("prevWeek"));
        JSONObject timesheets = result.getJSONObject("timesheetList");

        JSONArray matters = timesheets.getJSONArray("matters");
        JSONObject weektotal = timesheets.getJSONObject("weekTotal");
        WeekTotalModel weekModel = new WeekTotalModel();
        weekModel.setMon(weektotal.getString("Mon"));
        weekModel.setTue(weektotal.getString("Tue"));
        weekModel.setWed(weektotal.getString("Wed"));
        weekModel.setThu(weektotal.getString("Thu"));
        weekModel.setFri(weektotal.getString("Fri"));
        weekModel.setSat(weektotal.getString("Sat"));
        weekModel.setSun(weektotal.getString("Sun"));
        weektotalList.add(weekModel);
        for (int i = 0; i < matters.length(); i++) {
            JSONObject jsonObject = matters.getJSONObject(i);
            TSMatterModel matterModel = new TSMatterModel();
            matterModel.setMattername(jsonObject.getString("matterName"));
            matterModel.setMatterid(jsonObject.getString("matterId"));
            matterModel.setTasks(jsonObject.getJSONArray("tasks"));
            if (jsonObject.has("matterType")) {
                matterModel.setMatter_type(jsonObject.getString("matterType"));
            }
            matterList.add(matterModel);
//
        }
//            AndroidUtils.showAlert(result.getString("timesheetList").toString(),getContext());
        timeSheetsList.add(timeSheetModel);
        for (int j = 0; j < matterList.size(); j++) {
            for (int m = 0; m < matterList.get(j).getTasks().length(); m++) {
                JSONObject jsonObject = matterList.get(j).getTasks().getJSONObject(m);
                EventsModel eventsModel = new EventsModel();
                if (jsonObject.has("billing")) {
                    eventsModel.setBilling(jsonObject.getString("billing"));
                }
                eventsModel.setTaskName(jsonObject.getString("taskName"));
                eventsModel.setTotal(jsonObject.getString("total"));
                eventsModel.setMon(jsonObject.getJSONObject("Mon"));
                eventsModel.setTue(jsonObject.getJSONObject("Tue"));
                eventsModel.setWed(jsonObject.getJSONObject("Wed"));
                eventsModel.setThu(jsonObject.getJSONObject("Thu"));
                eventsModel.setFri(jsonObject.getJSONObject("Fri"));
                eventsModel.setSat(jsonObject.getJSONObject("Sat"));
                eventsModel.setSun(jsonObject.getJSONObject("Sun"));
                eventsModel.setMatter_id(matterList.get(j).getMatterid());
                eventsModel.setMatter_name(matterList.get(j).getMattername());
                //check matter type
                if (jsonObject.has("matterType")) {
                    eventsModel.setMatter_type(jsonObject.getString("matterType"));
                }
                eventsList.add(eventsModel);
            }
        }


        for (int i = 0; i < timeSheetsList.size(); i++) {
//            AndroidUtils.showToast(String.valueOf(timeSheetsList.get(i).isFrozen()),getContext());
            if (timeSheetsList.get(i).isFrozen()) {
                view.setAlpha(0.5f);
                issubmitted = true;
//
                disableAllViews(view);
                AndroidUtils.showAlert("TimeSheet already submitted,please select other week", getContext());

//
            } else {
                issubmitted = false;
//                loadTimesheetsRecyclerview();

                final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), matterList);

                sp_project.setAdapter(spinner_adapter);
                sp_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected_matter = matterList.get(position).getMattername();
                        selected_matter_id = matterList.get(position).getMatterid();
                        try {
                            selected_matter_type = matterList.get(position).getMatter_type();

                            if (containsMatterType(matterList, selected_matter_type)) {
                                isMatterTypeExists = true;
                                // The matterList contains the specified matterType
                            } else {
                                isMatterTypeExists = false;
                                // The matterList does not contain the specified matterType
                            }

                        } catch (NullPointerException e) {
//                       selected_ma

                            e.printStackTrace();
                        }
                        if (selected_matter_type != null) {
                            callTaskWebservice(selected_matter_type);
                        } else {
                            callTaskWebservice(selected_matter_id);
                        }
                        task_layout.setVisibility(View.VISIBLE);
                        tv_sp_task.setVisibility(View.VISIBLE);
                        ischecked_project = true;
                        tv_sp_project.setText(selected_matter);
                        sp_project.setVisibility(View.GONE);
                    }
                });
                StatusModel statusModel = new StatusModel("Billable");
                StatusModel statusModel1 = new StatusModel("Non Billable");
                statusList.add(statusModel);
                statusList.add(statusModel1);
                final CommonSpinnerAdapter status_adapter = new CommonSpinnerAdapter((Activity) getContext(), statusList);
                sp_status.setAdapter(status_adapter);
                sp_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected_status = statusList.get(position).getName();
                        status = selected_status;
                        ischecked_status = true;
                        tv_sp_status.setText(selected_status);
                        sp_status.setVisibility(View.GONE);
                    }
                });
            }
        }

        try {
            loadTimesheetsRecyclerview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTimesheetsRecyclerview() {
//        AndroidUtils.showAlert(weeksList.toString(),getContext());
        rv_non_submitted_timesheets.setLayoutManager(new GridLayoutManager(getContext(), 1));
        TimeSheetsAdapter timeSheetsAdapter = new TimeSheetsAdapter(weeksList, eventsList, weektotalList, getContext(), issubmitted, this);
        rv_non_submitted_timesheets.setAdapter(timeSheetsAdapter);
        rv_non_submitted_timesheets.setHasFixedSize(true);
        if (timeSheetsAdapter != null && timeSheetsAdapter.getItemCount() > 0) {
            int lastPosition = timeSheetsAdapter.getItemCount() - 1;
            rv_non_submitted_timesheets.smoothScrollToPosition(lastPosition);
        }
        timeSheetsAdapter.notifyDataSetChanged();
    }

    private void callTaskWebservice(String selected_matter_type) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/event/tasks/" + selected_matter_type, "Tasks", jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void DeleteMatter(String timesheet_id) {
        //..
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            LinearLayout ll_documents = view.findViewById(R.id.ll_documents);
            ll_documents.setBackground(getActivity().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            ImageView close_details = view.findViewById(R.id.close_documents);

            tv_confirmation.setText("Are you sure you want to Delete task ?");
//                Matter_Status = "Closed";


            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();
            close_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
//            ad_dialog_delete = dialog;
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    callDeleteTimesheetwebservice(timesheet_id);
//                    callCloseMatterWebService(viewMatterModel,Matter_Status);
                }
            });

            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
        //..
    }

    @Override
    public void Edit_Matter_Info(TaskModel eventsModels, String selected_date1, String matter_type) {
        tv_sp_task.setVisibility(View.VISIBLE);
        event_name = eventsModels.getTaskid();
        status = eventsModels.getTask_billing();
        task_name = eventsModels.getTask_name();
        matter_name = eventsModels.getTask_matter_name();
        event_task_id = eventsModels.getTask_matter_id();
        event_id = eventsModels.getMatterid();
        hours = eventsModels.getHours();
        minutes = eventsModels.getMinutes();
        hoursString = hours;
        minutesString = minutes;
        //...
        selected_matter_type = matter_type;
        selected_status = status;
        String value = selected_date1.substring(4);
        Log.d("Splitted_v", date + "./././" + value);
        SimpleDateFormat inputformat = new SimpleDateFormat("MMMM dd,yyyy");
        SimpleDateFormat outputformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = inputformat.parse(value);
            String formattedDate = outputformat.format(date);
            selected_date = formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_sp_date.setText(selected_date);
        btn_save_timesheet.setText(R.string.update);
        tv_sp_status.setText(status);
        tv_hours.setText(hours);
        String totalTimeText = hours + "h " + minutes + "m";
        tv_total_hours.setText(totalTimeText);
        tv_sp_project.setEnabled(false);
        tv_sp_task.setEnabled(false);

        Log.d("Edit11_timesheet", matter_name + "..." + event_name + "......" + status + "......" + task_name + "......" + event_task_id + "......" + event_id + "......" + hours + "......" + minutes + "......" + date);
        btn_save_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callEditTimesheetwebservice();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
