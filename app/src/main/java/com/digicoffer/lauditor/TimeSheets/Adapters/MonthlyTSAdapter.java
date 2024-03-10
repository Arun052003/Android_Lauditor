package com.digicoffer.lauditor.TimeSheets.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;

import java.util.ArrayList;

import android.widget.Filter;
import android.widget.Filterable;

import com.digicoffer.lauditor.TimeSheets.Models.Month_Model;
import com.digicoffer.lauditor.common.AndroidUtils;

public class MonthlyTSAdapter extends RecyclerView.Adapter<MonthlyTSAdapter.MyViewHolder> implements Filterable {
    ArrayList<Month_Model> teamList = new ArrayList<>();
    ArrayList<Month_Model> itemsList = new ArrayList<>();
    String status;
    String choosen_month;

    public MonthlyTSAdapter(ArrayList<Month_Model> teamList, String sStatus, String month) {
        this.teamList = teamList;
        this.itemsList = teamList;
        this.status = sStatus;
        this.choosen_month=month;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_timesheets, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    teamList = itemsList;
                } else {
                    ArrayList<Month_Model> filteredList = new ArrayList<>();
                    for (Month_Model row : itemsList) {
//                            if (row.isChecked()){
//                                row.setChecked(false);
//                            }else
//                            {
//                                row.setChecked(true  );
//                            }
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getName()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    teamList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = teamList.size();
                filterResults.values = teamList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                teamList = (ArrayList<Month_Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyTSAdapter.MyViewHolder holder, int position) {
        Month_Model tmModel = teamList.get(position);
        holder.ll_total_hours.setVisibility(View.VISIBLE);
        holder.month_matter_name.setText(tmModel.getName());
        Log.d("Model_name", "" + tmModel.getName());
        holder.tv_matter_hours.setText(tmModel.getBillable_week_tot());
        holder.tv_matter_minutes.setText(tmModel.getNon_billable_week_tot());
        holder.tv_total_hours.setText(tmModel.getTotal() + " Hours");

        holder.billable1.setText(tmModel.getBillable_week_1());
        holder.billable2.setText(tmModel.getBillable_week_2());
        holder.billable3.setText(tmModel.getBillable_week_3());
        holder.billable4.setText(tmModel.getBillable_week_4());
        holder.billable5.setText(tmModel.getBillable_week_5());

        holder.non_billable1.setText(tmModel.getNon_billable_week_1());
        holder.non_billable2.setText(tmModel.getNon_billable_week_2());
        holder.non_billable3.setText(tmModel.getNon_billable_week_3());
        holder.non_billable4.setText(tmModel.getNon_billable_week_4());
        holder.non_billable5.setText(tmModel.getNon_billable_week_5());

        holder.tot_billable.setText(tmModel.getBillable_week_tot());
        holder.tot_non_billable.setText(tmModel.getNon_billable_week_tot());
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_matter_name, tv_matter_hours, tv_matter_minutes, tv_matter_billable, tv_matter_task_name, month_matter_name, total_hours_id;
        LinearLayout ll_total_hours;
        LinearLayoutCompat week_linear;
        TextView timeheets_month, week1, week2, week3, week4, week5, total, month_billable, billable1, billable2, billable3, billable4, billable5, month_non_billable, non_billable1, non_billable2, non_billable3, non_billable4, non_billable5, tot_non_billable, tot_billable, tv_total_hours;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeheets_month = itemView.findViewById(R.id.timeheets_month);
            timeheets_month.setText("Month of "+choosen_month);
            week1 = itemView.findViewById(R.id.week1);
            week1.setText("Week 1");
            week2 = itemView.findViewById(R.id.week2);
            week2.setText("Week 2");
            week3 = itemView.findViewById(R.id.week3);
            week3.setText("Week 3");
            week4 = itemView.findViewById(R.id.week4);
            week4.setText("Week 4");
            week5 = itemView.findViewById(R.id.week5);
            week5.setText("Week 5");
            total = itemView.findViewById(R.id.total);
            total.setText("Total");
            month_billable = itemView.findViewById(R.id.month_billable);
            month_billable.setText("Billable");

            billable1 = itemView.findViewById(R.id.billable1);
            billable2 = itemView.findViewById(R.id.billable2);
            billable3 = itemView.findViewById(R.id.billable3);
            billable4 = itemView.findViewById(R.id.billable4);
            billable5 = itemView.findViewById(R.id.billable5);
            tot_billable = itemView.findViewById(R.id.tot_billable);
            month_non_billable = itemView.findViewById(R.id.month_non_billable);
            month_non_billable.setText("Non Billable");
            non_billable1 = itemView.findViewById(R.id.non_billable1);
            non_billable2 = itemView.findViewById(R.id.non_billable2);
            non_billable3 = itemView.findViewById(R.id.non_billable3);
            non_billable4 = itemView.findViewById(R.id.non_billable4);
            non_billable5 = itemView.findViewById(R.id.non_billable5);
            tot_non_billable = itemView.findViewById(R.id.tot_non_billable);

            tv_matter_name = itemView.findViewById(R.id.tv_matter_name);
            tv_matter_name.setVisibility(View.GONE);
            month_matter_name = itemView.findViewById(R.id.month_matter_name);
            month_matter_name.setTextSize(20);
            tv_matter_hours = itemView.findViewById(R.id.tv_matter_hours);
            tv_matter_minutes = itemView.findViewById(R.id.tv_matter_minutes);
            tv_matter_billable = itemView.findViewById(R.id.tv_matter_billable);
            tv_matter_task_name = itemView.findViewById(R.id.tv_matter_task_name);
            ll_total_hours = itemView.findViewById(R.id.ll_total_hours);
            tv_total_hours = itemView.findViewById(R.id.tv_total_hours);
            total_hours_id = itemView.findViewById(R.id.total_hours_id);
            total_hours_id.setText(R.string.total_hours);

            week_linear = itemView.findViewById(R.id.week_linear);
            week_linear.setPadding(30, 10, 10, 10);
            //Green count Color
            tv_total_hours.setTextColor(Color.parseColor("#00B3A7"));
            tv_total_hours.setTextSize(25);
            //Orange Color
            tv_matter_hours.setTextColor(Color.parseColor("#F15F12"));
            tv_matter_minutes.setTextColor(Color.parseColor("#F15F12"));
            //Blue Text Color
            tv_matter_task_name.setTextColor(Color.parseColor("#004D87"));
            tv_matter_task_name.setText("Non Billable");
            tv_matter_billable.setTextColor(Color.parseColor("#004D87"));
            tv_matter_billable.setText("Billable");
            total_hours_id.setTextColor(Color.parseColor("#004D87"));
        }
    }
}
