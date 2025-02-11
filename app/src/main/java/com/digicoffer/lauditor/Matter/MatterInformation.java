package com.digicoffer.lauditor.Matter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

//
//import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.digicoffer.lauditor.Matter.Models.AdvocateModel;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MatterInformation extends Fragment implements View.OnClickListener {
    TextInputEditText tv_matter_title, tv_matter_num, tv_case_type, tv_matter_description, tv_court, tv_judge;
    AppCompatButton tv_start_date, tv_end_date, tv_dof;
    private List<TextView> pagebuttons = new ArrayList<>();
    TextView tv_high_priority, tv_medium_priority, tv_low_priority, tv_status_active, tv_status_pending, Title, datefill, start_date, closedate, court, judge, priority, status, addopponentadvocate, name;
    Button btn_add_advocate, btn_cancel_edit;
    private int currentPage = 1;
    private int end_index;
    private int pageNumber;
    LinearLayout ll_page_navigaiton, pageNumberLayout;
    HorizontalScrollView scrollView;
    ArrayList<AdvocateModel> advocates_list = new ArrayList<>();

    ArrayList<MatterModel> matterArraylist;
    Matter matter;
    TextView m_c_number, m_c_type, description_name;
    AppCompatButton btn_cancel_save, btn_create;
    LinearLayout ll_add_advocate, ll_start_date, ll_end_date, ll_court, ll_judge, ll_dof;
    JSONArray existing_opponents;
    CardView cv_client_details, cv_add_opponent_advocate;
    TextView advocate_title, advocate_email, advocate_phone;
    String CASE_PRIORITY = "High";
    String STATUS = "Active";

    LinearLayout ll_opponent_advocate;
    TextInputEditText tv_advocate_name, tv_advocate_email, tv_advocate_phone;
    AppCompatButton btn_cancel_tag, btn_save_tag;
    EditMatterTimeline editMatterTimeline;
    ImageView iv_backward_button, iv_forward_button;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "MatterInformation";
    private Calendar finalMyCalendar = Calendar.getInstance();


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matter_information, container, false);
        tv_matter_title = view.findViewById(R.id.tv_matter_title);
        tv_matter_title.setHint(R.string.case_title);
        ll_opponent_advocate = view.findViewById(R.id.ll_opponent_advocate);
        ll_opponent_advocate.setVisibility(View.GONE);
        btn_save_tag = view.findViewById(R.id.btn_save_tag);
        ll_page_navigaiton = view.findViewById(R.id.ll_page_navigaiton);
        ll_page_navigaiton.setVisibility(View.GONE);
        scrollView = view.findViewById(R.id.PageScrollView);
        pageNumberLayout = view.findViewById(R.id.pageNumberLayout);
        iv_backward_button = view.findViewById(R.id.iv_backward_button);
        iv_backward_button.setVisibility(View.VISIBLE);
        iv_backward_button.setVisibility(View.VISIBLE);
        iv_forward_button = view.findViewById(R.id.iv_forward_button);
        iv_forward_button.setImageDrawable(getContext().getDrawable(R.drawable.baseline_arrow_forward_ios_24));
