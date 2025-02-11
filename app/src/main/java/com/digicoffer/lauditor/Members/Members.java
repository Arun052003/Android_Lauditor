
package com.digicoffer.lauditor.Members;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.digicoffer.lauditor.Groups.GroupModels.GroupModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Members extends Fragment implements AsyncTaskCompleteListener, MembersAdapter.EventListener, View.OnClickListener {
    TextView tv_member_name, tv_designation, tv_email, tv_confirm_email, tv_default_rate, tv_create_members, tv_view_members, textView2;
    Spinner sp_default_currency;
    TextInputEditText et_search_members, et_search_teammember;
    private NewModel mViewModel;


    AppCompatButton btn_cancel_members, bt_save_members, bt_cancel, bt_save, btn_cancel_save, btn_create, btn_add;
    RecyclerView rv_selected_member, rv_view_members;
    TextInputLayout tv_assign_groups, search_teammember;

    public static String FLAG = "";
    String default_currency = "";
    CardView cv_details, cv_members_details;
    GroupsAdapter groupsAdapter = null;
    MembersAdapter membersAdapter = null;
    ArrayList<ViewGroupModel> updatedMembersList = new ArrayList<>();
    ArrayList<String> currency_list = new ArrayList<>();
    ArrayList<MembersModel> members_list = new ArrayList<>();
    String TAG = "";
    ArrayList<GroupModel> MembersList = new ArrayList<>();
    ArrayList<ViewGroupModel> groupsList = new ArrayList<>();
    LinearLayoutCompat ll_buttons, ll_new_buttons, ll_save_buttons;
    LinearLayout ll_confirm_email;
    AlertDialog progress_dialog;
    Members members;
    private boolean MemberModelArrayList;
    private Object MembersModel;

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
//                    AndroidUtils.showToast("Dashboard",getContext());
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
        View v = inflater.inflate(R.layout.create_members, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        tv_member_name = v.findViewById(R.id.tv_create_member_name);

        tv_member_name.setHint(R.string.name);
        tv_designation = v.findViewById(R.id.tv_designation);
        tv_designation.setHint(R.string.designation);
        tv_email = v.findViewById(R.id.tv_email);
        tv_email.setHint(R.string.email);
        rv_view_members = v.findViewById(R.id.rv_view_members);
        tv_confirm_email = v.findViewById(R.id.tv_confirm_email);
        tv_confirm_email.setHint(R.string.confirm_email);
        tv_default_rate = v.findViewById(R.id.tv_default_rate);
        tv_default_rate.setHint(R.string.default_rate);
        bt_save = v.findViewById(R.id.btn_update);
        bt_cancel = v.findViewById(R.id.btn_cancel_edit);
        tv_create_members = v.findViewById(R.id.tv_create_members);

        tv_view_members = v.findViewById(R.id.tv_view_members);

        et_search_members = v.findViewById(R.id.et_search_members);
        et_search_teammember = v.findViewById(R.id.et_search_teammember);
        search_teammember = v.findViewById(R.id.search_teammember);
        sp_default_currency = v.findViewById(R.id.sp_default_currency);
        btn_cancel_members = v.findViewById(R.id.btn_cancel_members);
        bt_save_members = v.findViewById(R.id.btn_save_members);

        cv_details = v.findViewById(R.id.cv_details);
        btn_cancel_save = v.findViewById(R.id.btn_cancel_save);
        btn_create = v.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);
        btn_cancel_save.setOnClickListener(this);
        ll_save_buttons = v.findViewById(R.id.ll_save_buttons);
        ll_confirm_email = v.findViewById(R.id.ll_confirm_email);
        ll_buttons = v.findViewById(R.id.linearLayoutCompat);
        cv_members_details = v.findViewById(R.id.cv_details_2);
        ll_new_buttons = v.findViewById(R.id.ll_edit_buttons);
        tv_assign_groups = v.findViewById(R.id.tv_assign_group);
        FLAG = "first_click";
        tv_assign_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FLAG != "second_click") {
                    cv_members_details.setVisibility(View.VISIBLE);
                    callGroupsWebservice();
                    ll_save_buttons.setVisibility(GONE);

                } else {
                    FLAG = "first_click";
//                    cv_members_details.setVisibility(View.VISIBLE);
//                    callGroupsWebservice();
                    cv_members_details.setVisibility(GONE);
                    ll_save_buttons.setVisibility(View.VISIBLE);
                    groupsList.clear();
                }
            }
        });
        tv_member_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Set the height to 50dp if the text is empty
                int minHeightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                tv_member_name.getLayoutParams().height = minHeightInPixels;


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() > 0 && str.contains(" ")) {
//                    tv_member_name.setError("Space is not allowed");
                    AndroidUtils.showAlert("Space is not allowed", getContext());
                    tv_member_name.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        tv_designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Set the height to 50dp if the text is empty
                int minHeightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                tv_designation.getLayoutParams().height = minHeightInPixels;


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() > 0 && str.contains(" ")) {
//                    tv_member_name.setError("Space is not allowed");
                    AndroidUtils.showAlert("Space is not allowed", getContext());
                    tv_designation.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        tv_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Set the height to 50dp if the text is empty
                int minHeightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                tv_email.getLayoutParams().height = minHeightInPixels;


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() > 0 && str.contains(" ")) {
//                    tv_member_name.setError("Space is not allowed");
                    AndroidUtils.showAlert("Space is not allowed", getContext());
                    tv_email.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        tv_confirm_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // Set the height to 50dp if the text is empty
                int minHeightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                tv_confirm_email.getLayoutParams().height = minHeightInPixels;


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() > 0 && str.contains(" ")) {
//                    tv_member_name.setError("Space is not allowed");
                    AndroidUtils.showAlert("Space is not allowed", getContext());
                    tv_confirm_email.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });






        rv_selected_member = v.findViewById(R.id.rv_selected_member);
        String data = "View Members";
        setViewModelData(data);
        ViewMembersData();
        tv_view_members.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        tv_view_members.setTextColor(Color.WHITE);
        tv_create_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
