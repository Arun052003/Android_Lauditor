package com.digicoffer.lauditor.Groups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.Adapters.GroupAdapters;
import com.digicoffer.lauditor.Groups.Adapters.SearchAdapter;
import com.digicoffer.lauditor.Groups.Adapters.ViewGroupsAdpater;
import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Groups.GroupModels.GroupModel;
import com.digicoffer.lauditor.Groups.GroupModels.SearchDo;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.ItemClickListener;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Groups extends Fragment implements AsyncTaskCompleteListener, ViewGroupsAdpater.InterfaceListener {
    RecyclerView rv_select_team_members, rv_view_groups, rv_activity_log;
    TextInputEditText et_search, tv_description, tv_search_name, tv_Client_name_id, et_search_delete;

    //Initializing a search TextInputLayout..
    TextInputLayout search, search_tm, search_delete;
    CardView cv_delete_team;
    //rename of title name
    TextView description_name, category_id, team_member_id, from_id, to_id, client_id_name, search_name, group_name_delete;
    AppCompatButton tv_from_date, tv_to_date;
    TextInputLayout tv_selected_members;
    private NewModel mViewModel;
    ItemClickListener itemClickListener;
    ViewGroupModel new_viewGroupModel = null;
    public static String FLAG = "";
    TextInputLayout tv_search_message, tv_select_team_members;
    ViewGroupsItemClickListener new_itemClickListener;
    String group_head = "";
    ArrayList<SearchDo> searchList = new ArrayList<>();
    Spinner sp_category, sp_team_member;
    String selected_category = "";
    String selected_tm = "";
    ArrayList<ActionModel> actions_List = new ArrayList<ActionModel>();
    TextView tv_create_group, tv_view_group, tv_add_tm, tv_practice_head, tv_group_name, tv_group_description;
    ArrayList<GroupModel> selectedTMArrayList = new ArrayList<GroupModel>();
    public static String TM_TYPE = "";
    ArrayList<ViewGroupModel> viewGroupModelArrayList = new ArrayList<>();
    ArrayList<ViewGroupModel> viewGroupMembersList = new ArrayList<>();
    ArrayList<ViewGroupModel> updateGroupMembersList = new ArrayList<>();
    ArrayList<GroupModel> assignGroupsList = new ArrayList<>();
    private CheckBox chk_select_all;
    CardView cv_groups, cv_details, cv_activity_log;
    GroupAdapters adapter = null;
    ViewGroupsAdpater adapter_delete = null;
    ViewGroupsAdpater adapter_view_groups = null;
    AlertDialog progress_dialog;
    View v = null;
    TextView group_head_name;
    AppCompatButton btn_cancel, btn_save, btn_cancel_edit, btn_update, btn_cancel_gal, btn_search_gal;
    TextInputEditText et_Search;
    LinearLayoutCompat ll_tm, ll_select_all, ll_buttons, ll_group_list, ll_select_tm, ll_edit_groups;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setupOnBackPressed();
        super.onCreate(savedInstanceState);
    }

    private void setupOnBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isEnabled()) {
                    AndroidUtils.showToast("Dashboard", getContext());
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.groups, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        rv_select_team_members = v.findViewById(R.id.rv_selected_tm);
        rv_view_groups = v.findViewById(R.id.rv_view_group);
        et_search = v.findViewById(R.id.et_search);
        tv_group_name = v.findViewById(R.id.tv_group_name);
        tv_group_name.setHint(R.string.group_name);
        tv_group_description = v.findViewById(R.id.tv_description);
        rv_activity_log = v.findViewById(R.id.rv_view_activity_log);
        ll_edit_groups = v.findViewById(R.id.ll_edit_buttons);
        cv_groups = v.findViewById(R.id.cv_details);

        search_delete = v.findViewById(R.id.search_delete);
        search_tm = v.findViewById(R.id.search_tm);
        et_search_delete = v.findViewById(R.id.et_search_delete);
        group_name_delete = v.findViewById(R.id.group_name_delete);
        cv_delete_team = v.findViewById(R.id.cv_delete_team);
        //view_groups components...
        search_name = v.findViewById(R.id.search_name);
        search_name.setText(R.string.search);
        client_id_name = v.findViewById(R.id.client_id_name);
        client_id_name.setText(R.string.client);
        search = v.findViewById(R.id.search);
        from_id = v.findViewById(R.id.from_id);
        from_id.setText("From");
        to_id = v.findViewById(R.id.to_id);
        to_id.setText("To");
        tv_search_name = v.findViewById(R.id.tv_search_name);
        tv_search_name.setHint(R.string.search);
        tv_Client_name_id = v.findViewById(R.id.tv_Client_name_id);
        tv_Client_name_id.setHint(R.string.client);
        //Changing sub_module name..
        tv_description = v.findViewById(R.id.tv_description);
        tv_description.setHint(R.string.description);
        description_name = v.findViewById(R.id.description_name);
        description_name.setText(R.string.description);
        category_id = v.findViewById(R.id.category_id);
        category_id.setText(R.string.category);
        team_member_id = v.findViewById(R.id.team_member_id);
        team_member_id.setText(R.string.team_members);

        tv_select_team_members = v.findViewById(R.id.filledTextField3);
        tv_search_message = v.findViewById(R.id.search_message);
        tv_from_date = v.findViewById(R.id.btn_from_date);
        tv_from_date.setHint("From");
        tv_to_date = v.findViewById(R.id.btn_to_date);
        tv_to_date.setHint("To");
        et_Search = v.findViewById(R.id.et_search_tm);
        group_head_name = (TextView) v.findViewById(R.id.group_head_name);
        cv_details = v.findViewById(R.id.cv_details_2);
        btn_cancel_gal = v.findViewById(R.id.btn_cancel_activity_log);
        btn_search_gal = v.findViewById(R.id.btn_update_activity_log);
        btn_search_gal.setText("Search");

        cv_activity_log = v.findViewById(R.id.cv_details_activity_log);
        tv_selected_members = v.findViewById(R.id.filledTextField3);
        ll_tm = v.findViewById(R.id.linearLayoutCompat1);
        btn_cancel_edit = v.findViewById(R.id.btn_cancel_edit);
        //changing a submit button text to update..
        btn_update = v.findViewById(R.id.btn_update);
        btn_update.setText(R.string.update);
        ll_select_all = v.findViewById(R.id.ll_select_all);
        ll_group_list = v.findViewById(R.id.linearLayoutCompat);
        ll_select_tm = v.findViewById(R.id.linearLayoutCompat2);
        ll_buttons = v.findViewById(R.id.ll_buttons);
        tv_create_group = v.findViewById(R.id.tv_create_group);
        tv_create_group.setText(R.string.create_group);
        tv_view_group = v.findViewById(R.id.tv_view_group);
        tv_view_group.setText(R.string.view_group);
        tv_add_tm = v.findViewById(R.id.add_tm);
        tv_add_tm.setText(R.string.add_team_member);
        tv_practice_head = v.findViewById(R.id.add_phead);
        tv_practice_head.setText(R.string.add_practice_head);
        sp_category = v.findViewById(R.id.sp_category);
        sp_team_member = v.findViewById(R.id.sp_team_member);
        btn_cancel = v.findViewById(R.id.btn_cancel);
        chk_select_all = v.findViewById(R.id.chk_select_all);
        btn_save = (AppCompatButton) v.findViewById(R.id.btn_save);
        //Making a view group as a default view...
        String data = "View Groups";
        setViewModelData(data);
        ViewGroupsData();
        tv_view_group.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        //tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_view_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "View Groups";
                setViewModelData(data);
                ViewGroupsData();
            }
        });
        tv_practice_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (assignGroupsList.size() == 0) {
                    AndroidUtils.showToast("Please select atleast one Group Head", getContext());
                } else {
                    ll_select_all.setVisibility(View.GONE);
                    tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
                    tv_practice_head.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
                }
            }
        });
        tv_create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unhide_delete_data();
                cv_delete_team.setVisibility(View.GONE);
                tv_view_group.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                tv_create_group.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                assignGroupsList.clear();
                cv_groups.setVisibility(View.VISIBLE);
                String data = "Create Groups";
                setViewModelData(data);
                ll_edit_groups.setVisibility(View.VISIBLE);
                btn_save.setText(R.string.save);
                btn_cancel.setText(R.string.cancel);

                //Changing a text color in create group module
                tv_create_group.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_view_group.setTextColor(getContext().getResources().getColor(R.color.black));
                tv_add_tm.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_practice_head.setTextColor(getContext().getResources().getColor(R.color.black));