//        btn_cancel_tag=view.findViewById(R.id.btn_cancel_tag);
        tv_advocate_name = view.findViewById(R.id.tv_advocate_name);
        tv_advocate_email = view.findViewById(R.id.tv_advocate_email);
        tv_advocate_phone = view.findViewById(R.id.tv_advocate_phone);
        advocate_title = view.findViewById(R.id.advocate_title);
        advocate_title.setText(R.string.name);
        advocate_email = view.findViewById(R.id.advocate_email);
        advocate_email.setText(R.string.email);
        advocate_phone = view.findViewById(R.id.advocate_phone);
        advocate_phone.setText(R.string.phone_number);


        tv_matter_title.setTextSize(15);
        cv_add_opponent_advocate = view.findViewById(R.id.cv_add_opponent_advocate);
        tv_matter_num = view.findViewById(R.id.tv_matter_num);
        tv_matter_num.setHint(R.string.case_number);
        tv_matter_num.setTextSize(15);
        btn_cancel_edit = view.findViewById(R.id.btn_cancel_edit);
        tv_case_type = view.findViewById(R.id.tv_case_type);
        tv_case_type.setHint(R.string.case_type);
        tv_case_type.setTextSize(15);
        description_name = view.findViewById(R.id.description_name);
        description_name.setText(R.string.description);
        Title = view.findViewById(R.id.Title_name);
        Title.setText(R.string.case_title);
        datefill = view.findViewById(R.id.datefill);
        datefill.setText(R.string.date_of_filing);
        start_date = view.findViewById(R.id.start_date);
        start_date.setText(R.string.start_date);
        closedate = view.findViewById(R.id.closedate);
        closedate.setText(R.string.close_date);
        court = view.findViewById(R.id.court);
        court.setText(R.string.court);
        judge = view.findViewById(R.id.judge);
        judge.setText(R.string.judge_s);
        priority = view.findViewById(R.id.priority);
        priority.setText(R.string.priority);
        status = view.findViewById(R.id.status);
        status.setText(R.string.status);
        addopponentadvocate = view.findViewById(R.id.addopponentadvocate);
        addopponentadvocate.setText(R.string.opponent_advocate);
        ll_court = view.findViewById(R.id.ll_court);
        ll_judge = view.findViewById(R.id.ll_judge);
        ll_dof = view.findViewById(R.id.ll_dof);
        m_c_number = view.findViewById(R.id.m_c_number);
        m_c_number.setText(R.string.case_number);
        m_c_type = view.findViewById(R.id.m_c_type);
        m_c_type.setText(R.string.case_type);
        ll_end_date = view.findViewById(R.id.ll_end_date);
        ll_start_date = view.findViewById(R.id.ll_start_date);
        tv_start_date = view.findViewById(R.id.tv_start_date);
        tv_start_date.setHint("Start Data");
        tv_start_date.setTextSize(15);
        tv_end_date = view.findViewById(R.id.tv_end_date);
        tv_end_date.setHint("Close Data");
        tv_end_date.setTextSize(15);
        cv_client_details = view.findViewById(R.id.cv_client_details);
        tv_matter_description = view.findViewById(R.id.tv_matter_description);
        tv_matter_description.setHint(R.string.description);
        tv_matter_description.setTextSize(15);
        tv_dof = view.findViewById(R.id.tv_dof);
        tv_dof.setHint(R.string.date_of_filing);
        tv_dof.setTextSize(15);
        tv_court = view.findViewById(R.id.tv_court);
        tv_court.setHint(R.string.court);
        tv_court.setTextSize(15);
        tv_judge = view.findViewById(R.id.tv_judge);
        tv_judge.setHint(R.string.judge_s);
        tv_judge.setTextSize(15);
        tv_high_priority = view.findViewById(R.id.tv_high_priority);
        tv_high_priority.setOnClickListener(this);
        tv_medium_priority = view.findViewById(R.id.tv_medium_priority);
        tv_medium_priority.setOnClickListener(this);
        tv_low_priority = view.findViewById(R.id.tv_low_priority);
        tv_low_priority.setOnClickListener(this);
        tv_status_pending = view.findViewById(R.id.tv_status_pending);
        tv_status_pending.setOnClickListener(this);
        tv_status_active = view.findViewById(R.id.tv_status_active);
        tv_status_active.setOnClickListener(this);
        btn_add_advocate = view.findViewById(R.id.btn_add_advocate);
        btn_add_advocate.setOnClickListener(this);
        btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        btn_cancel_save.setOnClickListener(this);
        btn_create = view.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);
        ll_add_advocate = view.findViewById(R.id.ll_add_advocate);
        ll_add_advocate.setVisibility(View.GONE);
        matter = (Matter) getParentFragment();
        tv_start_date.setInputType(InputType.TYPE_NULL);
        tv_end_date.setInputType(InputType.TYPE_NULL);
        tv_dof.setInputType(InputType.TYPE_NULL);
        if (Objects.equals(Constants.MATTER_TYPE, "Legal")) {
            m_c_number.setText(R.string.case_number);
            m_c_type.setText(R.string.case_type);
            ll_court.setVisibility(View.VISIBLE);
            ll_judge.setVisibility(View.VISIBLE);
            ll_dof.setVisibility(View.VISIBLE);
            ll_start_date.setVisibility(View.GONE);
            ll_end_date.setVisibility(View.GONE);
            datePickerData();
        } else {
            m_c_number.setText(R.string.matter_number);
            m_c_type.setText(R.string.matter_type);
            ll_court.setVisibility(View.GONE);
            ll_judge.setVisibility(View.GONE);
            ll_dof.setVisibility(View.GONE);
            ll_start_date.setVisibility(View.VISIBLE);
            ll_end_date.setVisibility(View.VISIBLE);
            datePickerStartDate();
            datePickerEndDate();
        }

        matterArraylist = matter.getMatter_arraylist();

        if (!matterArraylist.isEmpty()) {
            for (int i = 0; i < matterArraylist.size(); i++) {
                MatterModel matterModel = matterArraylist.get(i);
                if (matterModel.getMatter_title() != null) {
                    tv_matter_title.setText(matterModel.getMatter_title());
                } else {
                    tv_matter_title.setText("");
                }
                if (matterModel.getCase_number() != null) {
                    tv_matter_num.setText(matterModel.getCase_number());

                } else {
                    tv_matter_num.setText("");
                }
                if (matterModel.getCase_type() != null) {
                    tv_case_type.setText(matterModel.getCase_type());
                } else {
                    tv_case_type.setText("");
                }
                if (matterModel.getDescription() != null) {
                    tv_matter_description.setText(matterModel.getDescription());
                } else {
                    tv_matter_description.setText("");
                }
                if (matterModel.getDate_of_filing() != null) {
                    tv_dof.setText(matterModel.getDate_of_filing());
                } else {
                    datePickerData();
                }
                if (matterModel.getStart_date() != null) {
                    tv_start_date.setText(matterModel.getStart_date());
                } else {
                    datePickerStartDate();
                }
                if (matterModel.getEnd_date() != null) {
                    tv_end_date.setText(matterModel.getEnd_date());
                } else {
                    datePickerEndDate();
                }
                if (matterModel.getCourt() != null) {
                    tv_court.setText(matterModel.getCourt());
                } else {
                    tv_court.setText("");
                }
                if (matterModel.getJudge() != null) {
                    tv_judge.setText(matterModel.getJudge());
                } else {
                    tv_judge.setText("");
                }
                if (matterModel.getCase_priority() != null) {

                    CASE_PRIORITY = matterModel.getCase_priority();
                }
                if (matterModel.getStatus() != null) {
                    STATUS = matterModel.getStatus();
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
                        loadOpponentsList();
                        loadActiveUI();
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            }
            if (Objects.equals(CASE_PRIORITY, "High")) {
                loadHighPriorityUI();
            } else if (Objects.equals(CASE_PRIORITY, "Medium")) {
                loadMediumPriorityUI();
            } else {
                loadLowPriorityUI();
            }
            if (Objects.equals(STATUS, "Active")) {
                loadActiveUI();
            } else {
                loadPendingUI();
            }
        }
        iv_forward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < pageNumber - 1) {
                    currentPage++;
                    AdvocateModel advocateModel = advocates_list.get(currentPage);
                    EditAdvocateUI(advocateModel.getAdvocate_name(), advocateModel.getEmail(), advocateModel.getNumber(), currentPage, view);
                    UpdatePageButton(currentPage);
                } else {
//                    AndroidUtils.showAlert("Index ended", getContext());
                    Log.d("page_index_f", "Index ended");
                }
            }
        });
        iv_backward_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    AdvocateModel advocateModel = advocates_list.get(currentPage);
                    EditAdvocateUI(advocateModel.getAdvocate_name(), advocateModel.getEmail(), advocateModel.getNumber(), currentPage, view);
                    UpdatePageButton(currentPage);
                } else {
                    Log.d("page_index_p", "Index ended");
                }
            }
        });
        return view;
    }

    //        loadPage(currentPage);
    private void datePickerStartDate() {
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
                tv_start_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        tv_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedYear = myCalendar.get(Calendar.YEAR);
                int selectedMonth = myCalendar.get(Calendar.MONTH);
                int selectedDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        date,
                        selectedYear, selectedMonth, selectedDay);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }


    private void datePickerEndDate() {
        final Calendar[] myCalendar = {Calendar.getInstance()};
        Calendar finalMyCalendar = myCalendar[0];
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                finalMyCalendar.set(Calendar.YEAR, year);
                finalMyCalendar.set(Calendar.MONTH, month);
                finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_dof.setText(sdf.format(finalMyCalendar.getTime()));
            }
        };
        // Declare a global variable to store the selected date
        final int[] selectedYear = new int[1];
        final int[] selectedMonth = new int[1];
        final int[] selectedDay = new int[1];
        tv_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar[0] = Calendar.getInstance();
                selectedYear[0] = myCalendar[0].get(Calendar.YEAR);
                selectedMonth[0] = myCalendar[0].get(Calendar.MONTH);
                selectedDay[0] = myCalendar[0].get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog;
                dialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        selectedYear[0], selectedMonth[0], selectedDay[0]);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            Log.d(TAG, "onDataSet : mm/dd/yy : " + month + "/" + dayOfMonth + "/" + year);
            updateDateInViews(year, month, dayOfMonth);
        };
    }

    private void updateDateInViews(int year, int month, int dayOfMonth) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month - 1, dayOfMonth);
        String formattedDate = df.format(selectedCalendar.getTime());
        tv_end_date.setText(formattedDate);
    }

    private void datePickerData() {
        final DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
            finalMyCalendar.set(Calendar.YEAR, year);
            finalMyCalendar.set(Calendar.MONTH, month);
            finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        tv_dof.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    v.getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    finalMyCalendar.get(Calendar.YEAR),
                    finalMyCalendar.get(Calendar.MONTH),
                    finalMyCalendar.get(Calendar.DAY_OF_MONTH));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }

    private void updateDateInView() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = sdf.format(finalMyCalendar.getTime());
        tv_dof.setText(formattedDate);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_high_priority:
                loadHighPriorityUI();
                break;
            case R.id.tv_medium_priority:
                loadMediumPriorityUI();
                break;
            case R.id.tv_low_priority:
                loadLowPriorityUI();
                break;
            case R.id.tv_status_pending:
                loadPendingUI();
                break;
            case R.id.tv_status_active:
                loadActiveUI();
                break;
            case R.id.btn_add_advocate:
                loadAdvocateUI();
                break;
            case R.id.btn_create:
                saveMatterInformation();
                break;
            case R.id.btn_cancel_save:
                matter.loadViewUI();
                break;
        }
    }

    public void saveMatterInformation() {
        String msg = "Please check the Title";
        if ((Objects.requireNonNull(tv_matter_title.getText()).toString().trim().isEmpty()) && (Objects.requireNonNull(tv_matter_num.getText()).toString().isEmpty())) {
            AndroidUtils.showAlert(msg + " , Case Number", getContext());
        } else if (Objects.requireNonNull(tv_matter_title.getText()).toString().trim().isEmpty()) {
            AndroidUtils.showAlert(msg, getContext());
            tv_matter_title.requestFocus();
        } else if (Objects.requireNonNull(tv_matter_num.getText()).toString().trim().isEmpty()) {
            AndroidUtils.showAlert(msg.replace("Title", "Case Number"), getContext());
            tv_matter_num.requestFocus();
        } else if (Objects.requireNonNull(tv_matter_description.getText()).toString().length() > 300) {
            AndroidUtils.showAlert("Please check the description field size..", getContext());
        } else {
            submitMatter();
        }
    }

    void submitMatter() {
        JSONArray group_acls = new JSONArray();
//            JSONArray client = new JSONArray();
//            JSONArray members = new JSONArray();
        MatterModel matterModel = new MatterModel();
        matterModel.setMatter_title(Objects.requireNonNull(tv_matter_title.getText()).toString());
        tv_matter_title.clearFocus();
        matterModel.setCase_number(Objects.requireNonNull(tv_matter_num.getText()).toString());
        tv_matter_num.clearFocus();
        matterModel.setCase_type(Objects.requireNonNull(tv_case_type.getText()).toString());
        matterModel.setDescription(Objects.requireNonNull(tv_matter_description.getText()).toString());
        matterModel.setDate_of_filing(tv_dof.getText().toString());
        matterModel.setStart_date(tv_start_date.getText().toString());
        matterModel.setEnd_date(tv_end_date.getText().toString());
        matterModel.setCourt(Objects.requireNonNull(tv_court.getText()).toString());
        matterModel.setJudge(Objects.requireNonNull(tv_judge.getText()).toString());
        matterModel.setCase_priority(CASE_PRIORITY);
        matterModel.setStatus(STATUS);
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
        matterModel.setOpponent_advocate(jsonArray);
        if (matterArraylist.isEmpty()) {
            matterArraylist.add(matterModel);
        } else {
            matterArraylist.set(0, matterModel);
        }
        // matterModel_info = matterModel;
        cv_client_details.setVisibility(View.GONE);
        matter.loadGCT();
    }

    private void loadAdvocateUI() {
        try {
            ll_opponent_advocate.setVisibility(View.VISIBLE);
            RefreshAdvocateView();
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            LayoutInflater inflater = requireActivity().getLayoutInflater();
//            View view = inflater.inflate(R.layout.add_opponent_advocate, null);
            tv_advocate_name.setHint(R.string.name);
            tv_advocate_name.setTextSize(15);
            tv_advocate_email.setHint(R.string.email);
            tv_advocate_email.setTextSize(15);
            tv_advocate_phone.setInputType(InputType.TYPE_CLASS_PHONE);
            tv_advocate_phone.setHint(R.string.phone_number);
            tv_advocate_phone.setTextSize(15);
            loadHighPriorityUI();
            loadActiveUI();
            tv_advocate_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty()) {
                        tv_advocate_name.setError("Please Enter the name.");
                        tv_advocate_name.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            tv_advocate_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!(Objects.requireNonNull(tv_advocate_email.getText()).toString().trim().matches(Patterns.EMAIL_ADDRESS.toString()))) {
                        tv_advocate_email.setError("Please enter the Email.");
                        tv_advocate_email.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            tv_advocate_phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().isEmpty() || (s.length() < 10)) {
                        tv_advocate_phone.setError("Please enter a 10 digit valid mobile number.");
                        tv_advocate_phone.requestFocus();
                    }
                }
            });

