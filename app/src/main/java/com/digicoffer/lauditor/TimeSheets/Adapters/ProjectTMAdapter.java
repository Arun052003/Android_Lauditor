package com.digicoffer.lauditor.TimeSheets.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectTMModel;

import java.util.ArrayList;

public class ProjectTMAdapter extends RecyclerView.Adapter<ProjectTMAdapter.MyviewHolder> {
    ArrayList<ProjectTMModel> projectTmList = new ArrayList<>();

    public ProjectTMAdapter(ArrayList<ProjectTMModel> projectTmList) {
        this.projectTmList = projectTmList;
    }

    @NonNull
    @Override
    public ProjectTMAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekly_timesheets, parent, false);
        return new ProjectTMAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectTMAdapter.MyviewHolder holder, int position) {
        ProjectTMModel projectTMModel = projectTmList.get(position);
        holder.ll_total_hours.setVisibility(View.VISIBLE);
        holder.tv_matter_name.setText(projectTMModel.getName());
        holder.tv_matter_billable.setText("Billable");
        holder.tv_matter_task_name.setText("Non Billable");
        holder.tv_matter_hours.setText(projectTMModel.getBillableHours() + " Hour");
        holder.tv_matter_minutes.setText(projectTMModel.getNonBillablehours() + " Hour");
        holder.tv_total_hours.setText(projectTMModel.getTotal() + " Hours");
    }

    @Override
    public int getItemCount() {
        return projectTmList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        private TextView tv_matter_name, tv_matter_hours, tv_matter_minutes, tv_matter_billable, tv_matter_task_name, tv_total_hours, total_hours_id;
        LinearLayout ll_total_hours;
        View week_line;

        public MyviewHolder(@NonNull View itemView) {

            super(itemView);
            tv_matter_billable = itemView.findViewById(R.id.tv_matter_billable);
            tv_matter_hours = itemView.findViewById(R.id.tv_matter_hours);
            tv_matter_minutes = itemView.findViewById(R.id.tv_matter_minutes);
            tv_matter_name = itemView.findViewById(R.id.tv_matter_name);
            tv_matter_task_name = itemView.findViewById(R.id.tv_matter_task_name);
            tv_total_hours = itemView.findViewById(R.id.tv_total_hours);
            ll_total_hours = itemView.findViewById(R.id.ll_total_hours);
            total_hours_id = itemView.findViewById(R.id.total_hours_id);
            tv_matter_name.setTextColor(Color.BLACK);
            total_hours_id.setText(R.string.total_hours);

            week_line = itemView.findViewById(R.id.week_line);
            week_line.setVisibility(View.VISIBLE);
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
