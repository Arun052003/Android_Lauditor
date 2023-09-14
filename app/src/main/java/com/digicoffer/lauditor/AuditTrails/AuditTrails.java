package com.digicoffer.lauditor.AuditTrails;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;
import com.digicoffer.lauditor.AuditTrails.Model.SpinnerItemModal;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.DateUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AuditTrails extends Fragment implements AsyncTaskCompleteListener {
    ArrayList<SpinnerItemModal> categoryList = new ArrayList<>();
    TextView tv_name, tv_advanced_search;
    HorizontalScrollView scrollView;
    private Button previousPageButton;
    AuditsAdapter audit_adapter;
    ArrayList<AuditsModel> sorted_list = new ArrayList<>();
    private ColorStateList greenButtonTint,whiteButtonTint;
    ArrayList<AuditsModel> pageItems = new ArrayList<>();
    Spinner sp_category;
    LinearLayout ll_page_navigation;
    CardView cv_list;
    int maxPageButtons;
    RecyclerView rv_audits;
    TextInputLayout tl_event_start_time;
    TextInputEditText tv_event_start_time,tv_event_end_time;
    private int currentPage = 1;
    ArrayList<AuditsModel> auditsList = new ArrayList<>();

    AlertDialog progress_dialog;
    String Catergory_type;
    TextInputLayout et_search;
    TextInputEditText et_search_relationships;
    LinearLayout datePickersLayout;
    private int itemsPerPage = 10;
    LinearLayout pageNumberLayout;
    private ColorStateList defaultButtonTint;
    ImageView iv_forward_button, iv_backward_button;
    private boolean isDatePickerVisible = false;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.audit_trials, container, false);
            sp_category = view.findViewById(R.id.sp_project);
            tv_name = view.findViewById(R.id.tv_name);
            datePickersLayout = view.findViewById(R.id.datePickersLayout);
            tv_advanced_search = view.findViewById(R.id.tv_advancedSearch);
            et_search = view.findViewById(R.id.et_search);
            rv_audits = view.findViewById(R.id.rv_audits);
            et_search.setHint(R.string.search);
            ll_page_navigation = view.findViewById(R.id.ll_page_navigaiton);
            cv_list = view.findViewById(R.id.cv_list);
            tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
            tv_event_end_time = view.findViewById(R.id.tv_event_end_time);
            tl_event_start_time = view.findViewById(R.id.tl_event_start_time);
            tv_event_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    tv_event_start_time.requestFocus();
                    DateUtils.showDatePickerDialog(getContext(),tv_event_start_time, getContext());
                }
            });
            tv_event_end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateUtils.showDatePickerDialog(getContext(),tv_event_end_time,getContext());
                    sorted_list.clear();
                    rv_audits.removeAllViews();
                    rv_audits.setAdapter(null);
                    et_search_relationships.setText("");
                    for (int i=0;i<auditsList.size();i++){
                        String timestamp = auditsList.get(i).getTimestamp();
                        Date formatted_date = AndroidUtils.stringToDateTimeDefault(timestamp,"MMM dd,yyyy, hh:mm a");
//                        String latest_timestamp = AndroidUtils.getDateToString(formatted_date,"MMM dd,yyyy hh:mm a");
//                        Log.d("New_Date",latest_timestamp);
//                        Log.d("Formatted_Date",formatted_date.toString());
                        Date updated_date =  DateUtils.stringToDate(et_search_relationships.getText().toString());
//                        Date updated_date = AndroidUtils.stringToDateTimeDefault(tv_event_end_time.getText().toString(),"MMM dd,YYYY");
                        if(formatted_date.after(updated_date)||formatted_date.equals(updated_date)) {
                            if (auditsList.get(i).getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                                AuditsModel auditsModel = new AuditsModel();
                                auditsModel.setName(auditsList.get(i).getName());
                                auditsModel.setTimestamp(auditsList.get(i).getTimestamp());
                                auditsModel.setMessage(auditsList.get(i).getMessage());
                                sorted_list.add(auditsModel);
                            }
                        }




                    }
                    loadRecyclerView(currentPage);

                }
            });
            scrollView = view.findViewById(R.id.scrollView);
            iv_forward_button = view.findViewById(R.id.iv_forward_button);
            iv_backward_button = view.findViewById(R.id.iv_backward_button);
            iv_forward_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (maxPageButtons < 5) {
//                      pageNumberLayout.removeAllViews();
                        setupPagination();

                    } else {
                        maxPageButtons = maxPageButtons + 5;
//                      pageNumberLayout.removeAllViews();
                        setupPagination();
//                  currentPage = pageNumber;
                    }
