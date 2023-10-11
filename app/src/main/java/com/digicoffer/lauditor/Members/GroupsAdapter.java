package com.digicoffer.lauditor.Members;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.Adapters.ViewGroupsAdpater;
import com.digicoffer.lauditor.Groups.GroupModels.GroupModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.Groups.Groups;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.minidns.record.A;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder>implements Filterable {
    ArrayList<ViewGroupModel> groupsList = new ArrayList<>();
    ArrayList<ViewGroupModel> list_item = new ArrayList<>();

    public GroupsAdapter(ArrayList<ViewGroupModel> groupsList) {
        this.groupsList = groupsList;
        this.list_item = groupsList;
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
        return new GroupsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.ViewHolder holder, int position) {
                ViewGroupModel groupModel = groupsList.get(position);
                groupsList = list_item;
        holder.cb_team_members.setChecked(groupsList.get(position).isChecked());
        holder.cb_team_members.setTag(position);

//            holder.cb_team_members.isChecked() = itemsArrayList.get(position).isChecked();
        holder.cb_team_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.cb_team_members.getTag();
                if (groupsList.get(pos).isChecked()) {
                    groupsList.get(pos).setChecked(false);
//                        itemsArrayList.add(itemsArrayList.get(pos));
                } else {
                    groupsList.get(pos).setChecked(true);
//                        itemsArrayList.remove(itemsArrayList.get(pos));
                }

            }
        });
//            holder.cb_team_members.setChecked(true);
        holder.tv_tm_name.setText(groupModel.getName());

    }
    public void selectOrDeselectAll(boolean isChecked)
    {
        for(int i = 0; i<list_item.size(); i++)
        {
//            if (list_item.get(i).isIsenabled())
//            if (list_item.get(i).isIsenabled()) {
            list_item.get(i).setChecked(isChecked);
//            }else {
//                list_item.get(i).setChecked(false);
//            }
        }
        notifyDataSetChanged();
    }
    public ArrayList<ViewGroupModel> getList_item() {
        return groupsList;
    }
    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    groupsList = list_item;
                } else {
                    ArrayList<ViewGroupModel> filteredList = new ArrayList<>();
                    for (ViewGroupModel row : list_item) {
//                            if (row.isChecked()){
//                                row.setChecked(false);
//                            }else
//                            {
//                                row.setChecked(true  );
//                            }
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getName()).toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }
                    groupsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = groupsList.size();
                filterResults.values = groupsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupsList = (ArrayList<ViewGroupModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_team_members;
        TextView tv_tm_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_team_members = itemView.findViewById(R.id.chk_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
        }
    }
}