//            btn_cancel_tag.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });

            btn_save_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Objects.requireNonNull(tv_advocate_name.getText()).toString().trim().isEmpty() && (Objects.requireNonNull(tv_advocate_email.getText()).toString().trim().isEmpty()) && (Objects.requireNonNull(tv_advocate_phone.getText()).toString().trim().isEmpty())) {
                        tv_advocate_name.setError("Please Enter the name.");
                        tv_advocate_email.setError("Please enter the Email.");
                        tv_advocate_phone.setError("Please enter a 10 digit valid mobile number.");
                    } else if (Objects.requireNonNull(tv_advocate_name.getText()).toString().trim().isEmpty()) {
                        tv_advocate_name.setError("Please Enter the name.");
                        tv_advocate_name.requestFocus();
                    } else if ((!(Objects.requireNonNull(tv_advocate_email.getText()).toString().matches(Patterns.EMAIL_ADDRESS.toString()))) || (tv_advocate_email.getText().toString().trim().isEmpty())) {
                        tv_advocate_email.setError("Please enter the Email.");
                        tv_advocate_email.requestFocus();
                    } else if (Objects.requireNonNull(tv_advocate_phone.getText()).toString().trim().isEmpty() || (tv_advocate_phone.getText().length() < 10)) {
                        tv_advocate_phone.setError("Please enter a 10 digit valid mobile number.");
                        tv_advocate_phone.requestFocus();
//                    } else if (!(tv_advocate_phone.getText().toString().matches(Patterns.PHONE.toString()))) {
//                        tv_advocate_phone.setError("Please enter a valid phone number");
//                        tv_advocate_phone.requestFocus();
                    } else {
                        AdvocateModel advocateModel = new AdvocateModel();
                        advocateModel.setAdvocate_name(tv_advocate_name.getText().toString());
                        advocateModel.setEmail(tv_advocate_email.getText().toString());
                        advocateModel.setNumber(tv_advocate_phone.getText().toString());
                        advocates_list.add(advocateModel);
//                        ll_opponent_advocate.setVisibility(View.GONE);
//                        setupPagination();
                        loadOpponentsList();
                        ll_page_navigaiton.setVisibility(View.VISIBLE);
                        RefreshAdvocateView();
                    }
                }
            });
