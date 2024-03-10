package com.digicoffer.lauditor.TimeSheets.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.EventsModel;
import com.digicoffer.lauditor.TimeSheets.Models.TaskModel;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class WeeklyTSAdapter extends RecyclerView.Adapter<WeeklyTSAdapter.MyViewHolder> {
    ArrayList<TaskModel> eventsList = new ArrayList<>();

    public WeeklyTSAdapter(ArrayList<TaskModel> eventsModels) {
        this.eventsList = eventsModels;
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
            holder.tv_matter_billable.setText(eventsModel.getTask_billing());
        }
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_matter_name, tv_matter_hours, tv_matter_minutes, tv_matter_billable, tv_matter_task_name, tv_total_hours, total_hours_id;
        LinearLayout ll_total_hours;
        CardView cardView;
        LinearLayoutCompat week_linear;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            week_linear = itemView.findViewById(R.id.week_linear);
            week_linear.setPadding(30, 10, 10, 10);
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
            CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 0, 10);
            cardView.setLayoutParams(params);
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