//                cv_members_details.setVisibility(View.GONE);

                tv_create_members.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                tv_view_members.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                ll_confirm_email.setVisibility(View.VISIBLE);
                ll_new_buttons.setVisibility(GONE);
                TAG = "CM";
                String data = "Create Members";
                setViewModelData(data);
                CreateMembersData();
                et_search_teammember.setVisibility(GONE);
                search_teammember.setVisibility(GONE);
                tv_create_members.setTextColor(getContext().getResources().getColor(R.color.white));
                tv_view_members.setTextColor(getContext().getResources().getColor(R.color.black));

            }
        });

        tv_view_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tv_view_members.setTextColor(Color.WHITE);
                String data = "View Members";
                setViewModelData(data);
                ViewMembersData();

                tv_create_members.setTextColor(getContext().getResources().getColor(R.color.black));
                tv_view_members.setTextColor(getContext().getResources().getColor(R.color.white));

            }
        });
        currency_list = getCurrency_list();
        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), currency_list);
        sp_default_currency.setAdapter(adapter);
        sp_default_currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                default_currency = currency_list.get(adapterView.getSelectedItemPosition());
//                AndroidUtils.showToast("selected item is:"+default_currency,getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bt_save_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validation()) {
                    String tag = "Create";
                    String id = "";
                    callCreateMemberWebservice(tv_member_name.getText().toString().trim(), tv_designation.getText().toString().trim(), tv_default_rate.getText().toString().trim(), tv_email.getText().toString().trim(), tv_confirm_email.getText().toString().trim(), tag, id);
                    tv_create_members.setBackgroundResource(R.drawable.button_left_background);
                    tv_create_members.setTextColor(Color.BLACK);
                    tv_view_members.setBackgroundResource(R.drawable.button_right_green_background);
                    tv_view_members.setTextColor(Color.WHITE);
                }

            }
        });

    }

    private void setViewModelData(String data) {
        mViewModel.setData(data);
    }

    private void clearData() {
        tv_member_name.setText("");
        tv_designation.setText("");
//        sp_default_currency.setSelection();
        tv_default_rate.setText("");
        tv_email.setText("");
        tv_confirm_email.setText("");
//        currency_list.clear();
        members_list.clear();
        groupsList.clear();
        updatedMembersList.clear();
        for (int i = 0; i < currency_list.size(); i++) {
            if (currency_list.get(i).equals("USDollar(USD)")) {
                sp_default_currency.setSelection(i);
            }
        }
    }

    private void callGroupsWebservice() {
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Get Groups", postdata.toString());


    }

    boolean validation() {
        boolean status = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]";
        Pattern pattern;
        Matcher matcher;
        if (tv_member_name.getText().toString().equals("")) {
            tv_member_name.setError("Name is required ");
            AndroidUtils.showToast("Name is required ", getContext());
            status = true;
        } else if (tv_designation.getText().toString().equals("")) {
            tv_designation.setError("Designation is required");
            AndroidUtils.showToast("Designation is required", getContext());
            status = true;
        } else if (tv_default_rate.getText().toString().equals("")) {
            tv_default_rate.setError("Hourly rate is required");
            AndroidUtils.showToast("Hourly rate is required", getContext());
            status = true;
        } else if (tv_email.getText().toString().equals("")) {
            tv_email.setError("Email is required");
            AndroidUtils.showToast("Email is required", getContext());
            status = true;
        } else if (tv_confirm_email.getText().toString().equals("")) {
            tv_confirm_email.setError("Confirm email is required");
            AndroidUtils.showToast("Confirm email is required", getContext());
            status = true;
        } else if (!tv_email.getText().toString().equals(tv_confirm_email.getText().toString())) {
            tv_confirm_email.setError("Email and Confirm Email doesn't match");
            AndroidUtils.showToast("Email and Confirm Email doesn't match", getContext());
            status = true;
        } else {
            if (!tv_email.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(tv_email.getText().toString()).matches()) {

                status = false;
            } else {
                tv_email.setError("Enter a valid email address");
                AndroidUtils.showToast("Enter a valid email address", getContext());
                status = true;
            }
        }