//            dialog.setCancelable(false);
//            dialog.setView(view);
//            dialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadEditedData(String adv_name, String adv_email, String adv_phone, int position, View view_advocate) {
        AdvocateModel advocateModel = new AdvocateModel();
        advocateModel.setAdvocate_name(adv_name);
        advocateModel.setEmail(adv_email);
        advocateModel.setNumber(adv_phone);
        advocates_list.set(position, advocateModel);
//        tv_opponent_name = view_advocate.findViewById(R.id.tv_opponent_name);
//        tv_opponent_name.setText(advocateModel.getAdvocate_name());
    }

    private void loadOpponentsList() {
        pageNumberLayout.removeAllViews();
        pagebuttons.clear();
        for (int i = 0; i < advocates_list.size(); i++) {
            //...
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.weekly_view_dates, null);
            TextView pageButton = view_opponents.findViewById(R.id.textDay);
            pageButton.setText(String.valueOf(i + 1));
            pageButton.setPadding(20, 15, 20, 15);
            pageNumber = i + 1;
            if (pageNumber == advocates_list.size()) {
                pageButton.setTextColor(getActivity().getColor(R.color.white));
                pageButton.setBackground(getActivity().getDrawable(R.drawable.rectangular_button_green_count));
            } else {
                pageButton.setTextColor(getActivity().getColor(R.color.black));
                pageButton.setBackground(getActivity().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            }
            currentPage = pageNumber;

            pageButton.setTag(i);
            pageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Set the background tint color of the clicked button to green
                    int position = 0;
                    if (view.getTag() instanceof Integer) {
                        position = (Integer) view.getTag();
                        view = ll_page_navigaiton.getChildAt(position);
//                        ll_add_advocate.addView(view);
                        currentPage = position;
                        AdvocateModel advocateModel = advocates_list.get(position);
                        EditAdvocateUI(advocateModel.getAdvocate_name(), advocateModel.getEmail(), advocateModel.getNumber(), position, view);
                        UpdatePageButton(currentPage);
                    }
                }
            });
            pagebuttons.add(pageButton);
            pageNumberLayout.addView(view_opponents);
        }
    }

    private void UpdatePageButton(int currentPage) {
        for (int i = 0; i < pagebuttons.size(); i++) {
            TextView pageButton = pagebuttons.get(i);
            if (i == currentPage) {
                pageButton.setTextColor(getActivity().getColor(R.color.white));
                pageButton.setBackground(getActivity().getDrawable(R.drawable.rectangular_button_green_count));
            } else {
                pageButton.setTextColor(getActivity().getColor(R.color.black));
                pageButton.setBackground(getActivity().getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
            }
        }
    }

    private void EditAdvocateUI(String advocate_name, String email, String number, int position, View view_advocate) {
        try {
            ll_opponent_advocate.setVisibility(View.VISIBLE);
            RefreshAdvocateView();
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            LayoutInflater inflater = requireActivity().getLayoutInflater();
//            View view = inflater.inflate(R.layout.add_opponent_advocate, null);
//            TextInputEditText tv_advocate_name = view.findViewById(R.id.tv_advocate_name);
//            TextInputEditText tv_advocate_email = view.findViewById(R.id.tv_advocate_email);
//            TextInputEditText tv_advocate_phone = view.findViewById(R.id.tv_advocate_phone);
            tv_advocate_phone.setInputType(InputType.TYPE_CLASS_PHONE);
//            AppCompatButton btn_cancel_tag = view.findViewById(R.id.btn_cancel_tag);
//            AppCompatButton btn_save_tag = view.findViewById(R.id.btn_save_tag);
//            final AlertDialog dialog = builder.create();

            tv_advocate_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().isEmpty()) {
                        tv_advocate_name.setError("Please Enter the name.");
                        tv_advocate_name.requestFocus();
                    }
                }
            });
            tv_advocate_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!(Objects.requireNonNull(tv_advocate_email.getText()).toString().trim().matches(Patterns.EMAIL_ADDRESS.toString()))) {
                        tv_advocate_email.setError("Please enter the Email.");
                        tv_advocate_email.requestFocus();
                    }
                }
            });
            tv_advocate_phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().isEmpty() || (s.length() < 10)) {
                        tv_advocate_phone.setError("Please enter a 10 digit valid mobile number.");
                        tv_advocate_phone.requestFocus();
                    }
                }
            });

