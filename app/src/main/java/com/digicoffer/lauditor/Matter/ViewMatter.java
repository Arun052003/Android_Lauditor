package com.digicoffer.lauditor.Matter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.Matter.Adapters.ViewMatterAdapter;
import com.digicoffer.lauditor.Matter.Models.ClientsModel;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.HistoryModel;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.Matter.Models.TeamModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.Members.GroupsAdapter;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ViewMatter extends Fragment implements AsyncTaskCompleteListener, ViewMatterAdapter.InterfaceListener {
    TextInputLayout search_matter;
    LinearLayout linear_notes;
    RecyclerView rv_matter_list;
    CardView cv_client_details;
    ViewMatterModel viewMatterModel1;
    RecyclerView rv_group_update;
    TextInputEditText et_search_matter;
    public static String FLAG = "";
    AlertDialog progressDialog;
    ArrayList<ViewMatterModel> matterList = new ArrayList<>();
    ArrayList<HistoryModel> historyList = new ArrayList<>();
    ArrayList<ViewGroupModel> groupsArrayList = new ArrayList<>();
    ArrayList<ActionModel> actions_List = new ArrayList<ActionModel>();
    ArrayList<ViewMatterModel> existing_clients = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_documents = new ArrayList<>();
    ArrayList<ViewMatterModel> timesheets = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_members = new ArrayList<>();
    GroupsAdapter groupsAdapter = null;
    ArrayList<ViewGroupModel> groupsList = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_advocates = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_groups = new ArrayList<>();
    ArrayList<GroupsModel> list_item = new ArrayList<>();
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<TeamModel> tmList = new ArrayList<>();
    TextInputEditText et_search_members;
    Matter matter;
    int i;
    String TimeLineId = "";
    String Header_name = "";
    String Matter_id = "";
    String Matter_Status = "";
    String Matter_Title = "";
    String Case_Number = "";
    GroupsModel groupsModel;
    //private ArrayList<GroupsModel> groupsList;
    private Activity v;
    ConstraintLayout con_id;
    ArrayList<MatterModel> matterArraylist;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_matter, container, false);
        rv_matter_list = view.findViewById(R.id.rv_matter_list);
        search_matter = view.findViewById(R.id.search_matter);
        et_search_matter = view.findViewById(R.id.et_search_matter);
        et_search_matter.setHint("Type to Search");
        et_search_matter.setTextSize(15);
        cv_client_details = view.findViewById(R.id.cv_client_details);
        con_id = view.findViewById(R.id.con_id);
        rv_group_update = view.findViewById(R.id.rv_group_update);
        matter = (Matter) getParentFragment();
        callMatterListWebservice();
        return view;
    }

    public void callMatterListWebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT), "Matter List", postdata.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog != null && progressDialog.isShowing())
            AndroidUtils.dismiss_dialog(progressDialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                boolean error = result.getBoolean("error");

                if (httpResult.getRequestType().equals("Matter List")) {
                    if (error) {
                        String msg = result.getString("msg");
                        AndroidUtils.showToast(msg, getContext());
                    } else {
                        JSONArray matters = result.getJSONArray("matters");
                        try {
                            loadMattersList(matters);
                        } catch (Exception e) {
                            AndroidUtils.showAlert(e.getMessage(), getContext());
                            e.fillInStackTrace();
                        }
                    }
                } else if (httpResult.getRequestType().equals("TimeLine")) {
                    if (error) {
                        String msg = result.getString("msg");
                        AndroidUtils.showToast(msg, getContext());
                    } else {
                        loadHistory(result);
                    }
                } else if (httpResult.getRequestType().equals("Notes")) {
                    historyList.clear();
                    callTimeLineWebservice();
                } else if (httpResult.getRequestType().equals("Groups")) {
                    groupsArrayList.clear();
                    JSONArray data = result.getJSONArray("data");
                    loadViewGroups(data);
                } else if (httpResult.getRequestType().equals("Update Groups")) {
                    groupsArrayList.clear();
                    callMatterListWebservice();

                } else if (httpResult.getRequestType().equals("Update Matter")) {

                    String msg = result.getString("msg");
                    Log.d("Message", msg);
                    if (error) {

                        AndroidUtils.showToast(msg, getContext());
                    } else {

                        rv_matter_list.removeAllViews();

                        callMatterListWebservice();
                        AndroidUtils.showToast(msg, getContext());
//                        viewMatter();
                    }

                } else if (httpResult.getRequestType().equals("Delete Matter")) {
                    String msg = result.getString("msg");
                    if (error) {

                        AndroidUtils.showToast(msg, getContext());
                    } else {

                        rv_matter_list.removeAllViews();
                        callMatterListWebservice();
                        AndroidUtils.showToast(msg, getContext());
//                        viewMatter();
                    }
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
    }

    private void viewMatter() {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ViewMatter matterInformation = new ViewMatter();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void openViewGroupsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.update_groups_popup, null);
        LinearLayout ll_groups = view.findViewById(R.id.ll_groups);
        rv_group_update = view.findViewById(R.id.rv_group_update);
        AppCompatButton btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        AppCompatButton btn_create = view.findViewById(R.id.btn_create);
        btn_create.setText(R.string.update);
        ImageView close_details = view.findViewById(R.id.close_details);
        et_search_members = view.findViewById(R.id.et_search_members);
        et_search_members.setHint(R.string.groups);
        et_search_members.setCompoundDrawables(null, null, getContext().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent), null);
        CheckBox chk_select_all = view.findViewById(R.id.chk_select_all);

