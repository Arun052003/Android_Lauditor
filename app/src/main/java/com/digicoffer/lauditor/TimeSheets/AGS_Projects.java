package com.digicoffer.lauditor.TimeSheets;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Adapters.ProjectAdapter;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectTMModel;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectsModel;
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

public class AGS_Projects extends Fragment implements AsyncTaskCompleteListener {
    private ListView sp_ags_project, sp_ags_tm;
    private TextInputEditText et_search_matter;
    private TextView tv_sp_project, tv_sp_team_member, project_id, team_member_id;
    private String date, isweek;
    private LinearLayout team_member_layout;
    private boolean ischecked_project = true, ischecked_team_member = true;
    private RecyclerView rv_projects;
    private TextView tv_billable_hours, tv_non_billable_hours, tv_total_project_hours, hours_id1, hours_id2, non_billable_id, billable_id, total_hours_id;
    private AlertDialog progress_dialog;
    private ArrayList<ProjectsModel> projectsList = new ArrayList<>();
    private ArrayList<ProjectsModel> updated_projectList = new ArrayList<>();
    private String selected_project;
    private ArrayList<ProjectTMModel> projectTmList = new ArrayList<>();
    private ArrayList<ProjectTMModel> updated_projectTmList = new ArrayList<>();
    private String selected_tm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ags_projects, container, false);
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        isweek = bundle.getString("isweek");
        sp_ags_project = view.findViewById(R.id.sp_ags_project);
        sp_ags_tm = view.findViewById(R.id.sp_ags_tm);
        sp_ags_project.setVisibility(View.GONE);
        sp_ags_tm.setVisibility(View.GONE);
        tv_sp_project = view.findViewById(R.id.tv_sp_project);
        tv_sp_team_member = view.findViewById(R.id.tv_sp_team_member);
        project_id = view.findViewById(R.id.project_id);
        team_member_layout = view.findViewById(R.id.team_member_layout);
        team_member_id = view.findViewById(R.id.team_member_id);
        project_id.setText(R.string.project);
        team_member_id.setText(R.string.team_members);
        tv_sp_team_member.setVisibility(View.GONE);
        team_member_layout.setVisibility(View.GONE);
        et_search_matter = view.findViewById(R.id.et_search_matter);
        rv_projects = view.findViewById(R.id.rv_projects);
        tv_billable_hours = view.findViewById(R.id.tv_billable_hours);
        hours_id1 = view.findViewById(R.id.hours_id1);
        hours_id2 = view.findViewById(R.id.hours_id2);
        non_billable_id = view.findViewById(R.id.non_billable_id);
        billable_id = view.findViewById(R.id.billable_id);
        total_hours_id = view.findViewById(R.id.total_hours_id);
        tv_non_billable_hours = view.findViewById(R.id.tv_non_billable_hours);
        tv_total_project_hours = view.findViewById(R.id.tv_total_project_hours);

        //..
        billable_id.setTextColor(getContext().getColor(R.color.Blue_text_color));
        non_billable_id.setTextColor(getContext().getColor(R.color.Blue_text_color));
        total_hours_id.setTextColor(getContext().getColor(R.color.Blue_text_color));
        tv_billable_hours.setTextColor(getContext().getColor(R.color.green_count_color));
        tv_non_billable_hours.setTextColor(getContext().getColor(R.color.green_count_color));
        hours_id1.setTextColor(getContext().getColor(R.color.green_count_color));
        hours_id2.setTextColor(getContext().getColor(R.color.green_count_color));
        tv_total_project_hours.setTextColor(getContext().getColor(R.color.green_count_color));
        non_billable_id.setText("Non Billable");
        billable_id.setText("Billable");
        total_hours_id.setText(R.string.total_hours);
        hours_id1.setText(R.string.hours);
        hours_id2.setText(R.string.hours);

        tv_sp_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_project, sp_ags_project);
                ischecked_project = !ischecked_project;
            }
        });
        tv_sp_team_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked_team_member, sp_ags_tm);
                ischecked_team_member = !ischecked_team_member;
            }
        });


        try {
            callProjectsWebService(isweek);
        } catch (ParseException e) {
            e.fillInStackTrace();
        }
        return view;
    }

    private void callProjectsWebService(String isweek) throws ParseException {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();

        if (isweek.equals("week")) {
            if (date.equals("") || date.equals(null)) {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/project-all-weekly", "Projects", postdata.toString());
            } else {
//            AndroidUtils.showToast(String.valueOf(date_status),getContext());
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
                Date new_date = inputFormat.parse(date);
                String outputDate = outputFormat.format(new_date);
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/project-all-weekly" + "-" + outputDate, "Projects", postdata.toString());

            }
        } else {
            if (date.equals("") || date.equals(null)) {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/project-all-monthly", "Projects", postdata.toString());
            } else {
//            AndroidUtils.showToast(String.valueOf(date_status),getContext());
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
                Date new_date = inputFormat.parse(date);
                String outputDate = outputFormat.format(new_date);//https://apidev2.digicoffer.com/professional/matter/timesheets/tms-all-weekly
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/timesheets/project-all-monthly" + "-" + outputDate, "Projects", postdata.toString());
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

                if (httpResult.getRequestType().equals("Projects")) {
                    JSONObject jsonObject = result.getJSONObject("timesheets");
                    JSONObject grandtotal = jsonObject.getJSONObject("grandTotal");
                    tv_billable_hours.setText(grandtotal.getString("billable"));
                    tv_non_billable_hours.setText(grandtotal.getString("nonbillable"));
                    tv_total_project_hours.setText(grandtotal.getString("total"));
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    loadProjects(jsonArray);
//                    loadtmProjects();
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
    }
//    private void loadtmProjects() throws JSONException {
//        for (int i = 0; i < projectsList.size(); i++) {
//            for (int j = 0; j < projectsList.get(i).getTeamMembers().length(); j++) {
//                JSONObject jsonObject = projectsList.get(i).getTeamMembers().getJSONObject(j);
//                ProjectTMModel projectTMModel = new ProjectTMModel();
//                projectTMModel.setBillableHours(jsonObject.getString("billableHours"));
//                projectTMModel.setName(jsonObject.getString("name"));
//                projectTMModel.setNonBillablehours(jsonObject.getString("nonBillablehours"));
//                projectTMModel.setTotal(jsonObject.getString("total"));
//                projectTmList.add(projectTMModel);
//            }
//        }
//        loadRecyclerview(projectsList, projectTmList);
//    }

    private void loadProjects(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProjectsModel projectsModel = new ProjectsModel();
            projectsModel.setCaseNo(jsonObject.getString("caseNo"));
            projectsModel.setProjectName(jsonObject.getString("projectName"));
            projectsModel.setClientNames(jsonObject.getJSONArray("clientNames"));
            projectsModel.setMatterId(jsonObject.getString("matterId"));
            projectsModel.setTeamMembers(jsonObject.getJSONArray("teamMembers"));
            projectsList.add(projectsModel);
        }
        loadProjectsRecyclerview();
    }

    private void loadProjectsRecyclerview() throws JSONException {
        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), projectsList);
        sp_ags_project.setAdapter(spinner_adapter);
        sp_ags_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                projectTmList.clear();
                updated_projectList.clear();
                selected_project = projectsList.get(position).getMatterId();
                String selected_project_name = projectsList.get(position).getProjectName();
                tv_sp_project.setText(selected_project_name);
                selected_tm = "";
                tv_sp_team_member.setText(selected_tm);
                try {
                    for (int i = 0; i < projectsList.size(); i++) {
                        if (projectsList.get(i).getMatterId().equals(selected_project)) {
                            for (int j = 0; j < projectsList.get(i).getTeamMembers().length(); j++) {
                                JSONObject jsonObject = projectsList.get(i).getTeamMembers().getJSONObject(j);
                                ProjectTMModel projectTMModel = new ProjectTMModel();
                                projectTMModel.setBillableHours(jsonObject.getString("billableHours"));
                                projectTMModel.setName(jsonObject.getString("name"));
                                projectTMModel.setNonBillablehours(jsonObject.getString("nonBillablehours"));
                                projectTMModel.setTotal(jsonObject.getString("total"));
                                projectTmList.add(projectTMModel);
                            }
                        }
                    }
                    for (int i = 0; i < projectsList.size(); i++) {
                        if (projectsList.get(i).getMatterId().equals(selected_project)) {
                            ProjectsModel projectsModel = projectsList.get(i);
                            updated_projectList.add(projectsModel);
                        }
                    }
                    loadRecyclerview(updated_projectList, projectTmList);

                    final CommonSpinnerAdapter status_adapter = new CommonSpinnerAdapter((Activity) getContext(), projectTmList);
                    sp_ags_tm.setAdapter(status_adapter);
                    sp_ags_tm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selected_tm = projectTmList.get(position).getName();
                            updated_projectTmList.clear();
                            tv_sp_team_member.setText(selected_tm);
//                          AndroidUtils.showToast(selected_project,getContext());
//                            loadRecyclerview();
                            sp_ags_tm.setVisibility(View.GONE);
                            ischecked_team_member = true;
                            for (int i = 0; i < projectTmList.size(); i++) {
                                if (projectTmList.get(i).getName().equals(selected_tm)) {
                                    ProjectTMModel projectsModel = projectTmList.get(i);
                                    updated_projectTmList.add(projectsModel);
                                }
                            }
                            loadRecyclerview(updated_projectList, updated_projectTmList);
                        }
                    });
