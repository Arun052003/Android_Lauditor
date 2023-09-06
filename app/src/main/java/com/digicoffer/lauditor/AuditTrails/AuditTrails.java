package com.digicoffer.lauditor.AuditTrails;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;
import com.digicoffer.lauditor.AuditTrails.Model.SpinnerItemModal;
import com.digicoffer.lauditor.Chat.ChatAdapter;
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
import java.util.List;

public class AuditTrails extends Fragment implements AsyncTaskCompleteListener {
        ArrayList<SpinnerItemModal> categoryList = new ArrayList<>();
        TextView tv_name,tv_advanced_search;
        Spinner sp_category;
        RecyclerView rv_audits;
    private int currentPage = 1;
        ArrayList<AuditsModel> auditsList = new ArrayList<>();

    AlertDialog progress_dialog;
        TextInputLayout et_search;
        TextInputEditText et_search_relationships;
    LinearLayout datePickersLayout;
    private int itemsPerPage = 10;
    LinearLayout pageNumberLayout;
    private boolean isDatePickerVisible = false;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      try {
         view  = inflater.inflate(R.layout.audit_trials, container, false);
          sp_category = view.findViewById(R.id.sp_project);
          tv_name = view.findViewById(R.id.tv_name);
          datePickersLayout = view.findViewById(R.id.datePickersLayout);
          tv_advanced_search = view.findViewById(R.id.tv_advancedSearch);
          et_search = view.findViewById(R.id.et_search);
          rv_audits = view.findViewById(R.id.rv_audits);
          et_search.setHint(R.string.search);
          pageNumberLayout = view.findViewById(R.id.pageNumberLayout);
          et_search_relationships = view.findViewById(R.id.et_search_relationships);
          loadSearchTextData();
          tv_name.setText("Category");

          loadSpinnerData();
          callAuditWebservice();
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
        ss.setSpan(clickableSpan,0,15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
        categoryList.add(new SpinnerItemModal("Authorizatoin"));
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
                Log.d("Request_Type",httpResult.getRequestType().toString());
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

    private void loadAuditsData(JSONArray jsonArray) throws JSONException{
            for (int i=0;i<jsonArray.length();i++){
                AuditsModel auditsModel = new AuditsModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                auditsModel.setMessage(jsonObject.getString("message"));
                auditsModel.setName(jsonObject.getString("name"));
                auditsModel.setTimestamp(jsonObject.getString("timestamp"));
                if(!(auditsModel.getMessage().equals("PROFILE UPDATE"))||!(auditsModel.getMessage().equals("DOCS COLLABORATION"))){
                auditsList.add(auditsModel);
                }
            }
            setupPagination();
            loadPage(currentPage);
//            LoadAuditsRecyclerview();
    }


    private void setupPagination() {
         // Maximum number of page buttons to display

        int totalPages = (int) Math.ceil((double) auditsList.size() / itemsPerPage);
        int maxPageButtons = 5;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonWidth = screenWidth / maxPageButtons;
        // Dynamically add page number buttons
        for (int i = 1; i <= totalPages; i++) {
            if (i >= maxPageButtons) {
                // Display only maxPageButtons page numbers
                break;
            }

            Button pageButton = new Button(getContext());
            pageButton.setText(String.valueOf(i));
            final int pageNumber = i;
            pageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage = pageNumber;
                    loadPage(currentPage);
                }
            });

            // Set the width of the page number button
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    buttonWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            pageButton.setLayoutParams(params);

            pageNumberLayout.addView(pageButton);
        }
//        loadPage(currentPage);
    }
    private void loadPage(int page) {
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, auditsList.size());
        ArrayList<AuditsModel> pageItems = new ArrayList<>(auditsList.subList(startIndex, endIndex));

        if (rv_audits.getAdapter() == null) {
            // If the adapter is null, create a new one and set it to the RecyclerView
            rv_audits.setLayoutManager(new GridLayoutManager(getContext(), 1));
            final AuditsAdapter adapter = new AuditsAdapter(pageItems);
            rv_audits.setAdapter(adapter);

            et_search_relationships.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(et_search_relationships.getText().toString());
                }
            });
        } else {
            // If the adapter already exists, notify it about the new data
            ((AuditsAdapter) rv_audits.getAdapter()).setData(pageItems);
        }
    }
//        adapter = new MyAdapter(pageItems);
//        recyclerView.setAdapter(adapter);

}

