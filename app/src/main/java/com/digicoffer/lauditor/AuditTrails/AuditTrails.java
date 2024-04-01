package com.digicoffer.lauditor.AuditTrails;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;
import com.digicoffer.lauditor.AuditTrails.Model.SpinnerItemModal;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.DateUtils;
import com.digicoffer.lauditor.common.DateUtilsEndDate;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AuditTrails extends Fragment implements AsyncTaskCompleteListener, DateUtils.OnDateSelectedListener, DateUtilsEndDate.OnDateSelectedListenerEndDate {
    ArrayList<SpinnerItemModal> categoryList = new ArrayList<>();

    TextView tv_name, tv_advanced_search;
    HorizontalScrollView scrollView;
    private Button previousPageButton;
    AuditsAdapter audit_adapter;
    Date startDate, endDate;
    ArrayList<AuditsModel> sorted_list = new ArrayList<>();

    ArrayList<AuditsModel> autentication_list = new ArrayList<>();
    ArrayList<AuditsModel> groups_list = new ArrayList<>();
    ArrayList<AuditsModel> tm_list = new ArrayList<>();
    ArrayList<AuditsModel> relationships_list = new ArrayList<>();
    ArrayList<AuditsModel> share_list = new ArrayList<>();
    ArrayList<AuditsModel> documents_list = new ArrayList<>();
    ArrayList<AuditsModel> merge_pdf_list = new ArrayList<>();

    ArrayList<AuditsModel> legal_matter_list = new ArrayList<>();
    ArrayList<AuditsModel> general_matter_list = new ArrayList<>();
    private ColorStateList greenButtonTint, whiteButtonTint;
    ArrayList<AuditsModel> pageItems = new ArrayList<>();
    Spinner sp_category;
    LinearLayout ll_page_navigation;
    CardView cv_list;
    int maxPageButtons;
    RecyclerView rv_audits;
    TextInputLayout tl_event_start_time;
    //    TextInputEditText tv_event_end_time, tv_event_start_time;
    AppCompatButton tv_event_start_time, tv_event_end_time;
    private int currentPage = 1;
    ArrayList<AuditsModel> auditsList = new ArrayList<>();

    AlertDialog progress_dialog;
    String Catergory_type;
    TextInputLayout et_search;
    TextInputEditText et_search_relationships;
    LinearLayout datePickersLayout;
    private int itemsPerPage = 10;
    LinearLayout pageNumberLayout;
    boolean isAdvancedSearchEnabled = false;
    private ColorStateList defaultButtonTint;
    ImageView iv_forward_button, iv_backward_button, ib_start_mandatory, ib_end_mandatory;

    ImageView ib_cancel_button, ib_cancel_button_end_date;

    private boolean isDatePickerVisible = false;
    View view;
    private Date selectedStartDate;
    private Date selectedEndDate;
    private NewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.audit_trials, container, false);
            sp_category = view.findViewById(R.id.sp_project);
            mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
            mViewModel.setData("Audit");
            tv_name = view.findViewById(R.id.tv_name);
            datePickersLayout = view.findViewById(R.id.datePickersLayout);
            tv_advanced_search = view.findViewById(R.id.tv_advancedSearch);
            et_search = view.findViewById(R.id.et_search);
            rv_audits = view.findViewById(R.id.rv_audits);
            TextView from_date = view.findViewById(R.id.from_date);
            TextView to_date = view.findViewById(R.id.to_date);
            from_date.setText("From");
            to_date.setText("To");
            ib_cancel_button = view.findViewById(R.id.cancel_button);
            ib_cancel_button_end_date = view.findViewById(R.id.cancel_button_end_date);
            ib_start_mandatory = view.findViewById(R.id.mandatory);
            ib_end_mandatory = view.findViewById(R.id.mandatory_end_date);
            ib_start_mandatory.setVisibility(View.GONE);
            ib_end_mandatory.setVisibility(View.GONE);
            ib_cancel_button.setVisibility(View.GONE);
            ib_cancel_button_end_date.setVisibility(View.GONE);
            ib_cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_event_start_time.setText("");
                    clearDates();

                }
            });
            ib_cancel_button_end_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_event_end_time.setText("");
