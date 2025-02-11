//package com.digicoffer.lauditor.AuditTrails;
//
//import android.app.AlertDialog;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextWatcher;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.text.style.ForegroundColorSpan;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.HorizontalScrollView;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;
//import com.digicoffer.lauditor.AuditTrails.Model.SpinnerItemModal;
//import com.digicoffer.lauditor.R;
//import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
//import com.digicoffer.lauditor.Webservice.HttpResultDo;
//import com.digicoffer.lauditor.Webservice.WebServiceHelper;
//import com.digicoffer.lauditor.common.AndroidUtils;
//import com.digicoffer.lauditor.common.DateUtils;
//import com.digicoffer.lauditor.common.DateUtilsEndDate;
//import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.textfield.TextInputLayout;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Locale;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class AuditTrailsNew extends Fragment implements AsyncTaskCompleteListener, DateUtils.OnDateSelectedListener, DateUtilsEndDate.OnDateSelectedListenerEndDate {
//    ArrayList<SpinnerItemModal> categoryList = new ArrayList<>();
//    private ExecutorService executor = Executors.newSingleThreadExecutor();
//    TextView tv_name, tv_advanced_search;
//    HorizontalScrollView scrollView;
//        ImageView iv_forward_button, iv_backward_button, ib_start_mandatory, ib_end_mandatory;
//
//    ImageButton ib_cancel_button, ib_cancel_button_end_date;
//
//    private Button previousPageButton;
//    AuditsAdapter audit_adapter;
//    ArrayList<AuditsModel> sorted_list = new ArrayList<>();
//    private ColorStateList greenButtonTint, whiteButtonTint;
//    ArrayList<AuditsModel> pageItems = new ArrayList<>();
//    Spinner sp_category;
//    LinearLayout ll_page_navigation;
//    CardView cv_list;
//    int maxPageButtons;
//    RecyclerView rv_audits;
//    TextInputLayout tl_event_start_time;
//    TextInputEditText tv_event_start_time, tv_event_end_time;
//    private int currentPage = 1;
//    ArrayList<AuditsModel> auditsList = new ArrayList<>();
//
//    AlertDialog progress_dialog;
//    String Catergory_type;
//    TextInputLayout et_search;
//    TextInputEditText et_search_relationships;
//    LinearLayout datePickersLayout;
//    private int itemsPerPage = 10;
//    LinearLayout pageNumberLayout;
//    boolean isAdvancedSearchEnabled = false;
//    private ColorStateList defaultButtonTint;
////    ImageView iv_forward_button, iv_backward_button;
//    private boolean isDatePickerVisible = false;
//    View view;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        try {
//            view = inflater.inflate(R.layout.audit_trials, container, false);
//            sp_category = view.findViewById(R.id.sp_project);
//            tv_name = view.findViewById(R.id.tv_name);
//            datePickersLayout = view.findViewById(R.id.datePickersLayout);
//            tv_advanced_search = view.findViewById(R.id.tv_advancedSearch);
//            et_search = view.findViewById(R.id.et_search);
//            rv_audits = view.findViewById(R.id.rv_audits);
//            TextView from_date = view.findViewById(R.id.from_date);
//            TextView to_date = view.findViewById(R.id.to_date);
//            from_date.setText("From");
//            to_date.setText("To");
//            et_search.setHint(R.string.search);
//            ib_cancel_button = view.findViewById(R.id.cancel_button);
//            ib_cancel_button_end_date = view.findViewById(R.id.cancel_button_end_date);
//            ib_start_mandatory = view.findViewById(R.id.mandatory);
//            ib_end_mandatory = view.findViewById(R.id.mandatory_end_date);
//            ib_start_mandatory.setVisibility(View.GONE);
//            ib_end_mandatory.setVisibility(View.GONE);
//            ib_cancel_button.setVisibility(View.VISIBLE);
//            ib_cancel_button_end_date.setVisibility(View.VISIBLE);
//            ib_cancel_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tv_event_start_time.setText("");
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "Start Time";
//                    loadnewPage(null, FLAG);
//
//                }
//            });
//            ib_cancel_button_end_date.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tv_event_end_time.setText("");
////                    tv_event_start_time.setText("");
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "End Time";
//                    loadnewPage(null, FLAG);
//
//                }
//            });
//            ll_page_navigation = view.findViewById(R.id.ll_page_navigaiton);
//            cv_list = view.findViewById(R.id.cv_list);
//            DateUtils dateUtils = new DateUtils(this);
//            DateUtilsEndDate dateUtilsEndDate = new DateUtilsEndDate(this);
//            dateUtilsEndDate.setOnDateSelectedListener(this);
//            dateUtils.setOnDateSelectedListener(this);
//            tv_event_start_time = view.findViewById(R.id.tv_event_start_time);
//            tv_event_end_time = view.findViewById(R.id.tv_event_end_time);
//            tl_event_start_time = view.findViewById(R.id.tl_event_start_time);
//            tv_event_start_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "Start Time";
//                    DateUtils.showDatePickerDialog(getContext(), tv_event_start_time, getContext(), FLAG);
//                }
//            });
//            tv_event_end_time.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    sorted_list.clear();
//                    et_search_relationships.setText("");
//                    String FLAG = "End Time";
//                    DateUtilsEndDate.showDatePickerDialog(getContext(), tv_event_end_time, getContext(), FLAG);
//
//                }
//            });
//
//            scrollView = view.findViewById(R.id.scrollView);
//            iv_forward_button = view.findViewById(R.id.iv_forward_button);
//            iv_backward_button = view.findViewById(R.id.iv_backward_button);
//            iv_forward_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (maxPageButtons < 5) {
//                        setupPagination();
//
//                    } else {
//                        maxPageButtons = maxPageButtons + 5;
//                        setupPagination();
//
//                    }
//
//                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//
//                }
//            });
//            iv_backward_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (maxPageButtons < 5) {
//
//                        setupPagination();
//
//                    } else {
//                        maxPageButtons = maxPageButtons - 5;
//
//                        setupPagination();
//
//                    }
//                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//
//                }
//            });
//            pageNumberLayout = view.findViewById(R.id.pageNumberLayout);
//            et_search_relationships = view.findViewById(R.id.et_search_relationships);
//            loadSearchTextData();
//            tv_name.setText("Category");
//
//            loadSpinnerData();
////          callAuditWebservice();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return view;
//    }
//
//    private void loadSearchTextData() {
//        String text = "Advanced Search";
//        SpannableString ss = new SpannableString(text);
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(@NonNull View widget) {
//                isDatePickerVisible = !isDatePickerVisible;
//                datePickersLayout.setVisibility(isDatePickerVisible ? View.VISIBLE : View.GONE);
//                if (!isDatePickerVisible) {
//                    tv_event_start_time.setText(""); // Clear start time
//                    tv_event_end_time.setText(""); // Clear end time
//                    sorted_list.clear();
//
//                }
//            }
//        };
////        ss.setSpan(new StyleSpan(Typeface.BOLD),0,0,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        ss.setSpan(clickableSpan, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(
//                new ForegroundColorSpan(Color.BLUE),
//                0, 15,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        );
//        tv_advanced_search.setText(ss);
//        tv_advanced_search.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    private void callAuditWebservice() {
//        progress_dialog = AndroidUtils.get_progress(getActivity());
//        JSONObject postData = new JSONObject();
//        try {
//            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/auditlogs", "Audit Logs", postData.toString());
//        } catch (Exception e) {
//            if (progress_dialog != null && progress_dialog.isShowing())
//                AndroidUtils.dismiss_dialog(progress_dialog);
//        }
//    }
//
//    private void loadSpinnerData() {
////        19731710
//        categoryList.add(new SpinnerItemModal("Authentication"));
//        categoryList.add(new SpinnerItemModal("Groups"));
//        categoryList.add(new SpinnerItemModal("Team Members"));
//        categoryList.add(new SpinnerItemModal("Relationships"));
//        categoryList.add(new SpinnerItemModal("Share"));
//        categoryList.add(new SpinnerItemModal("Documents"));
//        categoryList.add(new SpinnerItemModal("Merge PDF"));
//        categoryList.add(new SpinnerItemModal("Legal Matters"));
//        categoryList.add(new SpinnerItemModal("General Matters"));
//        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), categoryList);
//        sp_category.setAdapter(adapter);
////        callTimeZoneWebservice();
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
//                    if (auditsList.size() == 0) {
//                        callAuditWebservice();
//                    } else {
//                        if (audit_adapter != null) {
//                            audit_adapter.clearData();
//                        }
////                    sorted_list.clear();
//                        pageNumberLayout.removeAllViews();
//                        isAdvancedSearchEnabled = false;
//                        tv_event_start_time.setText("");
//                        tv_event_end_time.setText("");
//                        cv_list.setVisibility(View.VISIBLE);
////                     setupPagination();
//                        currentPage = 1;
//                        loadPage(currentPage, isAdvancedSearchEnabled);
//
//                        setupPagination();
//                    }
//                } catch (Exception e) {
//                    Log.d("Exception", e.getMessage());
//                    throw new RuntimeException(e);
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onClick(View view) {
//
//    }
//
//    @Override
//    public void onAsyncTaskComplete(HttpResultDo httpResult) {
//        if (progress_dialog != null && progress_dialog.isShowing())
//            AndroidUtils.dismiss_dialog(progress_dialog);
//        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
//            try {
//                JSONObject result = new JSONObject(httpResult.getResponseContent());
//                Log.d("Request_Type", httpResult.getRequestType().toString());
//                if (httpResult.getRequestType() == "Audit Logs") {
//                    JSONArray jsonArray = result.getJSONArray("data");
//                    et_search_relationships.setText("");
//                    loadAuditsData(jsonArray);
//
//                }
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    private void loadAuditsData(JSONArray jsonArray) throws JSONException {
//        auditsList.clear();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            AuditsModel auditsModel = new AuditsModel();
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            auditsModel.setMessage(jsonObject.getString("message"));
//            auditsModel.setName(jsonObject.getString("name"));
//            auditsModel.setTimestamp(jsonObject.getString("timestamp"));
//            if (!(auditsModel.getMessage().equals("PROFILE UPDATE")) || !(auditsModel.getMessage().equals("DOCS COLLABORATION"))) {
//
//                auditsList.add(auditsModel);
//            }
//        }
//
//        for (int i = 0; i < auditsList.size(); i++) {
//            Log.d("Audit_List", auditsList.get(i).getName());
//        }
//        loadPage(currentPage, isAdvancedSearchEnabled);
//        setupPagination();
////            LoadAuditsRecyclerview();
//    }
//
//    private void setupPagination() {
//        // Maximum number of page buttons to display
//        pageNumberLayout.removeAllViews();
//
//        int totalPages = (int) Math.ceil((double) sorted_list.size() / itemsPerPage);
//        if (totalPages == 0) {
//            cv_list.setVisibility(View.GONE);
//        }
//        if (maxPageButtons < 5) {
//            maxPageButtons = 5;
//        }
//        greenButtonTint = ColorStateList.valueOf(getResources().getColor(R.color.green_count_color));
//        whiteButtonTint = ColorStateList.valueOf(getResources().getColor(R.color.Blue_text_color));
////        int screenWidth = getResources().getDisplayMetrics().widthPixels;
////        int buttonWidth = screenWidth / maxPageButtons;
//        // Dynamically add page number buttons
//        for (int i = 1; i <= totalPages; i++) {
//            if (i >= maxPageButtons) {
//                // Display only maxPageButtons page numbers
//                break;
//            }
//            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.page_number_layout, null);
//            Button pageButton = view_opponents.findViewById(R.id.page_number_button);
//            pageButton.setText(String.valueOf(i));
//            final int pageNumber = i;
////            if (defaultButtonTint == null) {
////                defaultButtonTint = pageButton.getBackgroundTintList();
////            }
//            pageButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    pageButton.setBackgroundColor(R.color.green_count_color);
////                    currentPage = pageNumber;
////                    loadPage(currentPage);
//                    if (previousPageButton != null) {
//                        previousPageButton.setBackgroundTintList(whiteButtonTint);
//                    }
//
//                    // Set the background tint color of the clicked button to green
//                    pageButton.setBackgroundTintList(greenButtonTint);
//
//                    currentPage = pageNumber;
//                    loadPage(currentPage, isAdvancedSearchEnabled);
//
//                    // Update the previousPageButton reference
//                    previousPageButton = pageButton;
//                }
//            });
//
//            pageNumberLayout.addView(view_opponents);
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//        }
//
//    }
//
//    private void loadPage(int page, boolean isAdvancedSearchEnabled) {
//        try {
//
//            if (!isAdvancedSearchEnabled) {
//                sorted_list.clear();
//
//                for (int i = 0; i < auditsList.size(); i++) {
////                AuditsModel auditsModel = auditsList.get(i);
//                    if (auditsList.get(i).getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
//                        AuditsModel auditsModel1 = new AuditsModel();
//                        auditsModel1.setName(auditsList.get(i).getName());
//                        auditsModel1.setTimestamp(auditsList.get(i).getTimestamp());
//                        auditsModel1.setMessage(auditsList.get(i).getMessage());
//                        sorted_list.add(auditsModel1);
//                    }
//                }
//                Log.d("Sorted_list", String.valueOf(sorted_list.size()));
//                loadRecyclerView(page);
//            } else {
//                loadRecyclerView(page);
//            }
//        } catch (Exception e) {
//            Log.d("LoadPageException", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    private void loadRecyclerView(int page) {
//        try {
//            if (sorted_list.size() != 0) {
//                int startIndex = (page - 1) * itemsPerPage;
//                int endIndex = Math.min(startIndex + itemsPerPage, sorted_list.size());
//                pageItems = new ArrayList<>(sorted_list.subList(startIndex, endIndex));
//
//                if (rv_audits.getAdapter() == null) {
//                    // If the adapter is null, create a new one and set it to the RecyclerView
//                    rv_audits.setLayoutManager(new GridLayoutManager(getContext(), 1));
//                    audit_adapter = new AuditsAdapter(pageItems);
//                    rv_audits.setAdapter(audit_adapter);
//
//                    et_search_relationships.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
//                            audit_adapter.getFilter().filter(et_search_relationships.getText().toString());
//                        }
//                    });
//                } else {
//                    // If the adapter already exists, notify it about the new data
//                    ((AuditsAdapter) rv_audits.getAdapter()).setData(pageItems);
//                }
//            }
//        } catch (Exception e) {
//            Log.d("Recyclervie_Exception", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void onDateSelected(String selectedDate, String FLAG) {
//        loadnewPage(selectedDate, FLAG);
//    }
//
//    private void loadnewPage(String selectedDate, String FLAG) {
//        sorted_list.clear();
//        rv_audits.removeAllViews();
//        rv_audits.setAdapter(null);
//        et_search_relationships.setText("");
//        Date startDate = DateUtils.stringToDate(tv_event_start_time.getText().toString());
//        Date endDate = DateUtilsEndDate.stringToDate(tv_event_end_time.getText().toString());
//
//        Date selectedDateObj = DateUtils.stringToDate(selectedDate);
//        for (int i = 0; i < auditsList.size(); i++) {
//            AuditsModel auditsModel = auditsList.get(i);
//            String timestamp = auditsModel.getTimestamp();
//            Date formatted_date = AndroidUtils.stringToDateTimeDefault(timestamp, "MMM dd,yyyy, hh:mm a");
//            if ((startDate != null && endDate != null) && (formatted_date != null)) {
//                if ((formatted_date.equals(startDate) || formatted_date.after(startDate)) &&
//                        (formatted_date.equals(endDate) || formatted_date.before(endDate)) &&
//                        auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
//                    sorted_list.add(auditsModel);
//                }
//            } else if ((startDate != null) && (formatted_date != null)) {
//                if (formatted_date.after(startDate) || formatted_date.equals(startDate) && auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT)) ){
//                    sorted_list.add(auditsModel);
//                }
//            } else if ((endDate != null) && (formatted_date != null)) {
//                if (formatted_date.before(endDate) || formatted_date.equals(endDate) && auditsModel.getName().startsWith(Catergory_type.toUpperCase(Locale.ROOT))) {
//                    sorted_list.add(auditsModel);
//                }
//            }
//        }
//
//        setupPagination();
//        currentPage = 1;
//        isAdvancedSearchEnabled = true;
//        loadPage(currentPage, isAdvancedSearchEnabled);
//
//    }
//
//
//    @Override
//    public void onDateSelectedEndDate(String selectedDate, String FLAG) {
//        loadnewPage(selectedDate, FLAG);
//    }
//
//}
