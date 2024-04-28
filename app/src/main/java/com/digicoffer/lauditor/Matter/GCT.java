package com.digicoffer.lauditor.Matter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.minidns.record.A;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class
GCT extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {

    TextView matter_date, at_add_groups, at_add_clients, at_assigned_team_members, add_groups, add_clients, tv_assigned_team_members;
    boolean[] selectedLanguage;
    boolean[] selectedClients;
    boolean[] selectedTM;
    boolean is_error;
    String attachment_type;
    JSONArray client_list = new JSONArray();
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<MatterModel> matterArraylist;
    JSONArray exisiting_group_acls;
    TextView matter_title_tv, tv_name, tv_selected_clients, tv_selected_tm;
    ConstraintLayout cv_details;
    String matter_title, case_number, case_type, description, dof, start_date, end_date, court, judge, case_priority, case_status;
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
    JSONArray existing_documents_list;
    String ADAPTER_TAG = "Groups";
    Button btn_add_groups, btn_add_clients, btn_assigned_team_members, btn_create, btn_cancel_save;
    LinearLayout ll_selected_groups, ll_selected_clients, ll_assigned_team_members, selected_groups, selected_clients, selected_tm, ll_add_clients, ll_assign_team_members;
    AlertDialog progress_dialog;
    ArrayList<GroupsModel> selected_groups_list = new ArrayList<>();
    ArrayList<GroupsModel> updated_groups_list = new ArrayList<>();
    ArrayList<ClientsModel> selected_clients_list = new ArrayList<>();
    ArrayList<TeamModel> selected_tm_list = new ArrayList<>();
    ArrayList<GroupsModel> groupsList = new ArrayList<>();
    boolean ischecked_group = true;
    LinearLayoutCompat ll_save_buttons;
    boolean ischecked_client = true;
    boolean ischecked_tm = true;
    ArrayList<DocumentsModel> documentsList = new ArrayList<>();

    ArrayList<ViewMatterModel> new_groupsList = new ArrayList<>();
    ArrayList<TeamModel> tmList = new ArrayList<>();
    Matter matter;
    Matter mattermodel;
    RecyclerView rv_display_upload_groups_docs, rv_display_upload_client_docs, rv_display_upload_tm_docs;


    private JSONArray existing_opponents;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gct_layout, container, false);
        matter_date = view.findViewById(R.id.matter_date);
        at_add_groups = view.findViewById(R.id.at_add_groups);
//        at_add_groups.setHint(R.string.select_groups);
        tv_selected_clients = view.findViewById(R.id.tv_selected_clients);
        tv_selected_clients.setText(R.string.selected_clients);
        tv_selected_tm = view.findViewById(R.id.tv_selected_tm);
        tv_selected_tm.setText(R.string.selected_tm);
        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(R.string.selected_groups);
        at_add_groups.setOnClickListener(this);
        add_groups = view.findViewById(R.id.add_groups);
        btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        cv_details = view.findViewById(R.id.cv_details);
        ll_save_buttons = view.findViewById(R.id.ll_save_buttons);
        ll_save_buttons.setVisibility(View.GONE);
        ll_add_clients = view.findViewById(R.id.ll_add_clients);
        ll_add_clients.setVisibility(View.GONE);
        rv_display_upload_groups_docs = view.findViewById(R.id.rv_display_upload_groups_docs);
        rv_display_upload_groups_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        rv_display_upload_client_docs = view.findViewById(R.id.rv_display_upload_client_docs);
        rv_display_upload_client_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        rv_display_upload_tm_docs = view.findViewById(R.id.rv_display_upload_tm_docs);
        rv_display_upload_tm_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
        ll_add_groups = view.findViewById(R.id.ll_add_groups);
        add_groups.setText(R.string.add_groups);
        add_clients = view.findViewById(R.id.add_clients);
        add_clients.setText(R.string.add_clients);
        cv_client_details = view.findViewById(R.id.cv_client_details);
        tv_assigned_team_members = view.findViewById(R.id.tv_assigned_team_members);
        tv_assigned_team_members.setText(R.string.assign_team_members);
        at_add_clients = view.findViewById(R.id.at_add_clients);