//                cv_details.setVisibility(View.VISIBLE);
                ll_buttons.setVisibility(View.VISIBLE);
//                ll_tm.setVisibility(View.VISIBLE);
//                ll_select_all.setVisibility(View.VISIBLE);
                rv_view_groups.setVisibility(View.GONE);
                //Make search TextInputLayout invisible
                search.setVisibility(View.GONE);
                et_search.setVisibility(View.GONE);
                hideTM();
                viewGroupModelArrayList.removeAll(viewGroupModelArrayList);
                rv_view_groups.removeAllViews();
//                callMembersWebservice();
            }
        });
        tv_add_tm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Changing a text color in Add team-member design when clicking...
                tv_add_tm.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_practice_head.setTextColor(getContext().getResources().getColor(R.color.black));
                tv_practice_head.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                ll_select_all.setVisibility(View.VISIBLE);
                chk_select_all.setChecked(false);
                if (selectedTMArrayList.size() == 0) {
                    AndroidUtils.showToast("No team Members selected", getContext());
                } else {
                    adapter.selectOrDeselectAll(false);
                    adapter.getList_item().clear();
                    selectedTMArrayList.clear();
                    assignGroupsList.clear();
                    rv_select_team_members.removeAllViews();
                    callMembersWebservice();
                }
            }
        });