//            btn_cancel_tag.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });

            tv_advocate_name.setText(advocate_name);
            tv_advocate_email.setText(email);
            tv_advocate_phone.setText(number);
            btn_save_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Objects.requireNonNull(tv_advocate_name.getText()).toString().trim().isEmpty()) {
                        tv_advocate_name.setError("Please Enter the name.");
                        tv_advocate_name.requestFocus();
                    } else if ((!(Objects.requireNonNull(tv_advocate_email.getText()).toString().matches(Patterns.EMAIL_ADDRESS.toString()))) || (tv_advocate_email.getText().toString().trim().isEmpty())) {
                        tv_advocate_email.setError("Please enter the Email.");
                        tv_advocate_email.requestFocus();
                    } else if (Objects.requireNonNull(tv_advocate_phone.getText()).toString().trim().isEmpty() || (tv_advocate_phone.getText().length() < 10)) {
                        tv_advocate_phone.setError("Please enter a 10 digit valid mobile number.");
                        tv_advocate_phone.requestFocus();
//                    } else if (!(tv_advocate_phone.getText().toString().matches(Patterns.PHONE.toString()))) {
//                        tv_advocate_phone.setError("Please enter a valid phone number");
//                        tv_advocate_phone.requestFocus();
                    } else {
//                        dialog.dismiss();
//                        ll_opponent_advocate.setVisibility(View.GONE);
                        loadEditedData(tv_advocate_name.getText().toString(), tv_advocate_email.getText().toString(), tv_advocate_phone.getText().toString(), position, view_advocate);
//                        loadOpponentsList(advocates_list);
                        RefreshAdvocateView();
                        loadAdvocateUI();
                    }
                }
            });