//        rv_group_update.setLayoutManager(new GridLayoutManager(getContext(), 1));
        groupsAdapter = new GroupsAdapter(groupsArrayList);
        rv_group_update.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_group_update.setAdapter(groupsAdapter);
        rv_group_update.setHasFixedSize(true);
        et_search_members.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                groupsAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
//        et_search_members.setText("group");
//        et_search_members.setText("");
        rv_group_update.refreshDrawableState();

        // RecyclerView rv_groups = view.findViewById(R.id.rv_groups);
        // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //rv_groups.setLayoutManager(layoutManager);

        //  String ADAPTER_TAG = "UGM";
        //   GroupsAdapter groupsAdapter = new GroupsAdapter(groupsList, clientsList, tmList,groupsArrayList, ADAPTER_TAG);
        //  rv_groups.setAdapter(groupsAdapter);
        // rv_groups.setHasFixedSize(true);

        /*
        ll_groups.removeAllViews();
        for (i = 0; i < groupsArrayList.size(); i++) {
            View view_groups = LayoutInflater.from(getContext()).inflate(R.layout.select_team_members, null);
            TextView tv_group_name = view_groups.findViewById(R.id.tv_tm_name);
            CheckBox chk_selected = view_groups.findViewById(R.id.chk_selected);
            tv_group_name.setText(groupsArrayList.get(i).getGroup_name());
            chk_selected.setChecked(groupsArrayList.get(i).isChecked());
//            chk_selected.setId(groupsArrayList.get(i).getGroup_id());
            chk_selected.setTag(i);

            chk_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = 0;
                    if (v.getTag() instanceof Integer) {
                        position = (Integer) v.getTag();
                        viewMatterModel = groupsArrayList.get(position);


                        callgroupsWebservice(groupsArrayList);

                    }

//                    int position =i;
                    if (viewMatterModel.isChecked()) {
                        viewMatterModel.setChecked(false);

                    } else {
                        viewMatterModel.setChecked(true);


//                        groupsArrayList.set()
                    }
                    groupsArrayList.set(position, viewMatterModel);
                    CheckBox checkBox = v.findViewById(R.id.chk_selected);
                    checkBox.setChecked(viewMatterModel.isChecked());
//                    groupsArrayList.set(position,viewMatterModel);
//                    groupsArrayList.get(i).setChecked(chk_selected.isChecked());
                }
            });
            ll_groups.addView(view_groups);
        }
        */

        final AlertDialog dialog = builder.create();
        btn_cancel_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsArrayList.clear();
                dialog.dismiss();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