//        if ()
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_group_name.getText().toString().equals("")) {
                    tv_group_name.setError("Group name Required");
                    AndroidUtils.showToast("Group name Required", getContext());
                    assignGroupsList.clear();
                } else if (tv_group_description.getText().toString().equals("")) {
                    tv_group_description.setError("Description Required");
                    AndroidUtils.showToast("Description Required", getContext());
                    assignGroupsList.clear();
                } else {
                    ArrayList<GroupModel> arraylist = new ArrayList<>();
                    callCreateGroupWebservice(tv_group_name.getText().toString().trim(), tv_group_description.getText().toString().trim(), arraylist, "");
                }
            }
        });
        btn_cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_group_name.setText("");
                tv_group_description.setText("");
                assignGroupsList.clear();
                selectedTMArrayList.clear();
            }
        });
        hideTM();
        FLAG = "first_click";
        tv_select_team_members.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (FLAG!="first_click"){
                if (FLAG != "second_click") {
                    unhideTM();
                    callMembersWebservice();
                } else {
                    FLAG = "first_click";

                    hideTM();
                    selectedTMArrayList.clear();
                }
//                }else{
//                    FLAG="first_click";
//                }
            }
        });
        try {
//            callMembersWebservice();
//            loadRecylcerview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViewModelData(String data) {
        mViewModel.setData(data);
    }

    private void unhideTM() {
        ll_tm.setVisibility(View.VISIBLE);
        cv_details.setVisibility(View.VISIBLE);
        ll_edit_groups.setVisibility(View.GONE);
        ll_select_all.setVisibility(View.VISIBLE);
        btn_update.setText("Update");
    }

    private void hideTM() {
//        cv_groups.setVisibility(View.VISIBLE);
        ll_tm.setVisibility(View.GONE);
        et_Search.setText("");
        cv_details.setVisibility(View.GONE);
        ll_edit_groups.setVisibility(View.VISIBLE);
        ll_select_all.setVisibility(View.GONE);
        group_head_name.setVisibility(View.GONE);
        group_head_name.setText("");
        btn_update.setText("Save");
        updateGroupMembersList.clear();
        selectedTMArrayList.clear();
        actions_List.clear();
        assignGroupsList.clear();
        searchList.clear();
        viewGroupMembersList.clear();
        viewGroupModelArrayList.clear();
    }

    private void ViewGroupsData() {
        mViewModel.setData("View Groups");
        //Changing a text color in view group module
        tv_view_group.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        tv_view_group.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_create_group.setTextColor(getContext().getResources().getColor(R.color.black));
        tv_create_group.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_group_name.setText("");
        tv_group_description.setText("");
        cv_groups.setVisibility(View.GONE);
//        ll_buttons.setVisibility(View.GONE);
        ll_tm.setVisibility(View.GONE);
        ll_select_all.setVisibility(View.GONE);
        selectedTMArrayList.removeAll(selectedTMArrayList);
        rv_select_team_members.removeAllViews();
        cv_details.setVisibility(View.GONE);
        rv_view_groups.setVisibility(View.VISIBLE);
        //Make search TextInputLayout Visible
        search.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        et_search.setText("");
        callViewGroupsWebservice();
    }

    private void callMembersWebservice() {
        try {
            JSONObject postdata = new JSONObject();
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/member/groups", "Get Members", postdata.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void callViewGroupsWebservice() {

        try {
            JSONObject postdata = new JSONObject();
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Get Groups", postdata.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void loadViewGroupsRecylerview(String TAG_TYPE, ArrayList<ViewGroupModel> viewGroupModelArrayList) {
        if (TAG_TYPE == "VG") {
            rv_view_groups.setLayoutManager(new GridLayoutManager(getContext(), 1));
            adapter_view_groups = new ViewGroupsAdpater(viewGroupModelArrayList, getContext(), this, TAG_TYPE, new_itemClickListener, Groups.this);
            rv_view_groups.setAdapter(adapter_view_groups);
            rv_view_groups.setHasFixedSize(true);
        } else {
            try {
                rv_select_team_members.setLayoutManager(new GridLayoutManager(getContext(), 1));
                if (TAG_TYPE == "DG") {
//                    et_search_delete.setVisibility(View.VISIBLE);
                    adapter_view_groups = new ViewGroupsAdpater(viewGroupModelArrayList, getContext(), this, TAG_TYPE, new_itemClickListener, Groups.this);
                    Log.i("TAG", "List" + TAG_TYPE);
                    rv_select_team_members.setAdapter(adapter_view_groups);
                    rv_select_team_members.setHasFixedSize(true);
                } else {
                    adapter_view_groups = new ViewGroupsAdpater(viewGroupMembersList, getContext(), this, TAG_TYPE, new_itemClickListener, Groups.this);
                    rv_select_team_members.setAdapter(adapter_view_groups);
                    rv_select_team_members.setHasFixedSize(true);
                }
//            ViewGroupsAdpater finalAdapter_view_groups = adapter_view_groups;
            } catch (Exception e) {
                Log.e("Tag", "Error" + e.getMessage());
                e.printStackTrace();
            }
        }
        new_itemClickListener = new ViewGroupsItemClickListener() {
            @Override
            public void onClick(String s) {
                rv_view_groups.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter_view_groups.notifyDataSetChanged();
                        group_head = s;
//                        AndroidUtils.showToast("Selected:"+s, getContext());
                    }
                });
            }
        };
        et_search_delete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter_view_groups.getFilter().filter(et_search_delete.getText().toString());
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter_view_groups.getFilter().filter(et_search.getText().toString());
            }
        });
    }

    private void loadRecylcerview(ArrayList<GroupModel> selectedTMArrayList, String tmType) {
        tv_create_group.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_view_group.setTextColor(getContext().getResources().getColor(R.color.black));
        FLAG = "second_click";
        if (tmType == "TM") {
            ll_select_all.setVisibility(View.VISIBLE);
//            When clicking cancel button
            //Changing a text color for the Add-Group head and Add-Team member module..
            tv_add_tm.setTextColor(getContext().getResources().getColor(R.color.white));
            tv_practice_head.setTextColor(getContext().getResources().getColor(R.color.black));
            tv_practice_head.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
            tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        } else {
            ll_select_all.setVisibility(View.VISIBLE);
            tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
            tv_practice_head.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
//            When Clicking Save button
            //Changing a text color for the Add-group head and Add-team member module..
            tv_add_tm.setTextColor(getContext().getResources().getColor(R.color.black));
            tv_practice_head.setTextColor(getContext().getResources().getColor(R.color.white));
        }
        rv_select_team_members.removeAllViews();
        rv_select_team_members.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new GroupAdapters(selectedTMArrayList, tmType, itemClickListener, Groups.this);
        rv_select_team_members.setAdapter(adapter);
        rv_select_team_members.setHasFixedSize(true);
//        rv_select_team_members.notify();
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(et_Search.getText().toString());
            }
        });
        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String s) {
                rv_select_team_members.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        group_head = s;
//                        AndroidUtils.showToast("Selected:"+s, getContext());
                    }
                });
            }
        };
        chk_select_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chk_select_all.isChecked()) {
                    adapter.selectOrDeselectAll(true);
                } else {
                    adapter.selectOrDeselectAll(false);
                }
            }
        });
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (tmType == "TM") {
//                        ll_select_all.setVisibility(View.GONE);
                        et_Search.setText("");
                        assignGroupHead(adapter.getList_item());
                    } else {
                        ll_tm.setVisibility(View.GONE);
                        ll_select_all.setVisibility(View.GONE);
                        search.setVisibility(View.GONE);
                        try {
                            mViewModel.setData("View Groups");
                            callCreateGroupWebservice(tv_group_name.getText().toString().trim(), tv_group_description.getText().toString().trim(), adapter.getList_item(), group_head);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_practice_head.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                tv_add_tm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                chk_select_all.setChecked(false);
                ll_select_all.setVisibility(View.VISIBLE);
                if (selectedTMArrayList.size() == 0) {
                    AndroidUtils.showToast("No team Members selected", getContext());
                } else {
                    adapter.selectOrDeselectAll(false);
                    adapter.getList_item().clear();
                    selectedTMArrayList.clear();
                    rv_select_team_members.removeAllViews();
                    callMembersWebservice();
                }
//                selectedTMArrayList.clear();
//                adapter.getList_item().clear();
            }
        });
    }

    private void callCreateGroupWebservice(String tv_group_name, String tv_group_description, ArrayList<GroupModel> list_item, String group_head) {
        try {
            JSONObject postData = new JSONObject();
            JSONArray members = new JSONArray();
            for (int i = 0; i < list_item.size(); i++) {
                GroupModel model = list_item.get(i);
                if (model.isChecked()) {
                    members.put(model.getId());
                }
            }
            if (members.length() != 0 && group_head != "") {
                postData.put("name", tv_group_name);
                postData.put("description", tv_group_description);
                postData.put("groupHead", group_head);
                postData.put("members", members);
//                AndroidUtils.showToast("Selected:"+members+" "+"GH"+group_head,getContext());
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/group", "Create Groups", postData.toString());
            } else if (members.length() != 0 && group_head == "") {
                AndroidUtils.showToast("Please select a group head", getContext());
            } else {
                postData.put("name", tv_group_name);
                postData.put("description", tv_group_description);
                postData.put("groupHead", group_head);
                postData.put("members", members);
//                AndroidUtils.showToast("Selected:"+members+" "+"GH"+group_head,getContext());
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/group", "Create Groups", postData.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignGroupHead(ArrayList<GroupModel> list_item) {
//        selectedTMArrayList.clear();
        for (int i = 0; i < list_item.size(); i++) {
            GroupModel groupModel = list_item.get(i);
            if (groupModel.isChecked()) {
                assignGroupsList.add(groupModel);
            }
        }
        TM_TYPE = "GH";
        if (assignGroupsList.size() != 0) {
            if (tv_group_name.getText().toString().equals("")) {
                tv_group_name.setError("Group name Required");
                AndroidUtils.showToast("Group name Required", getContext());
                assignGroupsList.clear();
            } else if (tv_group_description.getText().toString().equals("")) {
                tv_group_description.setError("Description Required");
                AndroidUtils.showToast("Description Required", getContext());
                assignGroupsList.clear();
            } else {
                loadRecylcerview(assignGroupsList, TM_TYPE);
            }
        } else {
            AndroidUtils.showToast("Please select atleast one team member", getContext());
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("Get Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadViewGroups(data);
                } else if (httpResult.getRequestType().equals("Get Members")) {
                    JSONObject data = result.getJSONObject("data");
                    JSONArray users = data.getJSONArray("users");
                    loadMembers(users);

                } else if (httpResult.getRequestType().equals("Get Team Members")) {
                    JSONObject data = result.getJSONObject("data");
                    JSONArray users = data.getJSONArray("users");

                    loadTeamMembers(users);

                } else if (httpResult.getRequestType().equals("Create Groups")) {
//                    JSONObject jsonObject = result.getJSONObject("msg");
                    chk_select_all.setChecked(false);
                    group_head = "";
                    ViewGroupsData();
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                } else if (httpResult.getRequestType().equals("Update Groups")) {
                    if (httpResult.getStatus_code() == 200) {
                        unhideData();
                        ViewGroupsData();
                        clear_list();
                        AndroidUtils.showToast(result.getString("msg"), getContext());
                    } else {
                        AndroidUtils.showToast(result.getString("msg"), getContext());

                    }
                } else if (httpResult.getRequestType().equals("Delete Groups")) {
                    ViewGroupsData();
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    viewGroupModelArrayList.clear();
                    viewGroupMembersList.clear();
                    selectedTMArrayList.clear();
                    adapter.getList_item().clear();
                    updateGroupMembersList.clear();
                } else if (httpResult.getRequestType().equals("Update Group Head")) {
                    unhideData();

                    ViewGroupsData();
                    group_head = "";
                    viewGroupMembersList.clear();
                    viewGroupModelArrayList.clear();

                    AndroidUtils.showToast(result.getString("msg"), getContext());
                } else if (httpResult.getRequestType().equals("Search Results")) {
                    JSONArray data = result.getJSONArray("data");
                    loadSearchResults(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSearchResults(JSONArray data) throws JSONException {
        searchList.clear();
        for (int i = 0; i < data.length(); i++) {
            SearchDo searchDo = new SearchDo();
            JSONObject jsonObject = data.getJSONObject(i);
            searchDo.setCategory(jsonObject.getString("category"));
            searchDo.setTimestamp(jsonObject.getString("timestamp"));
            searchDo.setMsg(jsonObject.getString("msg"));
            searchList.add(searchDo);
        }
        if (data.length() != 0) {
            tv_search_message.setVisibility(View.VISIBLE);
        } else {
            tv_search_message.setVisibility(View.GONE);
        }
        loadSearchRecyclerview();
    }

    private void loadSearchRecyclerview() {
        rv_activity_log.setLayoutManager(new GridLayoutManager(getContext(), 1));
        SearchAdapter adapter = new SearchAdapter(searchList);
        rv_activity_log.setAdapter(adapter);
        rv_activity_log.setHasFixedSize(true);
    }

    private void loadTeamMembers(JSONArray users) throws JSONException {
        GroupModel groupModel;
        selectedTMArrayList.clear();
        for (int i = 0; i < users.length(); i++) {
            JSONObject jsonObject = users.getJSONObject(i);
            groupModel = new GroupModel();
            groupModel.setId(jsonObject.getString("id"));
            groupModel.setName(jsonObject.getString("name"));
            for (int a = 0; a < users.length(); a++) {
                for (int m = 0; m < updateGroupMembersList.size(); m++) {
                    if (groupModel.getId().matches(new_viewGroupModel.getGroup_head_id())) {
                        groupModel.setIsenabled(false);
                    } else {
                        groupModel.setIsenabled(true);
                    }
                }
            }
            for (int j = 0; j < users.length(); j++) {
                for (int k = 0; k < updateGroupMembersList.size(); k++) {
                    if (groupModel.getId().matches(updateGroupMembersList.get(k).getGroup_id())) {
                        groupModel.setChecked(true);
                    }
                }
            }
            selectedTMArrayList.add(groupModel);
        }
        if (selectedTMArrayList.size() != 0) {
            group_head_name.setVisibility(View.VISIBLE);
            group_head_name.setText(new_viewGroupModel.getGroup_head_name());
        } else {
            group_head_name.setVisibility(View.GONE);
            group_head_name.setText("");
        }
        TM_TYPE = "TM";
        loadTeamRecyclerview(selectedTMArrayList, TM_TYPE);
    }

    private void loadTeamRecyclerview(ArrayList<GroupModel> selectedTMArrayList, String tmType) {
        rv_select_team_members.removeAllViews();
        rv_select_team_members.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new GroupAdapters(selectedTMArrayList, tmType, itemClickListener, Groups.this);
        rv_select_team_members.setAdapter(adapter);
        rv_select_team_members.setHasFixedSize(true);
//        rv_select_team_members.notify();
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(et_Search.getText().toString());
            }
        });
        chk_select_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chk_select_all.isChecked()) {
                    adapter.selectOrDeselectAll(true);
                } else {
                    adapter.selectOrDeselectAll(false);
                }
            }
        });
        AppCompatButton btn_cancel_selected_tm = (AppCompatButton) v.findViewById(R.id.btn_cancel);
        btn_cancel_selected_tm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                unhideData();
                ll_select_all.setVisibility(View.GONE);
                chk_select_all.setChecked(false);
                et_Search.setText("");
                ViewGroupsData();
                clear_list();
            }
        });
        AppCompatButton btn_save_selected_tm = (AppCompatButton) v.findViewById(R.id.btn_save);
        btn_save_selected_tm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String update_type = "UGM";
                try {
                    callUpdateGroups("", "", new_viewGroupModel.getId(), update_type, adapter.getList_item());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                callUpdateUGMWebservice(adapter.getList_item(););
            }
        });
    }

    private void loadMembers(JSONArray users) throws JSONException {
        GroupModel groupModel;
        selectedTMArrayList.clear();
        for (int i = 0; i < users.length(); i++) {
            JSONObject jsonObject = users.getJSONObject(i);
            groupModel = new GroupModel();
            groupModel.setId(jsonObject.getString("id"));
            groupModel.setName(jsonObject.getString("name"));
            groupModel.setIsenabled(true);
            selectedTMArrayList.add(groupModel);
        }
        TM_TYPE = "TM";
        loadRecylcerview(selectedTMArrayList, TM_TYPE);
    }

    private void loadViewGroups(JSONArray data) throws JSONException {
        ViewGroupModel viewGroupModel;
        viewGroupModelArrayList.clear();
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
            viewGroupModelArrayList.add(viewGroupModel);
        }
        Log.i("ArrayList", "info" + viewGroupModelArrayList.toString());
        String mtag = "VG";
        loadViewGroupsRecylerview(mtag, viewGroupModelArrayList);
    }

    public void EditGroup(ViewGroupModel viewGroupModel) {

        btn_update.setText(R.string.update);
        hideData();
        tv_group_name.setText(viewGroupModel.getName());
        tv_group_description.setText(viewGroupModel.getDescription());
        cv_delete_team.setVisibility(View.GONE);
        ll_edit_groups.setVisibility(View.VISIBLE);
        btn_cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setData("View Groups");
                unhideData();
                ViewGroupsData();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unhideData();
                if (tv_group_name.getText().toString().equals("")) {
                    tv_group_name.setError("Group name Required");
                    AndroidUtils.showToast("Group name Required", getContext());
//                        assignGroupsList.clear();
                } else if (tv_group_description.getText().toString().equals("")) {
                    tv_group_description.setError("Description Required");
                    AndroidUtils.showToast("Description Required", getContext());
//                        assignGroupsList.clear();
                } else {
                    try {
                        String update_type = "EG";
                        ArrayList<GroupModel> new_list = new ArrayList<>();
                        callUpdateGroups(tv_group_name.getText().toString().trim(), tv_group_description.getText().toString().trim(), viewGroupModel.getId(), update_type, new_list);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void DeleteGroup(ViewGroupModel viewGroupModel, ArrayList<ViewGroupModel> itemsArrayList) {
        hideData();
        hidedelete_data();
        cv_details.setVisibility(View.VISIBLE);
        cv_delete_team.setVisibility(View.VISIBLE);//        delete_spinner_layout.setVisibility(View.VISIBLE);
        for (int i = 0; i < itemsArrayList.size(); i++) {
            if (viewGroupModel.getName().matches(itemsArrayList.get(i).getName())) {
                itemsArrayList.remove(i);
            }
        }
        group_name_delete.setText(viewGroupModel.getName());
        btn_update.setVisibility(View.GONE);
        btn_cancel_edit.setVisibility(View.GONE);
        tv_group_name.setText(viewGroupModel.getName());
        tv_group_name.setEnabled(false);
        tv_group_description.setText(viewGroupModel.getDescription());
        tv_group_description.setEnabled(false);
        AppCompatButton btn_cancel = (AppCompatButton) v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setData("View Groups");
                reverse_data();
                ViewGroupsData();
            }
        });
        AppCompatButton btn_delete = (AppCompatButton) v.findViewById(R.id.btn_save);
        btn_delete.setText(getContext().getResources().getString(R.string.delete));
        Log.i("TAG", "INFO" + itemsArrayList.toString());
        String mtag = "DG";
        loadViewGroupsRecylerview(mtag, itemsArrayList);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cv_details.setVisibility(View.GONE);
                unhideData();
                if (tv_group_name.getText().toString().equals("")) {
                    tv_group_name.setError("Group name Required");
                    AndroidUtils.showToast("Group name Required", getContext());
//                        assignGroupsList.clear();
                } else if (tv_group_description.getText().toString().equals("")) {
                    tv_group_description.setError("Description Required");
                    AndroidUtils.showToast("Description Required", getContext());
//                        assignGroupsList.clear();
                } else {
                    reverse_data();
                    try {
                        callDeleteGroups(viewGroupModel.getId());
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void CGH(final ViewGroupModel viewGroupModel, ArrayList<ViewGroupModel> itemsArrayList) {
//        hideData();
        unhide_delete_data();
        btn_save.setText(R.string.save);
        hide_CGH_UGM_data();
        search_tm.setVisibility(View.GONE);
        try {
            viewGroupMembersList.clear();
            group_head_name.setVisibility(View.VISIBLE);
            Log.i("Tag", "Info: " + viewGroupModel.getGroup_head_name());
            group_head_name.setText(viewGroupModel.getGroup_head_name());
            tv_group_name.setText(viewGroupModel.getName());
            tv_group_description.setText(viewGroupModel.getDescription());
            String mtag = "CGH";

            for (int i = 0; i < viewGroupModel.getMembers().length(); i++) {
                ViewGroupModel viewGroupModel_1 = new ViewGroupModel();
                JSONObject jsonObject = viewGroupModel.getMembers().getJSONObject(i);
                viewGroupModel_1.setGroup_name(jsonObject.getString("name"));
                viewGroupModel_1.setGroup_id(jsonObject.getString("id"));
                viewGroupMembersList.add(viewGroupModel_1);
            }

            loadViewGroupsRecylerview(mtag, viewGroupModelArrayList);
            AppCompatButton btn_cancel_CGH = v.findViewById(R.id.btn_cancel);
            btn_cancel_CGH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewModel.setData("View Groups");
                    unhideData();
                    viewGroupMembersList.clear();
                    viewGroupModelArrayList.clear();
//                    et_search.setText("");
                    ViewGroupsData();
                }
            });
            AppCompatButton btn_save_CGH = v.findViewById(R.id.btn_save);
            btn_save_CGH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewModel.setData("View Groups");
                    if (group_head.equals("")) {
                        AndroidUtils.showToast("Please select a Group Head", getContext());
                    } else {
                        try {
//                            unhideData();
                            callUpdateGroupHeadWebservice(viewGroupModel.getId(), group_head);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
//                    ViewGroupsData();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UGM(ViewGroupModel viewGroupModel) throws JSONException {
        new_viewGroupModel = viewGroupModel;
//        Log.e("Message for UGM ",":");
        unhide_delete_data();
        btn_save.setText(R.string.update);
        hide_CGH_UGM_data();
        ll_select_all.setVisibility(View.VISIBLE);
        for (int i = 0; i < viewGroupModel.getMembers().length(); i++) {
            ViewGroupModel viewGroupModel_1 = new ViewGroupModel();
            JSONObject jsonObject = viewGroupModel.getMembers().getJSONObject(i);
            viewGroupModel_1.setGroup_name(jsonObject.getString("name"));
            viewGroupModel_1.setGroup_id(jsonObject.getString("id"));
            updateGroupMembersList.add(viewGroupModel_1);
        }
        callViewGroupMembersWebservice();
//        callMembersWebservice();
    }

    @Override
    public void GAL(ViewGroupModel viewGroupModel) throws JSONException {
        unhide_delete_data();
        hide_CGH_UGM_data();
        cv_activity_log.setVisibility(View.VISIBLE);
        cv_details.setVisibility(View.GONE);
        actions_List.clear();
        viewGroupMembersList.clear();
//        actions_List.add(new ActionModel("Add|Remove"));
        actions_List.add(new ActionModel("Authorization"));
        actions_List.add(new ActionModel("Groups"));
        actions_List.add(new ActionModel("Team Members"));
        actions_List.add(new ActionModel("Relationships"));
        actions_List.add(new ActionModel("Share"));
        actions_List.add(new ActionModel("Documents"));
        actions_List.add(new ActionModel("Merge PDF"));
        actions_List.add(new ActionModel("Matters"));
        actions_List.add(new ActionModel("Timesheets"));
        for (int i = 0; i < viewGroupModel.getMembers().length(); i++) {
            ViewGroupModel viewGroupModel_1 = new ViewGroupModel();
            JSONObject jsonObject = viewGroupModel.getMembers().getJSONObject(i);
            viewGroupModel_1.setGroup_name(jsonObject.getString("name"));
            viewGroupModel_1.setGroup_id(jsonObject.getString("id"));
            viewGroupMembersList.add(viewGroupModel_1);
        }
        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) getContext(), actions_List);
        sp_category.setAdapter(spinner_adapter);
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_category = actions_List.get(adapterView.getSelectedItemPosition()).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final CommonSpinnerAdapter team_adpater = new CommonSpinnerAdapter((Activity) getContext(), viewGroupMembersList);
        sp_team_member.setAdapter(team_adpater);
        sp_team_member.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_tm = viewGroupMembersList.get(adapterView.getSelectedItemPosition()).getGroup_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tv_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datepicker(tv_from_date);
            }
        });
        tv_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datepicker(tv_to_date);
            }
        });
        btn_cancel_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setData("View Groups");
                unhideData();
                tv_from_date.setText("");
                tv_to_date.setText("");
                searchList.clear();
                rv_activity_log.removeAllViews();
                tv_search_message.setVisibility(View.GONE);
                ViewGroupsData();
            }
        });
        btn_search_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //search layout displaying....
