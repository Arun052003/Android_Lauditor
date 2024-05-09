package com.digicoffer.lauditor.Matter;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.digicoffer.lauditor.Matter.Models.HistoryModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class timeline extends Fragment {
    ArrayList<HistoryModel> historyList = new ArrayList<>();
    ViewMatter viewMatter = new ViewMatter();
    String header_name = "";
    Matter matter;

    public timeline(ArrayList<HistoryModel> historyList1, ViewMatter viewMatter1, String header_name1, Matter matter1) {
        historyList = historyList1;
        viewMatter = viewMatter1;
        header_name = header_name1;
        matter = matter1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_matter_details, container, false);
        ConstraintLayout timeline_c = view.findViewById(R.id.timeline_c);
        timeline_c.setVisibility(View.VISIBLE);
        //..
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ImageView close_details = view.findViewById(R.id.close_details);
        LinearLayout ll_timeline = view.findViewById(R.id.ll_timeLine);
        TextView tv_header_name = view.findViewById(R.id.header_name);
        tv_header_name.setText(header_name);
        final AlertDialog dialog = builder.create();
        ll_timeline.removeAllViews();
//        if (historyList.size() <= 1) {
//            timeline_c.setVisibility(View.GONE);
//        } else timeline_c.setVisibility(View.VISIBLE);
        for (int i = 0; i < historyList.size(); i++) {
            try {
                View view_timeLine = LayoutInflater.from(getContext()).inflate(R.layout.matter_timeline, null);
                TextView tv_timeline_title = view_timeLine.findViewById(R.id.tv_timeline_title);
                tv_timeline_title.setText(R.string.matter_timeline);
                LinearLayout notes_layout = view_timeLine.findViewById(R.id.notes_layout);
                notes_layout.setVisibility(View.VISIBLE);

                TextView tv_timeline_date = view_timeLine.findViewById(R.id.tv_timeline_date);
                tv_timeline_date.setText(R.string.date);
                tv_timeline_date.setTextColor(Color.BLACK);

                LinearLayout ll_empty_notes = view_timeLine.findViewById(R.id.ll_empty_notes);
                LinearLayout ll_edit_notes = view_timeLine.findViewById(R.id.ll_edit_notes);
                TextInputEditText tv_edit_notes = view_timeLine.findViewById(R.id.tv_edit_notes);
                TextView word_count_text_view = view_timeLine.findViewById(R.id.word_count_text_view);
                AppCompatButton btn_cancel_save = view_timeLine.findViewById(R.id.btn_cancel_save);
                AppCompatButton btn_create = view_timeLine.findViewById(R.id.btn_create);
                TextInputEditText tv_view_notes = view_timeLine.findViewById(R.id.tv_view_notes);
                LinearLayout linear_notes = view_timeLine.findViewById(R.id.linear_notes);
                ImageView iv_view_timeLine = view_timeLine.findViewById(R.id.iv_view);
                TextView normal_notes = view_timeLine.findViewById(R.id.normal_notes);
//                int maxLength = 3;
//                tv_edit_notes.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

                //Invisible the the Notes Radio button for the Date of filling..
                if (i == 0) {
                    notes_layout.setVisibility(View.GONE);
                } else {
                    notes_layout.setVisibility(View.VISIBLE);
                }
//                normal_notes.setPaintFlags(normal_notes.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ImageView iv_edit_notes = view_timeLine.findViewById(R.id.iv_notes);
                boolean allday = historyList.get(i).isAllday();
                if (allday) {
                    iv_edit_notes.setVisibility(View.GONE);
                } else {
                    iv_edit_notes.setVisibility(View.VISIBLE);
                }
                //Checking the notes value and adding Dots to it.
                if (!historyList.get(i).getNotes().isEmpty()) {
                    String notes_text = historyList.get(i).getNotes() + "...";
                    normal_notes.setText(notes_text);
                } else {
                    normal_notes.setText("...");
                }
                ll_empty_notes.setVisibility(View.VISIBLE);
                normal_notes.setVisibility(View.VISIBLE);

                //Adding the underline to notes text...
                SpannableString spannableString = new SpannableString(normal_notes.getText().toString());
                spannableString.setSpan(new UnderlineSpan(), 0, normal_notes.length(), 0);
                normal_notes.setText(spannableString);

                iv_edit_notes.setTag(i);
                iv_edit_notes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int position = 0;
                            if (v.getTag() instanceof Integer) {
                                position = (Integer) v.getTag();
                                v = ll_timeline.getChildAt(position);
                                if (ll_edit_notes.getVisibility() == View.VISIBLE) {
                                    ll_empty_notes.setVisibility(View.VISIBLE);
                                    ll_edit_notes.setVisibility(View.GONE);
                                    tv_view_notes.setVisibility(View.GONE);
                                } else {
                                    ll_empty_notes.setVisibility(View.GONE);
                                    ll_edit_notes.setVisibility(View.VISIBLE);
                                    tv_view_notes.setVisibility(View.GONE);

                                }

                                HistoryModel historyModel = historyList.get(position);
                                tv_edit_notes.setText(historyModel.getNotes());
                                btn_cancel_save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ll_empty_notes.setVisibility(View.VISIBLE);
                                        ll_edit_notes.setVisibility(View.GONE);
                                        tv_view_notes.setVisibility(View.GONE);
                                    }
                                });
                                btn_create.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (tv_edit_notes.getText().toString().equals("")) {
                                            tv_edit_notes.setError("Please fill the notes");
                                            tv_edit_notes.requestFocus();
                                        } else {
                                            dialog.dismiss();
                                            viewMatter.callEditNotesWebservice(historyModel.getId(), tv_edit_notes.getText().toString().trim());
                                        }
                                    }
                                });

                            }
                        } catch (Exception e) {
                            AndroidUtils.showAlert(e.getMessage(), getContext());
                        }
                    }
                });
                iv_view_timeLine.setTag(i);
                iv_view_timeLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            int position = 0;
                            if (v.getTag() instanceof Integer) {
                                position = (Integer) v.getTag();
                                v = ll_timeline.getChildAt(position);

                                if (tv_view_notes.getVisibility() == View.VISIBLE) {
                                    linear_notes.setVisibility(View.VISIBLE);
                                    ll_empty_notes.setVisibility(View.VISIBLE);
                                    ll_edit_notes.setVisibility(View.GONE);
                                    tv_view_notes.setVisibility(View.GONE);
                                } else {
                                    ll_empty_notes.setVisibility(View.GONE);
                                    ll_edit_notes.setVisibility(View.GONE);
                                    tv_view_notes.setVisibility(View.VISIBLE);

                                }

                                HistoryModel historyModel = historyList.get(position);
                                tv_view_notes.setText(historyModel.getNotes());
                            }
                        } catch (Exception e) {
                            AndroidUtils.showAlert(e.getMessage(), getContext());
                        }

                    }
                });
                tv_timeline_title.setText(historyList.get(i).getTitle());
                tv_timeline_date.setText(historyList.get(i).getFrom_ts());
                ll_timeline.addView(view_timeLine);
            } catch (Exception ex) {
                AndroidUtils.showAlert(ex.getMessage(), getContext());
            }
        }
        close_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matter.loadViewUI();
//                   matter.loadViewUI();
            }
        });
        //..
        return view;
    }
}