//        at_add_clients.setHint(R.string.select_clients);
//        tv_matter_title = view.findViewById(R.id.tv_matter_title);
//        String text = tv_matter_title.getText().toString();
//        matter_title.setText(text);
        matter_title_tv = view.findViewById(R.id.matter_title);

        at_add_clients.setOnClickListener(this);
        at_assigned_team_members = view.findViewById(R.id.at_assigned_team_members);
        at_assigned_team_members.setOnClickListener(this);
//        at_assigned_team_members.setHint(R.string.select_team_member);
        btn_add_groups = view.findViewById(R.id.btn_add_groups);
        btn_add_groups.setText(R.string.add);
        selected_groups = view.findViewById(R.id.selected_groups);
        selected_clients = view.findViewById(R.id.selected_clients);
        selected_clients.setVisibility(View.GONE);
        selected_tm = view.findViewById(R.id.selected_tm);
        selected_tm.setVisibility(View.GONE);
        btn_add_groups.setOnClickListener(this);
        btn_add_clients = view.findViewById(R.id.btn_add_clients);
        btn_add_clients.setText(R.string.add);
        btn_add_clients.setOnClickListener(this);

        ll_assign_team_members = view.findViewById(R.id.ll_assign_team_members);
        ll_assign_team_members.setVisibility(View.GONE);
        //  String matter_title = tv_matter_title.getText().toString();


        btn_create = view.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);
        btn_assigned_team_members = view.findViewById(R.id.btn_assigned_team_members);
        btn_assigned_team_members.setText(R.string.add);
        btn_assigned_team_members.setOnClickListener(this);
        ll_selected_groups = view.findViewById(R.id.ll_selected_groups);
        ll_assigned_team_members = view.findViewById(R.id.ll_assigned_team_members);
        ll_selected_clients = view.findViewById(R.id.ll_selected_clients);
        Calendar myCalendar = Calendar.getInstance();
//        TextInputEditText tv_matter_title = view. findViewById(R.id.tv_matter_title);
//        AppCompatButton matter_title = view.findViewById(R.id.matter_title);
//        AppCompatButton finalmatter_title = matter_title;
//        matter_title.setOnClickListener(new View.OnClickListener(){
//            private BreakIterator finalMatter_title;
//
//            public void onClick(View view){
//                String item = tv_matter_title.getText().toString();
//                finalMatter_title.setText(item);
//            }
//        });
        if (!Constants.create_matter) {
            load_existing_matter();
        }
        at_add_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupsList.isEmpty()) {
                    callGroupsWebservice();
                } else {
                    GroupsPopup();
                }
                if (ischecked_group) {
                    rv_display_upload_groups_docs.setVisibility(View.VISIBLE);
                    btn_add_groups.setEnabled(true);
                } else {
                    rv_display_upload_groups_docs.setVisibility(View.GONE);
                    btn_add_groups.setEnabled(false);
                }
                ischecked_group = !ischecked_group;
            }
        });

        at_add_clients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ischecked_client) {
                    if (clientsList.isEmpty()) {
                        callClientsWebservice();
                    } else {
                        rv_display_upload_client_docs.setVisibility(View.VISIBLE);
                        ClientssPopUp();
                    }
                } else {
                    rv_display_upload_client_docs.setVisibility(View.GONE);
                }
                //  callClientsWebservice();
                ischecked_client = !ischecked_client;
            }
        });


        at_assigned_team_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tmList.clear();
                if (ischecked_tm) {
                    if (tmList.isEmpty()) {
                        callTMWebservice();
                    } else {
                        rv_display_upload_tm_docs.setVisibility(View.VISIBLE);
                        TeamPopUp();
                    }
                } else {
                    rv_display_upload_tm_docs.setVisibility(View.GONE);
                }
                //  callClientsWebservice();
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
        assert matter != null;
        matterArraylist = matter.getMatter_arraylist();