//                  loadPage(currentPage);
                }
            });
            iv_backward_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (maxPageButtons < 5) {
//                      pageNumberLayout.removeAllViews();
                        setupPagination();

                    } else {
                        maxPageButtons = maxPageButtons - 5;
//                      pageNumberLayout.removeAllViews();
                        setupPagination();
//                  currentPage = pageNumber;
                    }
                }
            });
            pageNumberLayout = view.findViewById(R.id.pageNumberLayout);
            et_search_relationships = view.findViewById(R.id.et_search_relationships);
            loadSearchTextData();
            tv_name.setText("Category");

            loadSpinnerData();
//          callAuditWebservice();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void loadSearchTextData() {
        String text = "Advanced Search";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                isDatePickerVisible = !isDatePickerVisible;

                // Find the datePickersLayout by its ID


                // Set the visibility based on the flag
                datePickersLayout.setVisibility(isDatePickerVisible ? View.VISIBLE : View.GONE);

            }
        };
//        ss.setSpan(new StyleSpan(Typeface.BOLD),0,0,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(clickableSpan, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(
                new ForegroundColorSpan(Color.BLUE),
                0, 15,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tv_advanced_search.setText(ss);
        tv_advanced_search.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void callAuditWebservice() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        try {
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/auditlogs", "Audit Logs", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void loadSpinnerData() {
//        19731710
        categoryList.add(new SpinnerItemModal("Authentication"));
        categoryList.add(new SpinnerItemModal("Groups"));
        categoryList.add(new SpinnerItemModal("Team Members"));
        categoryList.add(new SpinnerItemModal("Relationships"));
        categoryList.add(new SpinnerItemModal("Share"));
        categoryList.add(new SpinnerItemModal("Documents"));
        categoryList.add(new SpinnerItemModal("Merge PDF"));
        categoryList.add(new SpinnerItemModal("Legal Matters"));
        categoryList.add(new SpinnerItemModal("General Matters"));
        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), categoryList);
        sp_category.setAdapter(adapter);
//        callTimeZoneWebservice();
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              sorted_list.clear();
                if (categoryList.get(i).getName().equalsIgnoreCase("Authentication")) {
                    Catergory_type = "AUTH";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Groups")) {
                    Catergory_type = "GROUPS";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Team Members")) {
                    Catergory_type = "TEAM MEMBER";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Relationships")) {
                    Catergory_type = "RELATIONSHIP";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Share")) {
                    Catergory_type = "SHARE";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Documents")) {
                    Catergory_type = "DOCUMENT";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Merge PDF")) {
                    Catergory_type = "MERGE PDF";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("Legal Matters")) {
                    Catergory_type = "LEGAL MATTER";
                } else if (categoryList.get(i).getName().equalsIgnoreCase("General Matters")) {
                    Catergory_type = "GENERAL MATTER";
                }
                Log.d("Category_type", Catergory_type);
//                setupPagination();
                if (auditsList.size() == 0) {
                    callAuditWebservice();
                } else {
                    if (audit_adapter != null) {
                        audit_adapter.clearData();
                    }
//                    sorted_list.clear();
                    pageNumberLayout.removeAllViews();
                    cv_list.setVisibility(View.VISIBLE);
                    loadPage(currentPage);
                    setupPagination();
                }
//                loadPage(currentPage);
//                entity_id = entities_list.get(sp_entities.getSelectedItemPosition()).getId();
//                callEntityClientWebservice(entity_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                Log.d("Request_Type", httpResult.getRequestType().toString());
                if (httpResult.getRequestType() == "Audit Logs") {
//                    if (!result.getBoolean("error")) {
//                        JSONObject jsonObject = result.getJSONObject("data");
                    JSONArray jsonArray = result.getJSONArray("data");
                    et_search_relationships.setText("");
                    loadAuditsData(jsonArray);
//                    } else {
//                        AndroidUtils.showValidationALert("Alert", String.valueOf(result.get("msg")), getContext());
//                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadAuditsData(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            AuditsModel auditsModel = new AuditsModel();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            auditsModel.setMessage(jsonObject.getString("message"));
            auditsModel.setName(jsonObject.getString("name"));
            auditsModel.setTimestamp(jsonObject.getString("timestamp"));
            if (!(auditsModel.getMessage().equals("PROFILE UPDATE")) || !(auditsModel.getMessage().equals("DOCS COLLABORATION"))) {
//                    if ()
                auditsList.add(auditsModel);
            }
        }

        loadPage(currentPage);
        setupPagination();