//                callgroupsWebservice(groupsArrayList);
                callUpdateGroupsWebservice(groupsArrayList);
            }
        });
        close_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsArrayList.clear();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void loadViewGroups(JSONArray data) throws JSONException {

        ViewGroupModel viewGroupModel;
        groupsList.clear();
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            viewGroupModel = new ViewGroupModel();
            viewGroupModel.setId(jsonObject.getString("id"));
            String date = jsonObject.getString("created");
            Date date_new = AndroidUtils.stringToDateTimeDefault(date, "yyyy-MM-dd'T'HH:mm:ss.SSS");
            String created = AndroidUtils.getDateToString(date_new, "MMM dd YYYY");
            viewGroupModel.setCreated(created);
            JSONArray members = jsonObject.getJSONArray("members");
            viewGroupModel.setMembers(members);
            viewGroupModel.setDescription(jsonObject.getString("description"));
            viewGroupModel.setName(jsonObject.getString("name"));
            JSONObject group_head = jsonObject.getJSONObject("groupHead");
            viewGroupModel.setGroup_head_id(group_head.getString("id"));
            viewGroupModel.setGroup_head_name(group_head.getString("name"));
            viewGroupModel.setOwner_name(group_head.getString("name"));
            for (int j = 0; j < data.length(); j++) {
                for (int k = 0; k < groupsArrayList.size(); k++) {
                    if (viewGroupModel.getId().matches(groupsArrayList.get(k).getGroup_id())) {
                        viewGroupModel.setChecked(true);
                    }
                }
            }
            groupsList.add(viewGroupModel);
        }
        Log.i("ArrayList", "info" + groupsList.toString());
        String mtag = "VG";
        loadGroupsRecylerview();

    }

    private void loadGroupsRecylerview() {

//        if (groupsList.size() != 0) {
        FLAG = "second_click";
        rv_group_update.setLayoutManager(new GridLayoutManager(getContext(), 1));

        groupsAdapter = new GroupsAdapter(groupsList);
        rv_group_update.setAdapter(groupsAdapter);
        rv_group_update.setHasFixedSize(true);
        et_search_members.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groupsAdapter.getFilter().filter(et_search_members.getText().toString());
            }

        });
        //  btn_cancel_members.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //   public void onClick(View view) {
        //    et_search_members.setText("");
        //  groupsList.clear();
        // callGroupsWebservice();
        // }
        // });

