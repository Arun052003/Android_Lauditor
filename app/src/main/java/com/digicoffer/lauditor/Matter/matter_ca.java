package com.digicoffer.lauditor.Matter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link matter_ca#newInstance} factory method to
 * create an instance of this fragment.
 */

public class matter_ca  extends  Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    TextView matter_date, at_add_groups, at_add_clients, at_assigned_team_members,add_groups,add_clients,tv_assigned_team_members,tv_selected_clients;
    boolean[] selectedLanguage;
    boolean[] selectedClients;
    boolean[] selectedTM;
    ViewMatterModel viewMatterModel1;
    String grp_name;
    String grp_chk;
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<MatterModel> matterArraylist;
    JSONArray exisiting_group_acls;
    TextView matter_title_tv ;
    ConstraintLayout cv_details;
    TextView matter_title, case_number, case_type, description, dof,start_date,end_date, court, judge, case_priority, case_status;
    JSONArray existing_clients;

    JSONArray existing_members;
    CardView cv_client_details;
    LinearLayout ll_add_groups;
    MatterInformation matterInformation;

    ArrayList<DocumentsModel> selected_documents_list = new ArrayList<>();
    ArrayList<AdvocateModel> advocates_list = new ArrayList<>();
    JSONArray existing_groups_list;
    JSONArray existing_clients_list;
    JSONArray existing_tm_list;
    JSONArray existing_documents;
    String  tm_name;
    String user_id;
    JSONArray existing_documents_list;
    String ADAPTER_TAG = "Groups";
    Button btn_add_groups, btn_add_clients, btn_assigned_team_members, btn_create,btn_cancel_save;
    LinearLayout ll_selected_groups, ll_selected_clients, ll_assigned_team_members, selected_groups, selected_clients, selected_tm,ll_add_clients,ll_assign_team_members;
    AlertDialog progress_dialog;
    JSONArray getGroups_list;
    ArrayList<GroupsModel> selected_groups_list = new ArrayList<>();
    ArrayList<GroupsModel> updated_groups_list = new ArrayList<>();
    ArrayList<ClientsModel> selected_clients_list = new ArrayList<>();
    LinearLayout upload_group_layout, upload_client_layout, upload_tm_layout ;
    ArrayList<TeamModel> selected_tm_list = new ArrayList<>();
    ArrayList<GroupsModel> groupsList = new ArrayList<>();
    boolean ischecked_group = true;
    LinearLayoutCompat ll_save_buttons;
    boolean ischecked_client = true;
    String  ad_email;
    boolean ischecked_tm = true;
    String   ad_name;

    ArrayList<DocumentsModel> documentsList = new ArrayList<>();

    ArrayList<ViewMatterModel> new_groupsList = new ArrayList<>();
    ArrayList<TeamModel> tmList = new ArrayList<>();
    Matter matter;
    Matter mattermodel;

    String grp_id;
    String  tm_id;
    String  cln_id;
    String  cln_name;
    String cln_type;
    String ad_phone;

    ViewMatter viewMatter1;
    EditMatterTimeline editMatterTimeline1;
    RecyclerView rv_display_upload_groups_docs,rv_display_upload_client_docs,rv_display_upload_tm_docs;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private JSONArray existing_opponents;
    public matter_ca(ViewMatterModel viewMatterModel, EditMatterTimeline editMatterTimeline) {
        viewMatterModel1 = viewMatterModel;
        editMatterTimeline1 = editMatterTimeline;

    }
    public matter_ca newInstance(ViewMatterModel viewMatterModel) throws IllegalAccessException, java.lang.InstantiationException {
        matter_ca fragment = new matter_ca(viewMatterModel,  EditMatterTimeline.class.newInstance());
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gct_layout, container, false);
        matter_date = view.findViewById(R.id.matter_date);
        at_add_groups = view.findViewById(R.id.at_add_groups);
        at_add_groups.setOnClickListener(this);
        add_groups = view.findViewById(R.id.add_groups);
        btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        cv_details = view.findViewById(R.id.cv_details);
        ll_save_buttons = view.findViewById(R.id.ll_save_buttons);
        ll_save_buttons.setVisibility(View.GONE);
        upload_group_layout = view.findViewById(R.id.upload_group_layout);
        upload_client_layout = view.findViewById(R.id. upload_client_layout);
        ll_add_clients = view.findViewById(R.id.ll_add_clients);
        tv_selected_clients = view.findViewById(R.id.tv_selected_clients);
        ll_add_clients.setVisibility(View.GONE);
        upload_tm_layout = view.findViewById(R.id. upload_tm_layout);
        rv_display_upload_client_docs = view.findViewById(R.id.rv_display_upload_client_docs);
        rv_display_upload_tm_docs = view.findViewById(R.id.rv_display_upload_tm_docs);
        ll_add_groups = view.findViewById(R.id.ll_add_groups);
        rv_display_upload_groups_docs = view.findViewById(R.id.rv_display_upload_groups_docs);
        add_groups.setText("Add Groups");
        add_clients = view.findViewById(R.id.add_clients);
        add_clients.setText("Add Clients");
        cv_client_details = view.findViewById(R.id.cv_client_details);
        tv_assigned_team_members = view.findViewById(R.id.tv_assigned_team_members);
        tv_assigned_team_members.setText("Assign Team Members");
        at_add_clients = view.findViewById(R.id.at_add_clients);
        at_add_clients.setText("Select Clients");

        matter_title_tv = view.findViewById(R.id.matter_title);

        at_add_clients.setOnClickListener(this);
        at_assigned_team_members = view.findViewById(R.id.at_assigned_team_members);
        at_assigned_team_members.setOnClickListener(this);
        at_assigned_team_members.setText("Select Team Members");
        btn_add_groups = view.findViewById(R.id.btn_add_groups);
        btn_add_groups.setText("Add");
        selected_groups = view.findViewById(R.id.selected_groups);
        selected_clients = view.findViewById(R.id.selected_clients);
        selected_clients.setVisibility(View.GONE);
        selected_tm = view.findViewById(R.id.selected_tm);
        selected_tm.setVisibility(View.GONE);
        btn_add_groups.setOnClickListener(this);
        btn_add_clients = view.findViewById(R.id.btn_add_clients);
        btn_add_clients.setText("Add");
        btn_add_clients.setOnClickListener(this);

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
        if (viewMatterModel1 != null) {//getMatter_title
            matter_title_tv.setText(viewMatterModel1.getTitle());
           /* case_number.setText(viewMatterModel1.getCase_number());
            case_type.setText(viewMatterModel1.getCase_type());
            description.setText(viewMatterModel1.getDescription());
            dof.setText(viewMatterModel1.getDate_of_filing());
            start_date.setText(viewMatterModel1.getStart_date());
            end_date.setText(viewMatterModel1.getEnd_date());
            court.setText(viewMatterModel1.getCourt());
            judge.setText(viewMatterModel1.getJudge());
            case_priority.setText(viewMatterModel1.getCase_priority());
            case_status.setText(viewMatterModel1.getStatus()); */
        }

        at_add_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupsList.size() == 0) {
                    callGroupsWebservice();
                } else {
                    GroupsPopup();
                }



                rv_display_upload_groups_docs.setVisibility(View.VISIBLE);
                if (ischecked_group) {
                    rv_display_upload_groups_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
                    upload_group_layout.setVisibility(View.VISIBLE);


                } else {
                    upload_group_layout.setVisibility(View.GONE);
                }
                ischecked_group = !ischecked_group;


            }

        });

        at_add_clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientsList.clear();
                if (clientsList.size() == 0) {
                    callClientsWebservice();
                } else {
                    ClientssPopUp();
                }

                //  callClientsWebservice();
                rv_display_upload_client_docs.setVisibility(View.VISIBLE);
                if (clientsList.size() == 0) {
                    upload_client_layout.setVisibility(View.GONE);
                    rv_display_upload_client_docs.setVisibility(View.GONE);
                } else {
                    upload_client_layout.setVisibility(View.VISIBLE);
                    rv_display_upload_client_docs.setVisibility(View.VISIBLE);
                }
                if (ischecked_client) {
                    rv_display_upload_client_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
                    upload_client_layout.setVisibility(View.VISIBLE);
                } else {
                    upload_client_layout.setVisibility(View.GONE);
                }
                ischecked_client = !ischecked_client;
            }
        });


        at_assigned_team_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmList.clear();
                callTMWebservice();
                rv_display_upload_tm_docs.setVisibility(View.VISIBLE);
                if (tmList.size() == 0) {
                    upload_tm_layout.setVisibility(View.GONE);
                    rv_display_upload_tm_docs.setVisibility(View.GONE);
                } else {
                    upload_tm_layout.setVisibility(View.VISIBLE);
                    rv_display_upload_tm_docs.setVisibility(View.VISIBLE);
                }

                if (ischecked_tm) {
                    rv_display_upload_tm_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
                    upload_tm_layout.setVisibility(View.VISIBLE);
                } else {
                    upload_tm_layout .setVisibility(View.GONE);
                }
                ischecked_tm = !ischecked_tm;
            }
        });

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
        JSONArray  groups = viewMatterModel1.getGroups();
        if (groups.length() > 0) {
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group_name = null;
                try {
                    group_name = groups.getJSONObject(i);
                    grp_id = group_name.getString("id");
                    grp_name = group_name.getString("name");
                    grp_chk = group_name.getString("isChecked");
                    Log.d("group_name", grp_id + grp_name + grp_chk);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                GroupsModel groupsModel = new GroupsModel();
                groupsModel.setGroup_id(grp_id);
                groupsModel.setGroup_name(grp_name);
                groupsModel.setChecked(Boolean.parseBoolean(grp_chk));
                selected_groups_list.add(groupsModel);
                JSONArray clients = viewMatterModel1.getClients();
                for (int p = 0; p < clients.length(); p++) {
                    JSONObject client_name = null;
                    try {
                        client_name = clients.getJSONObject(p);
                        cln_id = client_name.getString("id");
                        cln_name = client_name.getString("name");
                        cln_type = client_name.getString("type");
                        Log.d("client_name", cln_id + cln_name + cln_type);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                ClientsModel clientsModel = new ClientsModel();
                clientsModel.setClient_id(cln_id);
                clientsModel.setClient_name(cln_name);
                clientsModel.setClient_type(cln_type);
                selected_clients_list.add(clientsModel);
                JSONArray member = viewMatterModel1.getMembers();
                for (int t = 0; t < member.length(); t++) {
                    JSONObject member_name = null;
                    try {
                        member_name = member.getJSONObject(t);
                        tm_id = member_name.getString("id");
                        tm_name = member_name.getString("name");
                        user_id = member_name.getString("user_id");
                        Log.d("member_name", tm_id + tm_name + user_id);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                TeamModel teamModel = new TeamModel();
                teamModel.setTm_id(tm_id);
                teamModel.setTm_name(tm_name);
                teamModel.setUser_id(user_id);
                selected_tm_list.add(teamModel);

                if (viewMatterModel1.getGroups_list() != null) {
                    existing_groups_list = viewMatterModel1.getGroups_list();
                    try {
                        for (int m = 0; m < existing_groups_list.length(); m++) {
                            groupsModel = new GroupsModel();
                            JSONObject jsonObject = existing_groups_list.getJSONObject(m);
                            groupsModel.setGroup_id(jsonObject.getString("id"));
                            groupsModel.setGroup_name(jsonObject.getString("name"));
                            groupsList.add(groupsModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (viewMatterModel1.getClients_list() != null) {
                    existing_clients_list = viewMatterModel1.getClients_list();
                    try {
                        for (int n = 0; n < existing_clients_list.length(); n++) {
                            clientsModel = new ClientsModel();
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
                if (viewMatterModel1.getMembers_list() != null) {
                    existing_tm_list = viewMatterModel1.getMembers_list();
                    try {

                        for (int d = 0; d < existing_tm_list.length(); d++) {
                            teamModel = new TeamModel();
                            JSONObject jsonObject = existing_tm_list.getJSONObject(d);
                            teamModel.setTm_id(jsonObject.getString("id"));
                            teamModel.setTm_name(jsonObject.getString("name"));
                            teamModel.setUser_id(jsonObject.getString("user_id"));
                            tmList.add(teamModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (viewMatterModel1.getDocuments() != null) {
                        try {
                            existing_documents = viewMatterModel1.getDocuments();
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
                    if (viewMatterModel1.getDocuments_list() != null) {
                        try {
                            existing_documents_list = viewMatterModel1.getDocuments_list();
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
            case R.id.at_add_groups:

                break;
            case R.id.at_add_clients:
                if (clientsList.size() == 0) {
                    callClientsWebservice();
                } else {
                    ClientssPopUp();
                }

                break;
            case R.id.at_assigned_team_members:
//                if (tmList.size() == 0) {
//                    callTMWebservice();
//
//                } else {
//                    TeamPopUp();
//                }
                break;
            case R.id.btn_create:
                cv_client_details.setVisibility(View.VISIBLE);
                cv_details.setVisibility(View.VISIBLE);
                saveGCTinformation();
//
//                ll_add_clients.setVisibility(View.VISIBLE);
//                ll_assign_team_members.setVisibility(View.VISIBLE);
//                ll_save_buttons.setVisibility(View.VISIBLE);
                break;
        }
    }



    private void saveGCTinformation() {
        if (selected_groups_list.size() == 0) {
            AndroidUtils.showToast("Please select atealst one group", getContext());
        } else if (selected_clients_list.size() == 0) {

            AndroidUtils.showAlert("Please check  the add client", getContext());


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
                //   MatterModel matterModel = new MatterModel();
                ViewMatterModel viewMatterModel = new ViewMatterModel();
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

                viewMatterModel1.setClients(clients);
                viewMatterModel1.setGroup_acls(group_acls);
                viewMatterModel1.setMembers(members);
                viewMatterModel1.setGroups_list(new_groups_list);
                viewMatterModel1.setClients_list(new_clients_list);
                viewMatterModel1.setMembers_list(new_tm_list);
                viewMatterModel1.setOpponent_advocate(jsonArray);
                viewMatterModel1.setDocuments(documents);
                viewMatterModel1.setDocuments_list(new_documents_list);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void TeamPopUp() {
        try {
            rv_display_upload_tm_docs.setVisibility(View.VISIBLE);

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

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_tm_docs.setLayoutManager(layoutManager);
            rv_display_upload_tm_docs.setHasFixedSize(true);
            ADAPTER_TAG = "TM";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
            rv_display_upload_tm_docs.setAdapter(documentsAdapter);



            btn_assigned_team_members.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getTmList().size(); i++) {
                        TeamModel teamModel = documentsAdapter.getTmList().get(i);
                        if (teamModel.isChecked()) {
                            if (!selected_tm_list.contains(teamModel)) {


                                selected_tm_list.add(teamModel);
                                // ll_assigned_team_members.setVisibility(View.VISIBLE);
                                //   ll_add_clients.setVisibility(View.VISIBLE);
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }
                    }
                    upload_tm_layout.setVisibility(View.GONE);
                    rv_display_upload_tm_docs.setVisibility(View.GONE);


                    loadSelectedTM();
//                    loadSelectedClients();
//                    loadSelectedGroups();


                }

            });

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
                if (tmList.size() == 0) {
                    ll_assign_team_members.setVisibility(View.GONE);
                    rv_display_upload_tm_docs.setVisibility(View.GONE);
                } else {
                    ll_assign_team_members.setVisibility(View.VISIBLE);
                    rv_display_upload_tm_docs.setVisibility(View.VISIBLE);
                }
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
                if (clientsList.size() == 0) {
                    //ll_add_clients.setVisibility(View.GONE);
                    rv_display_upload_client_docs.setVisibility(View.GONE);
                } else {
                    // ll_add_clients.setVisibility(View.VISIBLE);
                    rv_display_upload_client_docs.setVisibility(View.VISIBLE);
                }

                selectedClients = new boolean[clientsList.size()];


            }
            ClientssPopUp();
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
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_client_docs.setLayoutManager(layoutManager);
            rv_display_upload_client_docs.setHasFixedSize(true);
            ADAPTER_TAG = "Clients";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
            rv_display_upload_client_docs.setAdapter(documentsAdapter);
            selected_clients_list.clear();



            btn_add_clients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getClientsList_item().size(); i++) {
                        ClientsModel clientsModel = documentsAdapter.getClientsList_item().get(i);
                        if (clientsModel.isChecked()) {
                            if (!selected_clients_list.contains(clientsModel)) {


                                selected_clients_list.add(clientsModel);




                            }
                            //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                        }
                        upload_client_layout.setVisibility(View.GONE);
                        rv_display_upload_client_docs.setVisibility(View.GONE);

                    }


//                    if(selected_clients_list.size()==0){
//
//                        ll_assign_team_members.setVisibility(View.VISIBLE);
//                        ll_save_buttons.setVisibility(View.VISIBLE);
//
//                    }else{
//                        selected_clients.setVisibility(View.VISIBLE);
//                        ll_assign_team_members.setVisibility(View.VISIBLE);
//                        ll_save_buttons.setVisibility(View.VISIBLE);
//                    }



                    //  ll_save_buttons.setVisibility(View.VISIBLE);





                    loadSelectedClients();

//                    loadSelectedGroups();

                }

            });




        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }
    private void ClientssPopUp() {
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

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_client_docs.setLayoutManager(layoutManager);
            rv_display_upload_client_docs.setHasFixedSize(true);
            ADAPTER_TAG = "Clients";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList, new_groupsList, ADAPTER_TAG);
            rv_display_upload_client_docs.setAdapter(documentsAdapter);


            btn_add_clients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getClientsList_item().size(); i++) {
                        ClientsModel clientsModel = documentsAdapter.getClientsList_item().get(i);
                        if (clientsModel.isChecked()) {
                            if (!selected_clients_list.contains(clientsModel)) {
                                selected_clients_list.add(clientsModel);
                                //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                            }
                        }

                        //upload_client_layout.setVisibility(View.GONE);
                        rv_display_upload_client_docs.setVisibility(View.GONE);

//                  }  ischecked_client = true;
                    }
                    if(selected_clients_list.size()==0){

                        ll_assign_team_members.setVisibility(View.VISIBLE);
                        ll_save_buttons.setVisibility(View.VISIBLE);

                    }else{
                        selected_clients.setVisibility(View.VISIBLE);
                        ll_assign_team_members.setVisibility(View.VISIBLE);
                        ll_save_buttons.setVisibility(View.VISIBLE);
                    }
                    loadSelectedClients();
//                    loadSelectedGroups();
                }

            });

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
        ll_save_buttons.setVisibility(View.VISIBLE);
        ll_assign_team_members.setVisibility(View.VISIBLE);
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
        ll_add_clients.setVisibility(View.VISIBLE);
        ll_assign_team_members.setVisibility(View.VISIBLE);
        selected_clients.setVisibility(View.VISIBLE);
        ll_save_buttons.setVisibility(View.VISIBLE);

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
//                        if (selected_clients_list.size() > 0) {
//
//                            ll_assign_team_members.setVisibility(View.VISIBLE);
//                            ll_save_buttons.setVisibility(View.VISIBLE);
//
//                        } else {
//
//                            ll_assign_team_members.setVisibility(View.VISIBLE);
//                            selected_groups.setVisibility(View.VISIBLE);
//                            ll_save_buttons.setVisibility(View.VISIBLE);
//                        }
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

    public void GroupsPopup() {
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
//            View view = inflater.inflate(R.layout.gct_layout, null);
            AlertDialog dialog = dialogBuilder.create();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_groups_docs.setLayoutManager(layoutManager);
            rv_display_upload_groups_docs.setHasFixedSize(true);
            ADAPTER_TAG = "Groups";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,new_groupsList, ADAPTER_TAG);
            rv_display_upload_groups_docs.setAdapter(documentsAdapter);

            selected_groups_list.clear();

            btn_add_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                        rv_display_upload_groups_docs.setVisibility(View.GONE);

                        GroupsModel groupsModel = documentsAdapter.getList_item().get(i);
                        if (groupsModel.isChecked()) {
                            if (!selected_groups_list.contains(groupsModel)) {
                                selected_groups_list.add(groupsModel);
                            }


                        }
                    }
                    if(selected_groups_list.size()>0){
                        ll_add_clients.setVisibility(View.VISIBLE);
                        ll_assign_team_members.setVisibility(View.VISIBLE);
                        ll_save_buttons.setVisibility(View.VISIBLE);
                        selected_clients.setVisibility(View.GONE);


                    }else {
                        ll_add_clients.setVisibility(View.GONE);
                        ll_assign_team_members.setVisibility(View.GONE);
                        ll_save_buttons.setVisibility(View.GONE);
                    }




                    detectListChanges();


                    loadSelectedGroups();

                }

            });


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
        // ll_selected_groups.removeAllViews();
        if (selected_groups_list.size() != 0) {
//            clientsList.clear();
//            selected_clients_list.clear();
//            ll_selected_clients.removeAllViews();
//            tmList.clear();
//            selected_tm_list.clear();
            ll_assigned_team_members.removeAllViews();
            at_add_groups.setText("Select Groups");
            at_add_clients.setText("Select Clients");
            at_assigned_team_members.setText("Assign Team Members");
            selected_groups.setVisibility(View.VISIBLE);
            selected_clients.setVisibility(View.VISIBLE);
            selected_tm.setVisibility(View.VISIBLE);

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
                        if (selected_groups_list.size() > 0) {
                            ll_add_clients.setVisibility(View.VISIBLE);
                            ll_assign_team_members.setVisibility(View.VISIBLE);
                            ll_save_buttons.setVisibility(View.VISIBLE);

                        } else {
                            ll_add_clients.setVisibility(View.GONE);
                            ll_assign_team_members.setVisibility(View.GONE);
                            selected_groups.setVisibility(View.GONE);
                            ll_save_buttons.setVisibility(View.GONE);
                        }
                    }
                    catch (Exception e) {
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