//            dialog.setCancelable(false);
//            dialog.setView(view);
//            dialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void RefreshAdvocateView() {
        tv_advocate_name.setText("");
        tv_advocate_email.setText("");
        tv_advocate_phone.setText("");
        tv_advocate_name.setError(null);
        tv_advocate_email.setError(null);
        tv_advocate_phone.setError(null);
    }

    private void loadActiveUI() {
        STATUS = "Active";
        tv_status_active.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_round_background));
        tv_status_active.setTextColor(Color.WHITE);
        tv_status_pending.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_round_background));
        tv_status_pending.setTextColor(Color.BLACK);
    }

    private void loadPendingUI() {
        STATUS = "Pending";
        tv_status_active.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_round_background));
        tv_status_active.setTextColor(Color.BLACK);
        tv_status_pending.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_round_background));
        tv_status_pending.setTextColor(Color.WHITE);
    }

    private void loadLowPriorityUI() {
        CASE_PRIORITY = "Low";
        tv_high_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_round_background));
        tv_high_priority.setTextColor(Color.BLACK);
        tv_medium_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.radiobutton_centre_background));
        tv_medium_priority.setTextColor(Color.BLACK);
        tv_low_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_round_background));
        tv_low_priority.setTextColor(Color.WHITE);
    }

    private void loadMediumPriorityUI() {
        CASE_PRIORITY = "Medium";
        tv_high_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_round_background));
        tv_high_priority.setTextColor(Color.BLACK);
        tv_medium_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.radiobutton_centre_green_background));
        tv_medium_priority.setTextColor(Color.WHITE);
        tv_low_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_round_background));
        tv_low_priority.setTextColor(Color.BLACK);
    }

    private void loadHighPriorityUI() {
        CASE_PRIORITY = "High";
        tv_high_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_round_background));
        tv_high_priority.setTextColor(Color.WHITE);
        tv_medium_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.radiobutton_centre_background));
        tv_medium_priority.setTextColor(Color.BLACK);
        tv_low_priority.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_round_background));
        tv_low_priority.setTextColor(Color.BLACK);
    }
}