//        }
//        else {
////            cv_members_details.setVisibility(View.GONE);
//        }
    }


    private void callUpdateGroupsWebservice(ArrayList<ViewGroupModel> groupsList) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject postdata = new JSONObject();
            JSONArray group_acls = new JSONArray();
            for (int i = 0; i < groupsList.size(); i++) {
                ViewGroupModel viewGroupModel = groupsList.get(i);
                if (viewGroupModel.isChecked()) {
                    group_acls.put(groupsList.get(i).getGroup_id());
                }
            }
            if (group_acls.length() == 0) {
                AndroidUtils.showToast("Please select atleast one group", getContext());
            } else {
                postdata.put("group_acls", group_acls);
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/legal/" + Matter_id + "/acls", "Update Groups", postdata.toString());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private void loadHistory(JSONObject result) {
        try {
            historyList.clear();
            JSONArray jsonArray = result.getJSONArray("history");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HistoryModel historyModel = new HistoryModel();
                if (jsonObject.has("allday")) {
                    historyModel.setAllday(jsonObject.getBoolean("allday"));
                } else {
                    historyModel.setAllday(true);
                }
                historyModel.setId(jsonObject.getString("id"));
                historyModel.setDescription(jsonObject.getString("description"));
                historyModel.setEvent_type(jsonObject.getString("event_type"));
                historyModel.setFrom_ts(jsonObject.getString("from_ts"));
                historyModel.setTo_ts(jsonObject.getString("to_ts"));
                historyModel.setTitle(jsonObject.getString("title"));
                historyModel.setNotes(jsonObject.getString("notes"));
                historyList.add(historyModel);
            }
        } catch (JSONException e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            e.fillInStackTrace();
        }
        openViewDetailsPopUp();
    }

    public void openViewDetailsPopUp() {
        try {
            matter.View_Details(viewMatterModel1, this, historyList, Header_name);
            con_id.setVisibility(View.GONE);
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    void callEditNotesWebservice(String id, String notes) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject postdata = new JSONObject();
            postdata.put("notes", notes);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "event/notes/" + id, "Notes", postdata.toString());
        } catch (Exception e) {
            if (progressDialog != null || progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
        }
    }

    private void loadMattersList(JSONArray matters) {
        try {
            matterList.clear();
            for (int i = 0; i < matters.length(); i++) {
                JSONObject jsonObject = matters.getJSONObject(i);

                ViewMatterModel viewMatterModel = new ViewMatterModel();
                viewMatterModel.setId(jsonObject.getString("id"));
                if (jsonObject.has("caseNumber")) {
                    viewMatterModel.setCaseNumber(jsonObject.getString("caseNumber"));
                }
                if (jsonObject.has("caseType")) {
                    viewMatterModel.setCasetype(jsonObject.getString("caseType"));
                }
//                viewMatterModel.setClients(jsonObject.getJSONArray("clients"));
                viewMatterModel.setClients(matters.getJSONObject(i).getJSONArray("clients"));

                if (jsonObject.has("courtName")) {
                    viewMatterModel.setCourtName(jsonObject.getString("courtName"));
                }
                if (jsonObject.has("date_of_filling")) {
                    viewMatterModel.setDate_of_filling(jsonObject.getString("date_of_filling"));
                }
                if (jsonObject.has("closedate")) {
                    viewMatterModel.setClosedate(jsonObject.getString("closedate"));
                }
                if (jsonObject.has("matterNumber")) {
                    viewMatterModel.setMatterNumber(jsonObject.getString("matterNumber"));
                }
                if (jsonObject.has("matterType")) {
                    viewMatterModel.setMatterType(jsonObject.getString("matterType"));
                }
                if (jsonObject.has("startdate")) {
                    viewMatterModel.setStartdate(jsonObject.getString("startdate"));
                }
                if (jsonObject.has("timesheets")) {
                    viewMatterModel.setTimesheets(jsonObject.getJSONArray("timesheets"));
                }
                viewMatterModel.setDescription(jsonObject.getString("description"));
                viewMatterModel.setDocuments(jsonObject.getJSONArray("documents"));

//                viewMatterModel.setGroupAcls(jsonObject.getJSONArray("groupAcls"));
//                viewMatterModel.setGroups(jsonObject.getJSONArray("groups"));
                viewMatterModel.setGroupAcls(matters.getJSONObject(i).getJSONArray("groupAcls"));
                viewMatterModel.setGroups(matters.getJSONObject(i).getJSONArray("groups"));

                if (jsonObject.has("hearingDateDetails")) {
                    viewMatterModel.setHearingDateDetails(jsonObject.getJSONObject("hearingDateDetails"));
                }
                viewMatterModel.setIs_editable(jsonObject.getBoolean("is_editable"));
                if (jsonObject.has("judges")) {
                    viewMatterModel.setJudges(jsonObject.getString("judges"));
                }
                if (jsonObject.has("matterClosedDate")) {
                    viewMatterModel.setMatterClosedDate(jsonObject.getString("matterClosedDate"));
                }
                viewMatterModel.setMembers(jsonObject.getJSONArray("members"));
                if (jsonObject.has("nextHearingDate")) {
                    viewMatterModel.setNextHearingDate(jsonObject.getString("nextHearingDate"));
                }
                if (jsonObject.has("opponentAdvocates")) {
                    viewMatterModel.setOpponentAdvocates(jsonObject.getJSONArray("opponentAdvocates"));
                }
                viewMatterModel.setOwner(jsonObject.getJSONObject("owner"));
                viewMatterModel.setPriority(jsonObject.getString("priority"));
                viewMatterModel.setStatus(jsonObject.getString("status"));
                viewMatterModel.setTags(jsonObject.getJSONObject("tags"));
                if (jsonObject.has("tempClients")) {
                    viewMatterModel.setTempClients(jsonObject.getJSONArray("tempClients"));
                }
                if (jsonObject.has("timesheets")) {
                    viewMatterModel.setTimesheets(jsonObject.getJSONArray("timesheets"));
                }

                viewMatterModel.setTemporaryClients(jsonObject.getJSONArray("temporaryClients"));
                viewMatterModel.setTitle(jsonObject.getString("title"));
//                actions_List.clear();
//              viewMatterModel = viewMatterModel;
                matterList.add(viewMatterModel);
            }


            loadMatterRecyclerview();
        } catch (JSONException e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            e.fillInStackTrace();
        }
    }

    private void loadMatterRecyclerview() {
        try {
            rv_matter_list.removeAllViews();
            rv_matter_list.setLayoutManager(new GridLayoutManager(getContext(), 1));
            ViewMatterAdapter viewMatterAdapter = new ViewMatterAdapter(matterList, getContext(), this);
            rv_matter_list.setAdapter(viewMatterAdapter);
            rv_matter_list.setHasFixedSize(true);
//            viewMatterAdapter.notifyDataSetChanged();
            et_search_matter.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    viewMatterAdapter.getFilter().filter(s);
                }

            });