//        detectListChanges();
        if (!matterArraylist.isEmpty()) {
            for (int i = 0; i < matterArraylist.size(); i++) {
                MatterModel matterModel = matterArraylist.get(i);
                matter_title_tv.setText("");
                matter_title_tv.setText(matterModel.getMatter_title());
                if (matterModel.getGroup_acls() != null) {
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
                        e.fillInStackTrace();
                    }
                }
                if (matterModel.getClients() != null) {
                    existing_clients = matterModel.getClients();
                    try {
                        for (int p = 0; p < existing_clients.length(); p++) {
                            ClientsModel clientsModel = new ClientsModel();
                            JSONObject jsonObject = existing_clients.getJSONObject(p);
                            clientsModel.setClient_id(jsonObject.getString("id"));
                            clientsModel.setClient_name(jsonObject.getString("name"));
                            clientsModel.setClient_type(jsonObject.getString("type"));
                            selected_clients_list.add(clientsModel);
                        }
                    } catch (JSONException e) {
                        e.fillInStackTrace();
                    }
                }
                if (matterModel.getMembers() != null) {
                    existing_members = matterModel.getMembers();
                    try {
                        for (int t = 0; t < existing_members.length(); t++) {
                            TeamModel teamModel = new TeamModel();
                            JSONObject jsonObject = existing_members.getJSONObject(t);
                            teamModel.setTm_id(jsonObject.getString("id"));
                            teamModel.setTm_name(jsonObject.getString("name"));
                            teamModel.setUser_id(jsonObject.getString("user_id"));
                            selected_tm_list.add(teamModel);
                        }
                    } catch (JSONException e) {
                        e.fillInStackTrace();
                    }
                }
                if (matterModel.getGroups_list() != null) {
                    existing_groups_list = matterModel.getGroups_list();
                    try {
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
                if (matterModel.getClients_list() != null) {
                    existing_clients_list = matterModel.getClients_list();
                    try {
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
                if (matterModel.getMembers_list() != null) {
                    existing_tm_list = matterModel.getMembers_list();
                    try {

                        for (int d = 0; d < existing_tm_list.length(); d++) {
                            TeamModel teamModel = new TeamModel();
                            JSONObject jsonObject = existing_tm_list.getJSONObject(d);
                            teamModel.setTm_id(jsonObject.getString("id"));
                            teamModel.setTm_name(jsonObject.getString("name"));
                            teamModel.setUser_id(jsonObject.getString("user_id"));
                            tmList.add(teamModel);
                        }
                    } catch (JSONException e) {
                        e.fillInStackTrace();
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
                                e.fillInStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
                    }
                }
                Log.d("tm_list..", String.valueOf(selected_groups_list.size() + " ...C." + selected_clients_list.size()) + " ..g." + selected_tm_list.size());
                if (matterModel.getMatter_title() != null) {
                    matter_title = (String) matterModel.getMatter_title();
                }
                if (matterModel.getCase_number() != null) {
                    case_number = matterModel.getCase_number();
                }
                if (matterModel.getCase_type() != null) {
                    case_type = matterModel.getCase_type();
                }
                if (matterModel.getDescription() != null) {
                    description = matterModel.getDescription();
                }
                if (matterModel.getDate_of_filing() != null) {
                    dof = matterModel.getDate_of_filing();
                }
                if (matterModel.getStart_date() != null) {
                    start_date = matterModel.getStart_date();
                }
                if (matterModel.getEnd_date() != null) {
                    end_date = matterModel.getEnd_date();
                }
                if (matterModel.getCourt() != null) {
                    court = matterModel.getCourt();
                }
                if (matterModel.getJudge() != null) {
                    judge = matterModel.getJudge();
                }
                if (matterModel.getCase_priority() != null) {
                    case_priority = matterModel.getCase_priority();
                }
                if (matterModel.getStatus() != null) {
                    case_status = matterModel.getStatus();
                }
                try {
                    updateDisplay();
                    if (!selected_groups_list.isEmpty()) {
//                    callGroupsWebservice();
                        loadSelectedGroups();
                    }
                    if (!selected_clients_list.isEmpty()) {
//                    callClientsWebservice();
                        loadSelectedClients();
                    }
                    if (!selected_tm_list.isEmpty()) {
//                    callTMWebservice();
                        loadSelectedTM();
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
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
        if (v.getId() == R.id.btn_create) {
            cv_client_details.setVisibility(View.VISIBLE);
            cv_details.setVisibility(View.VISIBLE);
            if (!Constants.create_matter) {
                try {
                    update_matter();
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            } else {
                saveGCTinformation();
            }
//
//                ll_add_clients.setVisibility(View.VISIBLE);
//                ll_assign_team_members.setVisibility(View.VISIBLE);
//                ll_save_buttons.setVisibility(View.VISIBLE);
        }
    }

    private void saveGCTinformation() {
        if (selected_groups_list.isEmpty()) {
            AndroidUtils.showToast("Please select atealst one group", getContext());
        } else if (selected_clients_list.isEmpty()) {
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
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
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
                        e.fillInStackTrace();
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
                    e.fillInStackTrace();
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
                for (int e = 0; e < documentsList.size(); e++) {
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
                e.fillInStackTrace();
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
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList, new_groupsList, ADAPTER_TAG);
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
                    if (selected_tm_list.isEmpty()) {
                        selected_tm.setVisibility(View.GONE);
                    } else {
                        loadSelectedTM();
                    }
                    rv_display_upload_tm_docs.setVisibility(View.GONE);
                    ischecked_tm = true;
                }
            });
        } catch (Exception e) {
            e.fillInStackTrace();
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
            e.fillInStackTrace();
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
            e.fillInStackTrace();
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
                } else if (httpResult.getRequestType().equals("chosen_member")) {
                    String id = result.getString("id");
//                    AndroidUtils.showAlert("Error_value.." + id, getContext());
                    JSONArray members = result.getJSONArray("members");
                    JSONArray clients = result.getJSONArray("clients");
                    display_existing_members(members, clients);
                    try {
                        load_existing_member_list();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else if (httpResult.getRequestType().equals("attachment_members")) {
                    is_error = result.getBoolean("error");
                    attachment_type = result.getString("attachment_type");
                    JSONArray members = result.getJSONArray("members");
                    loadMembers(members);
//                    AndroidUtils.showAlert("Att_value.." + attachment_type, getContext());
                    load_existing_clients_list();
                } else if (httpResult.getRequestType().equals("attachment_clients")) {
                    is_error = result.getBoolean("error");
                    attachment_type = result.getString("attachment_type");
                    JSONArray members = result.getJSONArray("clients");
                    loadClients(members);
//                    AndroidUtils.showAlert("Att_value.." + attachment_type, getContext());
                } else if (httpResult.getRequestType().equals("matter_update")) {
                    is_error = result.getBoolean("error");
                    String msg = result.getString("msg");
                    AndroidUtils.showToast("" + msg, getContext());
                    matter.loadViewUI();
                } else if (httpResult.getRequestType().equals("Clients")) {
                    JSONArray clients = result.getJSONArray("clients");
                    loadClients(clients);
                } else if (httpResult.getRequestType().equals("Members")) {
                    JSONArray members = result.getJSONArray("members");
                    loadMembers(members);
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
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
            e.fillInStackTrace();
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
            e.fillInStackTrace();
        }
    }

    private void ClientssPopUp() {
        ll_add_clients.setVisibility(View.VISIBLE);
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
                    }
                    ll_assign_team_members.setVisibility(View.VISIBLE);
                    ll_save_buttons.setVisibility(View.VISIBLE);
                    rv_display_upload_client_docs.setVisibility(View.GONE);
                    if (selected_clients_list.isEmpty()) {
                        selected_clients.setVisibility(View.GONE);
                    } else {
                        loadSelectedClients();
                    }
                    ischecked_client = true;
//                    loadSelectedGroups();
                }
            });
        } catch (Exception e) {
            e.fillInStackTrace();
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
            if (view_opponents != null) {
                TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
                ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
                ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
                iv_edit_opponent.setVisibility(View.GONE);
                if (tv_opponent_name != null && iv_remove_opponent != null) {
                    tv_opponent_name.setText(selected_tm_list.get(i).getTm_name());
                    iv_remove_opponent.setTag(i); // Tag each view with its position
                    iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int position = (int) v.getTag();
                                // Remove the view at the specified position
                                ll_assigned_team_members.removeViewAt(position);
                                // Remove the corresponding item from the list
                                TeamModel teamModel = selected_tm_list.remove(position);
                                teamModel.setChecked(false);
                                // Update the tags of the remaining views
                                for (int j = 0; j < ll_assigned_team_members.getChildCount(); j++) {
                                    ImageView iv_remove = ll_assigned_team_members.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                                    if (iv_remove != null) {
                                        iv_remove.setTag(j);
                                    }
                                }

                                String str = String.join(",", value);
                                at_assigned_team_members.setText(str);
                                // Update at_add_groups text
                                StringBuilder stringBuilder = new StringBuilder();
                                for (TeamModel model : selected_tm_list) {
                                    stringBuilder.append(model.getTm_name()).append(",");
                                }

                                if (stringBuilder.length() > 0) {
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                }
                                String strn = stringBuilder.toString();
                                at_assigned_team_members.setText(strn);
                            } catch (Exception e) {
                                e.fillInStackTrace();
                                AndroidUtils.showAlert(e.getMessage(), getContext());
                            }
                        }
                    });
                    iv_remove_opponent.setVisibility(View.VISIBLE); // Set visibility here
                }
                ll_assigned_team_members.addView(view_opponents);
            }
        }
    }

    private void updatedtmDisplay() {
        if (!selected_tm_list.isEmpty()) {
            selected_tm.setVisibility(View.VISIBLE);
        } else {
            selected_tm.setVisibility(View.GONE);
        }
    }

    private void loadSelectedClients() {
        selected_clients.setVisibility(View.VISIBLE);
        //views get cleared after clicking the add client.
        ll_selected_clients.removeAllViews();
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
        ll_save_buttons.setVisibility(View.VISIBLE);


        for (int i = 0; i < selected_clients_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            if (view_opponents != null) {
                TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
                ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
                ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
                iv_edit_opponent.setVisibility(View.GONE);
                if (tv_opponent_name != null && iv_remove_opponent != null) {
                    tv_opponent_name.setText(selected_clients_list.get(i).getClient_name());
                    iv_remove_opponent.setTag(i); // Tag each view with its position
                    iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int position = (int) v.getTag();
                                // Remove the view at the specified position
                                ll_selected_clients.removeViewAt(position);
                                // Remove the corresponding item from the list
                                ClientsModel clientsModel = selected_clients_list.remove(position);
                                clientsModel.setChecked(false);
                                // Update the tags of the remaining views
                                for (int j = 0; j < ll_selected_clients.getChildCount(); j++) {
                                    ImageView iv_remove = ll_selected_clients.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                                    if (iv_remove != null) {
                                        iv_remove.setTag(j);
                                    }
                                }

                                String str = String.join(",", value);
                                at_add_clients.setText(str);
                                // Update at_add_groups text
                                StringBuilder stringBuilder = new StringBuilder();
                                for (ClientsModel model : selected_clients_list) {
                                    stringBuilder.append(model.getClient_name()).append(",");
                                }

                                if (stringBuilder.length() > 0) {
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                }
                                String strn = stringBuilder.toString();
                                at_add_clients.setText(strn);
//                                updatedDisplay();
                            } catch (Exception e) {
                                e.fillInStackTrace();
                                AndroidUtils.showAlert(e.getMessage(), getContext());
                            }
                        }
                    });
                    iv_remove_opponent.setVisibility(View.VISIBLE); // Set visibility here
                }
                ll_selected_clients.addView(view_opponents);
            }
        }
    }

    private void updatedDisplay() {
        if (!selected_clients_list.isEmpty()) {
            selected_clients.setVisibility(View.VISIBLE);
        } else {
            selected_clients.setVisibility(View.GONE);
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
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    public void GroupsPopup() {
        try {
            for (int i = 0; i < groupsList.size(); i++) {
                for (int j = 0; j < selected_groups_list.size(); j++) {
                    if (groupsList.get(i).getGroup_id().equals(selected_groups_list.get(j).getGroup_id())) {
                        GroupsModel groupsModel = groupsList.get(i);
                        groupsModel.setChecked(true);
                    }
                }
            }

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            AlertDialog dialog = dialogBuilder.create();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_groups_docs.setLayoutManager(layoutManager);
            rv_display_upload_groups_docs.setHasFixedSize(true);
            ADAPTER_TAG = "Groups";
            GroupsAdapter documentsAdapter = new GroupsAdapter(groupsList, clientsList, tmList, new_groupsList, ADAPTER_TAG);
            rv_display_upload_groups_docs.setAdapter(documentsAdapter);

            btn_add_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Set<GroupsModel> selected_groups_set = new HashSet<>();
                    rv_display_upload_groups_docs.setVisibility(View.GONE);
                    for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                        GroupsModel groupsModel = documentsAdapter.getList_item().get(i);
                        if (groupsModel.isChecked()) {
                            selected_groups_set.add(groupsModel);
                        }
                    }

                    selected_groups_list.clear();
                    selected_groups_list.addAll(selected_groups_set);

                    detectListChanges();
                    updateDisplay();
                    if (selected_groups_list.isEmpty()) {
                        at_add_groups.setText("");
                        selected_groups.setVisibility(View.GONE);
                        selected_clients.setVisibility(View.GONE);
                        selected_tm.setVisibility(View.GONE);
                    } else {
                        loadSelectedGroups();
                    }
                    ischecked_group = true;
                    btn_add_groups.setEnabled(false);
                }
            });

        } catch (Exception e) {
            e.fillInStackTrace();
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
        } else if (newSize == originalSize) {
            // items have been removed from the list
        } else if (newSize == 0 || originalSize == 0) {
            selected_groups.setVisibility(View.GONE);
            ll_selected_groups.removeAllViews();
        }
        at_add_clients.setText("");
        at_assigned_team_members.setText("");
    }

    private void loadSelectedGroups() {
        ll_selected_groups.removeAllViews();
        selected_groups.setVisibility(View.VISIBLE);

        String[] value = new String[selected_groups_list.size()];
        for (int i = 0; i < selected_groups_list.size(); i++) {
            value[i] = selected_groups_list.get(i).getGroup_name();
        }
//            detectListChanges();
        String str = String.join(",", value);
        at_add_groups.setText(str);

        for (int i = 0; i < selected_groups_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            if (view_opponents != null) {
                TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
                ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
                ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
                iv_edit_opponent.setVisibility(View.GONE);
                if (tv_opponent_name != null && iv_remove_opponent != null) {
                    tv_opponent_name.setText(selected_groups_list.get(i).getGroup_name());
                    iv_remove_opponent.setTag(i); // Tag each view with its position
                    iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                int position = (int) v.getTag();
                                // Remove the view at the specified position
                                ll_selected_groups.removeViewAt(position);
                                // Remove the corresponding item from the list
                                GroupsModel groupsModel = selected_groups_list.remove(position);
                                groupsModel.setChecked(false);
                                // Update the tags of the remaining views
                                for (int j = 0; j < ll_selected_groups.getChildCount(); j++) {
                                    ImageView iv_remove = ll_selected_groups.getChildAt(j).findViewById(R.id.iv_remove_opponent);
                                    if (iv_remove != null) {
                                        iv_remove.setTag(j);
                                    }
                                }
                                detectListChanges();
                                String str = String.join(",", value);
                                at_add_groups.setText(str);
                                // Update at_add_groups text
                                StringBuilder stringBuilder = new StringBuilder();
                                for (GroupsModel model : selected_groups_list) {
                                    stringBuilder.append(model.getGroup_name()).append(",");
                                }

                                if (stringBuilder.length() > 0) {
                                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                                }
                                String strn = stringBuilder.toString();
                                at_add_groups.setText(strn);
                                // Update the display
                                updateDisplay();
                            } catch (Exception e) {
                                e.fillInStackTrace();
                                AndroidUtils.showAlert(e.getMessage(), getContext());
                            }
                        }
                    });
                    iv_remove_opponent.setVisibility(View.VISIBLE); // Set visibility here
                }
                ll_selected_groups.addView(view_opponents);
            }
        }
    }

    private void updateDisplay() {
        // Update UI visibility based on the size of the selected groups list
        if (!selected_groups_list.isEmpty()) {
            ll_add_clients.setVisibility(View.VISIBLE);
            ll_assign_team_members.setVisibility(View.VISIBLE);
            ll_save_buttons.setVisibility(View.VISIBLE);
        } else {
            clientsList.clear();
            tmList.clear();
            ll_selected_groups.removeAllViews();
            ll_selected_clients.removeAllViews();
            ll_assigned_team_members.removeAllViews();
            ll_add_clients.setVisibility(View.GONE);
            ll_assign_team_members.setVisibility(View.GONE);
            selected_groups.setVisibility(View.GONE);
            selected_clients.setVisibility(View.GONE);
            selected_tm.setVisibility(View.GONE);
            ll_save_buttons.setVisibility(View.GONE);
        }
    }

    private void load_existing_matter() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