//                    tv_event_start_time.setText("");
                    clearDates();
                }
            });
            et_search.setHint(R.string.search);
            ll_page_navigation = view.findViewById(R.id.ll_page_navigaiton);
            cv_list = view.findViewById(R.id.cv_list);
            DateUtils dateUtils = new DateUtils(this);
            DateUtilsEndDate dateUtilsEndDate = new DateUtilsEndDate(this);
            dateUtilsEndDate.setOnDateSelectedListener(this);
            dateUtils.setOnDateSelectedListener(this);
            tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
            tv_event_end_time = view.findViewById(R.id.tv_event_end_time);
//            tl_event_start_time = view.findViewById(R.id.tl_event_start_time);
//            tv_event_start_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "Start Time";
//                    DateUtils.showDatePickerDialog(getContext(), tv_event_start_time, getContext(), FLAG, new DateUtils.OnDateSelectedListener() {
//                        @Override
//                        public void onDateSelected(String selectedDate, String FLAG) {
//                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//
//                            try {
//                                Date date = originalFormat.parse(selectedDate);
//                                String formattedDate = desiredFormat.format(date);
//                                // Use formattedDate as needed, for example:
//                                tv_event_start_time.setText(formattedDate);
//                            } catch (ParseException | java.text.ParseException e) {
//                                e.printStackTrace();
//                                // Handle parsing exception here
//                            }
//                        }
//                    });
//                }
//            });
            int i;


//                    tv_event_start_time.requestFocus();
//                    DateUtils.showDatePickerDialog(getContext(),tv_event_start_time, getContext());

//            tv_event_end_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "Start Time";
//                    DateUtils.showDatePickerDialog(getContext(), tv_event_end_time, getContext(), FLAG, new DateUtils.OnDateSelectedListener() {
//                        @Override
//                        public void onDateSelected(String selectedDate, String FLAG) {
//                            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//
//                            try {
//                                Date date = originalFormat.parse(selectedDate);
//                                String formattedDate = desiredFormat.format(date);
//                                // Use formattedDate as needed, for example:
//                                tv_event_end_time.setText(formattedDate);
//                            } catch (ParseException | java.text.ParseException e) {
//                                e.printStackTrace();
//                                // Handle parsing exception here
//                            }
//                        }
//                    });
//                }
//            });
//                    DateUtils.showDatePickerDialog(getContext(),tv_event_end_time,getContext());
//
//            });
            scrollView = view.findViewById(R.id.scrollView);
            iv_forward_button = view.findViewById(R.id.iv_forward_button);
            iv_backward_button = view.findViewById(R.id.iv_backward_button);
            iv_forward_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    maxPageButtons += 5;

                    setupPagination();

                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            iv_backward_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    maxPageButtons -= 5;

                    setupPagination();

                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

            pageNumberLayout = view.findViewById(R.id.pageNumberLayout);
            et_search_relationships = view.findViewById(R.id.et_search_relationships);
            et_search_relationships.setHint("Search");
            loadSearchTextData();
            tv_name.setText("Category");
//            tv_advanced_search.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    isDatePickerVisible = !isDatePickerVisible;
//
//                    // Find the datePickersLayout by its ID
//                    datePickersLayout.setVisibility(isDatePickerVisible ? View.VISIBLE : View.GONE);
//
//                    // Check if the date picker layout is hidden, and clear the date fields
//                    if (!isDatePickerVisible) {
//                        tv_event_start_time.setText(""); // Clear start time
//                        tv_event_end_time.setText(""); // Clear end time
//                        sorted_list.clear();
//                    }
//                }
//            });

            loadSpinnerData();
