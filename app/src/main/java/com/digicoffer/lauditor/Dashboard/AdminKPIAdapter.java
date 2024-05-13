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

public class AdminKPIAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> items;

    public AdminKPIAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            return new NOGHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.admin_no_of_groups,parent,false)
            );
        }else  if (viewType==1){
            return new TMholder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.admin_total_team_members,parent,false)
            );
        }
        else  if (viewType==2){
            return new NewHiresHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_kpi_new_hire,parent,false)
            );
        }
        else  if (viewType==3){
            return new Data_storageHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.super_user_kpi_data_storage,parent,false)
            );
        }
        else {
            return new ProductSubcriptionHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.admin_product_subscription,parent,false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((NOGHolder) holder).setNOGData(meetingModel);
            } else if (getItemViewType(position) == 1) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((TMholder) holder).setTMdata(meetingModel);
            } else if (getItemViewType(position) == 2) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((NewHiresHolder) holder).setNewHiresdata(meetingModel);
            } else if (getItemViewType(position) == 3) {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((Data_storageHolder) holder).setData_storagedata(meetingModel);
            }  else {
                PracticeModel meetingModel = (PracticeModel) items.get(position).getObject();
                ((ProductSubcriptionHolder) holder).setProductSubcriptiondata(meetingModel);
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
    static class NOGHolder extends RecyclerView.ViewHolder{
        private TextView tv_nog_count;
        public NOGHolder(@NonNull View itemView) {
            super(itemView);
            tv_nog_count = itemView.findViewById(R.id.tv_nog_count);

        }

        void setNOGData(PracticeModel practiceModel){
            tv_nog_count.setText(practiceModel.getHours());


        }

    }

    static class TMholder extends RecyclerView.ViewHolder{
        private  TextView tv_admin_tm_count;
        public TMholder(@NonNull View itemView) {
            super(itemView);
            tv_admin_tm_count = itemView.findViewById(R.id.tv_admin_tm_count);

        }

        void setTMdata(PracticeModel practiceModel){
            tv_admin_tm_count.setText(practiceModel.getHours());
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

        void setData_storagedata(PracticeModel practiceModel){
            tv_data_storage.setText(practiceModel.getHours());
            tv_balance_storage.setText(practiceModel.getPercentage());

        }
    }
    static class ProductSubcriptionHolder extends RecyclerView.ViewHolder{
        private TextView tv_ps_year;
        public ProductSubcriptionHolder(@NonNull View itemView) {
            super(itemView);
            tv_ps_year = itemView.findViewById(R.id.tv_ps_year);
        }
        void setProductSubcriptiondata(PracticeModel practiceModel){
            tv_ps_year.setText(practiceModel.getHours());
        }
    }
}
