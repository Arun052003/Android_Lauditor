package com.digicoffer.lauditor.TimeSheets;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Adapters.MonthlyTSAdapter;
import com.digicoffer.lauditor.TimeSheets.Adapters.TeamMembersTSAdapter;
import com.digicoffer.lauditor.TimeSheets.Models.Month_Model;
import com.digicoffer.lauditor.TimeSheets.Models.TMModel;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class AGS_TeamMembers extends Fragment implements AsyncTaskCompleteListener {

    private AlertDialog progress_dialog;
    String month;
    private ArrayList<TMModel> teamList = new ArrayList<>();
    private ArrayList<Month_Model> monthlist = new ArrayList<>();
    private RecyclerView rv_time_sheets;
    private TextInputEditText et_search_matter;
    private String date, isweek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_members_timesheets, container, false);
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        isweek = bundle.getString("isweek");

//        AndroidUtils.showAlert(date,getContext());
        rv_time_sheets = view.findViewById(R.id.rv_time_sheets);
        et_search_matter = view.findViewById(R.id.et_search_matter);
        et_search_matter.setHint(R.string.search_team_members);
        try {
            if (isweek == "month")
                callTeamMembersWebservice("month");
            else callTeamMembersWebservice("week");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void callTeamMembersWebservice(String isweek) throws ParseException {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
        //..
        if (isweek.equals("week")) {
            if (date.equals("") || date.equals(null)) {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/tms-all-weekly", "Team Members", postdata.toString());
            } else {
//              AndroidUtils.showToast(String.valueOf(date_status),getContext());
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
                Date new_date = inputFormat.parse(date);
                String outputDate = outputFormat.format(new_date);//https://apidev2.digicoffer.com/professional/matter/timesheets/tms-all-weekly
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/tms-all-weekly" + "-" + outputDate, "Team Members", postdata.toString());
            }
        } else {
            if (date.equals("") || date.equals(null)) {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/tms-all-monthly", "Team Members month", postdata.toString());
            } else {
//            AndroidUtils.showToast(String.valueOf(date_status),getContext());
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
                Date new_date = inputFormat.parse(date);
                String outputDate = outputFormat.format(new_date);//https://apidev2.digicoffer.com/professional/matter/timesheets/tms-all-weekly
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/tms-all-monthly" + "-" + outputDate, "Team Members month", postdata.toString());
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog.isShowing() && progress_dialog != null) {
            AndroidUtils.dismiss_dialog(progress_dialog);
        }
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());

                if (httpResult.getRequestType().equals("Team Members")) {
                    try {
                        JSONArray jsonArray = result.getJSONArray("timesheets");
                        loadTmData(jsonArray);
                    } catch (Exception e) {

                    }
                } else if (httpResult.getRequestType().equals("Team Members month")) {
                    try {
                        JSONArray jsonArray = result.getJSONArray("timesheets");
                        loadTm_month_Data(jsonArray);
                    } catch (Exception e) {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Team_member_Month_view
    private void loadTm_month_Data(JSONArray jsonArray) throws JSONException {
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Month_Model tmModel = new Month_Model();
            tmModel.setName(jsonObject.getString("name"));
//            tmModel.setId(jsonObject.getString("id"));
            tmModel.setBillable_week_1(jsonObject.getString("bw1"));
            tmModel.setBillable_week_2(jsonObject.getString("bw2"));
            tmModel.setBillable_week_3(jsonObject.getString("bw3"));
            tmModel.setBillable_week_4(jsonObject.getString("bw4"));
            tmModel.setBillable_week_5(jsonObject.getString("bw5"));
            tmModel.setBillable_week_tot(jsonObject.getString("tb"));

            tmModel.setNon_billable_week_1(jsonObject.getString("nbw1"));
            tmModel.setNon_billable_week_2(jsonObject.getString("nbw2"));
            tmModel.setNon_billable_week_3(jsonObject.getString("nbw3"));
            tmModel.setNon_billable_week_4(jsonObject.getString("nbw4"));
            tmModel.setNon_billable_week_5(jsonObject.getString("nbw5"));
            tmModel.setNon_billable_week_tot(jsonObject.getString("tnb"));

            tmModel.setTotal(jsonObject.getString("total"));
            monthlist.add(tmModel);
            Log.d("Month_view1", String.valueOf(jsonArray.length()));
        }
        loadRecyclerview_month();
    }

    private void loadRecyclerview_month() {
//        AndroidUtils.showAlert(teamList.toString(),getContext());
        String status = "Team Members";
        rv_time_sheets.setLayoutManager(new GridLayoutManager(getContext(), 1));
        try {
            if (date.equals("") || date.equals(null)) {
                Date new_date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
                month = sdf.format(new_date);
            } else {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date new_date = inputFormat.parse(date);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
                month = sdf.format(new_date);
                Log.d("Month_name.", month);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MonthlyTSAdapter teamMembersTSAdapter = new MonthlyTSAdapter(monthlist, status, month);
        rv_time_sheets.setAdapter(teamMembersTSAdapter);
        rv_time_sheets.setHasFixedSize(true);
        et_search_matter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                teamMembersTSAdapter.getFilter().filter(s);
            }

        });
        teamMembersTSAdapter.notifyDataSetChanged();

    }


    //Team_member_week_view
    private void loadTmData(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            TMModel tmModel = new TMModel();
            if (jsonObject.has("id")) {
                tmModel.setId(jsonObject.getString("id"));
            }
            tmModel.setName(jsonObject.getString("name"));
            tmModel.setTb(jsonObject.getString("tb"));
            tmModel.setTnb(jsonObject.getString("tnb"));
            tmModel.setTotal(jsonObject.getString("total"));
            teamList.add(tmModel);
        }

        loadRecyclerview();
    }

    private void loadRecyclerview() {
//        AndroidUtils.showAlert(teamList.toString(),getContext());
        String status = "Team Members";
        rv_time_sheets.setLayoutManager(new GridLayoutManager(getContext(), 1));
        TeamMembersTSAdapter teamMembersTSAdapter = new TeamMembersTSAdapter(teamList, status);
        rv_time_sheets.setAdapter(teamMembersTSAdapter);
        rv_time_sheets.setHasFixedSize(true);
        et_search_matter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                teamMembersTSAdapter.getFilter().filter(s);
            }

        });
        teamMembersTSAdapter.notifyDataSetChanged();

    }

}


