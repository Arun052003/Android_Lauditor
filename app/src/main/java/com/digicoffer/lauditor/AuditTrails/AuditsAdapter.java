package com.digicoffer.lauditor.AuditTrails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class AuditsAdapter extends RecyclerView.Adapter<AuditsAdapter.MyViewHolder> implements Filterable {
    ArrayList<AuditsModel> filtered_list = new ArrayList<>();
    ArrayList<AuditsModel> itemList = new ArrayList<>();
    int item_position = 0;
    public AuditsAdapter(ArrayList<AuditsModel> auditsList) {
        this.filtered_list = auditsList;
        this.itemList = auditsList;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public AuditsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audit_recyclerview, parent, false);
        return new AuditsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditsAdapter.MyViewHolder holder, int position) {
//            item_position = position;
            AuditsModel auditsModel = filtered_list.get(position);
            if(auditsModel.getName().equals("AUTH")){
                holder.tv_category_name.setText("AUTHENTICATION");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("TEAM MEMBER")){
                holder.tv_category_name.setText("TEAM MEMBER");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("RELATIONSHIP")){
                holder.tv_category_name.setText("RELATIONSHIP");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("SHARE")){
                holder.tv_category_name.setText("SHARE");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("DOCUMENT")){
                holder.tv_category_name.setText("DOCUMENT");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("LEGAL MATTER")){
                holder.tv_category_name.setText("LEGAL MATTER");
                loadHiddenData(holder,auditsModel);
            }else  if(auditsModel.getName().equals("GENERAL MATTER")){
                holder.tv_category_name.setText("GENERAL MATTER");
                loadHiddenData(holder,auditsModel);
            }else{
                holder.tv_category_name.setText(auditsModel.getName());
                loadHiddenData(holder,auditsModel);
            }
//            holder.tv_category_name.setText(auditsModel.getName());
//            loadHiddenData(holder,auditsModel);
//            else if(){
//                removeItem(position);
//            }
    }

    private void loadHiddenData(MyViewHolder holder, AuditsModel auditsModel) {
        holder.tv_audit_matter.setText(auditsModel.getMessage());
        holder.tv_timestamp.setText(auditsModel.getTimestamp());
//        notifyItemChanged(item_position);
//        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        filtered_list.remove(position);
        notifyItemRemoved(position);
    }
    public void clearData() {
        filtered_list.clear();
        notifyDataSetChanged();
    }
    public void setData(ArrayList<AuditsModel> newData) {
        filtered_list = newData;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
    @Override
    public int getItemCount() {
        return filtered_list.size();
//        notifyDataSetChanged();
//        notifyItemChanged(item_position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category_name,tv_timestamp,tv_audit_matter;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
            tv_audit_matter = itemView.findViewById(R.id.tv_audit_matter);

        }
    }
}