//        else if(!tv_email.getText().toString().trim().equals("")) {
//
//                pattern = Pattern.compile(emailPattern);
//                matcher = pattern.matcher(tv_email.getText().toString());
//                if (!matcher.matches()) {
//
//                tv_email.requestFocus();
//                    status = true;
//                }
//            }else{
//                tv_email.setError("Email is required");
//                AndroidUtils.showToast("Email is required", getContext());
//                status = true;
//            }

        return status;
    }

    private void CreateMembersData() {
        if (TAG == "UGA") {
            cv_details.setVisibility(GONE);
            ll_new_buttons.setVisibility(GONE);
            ll_buttons.setVisibility(GONE);
            cv_members_details.setVisibility(View.VISIBLE);
            members_list.clear();
        } else {
            cv_details.setVisibility(View.VISIBLE);
            cv_members_details.setVisibility(GONE);
            members_list.clear();
        }
        if (TAG == "CM" || TAG == "UGA") {
            callGroupsWebservice();
        }

        rv_view_members.removeAllViews();
    }

    private void ViewMembersData() {
        ll_buttons.setVisibility(View.VISIBLE);
        tv_view_members.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        tv_create_members.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        cv_details.setVisibility(GONE);
        cv_members_details.setVisibility(GONE);
        callViewGroupsWebservice();

        et_search_members.setText("");
        search_teammember.setVisibility(View.VISIBLE);
        et_search_teammember.setVisibility(View.VISIBLE);
        et_search_teammember.setText("");
        members_list.clear();
        groupsList.clear();
        updatedMembersList.clear();

    }


    private void callViewGroupsWebservice() {

        try {
            JSONObject postdata = new JSONObject();
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/members", "Get Members", postdata.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void callCreateMemberWebservice(String name, String designation, String default_rate, String email, String confirm_email, String tag, String id) {
        try {
            JSONObject postdata = new JSONObject();
            JSONArray groups = new JSONArray();
            if (groupsAdapter != null) {
                for (int i = 0; i < groupsAdapter.getList_item().size(); i++) {
                    ViewGroupModel viewGroupModel = groupsAdapter.getList_item().get(i);
                    if (viewGroupModel.isChecked()) {
                        groups.put(viewGroupModel.getId());
                    }
                }
            }

            progress_dialog = AndroidUtils.get_progress(getActivity());
            if (tag == "Create" || tag == "Update") {
                postdata.put("currency", default_currency);
                postdata.put("defaultRate", default_rate);
                postdata.put("designation", designation);
                postdata.put("email", email);
                postdata.put("emailConfirm", confirm_email);
                postdata.put("name", name);
            }
            if (tag == "Create" || tag == "Update" || tag == "UGA")
                postdata.put("groups", groups);
            if (tag == "RP") {
                postdata.put("memberId", id);
            }
            if (tag == "Create") {
                Log.i("Tag", "Info:" + postdata.toString());
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/member", "Create Members", postdata.toString());

            } else if (tag == "RP") {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/member/resetpwd", "Reset Password", postdata.toString());

            } else if (tag == "Delete") {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.DELETE, "v3/member/" + id, "Delete Member", postdata.toString());


            } else {
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PATCH, "v3/member/" + id, "Update Members", postdata.toString());
            }
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    @Override
    public void onClick(View view) {
        try {


            switch (view.getId()) {
                case R.id.btn_create:
                    if (!validation()) {
                        String tag = "Create";
                        String id = "";
                        callCreateMemberWebservice(tv_member_name.getText().toString().trim(), tv_designation.getText().toString().trim(), tv_default_rate.getText().toString().trim(), tv_email.getText().toString().trim(), tv_confirm_email.getText().toString().trim(), tag, id);
                    }
                    break;
                case R.id.btn_cancel_save:
                    clearData();
                    break;


            }
        } catch (Exception e) {
            Log.e("TAG", "Error:" + e.getMessage());
            e.fillInStackTrace();
        }
    }

    private ArrayList<String> getCurrency_list() {
        ArrayList<String> currency = new ArrayList<>();
        currency.add("USDollar(USD)");
        currency.add("Euro(EUR)");
        currency.add("JapaneseYen(JPY)");
        currency.add("Pound(GBP)");
        currency.add("AustralianDollar(AUD)");
        currency.add("CanadianDOllar(CAD)");
        currency.add("SwissFranc(CHF)");
        currency.add("KuwaitiDinar(KWD)");
        currency.add("BahrainiDinar(BHD)");
        return currency;
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) ;
        try {
            JSONObject result = new JSONObject(httpResult.getResponseContent());
            if (httpResult.getRequestType().equals("Create Members")) {
                if (httpResult.getStatus_code() == 200) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    ViewMembersData();
                } else {
                    AndroidUtils.showToast(result.getString("msg"), getContext());

                }
            } else if (httpResult.getRequestType().equals("Update Members")) {
                if (httpResult.getStatus_code() == 200) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    ViewMembersData();
                    //After Member is updated..
                    ll_new_buttons.setVisibility(GONE);
                    tv_assign_groups.setVisibility(View.VISIBLE);
                    ll_save_buttons.setVisibility(View.VISIBLE);
                } else {
                    AndroidUtils.showToast(result.getString("msg"), getContext());

                }
            } else if (httpResult.getRequestType().equals("Reset Password")) {
                JSONObject data = result.getJSONObject("data");
                AndroidUtils.showToast(data.getString("msg"), getContext());
                ViewMembersData();
            } else if (httpResult.getRequestType().equals("Delete Member")) {
                if (httpResult.getStatus_code() == 200) {
                    AndroidUtils.showToast(result.getString("msg"), getContext());
                    ViewMembersData();
                } else {
                    AndroidUtils.showToast(result.getString("msg"), getContext());

                }
            } else if (httpResult.getRequestType().equals("Get Members")) {
                JSONObject data = result.getJSONObject("data");
                JSONArray users = data.getJSONArray("users");
                loadMembers(users);
            } else if (httpResult.getRequestType().equals("Get Groups")) {
                JSONArray data = result.getJSONArray("data");
                loadViewGroups(data);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
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
                for (int k = 0; k < updatedMembersList.size(); k++) {
                    if (viewGroupModel.getId().matches(updatedMembersList.get(k).getGroup_id())) {
                        viewGroupModel.setChecked(true);
                    }
                }
            }
            groupsList.add(viewGroupModel);
        }
        Log.i("ArrayList", "info" + groupsList.toString());
        String mtag = "VG";
        loadGroupsRecylerview();
        loadRecylcerview();

    }

    private void loadGroupsRecylerview() {

//        if (groupsList.size() != 0) {
        FLAG = "second_click";

        rv_selected_member.setLayoutManager(new GridLayoutManager(getContext(), 1));
        groupsAdapter = new GroupsAdapter(groupsList);
        rv_selected_member.setAdapter(groupsAdapter);
        rv_selected_member.setHasFixedSize(true);
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

    private void loadMembers(JSONArray users) throws JSONException {
        MembersModel membersModel;
        members_list.clear();
        for (int i = 0; i < users.length(); i++) {
            JSONObject jsonObject = users.getJSONObject(i);
            membersModel = new MembersModel();
            membersModel.setId(jsonObject.getString("id"));
            membersModel.setName(jsonObject.getString("name"));
            membersModel.setCurrency(jsonObject.getString("currency"));
            membersModel.setDefaultRate(jsonObject.getString("defaultRate"));
            membersModel.setDesignation(jsonObject.getString("designation"));
            membersModel.setEmail(jsonObject.getString("email"));
            membersModel.setLastLogin(jsonObject.getString("lastLogin"));
            membersModel.setGroups(jsonObject.getJSONArray("groups"));

            members_list.add(membersModel);
        }
        loadRecylcerview();
    }

    private void loadRecylcerview() {
        rv_view_members.setLayoutManager(new GridLayoutManager(getContext(), 1));
        MembersAdapter adapter = new MembersAdapter(members_list, getContext(), this, Members.this);
        // rv_view_members.setAdapter(adapter);
        // rv_view_members.setHasFixedSize(true);
        // membersApdater = new MembersAdapter(members_list);
        rv_view_members.setAdapter(adapter);
        rv_view_members.setHasFixedSize(true);
        et_search_teammember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.getFilter().filter(et_search_teammember.getText().toString());
            }
        });


    }

    //private void loadRecylcerviews(ArrayList<MembersModel>MemberModelArrayList) {
    // rv_view_members.setLayoutManager(new GridLayoutManager(getContext(), 1));
    // MembersAdapter adapter = new MembersAdapter(members_list, getContext(), this);
    // adapter = new MembersAdapter(MemberModelArrayList, getContext(), this);
    // rv_view_groups.setAdapter(adapter_view_groups);
    // rv_view_groups.setHasFixedSize(true);
    // rv_view_members.setAdapter(adapter);
    // rv_view_members.setHasFixedSize(true);
    //et_search_teammember.addTextChangedListener(new TextWatcher() {
    //   @Override
    //  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    // }

    //  @Override
    // public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    //  }

    // @Override
    // public void afterTextChanged(Editable editable) {

    // adapter.getFilter().filter(et_search_teammember.getText().toString());
    //  }
    // });

    // }


    @Override
    public void EditMember(MembersModel membersModel) {
//        cv_members_details.setVisibility(View.GONE);
        // TAG="UGA";
        CreateMembersData();
        cv_details.setVisibility(View.VISIBLE);
        search_teammember.setVisibility(GONE);
        ll_buttons.setVisibility(GONE);
        ll_save_buttons.setVisibility(GONE);
        tv_assign_groups.setVisibility(GONE);
        ll_new_buttons.setVisibility(View.VISIBLE);
        cv_members_details.setVisibility(GONE);
        ll_confirm_email.setVisibility(View.VISIBLE);
        tv_member_name.setText(membersModel.getName());
        tv_email.setText(membersModel.getEmail());
        tv_default_rate.setText(membersModel.getDefaultRate());
        tv_confirm_email.setVisibility(View.VISIBLE);
        tv_confirm_email.setText(tv_email.getText().toString());
        for (int i = 0; i < currency_list.size(); i++) {
            if (currency_list.get(i).equals(membersModel.getCurrency())) {
                sp_default_currency.setSelection(i);
            }
        }
        tv_designation.setText(membersModel.getDesignation());
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()) {
                    String tag = "Update";
                    String id = membersModel.getId();
                    callCreateMemberWebservice(tv_member_name.getText().toString().trim(), tv_designation.getText().toString().trim(), tv_default_rate.getText().toString().trim(), tv_email.getText().toString().trim(), tv_confirm_email.getText().toString().trim(), tag, id);
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ll_confirm_email.setVisibility(View.GONE);
//                membersModel.
                unhide();
                ViewMembersData();
                ////Edit Member is Cancelled..
                tv_assign_groups.setVisibility(View.VISIBLE);
                ll_save_buttons.setVisibility(View.VISIBLE);
            }
        });
    }

    public void edit_member() {

        MembersModel membersModel = new MembersModel();
        search_teammember.setVisibility(GONE);
        ll_buttons.setVisibility(GONE);
        ll_save_buttons.setVisibility(GONE);
        tv_assign_groups.setVisibility(GONE);
        ll_new_buttons.setVisibility(View.VISIBLE);
        cv_members_details.setVisibility(GONE);
        ll_confirm_email.setVisibility(View.VISIBLE);
        tv_member_name.setText(membersModel.getName());
        tv_email.setText(membersModel.getEmail());
        tv_default_rate.setText(membersModel.getDefaultRate());
        tv_confirm_email.setVisibility(View.VISIBLE);
        tv_confirm_email.setText(tv_email.getText().toString());
        for (int i = 0; i < currency_list.size(); i++) {
            if (currency_list.get(i).equals(membersModel.getCurrency())) {
                sp_default_currency.setSelection(i);
            }
        }
        tv_designation.setText(membersModel.getDesignation());
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()) {
                    String tag = "Update";
                    String id = membersModel.getId();
                    et_search_members.setText("");
                    callCreateMemberWebservice(tv_member_name.getText().toString().trim(), tv_designation.getText().toString().trim(), tv_default_rate.getText().toString().trim(), tv_email.getText().toString().trim(), tv_confirm_email.getText().toString().trim(), tag, id);
//                    tv_create_members.setBackgroundResource(R.drawable.button_left_background);
//                    tv_assign_groups.setVisibility(View.VISIBLE);
//                    tv_create_members.setTextColor(Color.BLACK);
//                    tv_view_members.setBackgroundResource(R.drawable.button_right_green_background);
//                    tv_view_members.setTextColor(Color.WHITE);

                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ll_confirm_email.setVisibility(View.GONE);
//                membersModel.
                tv_assign_groups.setVisibility(View.VISIBLE);
                unhide();
                ViewMembersData();
            }
        });
    }

    public void UpdateGroupAccess(MembersModel membersModel) throws JSONException {
        TAG = "UGA";

        for (int i = 0; i < membersModel.getGroups().length(); i++) {
            ViewGroupModel viewGroupModel = new ViewGroupModel();
            JSONObject jsonObject = membersModel.getGroups().getJSONObject(i);
            viewGroupModel.setGroup_id(jsonObject.getString("id"));
            viewGroupModel.setGroup_name(jsonObject.getString("name"));
            updatedMembersList.add(viewGroupModel);
        }
        CreateMembersData();

        btn_cancel_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // unhide();

                ViewMembersData();

            }
        });
        bt_save_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tag = "UGA";
                callCreateMemberWebservice("", "", "", "", "", tag, membersModel.getId());
            }
        });
        bt_save_members.setText("Add");
    }

    @Override
    public void ResetPassword(MembersModel membersModel) {
        String tag = "RP";
        callCreateMemberWebservice("", "", "", "", "", tag, membersModel.getId());
    }

    @Override
    public void DeleteMember(MembersModel membersModel) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            TextView header_name = view. findViewById(R.id.header_name);
            header_name.setText("Confirmation");
            header_name.setTextColor(Color.BLACK);
            ImageView close_documents = view.findViewById(R.id.close_documents);
            close_documents.setVisibility(GONE);
            tv_confirmation.setText("Are you sure you want to delete " + membersModel.getName());

            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();

            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ViewMembersData();
                }
            });
            close_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();

                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String tag = "Delete";
                    callCreateMemberWebservice("", "", "", "", "", tag, membersModel.getId());
                }
            });

            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }


    private void unhide() {
        ll_buttons.setVisibility(View.VISIBLE);
        tv_confirm_email.setVisibility(View.VISIBLE);
        et_search_members.setVisibility(View.VISIBLE);
        et_search_teammember.setVisibility(View.VISIBLE);
        tv_confirm_email.setText("");
        tv_designation.setText("");
        tv_email.setText("");
        tv_member_name.setText("");
        tv_default_rate.setText("");
    }

    public void model_name(String action_list) {
        if (action_list == "Edit Member") {
            mViewModel.setData("Edit Member");
        } else if (action_list == "Update Group Access") {
            mViewModel.setData("Update Group Access");
        } else if (action_list == "Delete Member") {
            mViewModel.setData("Delete Member");
        } else {
            mViewModel.setData("View Members");
        }
    }

}