//            LoadAuditsRecyclerview();
    }


    private void setupPagination() {
        // Maximum number of page buttons to display
        pageNumberLayout.removeAllViews();
        int totalPages = (int) Math.ceil((double) sorted_list.size() / itemsPerPage);
        if (totalPages == 0) {
            cv_list.setVisibility(View.GONE);
        }
        if (maxPageButtons < 5) {
            maxPageButtons = 5;
        }
        greenButtonTint = ColorStateList.valueOf(getResources().getColor(R.color.green_count_color));
        whiteButtonTint = ColorStateList.valueOf(getResources().getColor(R.color.white));
//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        int buttonWidth = screenWidth / maxPageButtons;
        // Dynamically add page number buttons
        for (int i = 1; i <= totalPages; i++) {
            if (i >= maxPageButtons) {
                // Display only maxPageButtons page numbers
                break;
            }
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.page_number_layout, null);
            Button pageButton = view_opponents.findViewById(R.id.page_number_button);
            pageButton.setText(String.valueOf(i));
            final int pageNumber = i;
//            if (defaultButtonTint == null) {
//                defaultButtonTint = pageButton.getBackgroundTintList();
//            }
            pageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    pageButton.setBackgroundColor(R.color.green_count_color);
//                    currentPage = pageNumber;
//                    loadPage(currentPage);
                    if (previousPageButton != null) {
                        previousPageButton.setBackgroundTintList(whiteButtonTint);
                    }

                    // Set the background tint color of the clicked button to green
                    pageButton.setBackgroundTintList(greenButtonTint);

                    currentPage = pageNumber;
                    loadPage(currentPage);

                    // Update the previousPageButton reference
                    previousPageButton = pageButton;
                }
            });

            // Set the width of the page number button
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
//            pageButton.setLayoutParams(params);

            pageNumberLayout.addView(view_opponents);
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
//        loadPage(currentPage);
    }

    private void loadPage(int page) {
        try {


            sorted_list.clear();
            rv_audits.removeAllViews();
            rv_audits.setAdapter(null);
            et_search_relationships.setText("");
//            if (audit_adapter!=null){
//                audit_adapter.itemList.clear();
//            }
            for (int i = 0; i < auditsList.size(); i++) {
//                AuditsModel auditsModel = auditsList.get(i);
                if (auditsList.get(i).getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                    AuditsModel auditsModel1 = new AuditsModel();
                    auditsModel1.setName(auditsList.get(i).getName());
                    auditsModel1.setTimestamp(auditsList.get(i).getTimestamp());
                    auditsModel1.setMessage(auditsList.get(i).getMessage());
                    sorted_list.add(auditsModel1);
                }
            }
            Log.d("Sorted_list", String.valueOf(sorted_list.size()));
          loadRecyclerView(page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadRecyclerView(int page) {
        if (sorted_list.size() != 0) {
            int startIndex = (page - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, sorted_list.size());
            pageItems = new ArrayList<>(sorted_list.subList(startIndex, endIndex));

            if (rv_audits.getAdapter() == null) {
                // If the adapter is null, create a new one and set it to the RecyclerView
                rv_audits.setLayoutManager(new GridLayoutManager(getContext(), 1));
                audit_adapter = new AuditsAdapter(pageItems);
                rv_audits.setAdapter(audit_adapter);

                et_search_relationships.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        audit_adapter.getFilter().filter(et_search_relationships.getText().toString());
                    }
                });
            } else {
                // If the adapter already exists, notify it about the new data
                ((AuditsAdapter) rv_audits.getAdapter()).setData(pageItems);
            }
        }
    }
//        adapter = new MyAdapter(pageItems);
//        recyclerView.setAdapter(adapter);

}