//          callAuditWebservice();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void clearDates() {
        sorted_list.clear();
        et_search_relationships.setText("");
        String FLAG = "";
        loadnewPage(null, FLAG);
        fetchpagedata();
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
                if (!isDatePickerVisible) {
                    tv_event_start_time.setText(""); // Clear start time
                    tv_event_end_time.setText(""); // Clear end time
                    sorted_list.clear();
                    clearDates();
//                    auditsList.clear();
//                    loadSpinnerData();
                }
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
        loadItemSelectedListner();
//        callTimeZoneWebservice();
//        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                try {
//                    rv_audits.removeAllViews();
//                    rv_audits.setAdapter(null);
//                    et_search_relationships.setText("");
////                 sorted_list.clear();
////              auditsList.clear();
//                    if (categoryList.get(i).getName().equalsIgnoreCase("Authentication")) {
//                        Catergory_type = "AUTH";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Groups")) {
//                        Catergory_type = "GROUPS";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Team Members")) {
//                        Catergory_type = "TEAM MEMBER";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Relationships")) {
//                        Catergory_type = "RELATIONSHIP";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Share")) {
//                        Catergory_type = "SHARE";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Documents")) {
//                        Catergory_type = "DOCUMENT";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Merge PDF")) {
//                        Catergory_type = "MERGE PDF";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("Legal Matters")) {
//                        Catergory_type = "LEGAL MATTER";
//                    } else if (categoryList.get(i).getName().equalsIgnoreCase("General Matters")) {
//                        Catergory_type = "GENERAL MATTER";
//                    }
//                    Log.d("Category_type", Catergory_type);
//                    Log.d("AuditSize", String.valueOf(auditsList.size()));
////                setupPagination();
////                    if (startDate!=null||endDate!=null){
////                        sorted_list.clear();
////                        et_search_relationships.setText("");
////                        String FLAG = "End Time";
////                        loadnewPage(null, FLAG);
////                    }
//                    if (auditsList.size() == 0) {
//                        callAuditWebservice();
//                    }
////                    else if(sorted_list.size()!=0){
////                        et_search_relationships.setText("");
////                            String FLAG = "End Time";
////                            loadnewPage(null,FLAG);
////                    }
////                    else if (selectedStartDate != null || selectedEndDate != null) {
////                        applyDateFilter(selectedStartDate, selectedEndDate);
////                    }
//                    else {
////                        if (sorted_list.size() == 0) {
//                        if (audit_adapter != null) {
//                            audit_adapter.clearData();
//                        }
////
////                         if (startDate != null || endDate != null) {
////                             sorted_list.clear();
////                             et_search_relationships.setText("");
////                             String FLAG = "End Time";
////                             loadnewPage(null, FLAG);
////                             fetchpagedata();
////                         }
////                         else {
//                        pageNumberLayout.removeAllViews();
//                        isAdvancedSearchEnabled = false;
//                        tv_event_start_time.setText("");
//                        tv_event_end_time.setText("");
//                        et_search_relationships.setText("");
//                        cv_list.setVisibility(View.VISIBLE);
////                     setupPagination();
//                        currentPage = 1;
////                             String FLAG = "";
////                         loadnewPage(null, FLAG);
//                        loadPage(currentPage, isAdvancedSearchEnabled);
//                        setupPagination();
////                        }
////                        else{
////
////                        }
//                    }
////                     }
////                     }
//
//                } catch (Exception e) {
//                    Log.d("Exception", e.getMessage());
//                    throw new RuntimeException(e);
//                }
////                loadPage(currentPage);
////                entity_id = entities_list.get(sp_entities.getSelectedItemPosition()).getId();
////                callEntityClientWebservice(entity_id);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

    }

    private void loadItemSelectedListner() {
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    rv_audits.removeAllViews();
                    rv_audits.setAdapter(null);
                    et_search_relationships.setText("");
//                 sorted_list.clear();
//              auditsList.clear();
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
                    Log.d("AuditSize", String.valueOf(auditsList.size()));
//                setupPagination();
//                    if (startDate!=null||endDate!=null){
//                        sorted_list.clear();
//                        et_search_relationships.setText("");
//                        String FLAG = "End Time";
//                        loadnewPage(null, FLAG);
//                    }
                    if (autentication_list.size() == 0 && groups_list.size() == 0 && tm_list.size() == 0 && relationships_list.size() == 0 && share_list.size() == 0 && documents_list.size() == 0 && merge_pdf_list.size() == 0 && legal_matter_list.size() == 0 && general_matter_list.size() == 0) {
                        callAuditWebservice();
                    }
//                    else if(sorted_list.size()!=0){
//                        et_search_relationships.setText("");
//                            String FLAG = "End Time";
//                            loadnewPage(null,FLAG);
//                    }
//                    else if (selectedStartDate != null || selectedEndDate != null) {
//                        applyDateFilter(selectedStartDate, selectedEndDate);
//                    }

                    else {
//                        if (sorted_list.size() == 0) {
                        if (audit_adapter != null) {
                            audit_adapter.clearData();
                        }
//
                        if (startDate != null || endDate != null) {
                            sorted_list.clear();
                            et_search_relationships.setText("");
                            String FLAG = "End Time";
                            loadnewPage(null, FLAG);
                            fetchpagedata();
                        } else {
                            pageNumberLayout.removeAllViews();
                            isAdvancedSearchEnabled = false;
                            tv_event_start_time.setText("");
                            tv_event_end_time.setText("");
                            et_search_relationships.setText("");
                            cv_list.setVisibility(View.VISIBLE);
//                     setupPagination();
                            currentPage = 1;
//                             String FLAG = "";
//                         loadnewPage(null, FLAG);
                            loadPage(currentPage, isAdvancedSearchEnabled);
                            setupPagination();
//                        }
//                        else{
//
                        }
//                    }
//                     }
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                    throw new RuntimeException(e);
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
//                    loadAuditsData(jsonArray);
                    loadNewAuditsData(jsonArray);
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
        auditsList.clear();
//        loadNewAuditsData(jsonArray);
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

        for (int i = 0; i < auditsList.size(); i++) {
            Log.d("Audit_List", auditsList.get(i).getName());
        }
        loadPage(currentPage, isAdvancedSearchEnabled);
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
        whiteButtonTint = ColorStateList.valueOf(getResources().getColor(R.color.Blue_text_color));
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
                    loadPage(currentPage, isAdvancedSearchEnabled);

                    // Update the previousPageButton reference
                    previousPageButton = pageButton;
                }
            });

            // Set the width of the page number button
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
//            pageButton.setLayoutParams(params);

            pageNumberLayout.addView(view_opponents);
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
//        loadPage(currentPage);
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, String> {
        int page;
        boolean isAdvancedSearchEnabled;

        // Override doInBackground method to define the background task
        @Override
        protected String doInBackground(Void... voids) {
            // Perform background computation here
            if (!isAdvancedSearchEnabled) {
                sorted_list.clear();

//            if (audit_adapter!=null){
//                audit_adapter.itemList.clear();
//            }
//                sorted_list.clear();
                if (Catergory_type.equals("AUTH")) {
                    for (int i = 0; i < autentication_list.size(); i++) {
//                AuditsModel auditsModel = auditsList.get(i);
//                    if (auditsList.get(i).getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                        AuditsModel auditsModel1 = autentication_list.get(i);
//                        auditsModel1.setName(auditsList.get(i).getName());
//                        auditsModel1.setTimestamp(auditsList.get(i).getTimestamp());
//                        auditsModel1.setMessage(auditsList.get(i).getMessage());
                        sorted_list.add(auditsModel1);
//                    }
                    }
//
                } else if (Catergory_type.equals("GROUPS")) {
                    for (int i = 0; i < groups_list.size(); i++) {
                        AuditsModel auditsModel = groups_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("TEAM MEMBER")) {
                    for (int i = 0; i < tm_list.size(); i++) {
                        AuditsModel auditsModel = tm_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("RELATIONSHIP")) {
                    for (int i = 0; i < relationships_list.size(); i++) {
                        AuditsModel auditsModel = relationships_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("SHARE")) {
                    for (int i = 0; i < share_list.size(); i++) {
                        AuditsModel auditsModel = share_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("DOCUMENT")) {
                    for (int i = 0; i < documents_list.size(); i++) {
                        AuditsModel auditsModel = documents_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("MERGE PDF")) {
                    for (int i = 0; i < merge_pdf_list.size(); i++) {
                        AuditsModel auditsModel = merge_pdf_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("LEGAL MATTER")) {
                    for (int i = 0; i < legal_matter_list.size(); i++) {
                        AuditsModel auditsModel = legal_matter_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                } else if (Catergory_type.equals("GENERAL MATTER")) {
                    for (int i = 0; i < general_matter_list.size(); i++) {
                        AuditsModel auditsModel = general_matter_list.get(i);
                        sorted_list.add(auditsModel);
                    }
                }
                loadRecyclerView(page);
                Log.d("Sorted_list", String.valueOf(sorted_list.size()));
            } else {
                try {
//
                    loadRecyclerView(page);
//                        }
                } catch (Exception e) {
                    AndroidUtils.showToast(e.getMessage(), getContext());
                    throw new RuntimeException(e);
                }
            }
            return "Background task completed";
        }

        // Override onPostExecute method to handle the result of the background task
        @Override
        protected void onPostExecute(String result) {
            // Update UI or perform any post-execution tasks here
            // For example, display a Toast message with the result

        }
    }

    private void loadPage(final int page, final boolean isAdvancedSearchEnabled) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Clear the sorted list
                    sorted_list.clear();

                    // Add data based on category type
                    if (!isAdvancedSearchEnabled) {
                        if (Catergory_type.equals("AUTH")) {
                            for (int i = 0; i < autentication_list.size(); i++) {
                                AuditsModel auditsModel1 = autentication_list.get(i);
                                sorted_list.add(auditsModel1);
                            }
                        } else if (Catergory_type.equals("GROUPS")) {
                            for (int i = 0; i < groups_list.size(); i++) {
                                AuditsModel auditsModel = groups_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("TEAM MEMBER")) {
                            for (int i = 0; i < tm_list.size(); i++) {
                                AuditsModel auditsModel = tm_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("RELATIONSHIP")) {
                            for (int i = 0; i < relationships_list.size(); i++) {
                                AuditsModel auditsModel = relationships_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("SHARE")) {
                            for (int i = 0; i < share_list.size(); i++) {
                                AuditsModel auditsModel = share_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("DOCUMENT")) {
                            for (int i = 0; i < documents_list.size(); i++) {
                                AuditsModel auditsModel = documents_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("MERGE PDF")) {
                            for (int i = 0; i < merge_pdf_list.size(); i++) {
                                AuditsModel auditsModel = merge_pdf_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("LEGAL MATTER")) {
                            for (int i = 0; i < legal_matter_list.size(); i++) {
                                AuditsModel auditsModel = legal_matter_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        } else if (Catergory_type.equals("GENERAL MATTER")) {
                            for (int i = 0; i < general_matter_list.size(); i++) {
                                AuditsModel auditsModel = general_matter_list.get(i);
                                sorted_list.add(auditsModel);
                            }
                        }
                    }

                    // Load RecyclerView on the UI thread
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadRecyclerView(page);
                        }
                    });

                } catch (final Exception e) {
                    // Handle any exceptions
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtils.showToast(e.getMessage(), getContext());
                            Log.e("LoadPageException", e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void runOnUiThread(Runnable loadPageException) {
    }

    private void loadNewAuditsData(JSONArray jsonArray) {
        try {
            clearLists();
//            autentication_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                AuditsModel auditsModel = new AuditsModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                auditsModel.setMessage(jsonObject.getString("message"));
                auditsModel.setName(jsonObject.getString("name"));
                auditsModel.setTimestamp(jsonObject.getString("timestamp"));
                if (!(auditsModel.getMessage().equals("PROFILE UPDATE")) || !(auditsModel.getMessage().equals("DOCS COLLABORATION"))) {
//                    if ()
                    if (jsonObject.getString("name").equals("AUTH")) {
                        autentication_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("GROUPS")) {
                        groups_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("TEAM MEMBER")) {
                        tm_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("RELATIONSHIP")) {
                        relationships_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("SHARE")) {
                        share_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("DOCUMENT")) {
                        documents_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("MERGE PDF")) {
                        merge_pdf_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("LEGAL MATTER")) {
                        legal_matter_list.add(auditsModel);
                    } else if (jsonObject.getString("name").equals("GENERAL MATTER")) {
                        general_matter_list.add(auditsModel);
                    }
                }
            }
            for (int i = 0; i < autentication_list.size(); i++) {
                Log.d("Audit_List", autentication_list.get(i).getName());
            }
            loadPage(currentPage, isAdvancedSearchEnabled);
            setupPagination();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearLists() {
        autentication_list.clear();
        groups_list.clear();
        tm_list.clear();
        relationships_list.clear();
        share_list.clear();
        documents_list.clear();
        merge_pdf_list.clear();
        legal_matter_list.clear();
        general_matter_list.clear();
    }


    private void loadRecyclerView(int page) {
        try {
            Log.d("Sorted_list_new", String.valueOf(sorted_list.size()));
            if (sorted_list.size() != 0) {
                int startIndex = (page - 1) * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, sorted_list.size());
                pageItems = new ArrayList<>(sorted_list.subList(startIndex, endIndex));

                if (rv_audits.getAdapter() == null) {
                    // If the adapter is null, create a new one and set it to the RecyclerView
                    rv_audits.setLayoutManager(new GridLayoutManager(getContext(), 1));

                    audit_adapter = new AuditsAdapter(pageItems);
                    rv_audits.setAdapter(audit_adapter);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_animation);
                    LayoutAnimationController controller = new LayoutAnimationController(animation);
                    rv_audits.setLayoutAnimation(controller);
                    rv_audits.scheduleLayoutAnimation();
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
//                    loadnewPage(null,"");
                }
            }
        } catch (Exception e) {
            Log.d("Recyclervie_Exception", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDateSelected(String selectedDate, String FLAG) {
        loadnewPage(selectedDate, FLAG);
        fetchpagedata();
    }

    private void fetchpagedata() {
        setupPagination();
        currentPage = 1;
        isAdvancedSearchEnabled = true;
        loadPage(currentPage, isAdvancedSearchEnabled);
    }

    private void loadnewPage(String selectedDate, String FLAG) {
        try {
            sorted_list.clear();
            rv_audits.removeAllViews();
            rv_audits.setAdapter(null);
            et_search_relationships.setText("");
//            clearLists();
            startDate = DateUtils.stringToDate(tv_event_start_time.getText().toString());
            endDate = DateUtilsEndDate.stringToDate(tv_event_end_time.getText().toString());
//            ArrayList<AuditsModel> advanced_list = new ArrayList<>();
            if (Catergory_type.equals("AUTH")) {
                loadAdvancedData(autentication_list);
            } else if (Catergory_type.equals("GROUPS")) {
                loadAdvancedData(groups_list);
            } else if (Catergory_type.equals("TEAM MEMBER")) {
                loadAdvancedData(tm_list);
            } else if (Catergory_type.equals("RELATIONSHIP")) {
                loadAdvancedData(relationships_list);
            } else if (Catergory_type.equals("SHARE")) {
                loadAdvancedData(share_list);
            } else if (Catergory_type.equals("DOCUMENT")) {
                loadAdvancedData(documents_list);
            } else if (Catergory_type.equals("MERGE PDF")) {
                loadAdvancedData(merge_pdf_list);
            } else if (Catergory_type.equals("LEGAL MATTER")) {
                loadAdvancedData(legal_matter_list);
            } else if (Catergory_type.equals("GENERAL MATTER")) {
                loadAdvancedData(general_matter_list);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //private void loadnewPage(String selectedDate, String FLAG) {
//    sorted_list.clear();
//    rv_audits.removeAllViews();
//    rv_audits.setAdapter(null);
//    et_search_relationships.setText("");
//    Date startDate = DateUtils.stringToDate(tv_event_start_time.getText().toString());
//    Date endDate = DateUtilsEndDate.stringToDate(tv_event_end_time.getText().toString());
//    Date selectedDateObj = DateUtils.stringToDate(selectedDate);
//    for (int i = 0; i < auditsList.size(); i++) {
//        AuditsModel auditsModel = auditsList.get(i);
//        String timestamp = auditsModel.getTimestamp();
//        Date formatted_date = AndroidUtils.stringToDateTimeDefault(timestamp, "MMM dd,yyyy, hh:mm a");
//        if ((startDate != null && endDate != null) && (formatted_date != null)) {
//            if ((formatted_date.equals(startDate) || formatted_date.after(startDate)) &&
//                    (formatted_date.equals(endDate) || formatted_date.before(endDate)) &&
//                    auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
//                sorted_list.add(auditsModel);
//            }
//        } else if ((startDate != null) && (formatted_date != null)) {
//            if (formatted_date.after(startDate) || formatted_date.equals(startDate) && auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT)) ){
//                sorted_list.add(auditsModel);
//            }
//        } else if ((endDate != null) && (formatted_date != null)) {
//            if (formatted_date.before(endDate) || formatted_date.equals(endDate) && auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
//                sorted_list.add(auditsModel);
//            }
//        }
//    }
//    setupPagination();
//    currentPage = 1;
//    isAdvancedSearchEnabled = true;
//    loadPage(currentPage, isAdvancedSearchEnabled);
//}
    private void loadAdvancedData(ArrayList<AuditsModel> advanced_list) {
        for (int i = 0; i < advanced_list.size(); i++) {
            try {
                AuditsModel auditsModel = advanced_list.get(i);
                Log.d("Advanced_DATA", auditsModel.getName());
                String timestamp = auditsModel.getTimestamp();
                Date formatted_date = AndroidUtils.stringToDateTimeDefault(timestamp, "MMM dd,yyyy, hh:mm a");
                if (startDate != null && endDate != null) {
                    if (formatted_date != null && formatted_date.after(startDate) && formatted_date.before(endDate)) {
                        if (auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                            sorted_list.add(auditsModel);
                        }
                    }
                } else if (startDate != null) {
                    if (formatted_date != null && (formatted_date.after(startDate) || formatted_date.equals(startDate))) {
                        if (auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                            try {
                                sorted_list.add(auditsModel);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else if (endDate != null) {
                    if (formatted_date != null && (formatted_date.before(endDate) || formatted_date.equals(endDate))) {
                        if (auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                            sorted_list.add(auditsModel);
                        }
                    }
                } else {
                    if (auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
                        sorted_list.add(auditsModel);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isDateInRange(Date date, Date startDate, Date endDate) {
        return (startDate == null || !date.before(startDate)) &&
                (endDate == null || !date.after(endDate));
    }

    @Override
    public void onDateSelectedEndDate(String selectedDate, String FLAG) {
        loadnewPage(selectedDate, FLAG);
        fetchpagedata();
    }

//        adapter = new MyAdapter(pageItems);
//        recyclerView.setAdapter(adapter);

}