//        https://api.staging.digicoffer.com/professional/matter/legal/65264766fffd8f05faaf6152/members
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/legal/" + Constants.Matter_id + "/members", "chosen_member", postdata.toString());
    }

    //    https://api.staging.digicoffer.com/professional/matter/attachments
    private void load_existing_member_list() throws JSONException {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
        postdata.put("attachment_type", "members");
//   array=["661fa5eafffd8f20732f439d", "65ee94cffffd8f453d0b1c95", "65ee9307fffd8f453d0b1c8f"];
        postdata.put("group_acls", Constants.ex_attachment);
//        https://api.staging.digicoffer.com/professional/matter/legal/65264766fffd8f05faaf6152/members
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/attachments", "attachment_members", postdata.toString());
    }

    private void load_existing_clients_list() throws JSONException {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
        postdata.put("attachment_type", "clients");
//   array=["661fa5eafffd8f20732f439d", "65ee94cffffd8f453d0b1c95", "65ee9307fffd8f453d0b1c8f"];
        postdata.put("group_acls", Constants.ex_attachment);
//        https://api.staging.digicoffer.com/professional/matter/legal/65264766fffd8f05faaf6152/members
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/attachments", "attachment_clients", postdata.toString());
    }

    private void update_matter() throws JSONException {
//        https://api.staging.digicoffer.com/professional/matter/legal/652f70e5fffd8f1bcd11bf36/members/update
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONArray clients = new JSONArray();
        JSONArray members = new JSONArray();
        for (int i = 0; i < selected_tm_list.size(); i++) {
            try {
                TeamModel teamModel = selected_tm_list.get(i);
                JSONObject team_object = new JSONObject();
                team_object.put("id", teamModel.getTm_id());
                team_object.put("name", teamModel.getTm_name());
//                        team_object.put("")
                members.put(team_object);
            } catch (Exception e) {
                e.fillInStackTrace();
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
                e.fillInStackTrace();
            }
        }
        JSONObject postdata = new JSONObject();
        postdata.put("clients", clients);
        postdata.put("members", members);
//   array=["661fa5eafffd8f20732f439d", "65ee94cffffd8f453d0b1c95", "65ee9307fffd8f453d0b1c8f"]
//        https://api.staging.digicoffer.com/professional/matter/legal/65264766fffd8f05faaf6152/members
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/legal/" + Constants.Matter_id + "/members/update", "matter_update", postdata.toString());
    }

    private void display_existing_members(JSONArray members, JSONArray clients) {
        try {
            for (int p = 0; p < clients.length(); p++) {
                ClientsModel clientsModel = new ClientsModel();
                JSONObject jsonObject = clients.getJSONObject(p);
                clientsModel.setClient_id(jsonObject.getString("id"));
                clientsModel.setClient_name(jsonObject.getString("name"));
                clientsModel.setClient_type(jsonObject.getString("type"));
                selected_clients_list.add(clientsModel);
            }
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        try {
            for (int t = 0; t < members.length(); t++) {
                TeamModel teamModel = new TeamModel();
                JSONObject jsonObject = members.getJSONObject(t);
                teamModel.setTm_id(jsonObject.getString("id"));
                teamModel.setTm_name(jsonObject.getString("name"));
                selected_tm_list.add(teamModel);
            }
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        try {
            updateDisplay();
            if (!selected_clients_list.isEmpty()) {
//                    callClientsWebservice();
                loadSelectedClients();
            }
            if (!selected_tm_list.isEmpty()) {
//                    callTMWebservice();
                loadSelectedTM();
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
//        {
//            "error": false,
//                "id": "65264766fffd8f05faaf6152",
//                "clients": [],
//            "members": [],
//            "corporate": [
//            {
//                "id": "64f18b78fffd8f4f4623ea3f",
//                    "name": "Perficient Technologent",
//                    "type": "corporate"
//            }
//    ]
//        }
    }

    {
//                        "error": false,
//                            "attachment_type": "clients",
//                            "clients": [
//                        {
//                            "id": "63ae641ea1db720425b4ab0d",
//                                "name": "Soundarya Vembaiyan Stage",
//                                "type": "consumer"
//                        },
//                        {
//                            "id": "63e4b0a4a1db726736fa5a8a",
//                                "name": "Soundarya StagContent Firm",
//                                "type": "entity"
//                        }
//    ]
//                    }
    }
}