//            viewMatterAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    //   @Override
//    public void View_Details(ViewMatterModel viewMatterModel) {
//        TimeLineId = viewMatterModel.getId();
//        Header_name = viewMatterModel.getTitle();
//        callTimeLineWebservice();
//
////        AndroidUtils.showAlert(viewMatterModel.getTitle(),getContext());
//    }

    private void callTimeLineWebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            Calendar calendar = new GregorianCalendar();
            TimeZone timeZone = calendar.getTimeZone();
            int offset = timeZone.getRawOffset();
            long hours = TimeUnit.MILLISECONDS.toMinutes(offset);
            long timezoneoffset = (-1) * (hours);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT) + "/" + TimeLineId + "/history/" + timezoneoffset, "TimeLine", postdata.toString());

        } catch (Exception e) {
            if (progressDialog != null || progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.fillInStackTrace();
        }
    }

    @Override
    public void DeleteMatter(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList) {
        try {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            ImageView close_details = view.findViewById(R.id.close_documents);

            tv_confirmation.setText("Are you sure you want to Delete " + viewMatterModel.getTitle() + "?");
//                Matter_Status = "Closed";


            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();
            close_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupsArrayList.clear();
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
                    callDeleteMatterWebService(viewMatterModel);
//                    callCloseMatterWebService(viewMatterModel,Matter_Status);
                }
            });

            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    @Override
    public void Edit_Matter_Info(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("viewMatterModel", viewMatterModel);
        Fragment fragment = new matter_edit(viewMatterModel, this);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.id_framelayout, fragment);
        ft.commit();
    }

    public void View_Matter_Page() {
        try {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);

            tv_confirmation.setText(" Do you want to view viewmatter list ");
//                Matter_Status = "Closed";


            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();
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
                    callMatterListWebservice();
//                    callCloseMatterWebService(viewMatterModel,Matter_Status);
                }
            });

            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    private void callDeleteMatterWebService(ViewMatterModel viewMatterModel) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.DELETE, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT) + "/delete/" + viewMatterModel.getId(), "Delete Matter", postdata.toString());

