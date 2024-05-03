package com.digicoffer.lauditor.Groups.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.Groups.Groups;
import com.digicoffer.lauditor.Groups.ViewGroupsItemClickListener;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class ViewGroupsAdpater extends RecyclerView.Adapter<ViewGroupsAdpater.ViewHolder> implements Filterable {
    ArrayList<ViewGroupModel> itemsArrayList;
    ArrayList<ViewGroupModel> list_item;
    ArrayList<ActionModel> actions_List = new ArrayList<ActionModel>();
    InterfaceListener eventListener;
    Context mcontext;
    ViewGroupsItemClickListener itemClickListener;
    String mTag = "";
    int selectedPosition = -1;
    Groups group;
    private boolean isSpinnerInitial = true;

    int hidingItemIndex = 0;

    public ViewGroupsAdpater(ArrayList<ViewGroupModel> itemsArrayList, Context context, InterfaceListener eventListener, String Tag, ViewGroupsItemClickListener itemClickListener, Groups groups) {
        this.itemsArrayList = itemsArrayList;
        this.list_item = itemsArrayList;
        this.mcontext = context;
        this.group = groups;
        this.eventListener = eventListener;
        this.mTag = Tag;
        this.itemClickListener = itemClickListener;
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
                itemsArrayList = (ArrayList<ViewGroupModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface InterfaceListener {
        void EditGroup(ViewGroupModel viewGroupModel);

        void DeleteGroup(ViewGroupModel viewGroupModel, ArrayList<ViewGroupModel> itemsArrayList);

        void CGH(ViewGroupModel viewGroupModel, ArrayList<ViewGroupModel> itemsArrayList);

        void UGM(ViewGroupModel viewGroupModel) throws JSONException;

        void GAL(ViewGroupModel viewGroupModel) throws JSONException;
    }

    @NonNull
    @Override
    public ViewGroupsAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mTag == "VG") {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_groups, parent, false);
            return new ViewGroupsAdpater.ViewHolder(itemView);
        } else if (mTag == "UGM") {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
            return new ViewGroupsAdpater.ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button_layout, parent, false);
            return new ViewGroupsAdpater.ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewGroupsAdpater.ViewHolder holder, int position) {
        Log.i("Tag", "tagname" + mTag);
        ViewGroupModel viewGroupModel = itemsArrayList.get(position);
        itemsArrayList = list_item;
        if (mTag == "VG") {
            holder.sp_action.setVisibility(View.GONE);
            holder.tv_user_type.setText(viewGroupModel.getName());
            holder.tv_owner_name.setText(viewGroupModel.getOwner_name());
            holder.tv_date.setText(viewGroupModel.getCreated());
            holder.tv_description.setText(viewGroupModel.getDescription());
            //Modifying a text and its color in a design
            holder.created_id.setText("Created :");
            holder.tv_owner_name.setTextColor(Color.BLACK);
            holder.tv_date.setTextColor(Color.BLACK);
            actions_List.clear();

////        actions_List.add(new ActionModel("Add|Remove"));
//            actions_List.add(new ActionModel("Choose Actions"));
            actions_List.add(new ActionModel("Edit Group"));
            actions_List.add(new ActionModel("Delete"));
            actions_List.add(new ActionModel("Change Group Head"));
            actions_List.add(new ActionModel("Update Group Members"));
            actions_List.add(new ActionModel("Group Activity Log"));
            final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) mcontext, actions_List);
            holder.sp_action.setAdapter(spinner_adapter);

            holder.custom_spinner_cardview.setOnClickListener(new View.OnClickListener() {
                boolean ischecked = true;

                @Override
                public void onClick(View v) {
                    if (ischecked)
                        holder.sp_action.setVisibility(View.VISIBLE);
                    else
                        holder.sp_action.setVisibility(View.GONE);
                    ischecked = !ischecked;
                }
            });
            holder.sp_action.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int name = position;
                    if (name == 0) {
                        group.page_name("Edit Group");
                        eventListener.EditGroup(viewGroupModel);
                    } else if (name == 1) {
                        group.page_name("Delete Group");
                        eventListener.DeleteGroup(viewGroupModel, itemsArrayList);
                    } else if (name == 2) {
                        group.page_name("Change Group Head");
                        eventListener.CGH(viewGroupModel, itemsArrayList);
                    } else if (name == 3) {
                        group.page_name("Update Group Members");
                        try {
                            eventListener.UGM(viewGroupModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (name == 4) {
                        group.page_name("Group Activity Log");
                        try {
                            eventListener.GAL(viewGroupModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    holder.sp_action.setVisibility(View.GONE);
                }
            });
        } else if (mTag == "UGM") {
            holder.cb_team_members.setChecked(itemsArrayList.get(position).isChecked());
            holder.cb_team_members.setTag(position);
            holder.cb_team_members.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_team_members.getTag();
                    if (itemsArrayList.get(pos).isChecked()) {
                        itemsArrayList.get(pos).setChecked(false);
                    } else {
                        itemsArrayList.get(pos).setChecked(true);
                    }
                }
            });
            holder.tv_tm_name.setText(viewGroupModel.getName());
        } else if (mTag == "DG") {
//            holder.check_layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.rb_group_selected.setChecked(true);
//                }
//            });
            holder.tv_gh_name.setText(viewGroupModel.getName());
            holder.rb_group_selected.setChecked(position == selectedPosition);
            holder.rb_group_selected.setTag(viewGroupModel.getId());
            holder.rb_group_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        selectedPosition = holder.getAdapterPosition();
                        itemClickListener.onClick(viewGroupModel.getId());

                    }
                }
            });
        } else {
            holder.tv_gh_name.setText(viewGroupModel.getGroup_name());
            holder.rb_group_selected.setChecked(position == selectedPosition);
            holder.rb_group_selected.setTag(viewGroupModel.getGroup_id());
            holder.rb_group_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        selectedPosition = holder.getAdapterPosition();
                        itemClickListener.onClick(viewGroupModel.getGroup_id());
                    }
                }
            });
        }
    }

    public void selectOrDeselectAll(boolean isChecked) {
        for (int i = 0; i < list_item.size(); i++) {
            list_item.get(i).setChecked(isChecked);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_user_type, tv_owner_name, tv_date, tv_description, tv_tm_name, created_id;
        private ListView sp_action;
        TextView custom_spinner, tv_gh_name;
        CardView custom_spinner_cardview;
        LinearLayout action_layout, check_layout;
        private CheckBox cb_team_members, rb_group;
        private RadioButton rb_group_head, rb_group_selected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user_type = itemView.findViewById(R.id.tv_group_name);
            tv_owner_name = itemView.findViewById(R.id.tv_group_head);
            tv_gh_name = itemView.findViewById(R.id.tv_gh_name);
            check_layout = itemView.findViewById(R.id.check_layout);
            //tv_owner_name.setTextColor(Color.BLACK);
            cb_team_members = itemView.findViewById(R.id.chk_selected);
            tv_date = itemView.findViewById(R.id.tv_date);
            created_id = itemView.findViewById(R.id.created_id);
            action_layout = itemView.findViewById(R.id.action_layout);
            //tv_date.setTextColor(Color.BLACK);
            tv_description = itemView.findViewById(R.id.tv_description);
            custom_spinner = itemView.findViewById(R.id.custom_spinner);
            custom_spinner_cardview = itemView.findViewById(R.id.custom_spinner_cardview);
            sp_action = itemView.findViewById(R.id.list_client);
            rb_group_selected = itemView.findViewById(R.id.rb_group_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
        }
    }
}
