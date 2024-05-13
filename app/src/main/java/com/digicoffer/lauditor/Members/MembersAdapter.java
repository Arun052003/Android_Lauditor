package com.digicoffer.lauditor.Members;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> implements Filterable {
    //  ArrayList<MembersModel> members_list = new ArrayList<>();
    ArrayList<ActionModel> actions_List = new ArrayList();
    ArrayList<MembersModel> itemsArrayList;


    ArrayList<MembersModel> list_item;

    // Constructor
    public MembersAdapter(ArrayList<MembersModel> members_list) {
        this.itemsArrayList = members_list;
        this.list_item = members_list;
    }

    Context mcontext;
    Members members;
    EventListener eventListener;

    // Constructor with listener
    public MembersAdapter(ArrayList<MembersModel> itemsArrayList, Context context, EventListener listener, Members members) {
        this.itemsArrayList = itemsArrayList;
        this.mcontext = context;
        this.eventListener = listener;
        this.members = members;
        this.list_item = itemsArrayList;
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsArrayList = list_item;
                } else {
                    ArrayList<MembersModel> filteredList = new ArrayList<>();
                    for (MembersModel row : list_item) {
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
                itemsArrayList = (ArrayList<MembersModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface EventListener {
        void EditMember(MembersModel membersModel);

        void UpdateGroupAccess(MembersModel membersModel) throws JSONException;

        void ResetPassword(MembersModel membersModel);

        void DeleteMember(MembersModel membersModel);
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_members, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.ViewHolder holder, int position) {
        MembersModel membersModel = itemsArrayList.get(position);
        actions_List.clear();
        holder.sp_action.setVisibility(View.GONE);
//        actions_List.add(new ActionModel("Choose Actions"));
        actions_List.add(new ActionModel("Edit Member"));
        actions_List.add(new ActionModel("Reset Password"));
        actions_List.add(new ActionModel("Update Group Access"));
        actions_List.add(new ActionModel("Delete Member"));

        holder.tv_members_name.setText(membersModel.getName());
        holder.tv_litigation.setText(membersModel.getDesignation());
        holder.tv_currency.setText(membersModel.getDefaultRate());
        holder.tv_currency_type.setText(membersModel.getCurrency());
        holder.tv_email.setText(membersModel.getEmail());

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

        holder.sp_action.findFocus();
        holder.sp_action.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int name = position;
                if (name == 0) {
                    members.model_name("Edit Member");
                    eventListener.EditMember(membersModel);
                } else if (name == 2) {
                    members.model_name("Update Group Access");
                    try {
                        eventListener.UpdateGroupAccess(membersModel);
                    } catch (JSONException e) {
                        e.fillInStackTrace();
                    }
                } else if (name == 1) {
                    eventListener.ResetPassword(membersModel);
                } else if (name == 3) {
                    members.model_name("Delete Member");
                    eventListener.DeleteMember(membersModel);
                }
                holder.sp_action.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_members_name, tv_member_type, tv_litigation, tv_email, tv_currency, tv_currency_type;
        private ListView sp_action;
        TextView custom_spinner;
        CardView custom_spinner_cardview;
        LinearLayout action_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_members_name = itemView.findViewById(R.id.tv_member_name);
            tv_member_type = itemView.findViewById(R.id.tv_member_type);
            tv_litigation = itemView.findViewById(R.id.tv_litigation);
            tv_currency_type = itemView.findViewById(R.id.tv_currency_type);
            tv_currency = itemView.findViewById(R.id.tv_currency);
            tv_email = itemView.findViewById(R.id.tv_email_id);

            custom_spinner = itemView.findViewById(R.id.custom_spinner);
            custom_spinner_cardview = itemView.findViewById(R.id.custom_spinner_cardview);
            sp_action = itemView.findViewById(R.id.list_client);
            action_layout = itemView.findViewById(R.id.action_layout);
        }
    }
}
