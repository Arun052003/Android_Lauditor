package com.digicoffer.lauditor.Dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Dashboard.DahboardModels.Item;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.PracticeModel;
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class TeamMemberKPIAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;

    public TeamMemberKPIAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            return new PracticeHeadKPIAdapter.TBHholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_tbh,parent,false)
            );
        }else  if (viewType==1){
            return new PracticeHeadKPIAdapter.NBHholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_nbh,parent,false)
            );
        }
        else  if (viewType==2){
            return new PracticeHeadKPIAdapter.ARholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.tm_submitted_timesheet,parent,false)
            );
        }
        else  if (viewType==3){
            return new PracticeHeadKPIAdapter.ABRholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.tm_pending_time_sheet,parent,false)
            );
        }
        else {
            return new PracticeHeadKPIAdapter.TimeSheetholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.tm_my_active_matters,parent,false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {


            if (getItemViewType(position) == 0) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((TBHholder) holder).TBHdata(meetingModel);
            } else if (getItemViewType(position) == 1) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((NBHholder) holder).NBHdata(meetingModel);
            } else if (getItemViewType(position) == 2) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((SubmittedTimeSheetholder) holder).SubmittedTimeSheetdata(meetingModel);
            } else if (getItemViewType(position) == 3) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((PendingTimeSheetholder) holder).PTSdata(meetingModel);
            } else  {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((ActiveMatterHolder) holder).Mattersdata(meetingModel);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }
    static class TBHholder extends RecyclerView.ViewHolder{
        private TextView tv_billing_hours,tv_bh_percentage;
        public TBHholder(@NonNull View itemView) {
            super(itemView);
            tv_billing_hours = itemView.findViewById(R.id.tv_bhours);
            tv_bh_percentage = itemView.findViewById(R.id.tv_bh_percentage);
        }

        void TBHdata(PracticeModel practiceModel){
            tv_billing_hours.setText(practiceModel.getHours());
            tv_bh_percentage.setText(practiceModel.getPercentage());

        }

    }

    static class NBHholder extends RecyclerView.ViewHolder{
        private  TextView tv_non_billing_hours,tv_nbh_percentage;
        public NBHholder(@NonNull View itemView) {
            super(itemView);
            tv_non_billing_hours = itemView.findViewById(R.id.tv_nb_hours);
            tv_nbh_percentage = itemView.findViewById(R.id.tv_nbh_percentage);
        }

        void NBHdata(PracticeModel practiceModel){
            tv_non_billing_hours.setText(practiceModel.getHours());
            tv_non_billing_hours.setText(practiceModel.getPercentage());
        }
    }

    static class SubmittedTimeSheetholder extends RecyclerView.ViewHolder{
        private TextView tv_from_date,tv_to_date;
        public SubmittedTimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_from_date = itemView.findViewById(R.id.tv_from_date);
            tv_to_date = itemView.findViewById(R.id.tv_to_date);
        }
        void SubmittedTimeSheetdata(PracticeModel practiceModel){

            tv_from_date.setText(practiceModel.getHours());
            tv_to_date.setText(practiceModel.getPercentage());

        }
    }
    static class PendingTimeSheetholder extends RecyclerView.ViewHolder{
                private TextView tv_from_date,tv_to_date;
        public PendingTimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_from_date = itemView.findViewById(R.id.tv_from_date);
            tv_to_date = itemView.findViewById(R.id.tv_to_date);
        }

        void PTSdata(PracticeModel practiceModel){
            tv_from_date.setText(practiceModel.getHours());
            tv_to_date.setText(practiceModel.getPercentage());
        }
    }
    static class ActiveMatterHolder extends RecyclerView.ViewHolder{
      private   TextView tv_general_count,tv_legal_count;
        public ActiveMatterHolder(@NonNull View itemView) {
            super(itemView);
            tv_general_count = itemView.findViewById(R.id.tv_general_count);
            tv_legal_count = itemView.findViewById(R.id.tv_legal_count);

        }

        void Mattersdata(PracticeModel practiceModel){
            tv_general_count.setText(practiceModel.getHours());
            tv_legal_count.setText(practiceModel.getPercentage());
        }
    }


}
