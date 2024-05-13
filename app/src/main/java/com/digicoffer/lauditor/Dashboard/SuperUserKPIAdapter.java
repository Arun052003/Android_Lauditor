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

public class SuperUserKPIAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;

    public SuperUserKPIAdapter(ArrayList<Item> items) {
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
                            inflate(R.layout.practice_head_approximate_revenue,parent,false)
            );
        }
        else  if (viewType==3){
            return new PracticeHeadKPIAdapter.ABRholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.practice_head_average_billing_rate,parent,false)
            );
        }
        else  if (viewType==4){
            return new PracticeHeadKPIAdapter.TimeSheetholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_matter_kpi,parent,false)
            );
        }
        else  if (viewType==5){
            return new PracticeHeadKPIAdapter.Matterholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_kpi_new_client,parent,false)
            );
        }
        else  if (viewType==6){
            return new PracticeHeadKPIAdapter.USholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_kpi_new_hire,parent,false)
            );
        }
        else  {
            return new PracticeHeadKPIAdapter.CRholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_kpi_data_storage,parent,false)
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
                ((Matterholder) holder).Mattersdata(meetingModel);
            } else if (getItemViewType(position) == 5) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((NewClientsHolder) holder).setNewClientsdata(meetingModel);
            } else if (getItemViewType(position) == 6) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((NewHiresHolder) holder).setNewHiresdata(meetingModel);
            } else {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((Data_storageHolder) holder).setData_storageHolder(meetingModel);
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

    static class Matterholder extends RecyclerView.ViewHolder{
        TextView tv_total_members_count,tv_active_matters_count;
        public Matterholder(@NonNull View itemView) {
            super(itemView);
            tv_total_members_count = itemView.findViewById(R.id.tv_total_matters_count);
            tv_active_matters_count = itemView.findViewById(R.id.tv_active_matters_count);

        }

        void Mattersdata(PracticeModel practiceModel){
            tv_total_members_count.setText(practiceModel.getHours());
            tv_active_matters_count.setText(practiceModel.getPercentage());
        }
    }
    static class NewClientsHolder extends RecyclerView.ViewHolder{
        private TextView tv_legal_count,tv_civil_count;
        public NewClientsHolder(@NonNull View itemView) {
            super(itemView);
            tv_legal_count = itemView.findViewById(R.id.tv_newclients_legal_count);
            tv_civil_count = itemView.findViewById(R.id.tv_newclients_civil_count);
        }

        void setNewClientsdata(PracticeModel practiceModel){
            tv_legal_count.setText(practiceModel.getHours());
            tv_civil_count.setText(practiceModel.getPercentage());
        }
    }
    static class NewHiresHolder extends RecyclerView.ViewHolder{
        private TextView tv_corporate_count,tv_criminal_count;
        public NewHiresHolder(@NonNull View itemView) {
            super(itemView);
            tv_corporate_count = itemView.findViewById(R.id.tv_newhire_corporate_count);
            tv_criminal_count = itemView.findViewById(R.id.tv_newhire_criminal_count);

        }

        void setNewHiresdata(PracticeModel practiceModel){
            tv_corporate_count.setText(practiceModel.getHours());
            tv_criminal_count.setText(practiceModel.getPercentage());

        }
    }
    static class Data_storageHolder extends RecyclerView.ViewHolder{
        private TextView tv_data_storage,tv_balance_storage;
        public Data_storageHolder(@NonNull View itemView) {
            super(itemView);
            tv_data_storage = itemView.findViewById(R.id.tv_sp_data_storage);
            tv_balance_storage = itemView.findViewById(R.id.tv_sp_balance_storage);

        }

        void setData_storageHolder(PracticeModel practiceModel){
            tv_data_storage.setText(practiceModel.getHours());
            tv_balance_storage.setText(practiceModel.getPercentage());

        }
    }
}
