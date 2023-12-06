package com.digicoffer.lauditor.Matter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Matter.Adapters.GroupsAdapter;
import com.digicoffer.lauditor.Matter.Models.AdvocateModel;
import com.digicoffer.lauditor.Matter.Models.ClientsModel;
import com.digicoffer.lauditor.Matter.Models.DocumentsModel;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.Matter.Models.TeamModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.minidns.record.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GCT extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {

    TextView matter_date, at_add_groups, at_add_clients, at_assigned_team_members,add_groups,add_clients,tv_assigned_team_members;
    boolean[] selectedLanguage;
    boolean[] selectedClients;
    boolean[] selectedTM;
    ArrayList<MatterModel> matterArraylist;
    JSONArray exisiting_group_acls;
    String matter_title, case_number, case_type, description, dof,start_date,end_date, court, judge, case_priority, case_status;
    JSONArray existing_clients;
    JSONArray existing_members;
    CardView cv_client_details;
    MatterInformation matterInformation;

    ArrayList<DocumentsModel> selected_documents_list = new ArrayList<>();
    ArrayList<AdvocateModel> advocates_list = new ArrayList<>();
    JSONArray existing_groups_list;
    JSONArray existing_clients_list;
    JSONArray existing_tm_list;
    JSONArray existing_documents;
    JSONArray existing_documents_list;
    String ADAPTER_TAG = "Groups";
    Button btn_add_groups, btn_add_clients, btn_assigned_team_members, btn_create;
    LinearLayout ll_selected_groups, ll_selected_clients, ll_assigned_team_members, selected_groups, selected_clients, selected_tm,ll_add_clients,ll_assign_team_members;
    AlertDialog progress_dialog;
    ArrayList<GroupsModel> selected_groups_list = new ArrayList<>();
    ArrayList<GroupsModel> updated_groups_list = new ArrayList<>();
    ArrayList<ClientsModel> selected_clients_list = new ArrayList<>();
    ArrayList<TeamModel> selected_tm_list = new ArrayList<>();
    ArrayList<GroupsModel> groupsList = new ArrayList<>();
    ArrayList<DocumentsModel> documentsList = new ArrayList<>();
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<ViewMatterModel> new_groupsList = new ArrayList<>();
    ArrayList<TeamModel> tmList = new ArrayList<>();
    Matter matter;


    private JSONArray existing_opponents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gct_layout, container, false);
        matter_date = view.findViewById(R.id.matter_date);
        at_add_groups = view.findViewById(R.id.at_add_groups);
        at_add_groups.setOnClickListener(this);
        add_groups = view.findViewById(R.id.add_groups);
        add_groups.setText("Add Groups");
        add_clients = view.findViewById(R.id.add_clients);
        add_clients.setText("Add Clients");
        cv_client_details = view.findViewById(R.id.cv_client_details);
        tv_assigned_team_members = view.findViewById(R.id.tv_assigned_team_members);
        tv_assigned_team_members.setText("Assign Team Members");
        at_add_clients = view.findViewById(R.id.at_add_clients);
        at_add_clients.setOnClickListener(this);
        at_assigned_team_members = view.findViewById(R.id.at_assigned_team_members);
        at_assigned_team_members.setOnClickListener(this);
        btn_add_groups = view.findViewById(R.id.btn_add_groups);
        btn_add_groups.setText("Add");
        selected_groups = view.findViewById(R.id.selected_groups);
        selected_clients = view.findViewById(R.id.selected_clients);
        selected_tm = view.findViewById(R.id.selected_tm);
        btn_add_groups.setOnClickListener(this);
        btn_add_clients = view.findViewById(R.id.btn_add_clients);
        btn_add_clients.setText("Add");
        btn_add_clients.setOnClickListener(this);
        ll_add_clients = view.findViewById(R.id.ll_add_clients);
        ll_add_clients.setVisibility(View.GONE);
        ll_assign_team_members = view.findViewById(R.id.ll_assign_team_members);
        ll_assign_team_members.setVisibility(View.GONE);

        btn_create = view.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);
        btn_assigned_team_members = view.findViewById(R.id.btn_assigned_team_members);
        btn_assigned_team_members.setText("Add");
        btn_assigned_team_members.setOnClickListener(this);
        ll_selected_groups = view.findViewById(R.id.ll_selected_groups);
        ll_assigned_team_members = view.findViewById(R.id.ll_assigned_team_members);
        ll_selected_clients = view.findViewById(R.id.ll_selected_clients);
        Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                matter_date.setText(sdf.format(myCalendar.getTime()));
            }
        };
        matter_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        matter_date.setText(formattedDate);
        matter = (Matter) getParentFragment();
        matterArraylist = matter.getMatter_arraylist();
        if (matterArraylist.size() != 0) {
            for (int i = 0; i < matterArraylist.size(); i++) {
                MatterModel matterModel = matterArraylist.get(i);
                if (matterModel.getGroup_acls()!=null){
                exisiting_group_acls = matterModel.getGroup_acls();
                try {
                    for (int g = 0; g < exisiting_group_acls.length(); g++) {
                        GroupsModel groupsModel = new GroupsModel();
                        JSONObject jsonObject = exisiting_group_acls.getJSONObject(g);
                        groupsModel.setGroup_id(jsonObject.getString("id"));
                        groupsModel.setGroup_name(jsonObject.getString("name"));
                        groupsModel.setChecked(jsonObject.getBoolean("isChecked"));
                        selected_groups_list.add(groupsModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
                if(matterModel.getClients()!=null){
                    existing_clients = matterModel.getClients();
                    try{
                        for (int p = 0; p < existing_clients.length(); p++) {
                            ClientsModel clientsModel = new ClientsModel();
                            JSONObject jsonObject = existing_clients.getJSONObject(p);
                            clientsModel.setClient_id(jsonObject.getString("id"));
                            clientsModel.setClient_name(jsonObject.getString("name"));
                            clientsModel.setClient_type(jsonObject.getString("type"));
                            selected_clients_list.add(clientsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(matterModel.getMembers()!=null) {
                    existing_members = matterModel.getMembers();
                    try{
                        for (int t = 0; t < existing_members.length(); t++) {
                            TeamModel teamModel = new TeamModel();
                            JSONObject jsonObject = existing_members.getJSONObject(t);
                            teamModel.setTm_id(jsonObject.getString("id"));
                            teamModel.setTm_name(jsonObject.getString("name"));
                            teamModel.setUser_id(jsonObject.getString("user_id"));
                            selected_tm_list.add(teamModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(matterModel.getGroups_list()!=null){
                    existing_groups_list = matterModel.getGroups_list();
                    try{
                        for (int m = 0; m < existing_groups_list.length(); m++) {
                            GroupsModel groupsModel = new GroupsModel();
                            JSONObject jsonObject = existing_groups_list.getJSONObject(m);
                            groupsModel.setGroup_id(jsonObject.getString("id"));
                            groupsModel.setGroup_name(jsonObject.getString("name"));
                            groupsList.add(groupsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(matterModel.getClients_list()!=null){
                    existing_clients_list = matterModel.getClients_list();
                    try{
                        for (int n = 0; n < existing_clients_list.length(); n++) {
                            ClientsModel clientsModel = new ClientsModel();
                            JSONObject jsonObject = existing_clients_list.getJSONObject(n);
                            clientsModel.setClient_id(jsonObject.getString("id"));
                            clientsModel.setClient_name(jsonObject.getString("name"));
                            clientsModel.setClient_type(jsonObject.getString("type"));
                            clientsList.add(clientsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (matterModel.getMembers_list()!=null){
                    existing_tm_list = matterModel.getMembers_list();
                    try{

                        for (int d = 0; d < existing_tm_list.length(); d++) {
                            TeamModel teamModel = new TeamModel();
                            JSONObject jsonObject = existing_tm_list.getJSONObject(d);
                            teamModel.setTm_id(jsonObject.getString("id"));
                            teamModel.setTm_name(jsonObject.getString("name"));
                            teamModel.setUser_id(jsonObject.getString("user_id"));
                            tmList.add(teamModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (matterArraylist.get(i).getOpponent_advocate() != null) {
                    existing_opponents = matterArraylist.get(i).getOpponent_advocate();
                    try {
                        for (int j = 0; j < existing_opponents.length(); j++) {
                            try {
                                JSONObject jsonObject = existing_opponents.getJSONObject(j);
                                AdvocateModel advocateModel = new AdvocateModel();
                                advocateModel.setAdvocate_name(jsonObject.getString("name"));
                                advocateModel.setNumber(jsonObject.getString("phone"));
                                advocateModel.setEmail(jsonObject.getString("email"));
                                advocates_list.add(advocateModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (matterModel.getDocuments() != null) {
                    try {
                        existing_documents = matterModel.getDocuments();
                        for (int d = 0; d < existing_documents.length(); d++) {
                            DocumentsModel documentsModel = new DocumentsModel();
                            JSONObject jsonObject = existing_documents.getJSONObject(d);
                            documentsModel.setDocid(jsonObject.getString("docid"));
                            documentsModel.setName(jsonObject.getString("name"));
                            documentsModel.setUser_id(jsonObject.getString("user_id"));
                            documentsModel.setDoctype(jsonObject.getString("doctype"));
                            selected_documents_list.add(documentsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (matterModel.getDocuments_list() != null) {
                    try {
                        existing_documents_list = matterModel.getDocuments_list();
                        for (int ed = 0; ed < existing_documents_list.length(); ed++) {
                            DocumentsModel documentsModel = new DocumentsModel();
                            JSONObject jsonObject = existing_documents_list.getJSONObject(ed);
                            documentsModel.setDocid(jsonObject.getString("docid"));
                            documentsModel.setName(jsonObject.getString("name"));
                            documentsModel.setUser_id(jsonObject.getString("user_id"));
                            documentsModel.setDoctype(jsonObject.getString("doctype"));
                            documentsList.add(documentsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                    if (matterModel.getMatter_title()!=null){
                    matter_title = matterModel.getMatter_title();
                }
                if(matterModel.getCase_number()!=null){
                    case_number = matterModel.getCase_number();
                }
                if(matterModel.getCase_type()!=null){
                    case_type = matterModel.getCase_type();
                }
               if(matterModel.getDescription()!=null){
                   description = matterModel.getDescription();
               }
                if(matterModel.getDate_of_filing()!=null){
                    dof =matterModel.getDate_of_filing();
                }
                if (matterModel.getStart_date()!=null){
                    start_date = matterModel.getStart_date();
                }
                if (matterModel.getEnd_date()!=null){
                    end_date = matterModel.getEnd_date();
                }
                if (matterModel.getCourt()!=null){
                    court = matterModel.getCourt();
                }
                 if(matterModel.getJudge()!=null){
                     judge = matterModel.getJudge();
                 }
                 if(matterModel.getCase_priority()!=null){
                     case_priority = matterModel.getCase_priority();
                 }
                if(matterModel.getStatus()!=null){
                    case_status = matterModel.getStatus();
                }
                try {

                    if (selected_groups_list.size() != 0) {
//                    callGroupsWebservice();
                        loadSelectedGroups();
                    }
                    if (selected_clients_list.size() != 0) {
//                    callClientsWebservice();
                        loadSelectedClients();
                    }
                    if (selected_tm_list.size() != 0) {
//                    callTMWebservice();
                        loadSelectedTM();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
//        callGroupsWebservice();
        return view;
    }

    private void callGroupsWebservice() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", postdata.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_groups:
                if (groupsList.size() == 0) {
                    callGroupsWebservice();
                } else {
                    GroupsPopup();
                }
                break;
            case R.id.btn_add_clients:
                if (clientsList.size() == 0) {
                    callClientsWebservice();
                } else {
                    ClientsPopUp();
                }

                break;
            case R.id.btn_assigned_team_members:
                if (tmList.size() == 0) {
                    callTMWebservice();
                } else {
                    TeamPopUp();
                }
                break;
            case R.id.btn_create:
                cv_client_details.setVisibility(View.GONE);
                saveGCTinformation();
                break;
        }
    }

    private void saveGCTinformation() {
        if (selected_groups_list.size() == 0) {
            AndroidUtils.showToast("Please select atealst one group", getContext());
        } else if (selected_clients_list.size() == 0) {
            AndroidUtils.showToast("Please select atleast one client", getContext());
        } else if (selected_tm_list.size() == 0) {
            AndroidUtils.showToast("Please Assign atleast one Team Member", getContext());
        } else {
            try {
                JSONArray clients = new JSONArray();
                JSONArray group_acls = new JSONArray();
                JSONArray members = new JSONArray();
                JSONArray new_groups_list = new JSONArray();
                JSONArray new_clients_list = new JSONArray();
                JSONArray new_tm_list = new JSONArray();
                JSONArray documents = new JSONArray();
                JSONArray new_documents_list = new JSONArray();
//                JSONArray advocates_list = new JSONArray();
                MatterModel matterModel = new MatterModel();
                for (int i = 0; i < selected_groups_list.size(); i++) {
                    try {
                        GroupsModel groupsModel = selected_groups_list.get(i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", groupsModel.getGroup_id());
                        jsonObject.put("name", groupsModel.getGroup_name());
                        jsonObject.put("isChecked", groupsModel.isChecked());
                        group_acls.put(jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < groupsList.size(); i++) {
                    try {
                        GroupsModel groupsModel = groupsList.get(i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", groupsModel.getGroup_id());
                        jsonObject.put("name", groupsModel.getGroup_name());
                        new_groups_list.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < selected_clients_list.size(); i++) {
                    try {
                        ClientsModel clientsModel = selected_clients_list.get(i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", clientsModel.getClient_id());
                        jsonObject.put("type", clientsModel.getClient_type());
                        jsonObject.put("name", clientsModel.getClient_name());
                        clients.put(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                for (int i = 0; i < clientsList.size(); i++) {
                    ClientsModel clientsModel = clientsList.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", clientsModel.getClient_id());
                    jsonObject.put("type", clientsModel.getClient_type());
                    jsonObject.put("name", clientsModel.getClient_name());
                    new_clients_list.put(jsonObject);
                }
                for (int i = 0; i < selected_tm_list.size(); i++) {
                    try {
                        TeamModel teamModel = selected_tm_list.get(i);
                        JSONObject team_object = new JSONObject();
                        team_object.put("id", teamModel.getTm_id());
                        team_object.put("name", teamModel.getTm_name());
                        team_object.put("user_id", teamModel.getUser_id());
//                        team_object.put("")
                        members.put(team_object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < tmList.size(); i++) {
                    try {
                        TeamModel teamModel = tmList.get(i);
                        JSONObject team_object = new JSONObject();
                        team_object.put("id", teamModel.getTm_id());
                        team_object.put("name", teamModel.getTm_name());
                        team_object.put("user_id", teamModel.getUser_id());
//                        team_object.put("")
                        new_tm_list.put(team_object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JSONArray jsonArray = new JSONArray();
                try {
                    for (int i = 0; i < advocates_list.size(); i++) {
                        AdvocateModel advocateModel = advocates_list.get(i);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", advocateModel.getAdvocate_name());
                        jsonObject.put("email", advocateModel.getEmail());
                        jsonObject.put("phone", advocateModel.getNumber());
                        jsonArray.put(jsonObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int d = 0; d < selected_documents_list.size(); d++) {
                    DocumentsModel documentsModel = selected_documents_list.get(d);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("docid", documentsModel.getDocid());
                    jsonObject.put("doctype", documentsModel.getDoctype());
                    jsonObject.put("user_id", documentsModel.getUser_id());
                    jsonObject.put("name", documentsModel.getName());
                    documents.put(jsonObject);
                }
                for (int e = 0; e< documentsList.size(); e++) {
                    DocumentsModel documentsModel = documentsList.get(e);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("docid", documentsModel.getDocid());
                    jsonObject.put("doctype", documentsModel.getDoctype());
                    jsonObject.put("user_id", documentsModel.getUser_id());
                    jsonObject.put("name", documentsModel.getName());
                    new_documents_list.put(jsonObject);
                }
                matterModel.setMatter_title(matter_title);
                matterModel.setCase_number(case_number);
                matterModel.setCase_type(case_type);
                matterModel.setDescription(description);
                matterModel.setDate_of_filing(dof);
                matterModel.setStart_date(start_date);
                matterModel.setEnd_date(end_date);
                matterModel.setCourt(court);
                matterModel.setJudge(judge);
                matterModel.setCase_priority(case_priority);
                matterModel.setStatus(case_status);
                matterModel.setClients(clients);
                matterModel.setGroup_acls(group_acls);
                matterModel.setMembers(members);
                matterModel.setGroups_list(new_groups_list);
                matterModel.setClients_list(new_clients_list);
                matterModel.setMembers_list(new_tm_list);
                matterModel.setOpponent_advocate(jsonArray);
                matterModel.setDocuments(documents);
                matterModel.setDocuments_list(new_documents_list);
                matterArraylist.set(0, matterModel);
                matter.loadDocuments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void TeamPopUp() {
        try {

            for (int i = 0; i < tmList.size(); i++) {
                for (int j = 0; j < selected_tm_list.size(); j++) {
                    if (tmList.get(i).getTm_id().matches(selected_tm_list.get(j).getTm_id())) {
                        TeamModel teamModel = tmList.get(i);
                        teamModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                    }
                }
            }
            selected_tm_list.clear();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.groups_list_adapter, null);
            RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
            ImageView iv_cancel = view.findViewById(R.id.close_groups);
            iv_cancel.setImageResource(R.drawable.cancel_icon);
            AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
            TextView header_name_group=view.findViewById(R.id.header_name_group);
            header_name_group.setText("Select Team Member");
            AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_groups.setLayoutManager(layoutManager);
            rv_groups.setHasFixedSize(true);
            ADAPTER_TAG = "TM";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
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
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getTmList().size(); i++) {
                        TeamModel teamModel = documentsAdapter.getTmList().get(i);
                        if (teamModel.isChecked()) {
                            selected_tm_list.add(teamModel);
                            ll_add_clients.setVisibility(View.VISIBLE);
                            //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                        }
                    }


                    loadSelectedTM();
//                    loadSelectedClients();
//                    loadSelectedGroups();
                    dialog.dismiss();
                }

            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void callTMWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONArray group_acls = new JSONArray();
            JSONObject postdata = new JSONObject();
            for (int i = 0; i < selected_groups_list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                GroupsModel groupsModel = selected_groups_list.get(i);
                group_acls.put(groupsModel.getGroup_id());
            }
            postdata.put("group_acls", group_acls);
            postdata.put("attachment_type", "members");
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/attachments", "Members", postdata.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callClientsWebservice() {
        try {


            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONArray group_acls = new JSONArray();
            JSONObject postdata = new JSONObject();
            for (int i = 0; i < selected_groups_list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                GroupsModel groupsModel = selected_groups_list.get(i);
                group_acls.put(groupsModel.getGroup_id());
            }
            postdata.put("group_acls", group_acls);
            postdata.put("attachment_type", "clients");
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/attachments", "Clients", postdata.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadGroupsData(data);
                } else if (httpResult.getRequestType().equals("Clients")) {
                    JSONArray clients = result.getJSONArray("clients");
                    loadClients(clients);
                } else if (httpResult.getRequestType().equals("Members")) {
                    JSONArray members = result.getJSONArray("members");
                    loadMembers(members);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMembers(JSONArray members) {
        try {
            for (int i = 0; i < members.length(); i++) {
                JSONObject jsonObject = members.getJSONObject(i);
                TeamModel teamModel = new TeamModel();
                teamModel.setTm_id(jsonObject.getString("id"));
                teamModel.setTm_name(jsonObject.getString("name"));
                teamModel.setUser_id(jsonObject.getString("user_id"));
                tmList.add(teamModel);
                selectedTM = new boolean[tmList.size()];

            }
            TeamPopUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClients(JSONArray clients) {
        try {
            for (int i = 0; i < clients.length(); i++) {
                JSONObject jsonObject = clients.getJSONObject(i);
                ClientsModel clientsModel = new ClientsModel();
                clientsModel.setClient_id(jsonObject.getString("id"));
                clientsModel.setClient_name(jsonObject.getString("name"));
                clientsModel.setClient_type(jsonObject.getString("type"));
                clientsList.add(clientsModel);
                selectedClients = new boolean[clientsList.size()];

            }
            ClientsPopUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ClientsPopUp() {
        try {

            for (int i = 0; i < clientsList.size(); i++) {
                for (int j = 0; j < selected_clients_list.size(); j++) {
                    if (clientsList.get(i).getClient_id().matches(selected_clients_list.get(j).getClient_id())) {
                        ClientsModel clientsModel = clientsList.get(i);
                        clientsModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                    }
                }
            }
            selected_clients_list.clear();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.groups_list_adapter, null);
            RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
            ImageView iv_cancel = view.findViewById(R.id.close_groups);
            iv_cancel.setImageResource(R.drawable.cancel_icon);
            AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
            TextView header_name_group=view.findViewById(R.id.header_name_group);
            header_name_group.setText("Select Client");
            AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);

           LinearLayout ll_add_clients = view.findViewById(R.id.ll_add_clients);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_groups.setLayoutManager(layoutManager);
            rv_groups.setHasFixedSize(true);
            ADAPTER_TAG = "Clients";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
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
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getClientsList_item().size(); i++) {
                        ClientsModel clientsModel = documentsAdapter.getClientsList_item().get(i);
                        if (clientsModel.isChecked()) {
                            selected_clients_list.add(clientsModel);
                            ll_assign_team_members.setVisibility(View.VISIBLE);

                            //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                        }
                    }


                    loadSelectedClients();
//                    loadSelectedGroups();
                    dialog.dismiss();
                }

            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadSelectedTM() {
        String[] value = new String[selected_tm_list.size()];
        for (int i = 0; i < selected_tm_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_tm_list.get(i).getTm_name();

        }

        String str = String.join(",", value);
        at_assigned_team_members.setText(str);
        selected_tm.setVisibility(View.VISIBLE);
        ll_assigned_team_members.removeAllViews();
        for (int i = 0; i < selected_tm_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_tm_list.get(i).getTm_name());
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
                            v = ll_assigned_team_members.getChildAt(position);
                            ll_assigned_team_members.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            TeamModel teamModel = selected_tm_list.get(position);
                            teamModel.setChecked(false);
                            selected_tm_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_tm_list.size()];
                            for (int i = 0; i < selected_tm_list.size(); i++) {
                                value[i] = selected_tm_list.get(i).getTm_name();
                            }

                            String str = String.join(",", value);
                            at_assigned_team_members.setText(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);

            ll_assigned_team_members.addView(view_opponents);
        }
    }

    private void loadSelectedClients() {
        String[] value = new String[selected_clients_list.size()];
        for (int i = 0; i < selected_clients_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_clients_list.get(i).getClient_name();

        }

        String str = String.join(",", value);
        at_add_clients.setText(str);
        selected_clients.setVisibility(View.VISIBLE);
        ll_selected_clients.removeAllViews();
        for (int i = 0; i < selected_clients_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_clients_list.get(i).getClient_name());
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
                            v = ll_selected_clients.getChildAt(position);
                            ll_selected_clients.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            ClientsModel clientsModel = selected_clients_list.get(position);
                            clientsModel.setChecked(false);
                            selected_clients_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_clients_list.size()];
                            for (int i = 0; i < selected_clients_list.size(); i++) {
                                value[i] = selected_clients_list.get(i).getClient_name();
                            }

                            String str = String.join(",", value);
                            at_add_clients.setText(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_selected_clients.addView(view_opponents);
        }
    }


    private void loadGroupsData(JSONArray data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                GroupsModel groupsModel = new GroupsModel();
                groupsModel.setGroup_id(jsonObject.getString("id"));
                groupsModel.setGroup_name(jsonObject.getString("name"));
                groupsList.add(groupsModel);
            }
            selectedLanguage = new boolean[groupsList.size()];
//            GroupsAlert();
            GroupsPopup();
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void GroupsPopup() {
        try {

            for (int i = 0; i < groupsList.size(); i++) {
                for (int j = 0; j < selected_groups_list.size(); j++) {
                    if (groupsList.get(i).getGroup_id().matches(selected_groups_list.get(j).getGroup_id())) {
                        GroupsModel groupsModel = groupsList.get(i);
                        groupsModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                    }
                }
            }
            selected_groups_list.clear();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.groups_list_adapter, null);
            RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
            ImageView iv_cancel = view.findViewById(R.id.close_groups);
            iv_cancel.setImageResource(R.drawable.cancel_icon);
            AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
            AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_groups.setLayoutManager(layoutManager);
            rv_groups.setHasFixedSize(true);
            ADAPTER_TAG = "Groups";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
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
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                        GroupsModel groupsModel = documentsAdapter.getList_item().get(i);
                        if (groupsModel.isChecked()) {
                            selected_groups_list.add(groupsModel);
                            ll_add_clients.setVisibility(View.VISIBLE);

                            //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                        }
                    }

                    detectListChanges();


                    loadSelectedGroups();
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void detectListChanges() {
        updated_groups_list.addAll(selected_groups_list);
        int originalSize = updated_groups_list.size();
        updated_groups_list.removeAll(selected_groups_list);
        int newSize = updated_groups_list.size();
        if (newSize > originalSize || newSize < originalSize) {
            clientsList.clear();
            tmList.clear();
            selected_clients_list.clear();
            selected_tm_list.clear();
            ll_selected_clients.removeAllViews();
            selected_clients.setVisibility(View.GONE);
            ll_assigned_team_members.removeAllViews();
            selected_tm.setVisibility(View.GONE);
            // items have been added to the list
        } else if (newSize == originalSize) {
            // items have been removed from the list
        } else if (newSize == 0 || originalSize == 0) {
            selected_groups.setVisibility(View.GONE);
            ll_selected_groups.removeAllViews();
        } else {
            // the list has not changed in size
        }
        at_add_clients.setText("");
        at_assigned_team_members.setText("");
    }


    private void loadSelectedGroups() {
        String[] value = new String[selected_groups_list.size()];
        for (int i = 0; i < selected_groups_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_groups_list.get(i).getGroup_name();

        }

        String str = String.join(",", value);
        at_add_groups.setText(str);
        selected_groups.setVisibility(View.VISIBLE);
        ll_selected_groups.removeAllViews();
        if (selected_groups_list.size() == 0) {
            clientsList.clear();
            selected_clients_list.clear();
            ll_selected_clients.removeAllViews();
            tmList.clear();
            selected_tm_list.clear();
            ll_assigned_team_members.removeAllViews();
            at_add_groups.setText("Select Groups");
            at_add_clients.setText("Select Clients");
            at_assigned_team_members.setText("Assign Team Members");
            selected_groups.setVisibility(View.GONE);
            selected_clients.setVisibility(View.GONE);
            selected_tm.setVisibility(View.GONE);

        }
        for (int i = 0; i < selected_groups_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_groups_list.get(i).getGroup_name());
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
                            v = ll_selected_groups.getChildAt(position);
                            ll_selected_groups.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            GroupsModel groupsModel = selected_groups_list.get(position);
                            groupsModel.setChecked(false);
                            selected_groups_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_groups_list.size()];
                            for (int i = 0; i < selected_groups_list.size(); i++) {
                                value[i] = selected_groups_list.get(i).getGroup_name();
                            }
                            detectListChanges();
                            String str = String.join(",", value);
                            at_add_groups.setText(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_selected_groups.addView(view_opponents);
        }
    }

}