//            WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.DELETE,)

        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.fillInStackTrace();
        }

    }

    @SuppressLint("ResourceType")
    @Override
    public void View_Details(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList) {
        Constants.Matter_CreateOrViewDetails = "View Details";
        viewMatterModel1 = viewMatterModel;
        TimeLineId = viewMatterModel.getId();
        Header_name = viewMatterModel.getTitle();
        Constants.Matter_title = "";
        Constants.Matter_title = viewMatterModel.getTitle();
        callTimeLineWebservice();
       /* Bundle bundle = new Bundle();
        bundle.putParcelable("viewMatterModel", viewMatterModel);
        Fragment fragment = new EditMatterTimeline();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.id_framelayout, fragment);
        ft.commit(); */

    }

    @Override
    public void Update_Group(ViewMatterModel viewMatterModel) {
        try {
            groupsArrayList.clear();
            Matter_id = viewMatterModel.getId();
            for (int j = 0; j < Constants.groupsList_Access.size(); j++) {
                GroupsModel fullGroupList = Constants.groupsList_Access.get(j);
                ViewGroupModel viewgroypModel = new ViewGroupModel();
                viewgroypModel.setGroup_id(fullGroupList.getGroup_id());
                viewgroypModel.setGroup_name(fullGroupList.getGroup_name());
                viewgroypModel.setName(fullGroupList.getGroup_name());
                viewgroypModel.setChecked(false);
                for (int i = 0; i < viewMatterModel.getGroups().length(); i++) {
                    JSONObject jsonObject = viewMatterModel.getGroups().getJSONObject(i);
                    if (jsonObject.getString("id").equalsIgnoreCase(viewgroypModel.getGroup_id())) {
                        viewgroypModel.setChecked(true);
                    }
                }
                groupsArrayList.add(viewgroypModel);
            }
            openViewGroupsPopup();
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
//        callgroupsWebservice();

    }

    private void callgroupsWebservice(ArrayList<ViewGroupModel> groupsArrayList) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", postdata.toString());

        } catch (Exception e) {
            AndroidUtils.showAlert(e.getMessage(), getContext());
            e.fillInStackTrace();
        }
    }

    @Override
    public void Close_Matter(ViewMatterModel viewMatterModel) {
        existing_clients.clear();
        existing_groups.clear();
        existing_documents.clear();
        existing_advocates.clear();
        existing_members.clear();
        for (int i = 0; i < matterList.size(); i++) {
            try {
                if (viewMatterModel.getId().equals(matterList.get(i).getId())) {

                    for (int j = 0; j < matterList.get(i).getClients().length(); j++) {
                        JSONObject clients_obj = matterList.get(i).getClients().getJSONObject(j);
                        ViewMatterModel viewMatterModel1 = new ViewMatterModel();
                        viewMatterModel1.setClient_id(clients_obj.getString("id"));
                        viewMatterModel1.setClient_type(clients_obj.getString("type"));
                        existing_clients.add(viewMatterModel1);
                    }
                    if (matterList.get(i).getOpponentAdvocates() != null) {

                        for (int a = 0; a < matterList.get(i).getOpponentAdvocates().length(); a++) {
                            ViewMatterModel viewMatterModel1 = new ViewMatterModel();
                            JSONObject advocate_obj = matterList.get(i).getOpponentAdvocates().getJSONObject(a);
                            viewMatterModel1.setAdvocate_name(advocate_obj.getString("name"));
                            viewMatterModel1.setNumber(advocate_obj.getString("phone"));
                            viewMatterModel1.setEmail(advocate_obj.getString("email"));
                            existing_advocates.add(viewMatterModel1);
                        }
                    }

                    for (int m = 0; m < matterList.get(i).getMembers().length(); m++) {
                        JSONObject mem_obj = matterList.get(i).getMembers().getJSONObject(m);
                        ViewMatterModel viewMatterModel1 = new ViewMatterModel();
                        viewMatterModel1.setMember_id(mem_obj.getString("id"));
                        viewMatterModel1.setMember_name(mem_obj.getString("name"));
                        existing_members.add(viewMatterModel1);
                    }
                    for (int g = 0; g < matterList.get(i).getGroups().length(); g++) {
                        JSONObject groups_obj = matterList.get(i).getGroups().getJSONObject(g);
                        ViewMatterModel viewMatterModel1 = new ViewMatterModel();
                        viewMatterModel1.setGroup_id(groups_obj.getString("id"));
                        viewMatterModel1.setGroup_name(groups_obj.getString("name"));
                        viewMatterModel1.setCanDelete(groups_obj.getBoolean("canDelete"));
                        existing_groups.add(viewMatterModel1);
                    }
                    for (int k = 0; k < matterList.get(i).getDocuments().length(); k++) {
                        JSONObject doc_obj = matterList.get(i).getDocuments().getJSONObject(k);
                        ViewMatterModel viewMatterModel1 = new ViewMatterModel();
                        viewMatterModel1.setDoc_id(doc_obj.getString("docid"));
                        viewMatterModel1.setDoc_type(doc_obj.getString("doctype"));
                        viewMatterModel1.setUser_id(doc_obj.getString("user_id"));
                        existing_documents.add(viewMatterModel1);
                    }
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
        openCloseMatterPopup(viewMatterModel);

    }

    private void openCloseMatterPopup(ViewMatterModel viewMatterModel) {
        try {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            ImageView close_documents = view.findViewById(R.id.close_documents);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            String reopen_msg = "Are you sure you want to ReOpen " + viewMatterModel.getTitle() + "?";
            String close_msg = "Are you sure you want to Close " + viewMatterModel.getTitle() + "?";

            if (viewMatterModel.getStatus().equals("Closed")) {
                tv_confirmation.setText(reopen_msg);
                Matter_Status = "Active";
            } else {
                tv_confirmation.setText(close_msg);
                Matter_Status = "Closed";
            }
            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();
//            ad_dialog_delete = dialog;
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            close_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        callCloseMatterWebService(viewMatterModel, Matter_Status);
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        AndroidUtils.showToast(e.getMessage(), getContext());
                    }
                }
            });
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    private void callCloseMatterWebService(ViewMatterModel viewMatterModel, String matter_Status) {
        progressDialog = AndroidUtils.get_progress(getActivity());
        try {
            JSONObject postdata = new JSONObject();
            JSONArray clients = new JSONArray();
            JSONArray documents = new JSONArray();
            JSONArray members = new JSONArray();
            JSONArray groups = new JSONArray();
            JSONArray group_acls = new JSONArray();
            JSONArray advocates = new JSONArray();
            for (int i = 0; i < existing_clients.size(); i++) {
//                ViewMatterModel viewMatterModel1 = viewMatterModel.getClients().get(i);
                ViewMatterModel viewMatterModel1 = existing_clients.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", viewMatterModel1.getClient_id());
                jsonObject.put("type", viewMatterModel1.getClient_type());
                clients.put(jsonObject);
            }
            for (int i = 0; i < existing_documents.size(); i++) {
                ViewMatterModel viewMatterModel1 = existing_documents.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("docid", viewMatterModel1.getDoc_id());
                jsonObject.put("doctype", viewMatterModel1.getDoc_type());
                jsonObject.put("user_id", viewMatterModel1.getUser_id());
                documents.put(jsonObject);
            }
            for (int i = 0; i < existing_members.size(); i++) {
                ViewMatterModel viewMatterModel1 = existing_members.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", viewMatterModel1.getMember_id());
//                jsonObject.put("name",viewMatterModel1.getMember_name());
                members.put(jsonObject);
            }
            for (int i = 0; i < existing_advocates.size(); i++) {
                ViewMatterModel advocateModel = existing_advocates.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", advocateModel.getAdvocate_name());
                jsonObject.put("email", advocateModel.getEmail());
                jsonObject.put("phone", advocateModel.getNumber());
                advocates.put(jsonObject);
            }
            for (int i = 0; i < existing_groups.size(); i++) {
                ViewMatterModel viewMatterModel1 = existing_groups.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", viewMatterModel1.getGroup_id());
                jsonObject.put("name", viewMatterModel1.getGroup_name());
                jsonObject.put("canDelete", viewMatterModel1.isCanDelete());
                groups.put(jsonObject);
                group_acls.put(viewMatterModel1.getGroup_id());
            }
//            for (int i=0;i<existing_groups.size())
            postdata.put("affidavit_filing_date", "");
            postdata.put("affidavit_isfiled", "");
            if (Constants.MATTER_TYPE.equals("Legal")) {
                String inputDate = viewMatterModel.getDate_of_filling();
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy");

                //Rectify the un Parseable date.......
                if (viewMatterModel.getDate_of_filling().isEmpty()) {
//                    Date date = inputFormat.parse(inputDate);
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
//                    String date_of_filling = outputFormat.format(date);
                    postdata.put("case_number", viewMatterModel.getCaseNumber());
                    postdata.put("judges", viewMatterModel.getJudges());
                    postdata.put("case_type", viewMatterModel.getCasetype());
                    postdata.put("opponent_advocates", advocates);
                    postdata.put("court_name", viewMatterModel.getCourtName());
                    postdata.put("date_of_filling", "");
                } else {
                    Date date = inputFormat.parse(inputDate);

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date_of_filling = outputFormat.format(date);
                    postdata.put("case_number", viewMatterModel.getCaseNumber());
                    postdata.put("judges", viewMatterModel.getJudges());
                    postdata.put("case_type", viewMatterModel.getCasetype());
                    postdata.put("opponent_advocates", advocates);
                    postdata.put("court_name", viewMatterModel.getCourtName());
                    postdata.put("date_of_filling", date_of_filling);
                }
                //.......
            } else {
                String inputDate = viewMatterModel.getClosedate();
                String new_start_date = "";
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Date date = inputFormat.parse(inputDate);
//
//                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
//                String date_of_filling = outputFormat.format(date);
//                postdata.put("closedate", date_of_filling);
//
//
//                String startdate = viewMatterModel.getStartdate();
//                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//                Date date2 = inputFormat2.parse(startdate);
//                SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd-MM-yyyy");
//                String new_start_date = outputFormat2.format(date2);

                postdata.put("startdate", new_start_date);
                postdata.put("matter_number", viewMatterModel.getMatterNumber());
                postdata.put("matter_type", viewMatterModel.getMatterType());
            }
            postdata.put("clients", clients);
            postdata.put("description", viewMatterModel.getDescription());
            postdata.put("documents", documents);
            postdata.put("group_acls", group_acls);
            postdata.put("members", members);
            postdata.put("priority", viewMatterModel.getPriority());
            postdata.put("status", matter_Status);
            postdata.put("title", viewMatterModel.getTitle());
//            AndroidUtils.showAlert(postdata.toString(),getContext());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT) + "/update/" + viewMatterModel.getId(), "Update Matter", postdata.toString());
            Log.d("Update_Matter", postdata.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                AndroidUtils.showToast(e.getMessage(), getContext());
            }
            e.fillInStackTrace();
        }
    }

    @Override
    public void ReopenMatter(ViewMatterModel viewMatterModel) {

    }
//
//    public void page_name(String action_list) {
//        if (Objects.equals(action_list, "View Details")) {
//
//        } else if (Objects.equals(action_list, "Edit Matter Info")) {
//
//        } else if (action_list == "Update Groups") {
//
//        } else if (action_list == "Delete") {
//
//        } else if (action_list == "Close Matter/Reopen Matter") {
//
//        } else {
//
//        }
//    }
}
