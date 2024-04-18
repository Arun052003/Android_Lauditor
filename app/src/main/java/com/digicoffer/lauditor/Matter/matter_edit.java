package com.digicoffer.lauditor.Matter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digicoffer.lauditor.Matter.Models.AdvocateModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link matter_edit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class matter_edit extends Fragment implements AsyncTaskCompleteListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;


    //Adding all the filed items in edit matter page
    ViewMatterModel viewMatterModel1;
    private AlertDialog progress_dialog;

    ArrayList<AdvocateModel> advocates_list = new ArrayList<>();
    TextView tv_matter_title, tv_matter_num, tv_case_type, tv_matter_description, tv_court, tv_judge;
    AppCompatButton tv_start_date, tv_end_date, tv_dof;
    TextView description_name, court, judge, Title, datefill, start_date, closedate, priority, status, addopponentadvocate, m_c_number, m_c_type;
    private String mParam2;
    MatterInformation matterInformation;
    LinearLayoutCompat ll_save_buttons;
    private Calendar finalMyCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    ArrayList<ViewMatterModel> existing_clients = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_documents = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_members = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_advocates = new ArrayList<>();
    ArrayList<ViewMatterModel> existing_groups = new ArrayList<>();
    ConstraintLayout edit_matter_info;
    CardView cv_client_details;
    String CASE_PRIORITY = "High";
    String STATUS = "Active";
    String DATE_ROLE = "";
    ShapeableImageView edit_matter_page_icon;
    TextView tv_high_priority, tv_medium_priority, tv_low_priority, tv_status_active, tv_status_pending, tv_view_matter, edit_matter_page_txt;
    AppCompatButton btn_cancel_save, btn_create, btn_add_advocate;
    LinearLayout ll_start_date, ll_end_date, ll_court, ll_judge, ll_dof;
    Matter matter;
    //    private NewModel mViewModel;
    LinearLayoutCompat ll_header;
    LinearLayout ll_add_advocate;
    ViewMatter viewMatter1;
    String ad_name = "";
    String ad_email = "";
    String ad_phone = "";

    public matter_edit(ViewMatterModel viewMatterModel, ViewMatter viewMatter) {
        viewMatterModel1 = viewMatterModel;
        viewMatter1 = viewMatter;
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public matter_edit newInstance(ViewMatterModel viewMatterModel) throws IllegalAccessException, java.lang.InstantiationException {
        matter_edit fragment = new matter_edit(viewMatterModel, ViewMatter.class.newInstance());
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matter_information, container, false);
//        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
//        mViewModel.setData("Edit Matter Info");
        tv_matter_title = view.findViewById(R.id.tv_matter_title);
        tv_matter_title.setHint(R.string.case_title);
        tv_matter_title.setTextSize(15);
        tv_view_matter = view.findViewById(R.id.tv_view_matter);

        tv_matter_num = view.findViewById(R.id.tv_matter_num);
        tv_matter_num.setHint(R.string.case_number);
        tv_matter_num.setTextSize(15);

        edit_matter_page_icon = view.findViewById(R.id.edit_matter_page_icon);
        edit_matter_page_icon.setVisibility(View.VISIBLE);
        edit_matter_page_txt = view.findViewById(R.id.edit_matter_page_txt);
        edit_matter_page_txt.setVisibility(View.VISIBLE);

        btn_add_advocate = view.findViewById(R.id.btn_add_advocate);
        edit_matter_info = view.findViewById(R.id.edit_matter_info);
//        ll_save_buttons = view.findViewById(R.id.ll_save_buttons_edit);
        ll_header = view.findViewById(R.id.ll_header);
        ll_add_advocate = view.findViewById(R.id.ll_add_advocate);
        tv_high_priority = view.findViewById(R.id.tv_high_priority);
        tv_medium_priority = view.findViewById(R.id.tv_medium_priority);
        tv_low_priority = view.findViewById(R.id.tv_low_priority);
        tv_status_pending = view.findViewById(R.id.tv_status_pending);
        tv_status_active = view.findViewById(R.id.tv_status_active);

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
        addopponentadvocate.setText(R.string.add_opponent_advocate);
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
        tv_start_date.setHint(R.string.start_date);
        tv_start_date.setTextSize(15);
        tv_end_date = view.findViewById(R.id.tv_end_date);
        tv_end_date.setHint(R.string.close_date);
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
        tv_judge.setHint(R.string.judge);
        tv_judge.setTextSize(15);
//        tv_matter_title=view.findViewById(R.id.tv_matter_title);
        tv_matter_title.setText(viewMatterModel1.getTitle());
        tv_matter_num.setText(viewMatterModel1.getCaseNumber());
        tv_case_type.setText(viewMatterModel1.getCasetype());
        tv_matter_description.setText(viewMatterModel1.getDescription());
        tv_dof.setText(viewMatterModel1.getDate_of_filling());
        tv_court.setText(viewMatterModel1.getCourtName());
        tv_judge.setText(viewMatterModel1.getJudges());

        //Checking a matter type is legal or general and filling date.
        if (Constants.MATTER_TYPE.equals("Legal")) {
            if (viewMatterModel1.getDate_of_filling().trim().isEmpty())
                tv_dof.setText("");
            else
                tv_dof.setText(viewMatterModel1.getDate_of_filling());
        } else {
            try {
                String inputDate = viewMatterModel1.getStartdate();
                String inputDate2 = viewMatterModel1.getClosedate();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                if (inputDate.trim().isEmpty()) {
                    tv_start_date.setText("");
                } else {
                    Date date = inputFormat.parse(inputDate);
                    assert date != null;
                    String st_date = outputFormat.format(date);
                    tv_start_date.setText(st_date);
                }
                if (inputDate2.trim().isEmpty())
                    tv_end_date.setText("");
                else {
                    Date date2 = inputFormat.parse(inputDate2);
                    assert date2 != null;
                    String end_date = outputFormat.format(date2);
                    tv_end_date.setText(end_date);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        btn_create = view.findViewById(R.id.btn_create);
        btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        btn_create.setText(R.string.update);
        btn_cancel_save.setText(R.string.cancel);

        if (viewMatterModel1.getStatus().equals("Active")) {
            loadActiveUI();
        } else {
            loadPendingUI();
        }

        if (viewMatterModel1.getPriority().equals("High")) {
            loadHighPriorityUI();
        } else if (viewMatterModel1.getPriority().equals("Medium")) {
            loadMediumPriorityUI();
        } else {
            loadLowPriorityUI();
        }
        if (Constants.MATTER_TYPE.equals("Legal")) {
            ll_start_date.setVisibility(View.GONE);
            ll_end_date.setVisibility(View.GONE);
            ll_dof.setVisibility(View.VISIBLE);
            ll_header.setVisibility(View.VISIBLE);
            btn_create.setText(R.string.update);
            load_existing_advocates();
            datePickerData();
        } else {
            ll_start_date.setVisibility(View.VISIBLE);
            ll_end_date.setVisibility(View.VISIBLE);
            ll_dof.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            btn_create.setText(R.string.next);
            datePickerendData();
            datePickerstartData();
        }

        btn_cancel_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nav_view_matter();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Please check the Title";
                if ((Objects.requireNonNull(tv_matter_title.getText()).toString().trim().isEmpty()) && (Objects.requireNonNull(tv_matter_num.getText()).toString().isEmpty())) {
                    AndroidUtils.showAlert(msg + " , Case Number", getContext());
                } else if (Objects.requireNonNull(tv_matter_title.getText()).toString().trim().isEmpty()) {
                    AndroidUtils.showAlert(msg, getContext());
                    tv_matter_title.requestFocus();
                } else if (Objects.requireNonNull(tv_matter_num.getText()).toString().trim().isEmpty()) {
                    AndroidUtils.showAlert(msg.replace("Title", "Case Number"), getContext());
                    tv_matter_num.requestFocus();
                } else {
                    call_load_edit();
                }
            }
        });

        tv_status_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadActiveUI();
            }
        });
        tv_status_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPendingUI();
            }
        });
        tv_high_priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHighPriorityUI();
            }
        });
        tv_medium_priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMediumPriorityUI();
            }
        });
        tv_low_priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLowPriorityUI();
            }
        });
        btn_add_advocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAdvocateUI();
            }
        });
        // Inflate the layout for this fragment

        return view;
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

    private void loadOpponentsList() {
        ll_add_advocate.removeAllViews();
        for (int i = 0; i < advocates_list.size(); i++) {
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(advocates_list.get(i).getAdvocate_name());
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
                            v = ll_add_advocate.getChildAt(position);
                            ll_add_advocate.removeView(v);
                            AdvocateModel advocateModel = advocates_list.get(position);
                            advocateModel.setAdvocate_name("");
                            advocateModel.setEmail("");
                            advocateModel.setNumber("");
                            advocates_list.set(position, advocateModel);
                            advocates_list.remove(position);
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setTag(i);
            iv_edit_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (view.getTag() instanceof Integer) {
                        position = (Integer) view.getTag();
                        view = ll_add_advocate.getChildAt(position);
                        AdvocateModel advocateModel = advocates_list.get(position);
                        EditAdvocateUI(advocateModel.getAdvocate_name(), advocateModel.getEmail(), advocateModel.getNumber(), position, tv_opponent_name, view);
                    }
                }
            });
            ll_add_advocate.addView(view_opponents);
        }
    }

    private void EditAdvocateUI(String advocate_name, String email, String number, int position, TextView tv_opponent_name, View view_advocate) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.add_opponent_advocate, null);
            TextInputEditText tv_advocate_name = view.findViewById(R.id.tv_advocate_name);
            TextInputEditText tv_advocate_email = view.findViewById(R.id.tv_advocate_email);
            TextInputEditText tv_advocate_phone = view.findViewById(R.id.tv_advocate_phone);
            tv_advocate_phone.setInputType(InputType.TYPE_CLASS_PHONE);
            AppCompatButton btn_cancel_tag = view.findViewById(R.id.btn_cancel_tag);
            AppCompatButton btn_save_tag = view.findViewById(R.id.btn_save_tag);
            final AlertDialog dialog = builder.create();
            tv_advocate_name.setText(advocate_name);
            tv_advocate_email.setText(email);
            tv_advocate_phone.setText(number);
            btn_cancel_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
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
                        dialog.dismiss();
                        loadEditedData(tv_advocate_name.getText().toString(), tv_advocate_email.getText().toString(), tv_advocate_phone.getText().toString(), position, view_advocate, tv_opponent_name);
