package com.digicoffer.lauditor.TimeSheets.Adapters;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Dashboard.Dashboard;
import com.digicoffer.lauditor.Matter.matter_edit;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.TaskModel;
import com.digicoffer.lauditor.TimeSheets.NonSubmittedTimesheets;
import com.digicoffer.lauditor.TimeSheets.TimeSheets;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.pgpainless.key.selection.key.util.And;

import java.util.ArrayList;

public class WeeklyTSAdapter extends RecyclerView.Adapter<WeeklyTSAdapter.MyViewHolder> {
    ArrayList<TaskModel> eventsList = new ArrayList<>();
    private boolean issubmitted;
    TimeSheets timeSheets=new TimeSheets();
    Context context_timesheets;

    public WeeklyTSAdapter(ArrayList<TaskModel> eventsModels, boolean issubmitted, Context context_timesheets) {
        this.eventsList = eventsModels;
        this.issubmitted=issubmitted;
        this.context_timesheets=context_timesheets;
    }

    @NonNull
    @Override
    public WeeklyTSAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekly_timesheets, parent, false);
        return new WeeklyTSAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyTSAdapter.MyViewHolder holder, int position) {
        TaskModel eventsModel = eventsList.get(position);
            if (!(eventsModel.getTaskid() == null)) {
                holder.tv_matter_task_name.setText(eventsModel.getTask_name());
                holder.tv_matter_name.setText(eventsModel.getTask_matter_name());
                holder.tv_matter_hours.setText(eventsModel.getHours() + " Hour");
                holder.tv_matter_minutes.setText(eventsModel.getMinutes() + " Hour");
                holder.tv_matter_billable.setText(eventsModel.getTask_billing() + " Hours");
            }
            if(holder.tv_matter_name.getText().toString().equals(""))
            {
                holder.cardView.setVisibility(View.GONE);
                holder.edit_del_timesheets.setVisibility(View.GONE);
            }
            else {
                if(!issubmitted)
                    holder.edit_del_timesheets.setVisibility(View.VISIBLE);
                else
                    holder.edit_del_timesheets.setVisibility(View.GONE);

                holder.cardView.setVisibility(View.VISIBLE);
            }
            holder.edit_timesheets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AndroidUtils.showToast("Edit_timesheet", v.getContext());
                    Log.d("Edit_timesheets","Edit_timesheets");
//                    Intent intent=new Intent(context_timesheets, NonSubmittedTimesheets.class);
                    }
            });
            holder.delete_timesheets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AndroidUtils.showToast("Delete_timesheet", v.getContext());
                    String task_id=eventsModel.getTaskid();
                    String task_matter_id=eventsModel.getTask_matter_id();
                    Log.d("T1",task_id);
                    //..
//                    try {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        LayoutInflater inflater = getLayoutInflater();
//                        final View dialogLayout = inflater.inflate(R.layout.alert_dialog_delete, null);
//                        final TextView delete_event_msg = dialogLayout.findViewById(R.id.delete_event_msg);
//                        delete_event_msg.setText("Are you Sure do you want to delete " + event_name + " ?");
//                        delete_event_msg.setTextSize(20);
//                        delete_event_msg.setTextColor(Color.BLACK);
//                        delete_event_msg.setTypeface(null, Typeface.NORMAL);
//                        final Button delete = dialogLayout.findViewById(R.id.delete_event);
//                        final Button btn_close_event = dialogLayout.findViewById(R.id.btn_close_event);
//                        btn_close_event.setTextColor(Color.RED);
//                        btn_close_event.setText(R.string.cancel);
//
//                        final AlertDialog dialog = builder.create();
//                        ad_dialog = dialog;
//                        btn_close_event.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ad_dialog.dismiss();
//                            }
//                        });
//                        delete.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                recurring_edit_choice = "this";
//                                callDeleteEventwebservice(event_id, recurring_edit_choice);
//                            }
//                        });
//
//                        dialog.setView(dialogLayout);
//                        dialog.show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    //..
                }
            });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_matter_name, tv_matter_hours, tv_matter_minutes, tv_matter_billable, tv_matter_task_name, tv_total_hours, total_hours_id;
        LinearLayout ll_total_hours,edit_del_timesheets;
        CardView cardView;
        ImageView edit_timesheets,delete_timesheets;
        LinearLayoutCompat week_linear;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            week_linear = itemView.findViewById(R.id.week_linear);
            week_linear.setPadding(30, 10, 10, 10);
            edit_timesheets=itemView.findViewById(R.id.edit_timesheets);
            delete_timesheets=itemView.findViewById(R.id.delete_timesheets);
            edit_del_timesheets = itemView.findViewById(R.id.edit_del_timesheets);
            tv_matter_billable = itemView.findViewById(R.id.tv_matter_billable);
            tv_matter_hours = itemView.findViewById(R.id.tv_matter_hours);
            tv_matter_minutes = itemView.findViewById(R.id.tv_matter_minutes);
            tv_matter_name = itemView.findViewById(R.id.tv_matter_name);
            tv_matter_task_name = itemView.findViewById(R.id.tv_matter_task_name);
            tv_total_hours = itemView.findViewById(R.id.tv_total_hours);
            ll_total_hours = itemView.findViewById(R.id.ll_total_hours);
            ll_total_hours.setVisibility(View.GONE);
            total_hours_id = itemView.findViewById(R.id.total_hours_id);
            tv_matter_name.setTextColor(Color.BLACK);
            total_hours_id.setText(R.string.total_hours);

            cardView = itemView.findViewById(R.id.cardview_week);
//            CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.setMargins(10, 10, 0, 10);
//            cardView.setLayoutParams(params);
            //Green count Color
            tv_total_hours.setTextColor(Color.parseColor("#00B3A7"));
            tv_total_hours.setTextSize(25);
            //Orange Color
            tv_matter_hours.setTextColor(Color.parseColor("#F15F12"));
            tv_matter_minutes.setTextColor(Color.parseColor("#F15F12"));
            //Blue Text Color
            tv_matter_task_name.setTextColor(Color.parseColor("#004D87"));
            tv_matter_billable.setTextColor(Color.parseColor("#004D87"));
            total_hours_id.setTextColor(Color.parseColor("#004D87"));

        }
    }
}
