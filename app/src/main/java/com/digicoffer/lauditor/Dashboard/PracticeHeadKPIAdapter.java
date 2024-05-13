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

public class PracticeHeadKPIAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;

    public PracticeHeadKPIAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            return new TBHholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_tbh,parent,false)
            );
        }else  if (viewType==1){
            return new NBHholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_nbh,parent,false)
            );
        }
        else  if (viewType==2){
            return new ARholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_approximate_revenue,parent,false)
            );
        }
        else  if (viewType==3){
            return new ABRholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_average_billing_rate,parent,false)
            );
        }
        else  if (viewType==4){
            return new TimeSheetholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_timesheet_card,parent,false)
            );
        }
        else  if (viewType==5){
            return new Matterholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_matters,parent,false)
            );
        }
        else  if (viewType==6){
            return new USholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_used_storage_limit,parent,false)
            );
        }
        else  {
            return new CRholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_client_relationships,parent,false)
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
                ((ARholder) holder).ARdata(meetingModel);
            } else if (getItemViewType(position) == 3) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((ABRholder) holder).ABRdata(meetingModel);
            } else if (getItemViewType(position) == 4) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((TimeSheetholder) holder).TimeSheetdata(meetingModel);
            } else if (getItemViewType(position) == 5) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((Matterholder) holder).Mattersdata(meetingModel);
            } else if (getItemViewType(position) == 6) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((USholder) holder).USdata(meetingModel);
            } else {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((CRholder) holder).CRdata(meetingModel);
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
        private  TextView tv_billing_hours,tv_bh_percentage;
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
    static class ARholder extends RecyclerView.ViewHolder{
        private TextView tv_revenue;
        public ARholder(@NonNull View itemView) {

            super(itemView);
            tv_revenue = itemView.findViewById(R.id.tv_revenue);
        }

        void ARdata(PracticeModel model){
            tv_revenue.setText(model.getHours());
        }
    }
    static class ABRholder extends RecyclerView.ViewHolder{
        private TextView tv_billing_rate;
        public ABRholder(@NonNull View itemView) {
            super(itemView);
            tv_billing_rate = itemView.findViewById(R.id.tv_billing_rate);
        }

        void ABRdata(PracticeModel practiceModel){
            tv_billing_rate.setText(practiceModel.getHours());
        }
    }
    static class TimeSheetholder extends RecyclerView.ViewHolder{
        private TextView tv_ttm,tv_nsm,tv_sm;
        public TimeSheetholder(@NonNull View itemView) {
            super(itemView);
            tv_ttm = itemView.findViewById(R.id.tv_total_team_members_count);
            tv_nsm = itemView.findViewById(R.id.tv_not_submitted_count);
            tv_sm = itemView.findViewById(R.id.tv_submitted_members_count);
        }
        void TimeSheetdata(PracticeModel practiceModel){

            tv_ttm.setText(practiceModel.getHours());
            tv_nsm.setText(practiceModel.getPercentage());
            tv_sm.setText(practiceModel.getSubmitted_members());
        }
    }
    static class Matterholder extends RecyclerView.ViewHolder{
        TextView tv_closed_members_count,tv_active_members_count;
        public Matterholder(@NonNull View itemView) {
            super(itemView);
            tv_closed_members_count = itemView.findViewById(R.id.tv_closed_members_count);
            tv_active_members_count = itemView.findViewById(R.id.tv_active_members_count);

        }

        void Mattersdata(PracticeModel practiceModel){
            tv_closed_members_count.setText(practiceModel.getHours());
            tv_active_members_count.setText(practiceModel.getPercentage());
        }
    }
    static class USholder extends RecyclerView.ViewHolder{
            private TextView tv_storage;
        public USholder(@NonNull View itemView) {
            super(itemView);
            tv_storage = itemView.findViewById(R.id.tv_storage);
        }

        void USdata(PracticeModel practiceModel){
            tv_storage.setText(practiceModel.getHours());
        }
    }
    static class CRholder extends RecyclerView.ViewHolder{
        private TextView tv_accepted_count,tv_pending_count,tv_deleted_count;
        public CRholder(@NonNull View itemView) {
            super(itemView);
            tv_accepted_count = itemView.findViewById(R.id.tv_accepted_count);
            tv_pending_count = itemView.findViewById(R.id.tv_pending_count);
            tv_deleted_count = itemView.findViewById(R.id.tv_delete_count);
        }

        void CRdata(PracticeModel practiceModel){
            tv_accepted_count.setText(practiceModel.getHours());
            tv_deleted_count.setText(practiceModel.getPercentage());
            tv_pending_count.setText(practiceModel.getSubmitted_members());
        }
    }

}