//                    AndroidUtils.showAlert(updated_projectList.toArray().toString(),getContext());
                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
                sp_ags_project.setVisibility(View.GONE);
                ischecked_project = true;
                team_member_layout.setVisibility(View.VISIBLE);
                tv_sp_team_member.setVisibility(View.VISIBLE);
            }
        });
//        sp_ags_project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//               projectTmList.clear();
//               updated_projectList.clear();
//                selected_project = projectsList.get(adapterView.getSelectedItemPosition()).getMatterId();
//                try {
//                    for (int i = 0; i < projectsList.size(); i++) {
//                        if (projectsList.get(i).getMatterId().equals(selected_project)) {
//                            for(int j=0;j<projectsList.get(i).getTeamMembers().length();j++) {
//                                JSONObject jsonObject = projectsList.get(i).getTeamMembers().getJSONObject(j);
//                                ProjectTMModel projectTMModel = new ProjectTMModel();
//                                projectTMModel.setBillableHours(jsonObject.getString("billableHours"));
//                                projectTMModel.setName(jsonObject.getString("name"));
//                                projectTMModel.setNonBillablehours(jsonObject.getString("nonBillablehours"));
//                                projectTMModel.setTotal(jsonObject.getString("total"));
//                                projectTmList.add(projectTMModel);
//                            }
//                        }
//                    }
//                    for (int i = 0; i < projectsList.size(); i++) {
//                        if (projectsList.get(i).getMatterId().equals(selected_project)) {
//                            ProjectsModel projectsModel = projectsList.get(i);
//                            updated_projectList.add(projectsModel);
//                        }
//                    }
//                    final CommonSpinnerAdapter status_adapter = new CommonSpinnerAdapter((Activity) getContext(), projectTmList);
//                    sp_ags_tm.setAdapter(status_adapter);
//                    sp_ags_tm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            selected_tm = projectTmList.get(adapterView.getSelectedItemPosition()).getName();
////                          AndroidUtils.showToast(selected_project,getContext());
//                            loadRecyclerview();
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });
////                    AndroidUtils.showAlert(updated_projectList.toArray().toString(),getContext());
//                } catch (JSONException e) {
//                    e.fillInStackTrace();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
////        AndroidUtils.showAlert(projectTmList.toString(),getContext());


    }

    private void loadRecyclerview(ArrayList<ProjectsModel> updated_projectlist, ArrayList<ProjectTMModel> updated_projecttmList) {
        rv_projects.setLayoutManager(new GridLayoutManager(getContext(), 1));
        ProjectAdapter projectAdapter = new ProjectAdapter(updated_projectlist, updated_projecttmList, getContext(), selected_project);
        rv_projects.setAdapter(projectAdapter);
        rv_projects.setHasFixedSize(true);
//        rv_projects.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//
//                if (lastVisibleItemPosition == projectAdapter.getItemCount() - 1) {
//                    projectAdapter.loadMoreItems();
//                }
//            }
//        });
        et_search_matter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                projectAdapter.getFilter().filter(s);
            }

        });

        projectAdapter.notifyDataSetChanged();
    }

}