//                String update_type = "UGM";
                try {
                    callSearchResultsWebservice(selected_category, selected_tm, tv_from_date.getText().toString(), tv_to_date.getText().toString(), viewGroupModel.getId());
                    mViewModel.setData("View Groups");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                callUpdateUGMWebservice(adapter.getList_item(););
            }
        });
    }

    private void callSearchResultsWebservice(String selected_category, String selected_tm, String from_date, String to_date, String id) throws JSONException {
        JSONObject postdate = new JSONObject();
        postdate.put("category", selected_category);
        postdate.put("client", "");
        postdate.put("fromDate", from_date);
        postdate.put("search", "");
        postdate.put("tm", selected_tm);
        postdate.put("toDate", to_date);
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/auditlogs/" + id, "Search Results", postdate.toString());
    }

    private void datepicker(Button bt_date) {
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
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                bt_date.setText(sdf.format(myCalendar.getTime()));
            }
        };
        bt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }

    private void callViewGroupMembersWebservice() {
        try {
            JSONObject postdata = new JSONObject();
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/member/groups", "Get Team Members", postdata.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void callUpdateGroupHeadWebservice(String id, String group_head) throws JSONException {
        try {
            JSONObject postData = new JSONObject();
            postData.put("groupHead", group_head);
            Log.i("Tag", "Info:" + id);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PATCH, "v3/group/" + id, "Update Group Head", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hide_CGH_UGM_data() {
        ll_group_list.setVisibility(View.GONE);
        ll_tm.setVisibility(View.GONE);
        ll_select_all.setVisibility(View.GONE);
        ll_select_tm.setVisibility(View.VISIBLE);
        rv_view_groups.setVisibility(View.GONE);
        rv_select_team_members.setVisibility(View.VISIBLE);
        //Make search TextInputLayout invisible
        search.setVisibility(View.GONE);
        et_search.setVisibility(View.GONE);
        cv_groups.setVisibility(View.GONE);
        cv_details.setVisibility(View.VISIBLE);
        ll_edit_groups.setVisibility(View.GONE);
        tv_selected_members.setVisibility(View.GONE);
    }

    private void callDeleteGroups(String id) throws JSONException {
        try {
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.DELETE, "v3/group/" + id + "/" + group_head, "Delete Groups", postData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callUpdateGroups(String group_name, String description, String id, String update_type, ArrayList<GroupModel> list_item) throws JSONException {
        try {
            JSONObject postData = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            if (update_type == "EG") {
                postData.put("name", group_name);
                postData.put("description", description);
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PATCH, "v3/group/" + id, "Update Groups", postData.toString());
            } else if (update_type == "UGM") {
                for (int i = 0; i < list_item.size(); i++) {
                    GroupModel model = list_item.get(i);
                    if (model.isChecked()) {
                        jsonArray.put(model.getId());
                    }
                }
                if (jsonArray.length() != 0) {
                    postData.put("members", jsonArray);
                    WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PATCH, "v3/group/" + id, "Update Groups", postData.toString());

                } else {
                    AndroidUtils.showToast("Please select atleast one team member", getContext());
                }
            }
//            AndroidUtils.showToast(postData.toString(),getContext());
            Log.i("TAG", "Object:" + postData.toString() + ":" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear_list() {
        viewGroupModelArrayList.clear();
        viewGroupMembersList.clear();
        selectedTMArrayList.clear();
        adapter.getList_item().clear();
        updateGroupMembersList.clear();
    }

    private void hideData() {
        ll_group_list.setVisibility(View.GONE);
        ll_tm.setVisibility(View.GONE);
        ll_select_all.setVisibility(View.GONE);
        ll_select_tm.setVisibility(View.VISIBLE);
        rv_view_groups.setVisibility(View.GONE);
        rv_select_team_members.setVisibility(View.VISIBLE);
        //Make search TextInputLayout invisible
        search.setVisibility(View.GONE);
        et_search.setVisibility(View.GONE);
        cv_groups.setVisibility(View.VISIBLE);
        ll_edit_groups.setVisibility(View.VISIBLE);
        tv_selected_members.setVisibility(View.GONE);
    }

    private void reverse_data() {
        tv_group_description.setEnabled(true);
        tv_group_name.setEnabled(true);
        btn_update.setVisibility(View.VISIBLE);
        btn_cancel_edit.setVisibility(View.VISIBLE);
        cv_details.setVisibility(View.GONE);
        unhideData();
//        ViewGroupsData();
    }

    public void hidedelete_data() {
        search_delete.setVisibility(View.VISIBLE);
        et_search_delete.setVisibility(View.VISIBLE);
        search_tm.setVisibility(View.GONE);
    }

    public void unhide_delete_data() {
//        et_search.setVisibility(View.VISIBLE);
        search_delete.setVisibility(View.GONE);
        et_search_delete.setVisibility(View.GONE);
        search_tm.setVisibility(View.VISIBLE);
    }

    public void unhide_cgroup() {
        ll_edit_groups.setVisibility(View.VISIBLE);
    }

    private void unhideData() {
        ll_group_list.setVisibility(View.VISIBLE);
        ll_tm.setVisibility(View.VISIBLE);
        ll_select_all.setVisibility(View.VISIBLE);
        ll_select_tm.setVisibility(View.VISIBLE);
        rv_view_groups.setVisibility(View.VISIBLE);
        //Make search TextInputLayout Visible
        search.setVisibility(View.VISIBLE);
        et_search.setVisibility(View.VISIBLE);
        cv_groups.setVisibility(View.GONE);
        ll_edit_groups.setVisibility(View.GONE);
        tv_selected_members.setVisibility(View.VISIBLE);
        cv_activity_log.setVisibility(View.GONE);
    }

    public void check_select_all(boolean check_status) {
        if (check_status) {
            chk_select_all.setChecked(true);
        } else {
            chk_select_all.setChecked(false);
        }
    }

    public void page_name(String action_list) {
        if (action_list == "Edit Group") {
            mViewModel.setData("Edit Group");
        } else if (action_list == "Delete Group") {
            mViewModel.setData("Delete Group");
        } else if (action_list == "Update Group Members") {
            mViewModel.setData("Update Group Members");
        } else if (action_list == "Change Group Head") {
            mViewModel.setData("Change Group Head");
        } else if (action_list == "Group Activity Log") {
            mViewModel.setData("Group Activity Log");
        } else {
            mViewModel.setData("View Groups");
        }
    }
}

