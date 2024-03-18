package com.digicoffer.lauditor.Groups.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.GroupModel;
import com.digicoffer.lauditor.Groups.Groups;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.ItemClickListener;
import com.digicoffer.lauditor.common.AndroidUtils;

import java.util.ArrayList;

public class GroupAdapters extends RecyclerView.Adapter<GroupAdapters.ViewHolder> implements Filterable {

    ArrayList<GroupModel> itemsArrayList;
    ArrayList<GroupModel> list_item;
    ItemClickListener itemClickListener;
    public boolean select_checked;
    int add_tm = 1;
    String mTag = "";
    int add_group_head = 2;
    Context context;
    int selectedPosition = -1;
    Groups group;


    public GroupAdapters(ArrayList<GroupModel> itemsArrayList, String Tag, ItemClickListener itemClickListener, Groups context) {
        this.itemsArrayList = itemsArrayList;
        this.list_item = itemsArrayList;
        this.group = context;
        this.mTag = Tag;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mTag == "TM") {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
            return new GroupAdapters.ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assign_group_head, parent, false);
            return new GroupAdapters.ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapters.ViewHolder holder, int position) {
        final GroupModel groupModel = itemsArrayList.get(position);
        itemsArrayList = list_item;
        if (mTag == "TM") {
            holder.cb_team_members.setChecked(itemsArrayList.get(position).isChecked());
            holder.cb_team_members.setTag(position);
//            if (itemsArrayList.get(position).isIsenabled()) {
//                holder.cb_team_members.setEnabled(true);
//            } else {
//                holder.cb_team_members.setEnabled(false);
//            }
            holder.cb_team_members.setEnabled(true);

            holder.cb_team_members.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_team_members.getTag();
                    if (itemsArrayList.get(pos).isChecked()) {
                        itemsArrayList.get(pos).setChecked(false);
//                        itemsArrayList.add(itemsArrayList.get(pos));
                    } else {
                        itemsArrayList.get(pos).setChecked(true);
//                        itemsArrayList.remove(itemsArrayList.get(pos));
                    }
                    check_allselected();
                }
            });
            holder.tv_tm_name.setText(groupModel.getName());
        } else {
            holder.rb_group_head.setText(groupModel.getName());
            holder.rb_group_head.setChecked(position == selectedPosition);
            holder.rb_group_head.setTag(groupModel.getId());
            holder.rb_group_head.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        selectedPosition = holder.getAdapterPosition();
                        itemClickListener.onClick(groupModel.getId());
                    }
                }
            });
        }
    }

    public ArrayList<GroupModel> getList_item() {
        return itemsArrayList;
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsArrayList = list_item;
                } else {
                    ArrayList<GroupModel> filteredList = new ArrayList<>();
                    for (GroupModel row : list_item) {
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
                    itemsArrayList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = itemsArrayList.size();
                filterResults.values = itemsArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsArrayList = (ArrayList<GroupModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public boolean selectOrDeselectAll(boolean isChecked) {
        for (int i = 0; i < list_item.size(); i++) {
            list_item.get(i).setChecked(isChecked);
            notifyDataSetChanged();
        }
        return isChecked;
    }

    public void check_allselected() {
        for (int i = 0; i < itemsArrayList.size(); i++) {
            if (!itemsArrayList.get(i).isChecked()) {
                select_checked = false;
                break;
            } else {
                select_checked = true;
            }
        }
        group.check_select_all(select_checked);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_team_members;
        private TextView tv_tm_name, tv_gh_name;
        private RadioButton rb_group_head;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_team_members = itemView.findViewById(R.id.chk_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
//            tv_gh_name = itemView.findViewById(R.id.tv_gh_name);
            rb_group_head = itemView.findViewById(R.id.rb_selected);
        }
    }
}