//                        loadOpponentsList(advocates_list);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadAdvocateUI() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.add_opponent_advocate, null);
            TextInputEditText tv_advocate_name = view.findViewById(R.id.tv_advocate_name);
            tv_advocate_name.setHint(R.string.name);
            tv_advocate_name.setTextSize(15);
            TextInputEditText tv_advocate_email = view.findViewById(R.id.tv_advocate_email);
            tv_advocate_email.setHint(R.string.email);
            tv_advocate_email.setTextSize(15);
            TextInputEditText tv_advocate_phone = view.findViewById(R.id.tv_advocate_phone);
            tv_advocate_phone.setInputType(InputType.TYPE_CLASS_PHONE);
            tv_advocate_phone.setHint(R.string.phone_number);
            tv_advocate_phone.setTextSize(15);
            AppCompatButton btn_cancel_tag = view.findViewById(R.id.btn_cancel_tag);
            AppCompatButton btn_save_tag = view.findViewById(R.id.btn_save_tag);
            final AlertDialog dialog = builder.create();
            btn_cancel_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

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
                        dialog.dismiss();
                        loadOpponentsList();
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
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
                if (httpResult.getRequestType().equals("Edit Matter")) {
                    if (!result.getBoolean("error")) {
                        String message = result.getString("msg");
                        AndroidUtils.showToast(message, getContext());
                        nav_view_matter();
                    } else {
                        AndroidUtils.showToast("Matter Not Updated. Please Check the filled Details", getContext());
                    }
                }
            } catch (Exception e) {
                if (progress_dialog != null && progress_dialog.isShowing())
                    AndroidUtils.dismiss_dialog(progress_dialog);
                e.fillInStackTrace();
            }
        }
    }

    private void loadEditedData(String adv_name, String adv_email, String adv_phone, int position, View view_advocate, TextView tv_opponent_name) {
        AdvocateModel advocateModel = new AdvocateModel();
        advocateModel.setAdvocate_name(adv_name);
        advocateModel.setEmail(adv_email);
        advocateModel.setNumber(adv_phone);
        advocates_list.set(position, advocateModel);
        tv_opponent_name = view_advocate.findViewById(R.id.tv_opponent_name);
        tv_opponent_name.setText(advocateModel.getAdvocate_name());
    }

    private void call_load_edit() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
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
                viewMatterModel1 = existing_clients.get(i);
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
            for (int i = 0; i < advocates_list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                AdvocateModel advocateModel = advocates_list.get(i);
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
            postdata.put("affidavit_filing_date", "");
            postdata.put("affidavit_isfiled", "");
            if (Constants.MATTER_TYPE.equals("Legal")) {
                String inputDate = tv_dof.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy");
                //Rectify the un Parseable date.......
                if (inputDate.trim().isEmpty()) {
                    postdata.put("case_number", tv_matter_num.getText().toString());
                    postdata.put("judges", tv_judge.getText().toString());
                    postdata.put("case_type", tv_case_type.getText().toString());
                    postdata.put("opponent_advocates", advocates);
                    postdata.put("court_name", tv_court.getText().toString());
                    postdata.put("date_of_filling", "");
                } else {
                    Date date = inputFormat.parse(inputDate);

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date_of_filling = outputFormat.format(date);
                    postdata.put("case_number", tv_matter_num.getText().toString());//Updating the matter number
                    postdata.put("judges", tv_judge.getText().toString());
                    postdata.put("case_type", tv_case_type.getText().toString());
                    postdata.put("opponent_advocates", advocates);
                    postdata.put("court_name", tv_court.getText().toString());
                    postdata.put("date_of_filling", date_of_filling);
                }
                //.......
            } else {
                String closeDate = tv_end_date.getText().toString();
                String startDate = tv_start_date.getText().toString();

                if (closeDate.trim().isEmpty()) {
                    postdata.put("closedate", "");
                } else {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = inputFormat.parse(closeDate);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date_of_filling = outputFormat.format(date);
                    postdata.put("closedate", date_of_filling);
                }
                if (startDate.trim().isEmpty()) {
                    postdata.put("startdate", "");
                } else {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = inputFormat.parse(startDate);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date_of_filling = outputFormat.format(date);
                    postdata.put("startdate", date_of_filling);
                }
//                    SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//                    Date date2 = inputFormat2.parse(startdate);
//                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd-MM-yyyy");
//                    String new_start_date = outputFormat2.format(date2);
                postdata.put("matter_number", viewMatterModel1.getMatterNumber());
                postdata.put("matter_type", viewMatterModel1.getMatterType());
            }
            postdata.put("clients", clients);
            postdata.put("description", tv_matter_description.getText().toString());//updating the matter description
            postdata.put("documents", documents);
            postdata.put("group_acls", group_acls);
            postdata.put("members", members);
            postdata.put("priority", CASE_PRIORITY);
            postdata.put("status", STATUS);
            postdata.put("title", tv_matter_title.getText().toString());
//            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "/matter/legal/update/64ad2f54a1db7203e4fd6014", "Edit Document",postdata.toString());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT) + "/update/" + viewMatterModel1.getId(), "Edit Matter", postdata.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                progress_dialog.dismiss();
                AndroidUtils.showToast(e.getMessage(), getContext());
            }
            e.fillInStackTrace();
        }
    }

    private void nav_view_matter() {
        Fragment fragment = new Matter();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.id_framelayout, fragment);
        ft.commit();
    }

    private void load_existing_advocates() {
        ll_add_advocate.removeAllViews();

        //Displaying the name of the Advocates from the advocates array in ViewMatter Model....
        JSONArray advocates = viewMatterModel1.getOpponentAdvocates();
        for (int i = 0; i < advocates.length(); i++) {
            JSONObject client_value = null;
            try {
                client_value = advocates.getJSONObject(i);
                ad_name = client_value.getString("name");
                ad_email = client_value.getString("email");
                ad_phone = client_value.getString("phone");
                Log.d("Advocate_value_name", ad_name + ad_email + ad_phone);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            AdvocateModel advocateModel = new AdvocateModel();
            advocateModel.setAdvocate_name(ad_name);
            advocateModel.setEmail(ad_email);
            advocateModel.setNumber(ad_phone);
            advocates_list.add(advocateModel);
        }
        loadOpponentsList();
    }

    private void datePickerData() {
        final DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
            finalMyCalendar.set(Calendar.YEAR, year);
            finalMyCalendar.set(Calendar.MONTH, month);
            finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        tv_dof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedYear = finalMyCalendar.get(Calendar.YEAR);
                int selectedMonth = finalMyCalendar.get(Calendar.MONTH);
                int selectedDay = finalMyCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        selectedYear, selectedMonth, selectedDay);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void datePickerendData() {
        final DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
            finalMyCalendar.set(Calendar.YEAR, year);
            finalMyCalendar.set(Calendar.MONTH, month);
            finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateendInView();
        };
        tv_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_start_date.getText().toString().isEmpty()) {
                    AndroidUtils.showToast("Please Select the Start Date first", getContext());
                } else {
                    int selectedYear = finalMyCalendar.get(Calendar.YEAR);
                    int selectedMonth = finalMyCalendar.get(Calendar.MONTH);
                    int selectedDay = finalMyCalendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialog = new DatePickerDialog(
                            v.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDateSetListener,
                            selectedYear, selectedMonth, selectedDay);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });
    }

    private void datePickerstartData() {
        final DatePickerDialog.OnDateSetListener mDateSetListener = (view, year, month, dayOfMonth) -> {
            finalMyCalendar.set(Calendar.YEAR, year);
            finalMyCalendar.set(Calendar.MONTH, month);
            finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDatestartInView();
        };

        tv_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedYear = finalMyCalendar.get(Calendar.YEAR);
                int selectedMonth = finalMyCalendar.get(Calendar.MONTH);
                int selectedDay = finalMyCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        selectedYear, selectedMonth, selectedDay);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void updateDateInView() {
        String myFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = sdf.format(finalMyCalendar.getTime());
        tv_dof.setText(formattedDate);
    }

    private void updateDatestartInView() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = sdf.format(finalMyCalendar.getTime());
        tv_start_date.setText(formattedDate);
    }

    private void updateDateendInView() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String formattedDate = sdf.format(finalMyCalendar.getTime());
        tv_end_date.setText(formattedDate);
    }
}