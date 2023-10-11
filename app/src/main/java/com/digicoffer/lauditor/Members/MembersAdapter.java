package com.digicoffer.lauditor.Members;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;

import org.json.JSONException;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private ArrayList<MembersModel> members_list = new ArrayList<>();
    private ArrayList<ActionModel> actions_List = new ArrayList<>();
    private Context mcontext;
    private MembersAdapter.EventListener eventListener;

    public MembersAdapter(ArrayList<MembersModel> members_list, Context context, MembersAdapter.EventListener listener) {
        this.members_list = members_list;
        this.mcontext = context;
        this.eventListener = listener;
    }

    public interface EventListener {
        void EditMember(MembersModel membersModel);
        void UpdateGroupAcess(MembersModel membersModel) throws JSONException;
        void ResetPassword(MembersModel membersModel);
        void DeleteMember(MembersModel membersModel);
    }

    @NonNull
    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_members, parent, false);
        return new MembersAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MembersModel membersModel = members_list.get(position);

        actions_List.clear();
        actions_List.add(new ActionModel("Choose Actions"));
        actions_List.add(new ActionModel("Edit Member"));
        actions_List.add(new ActionModel("Reset Password"));
        actions_List.add(new ActionModel("Add|Remove Group Access"));
        actions_List.add(new ActionModel("Delete Member"));

        holder.tv_members_name.setText(membersModel.getName());
        holder.tv_litigation.setText(membersModel.getDesignation());
        holder.tv_currency.setText(membersModel.getDefaultRate());
        holder.tv_currency_type.setText(membersModel.getCurrency());
        holder.tv_email.setText(membersModel.getEmail());


        final CommonSpinnerAdapter spinner_adapter = new CommonSpinnerAdapter((Activity) mcontext, actions_List);
        holder.sp_action.setAdapter(spinner_adapter);

        holder.sp_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String name = actions_List.get(i).getName(); // Use 'i' directly

                if ("Edit Member".equals(name)) {
                    eventListener.EditMember(membersModel);
                } else if ("Add|Remove Group Access".equals(name)) {
                    try {
                        eventListener.UpdateGroupAcess(membersModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("Reset Password".equals(name)) {
                    eventListener.ResetPassword(membersModel);
                } else if ("Delete Member".equals(name)) {
                    eventListener.DeleteMember(membersModel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        holder.tv_textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                MembersModel membersModel = members_list.get(position);

               // actions_List.clear();
               actions_List.add(new ActionModel("Choose Actions"));
                actions_List.add(new ActionModel("Edit Member"));
                actions_List.add(new ActionModel("Reset Password"));
                actions_List.add(new ActionModel("Add|Remove Group Access"));
                actions_List.add(new ActionModel("Delete Member"));
                holder.tv_members_name.setText(membersModel.getName());
                holder.tv_litigation.setText(membersModel.getDesignation());
                holder.tv_currency.setText(membersModel.getDefaultRate());
                holder.tv_currency_type.setText(membersModel.getCurrency());
                holder.tv_email.setText(membersModel.getEmail());
                holder.sp_action.setAdapter(spinner_adapter);
                int Selection = 1;
                holder.sp_action.setSelection(Selection);



            }
        });
    }

    @Override
    public int getItemCount() {
        return members_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_members_name, tv_member_type, tv_litigation, tv_email, tv_currency, tv_currency_type, tv_textView2;
        Spinner sp_action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_members_name = itemView.findViewById(R.id.tv_member_name);
            tv_member_type = itemView.findViewById(R.id.tv_member_type);
            tv_litigation = itemView.findViewById(R.id.tv_litigation);
            tv_currency_type = itemView.findViewById(R.id.tv_currency_type);
            tv_currency = itemView.findViewById(R.id.tv_currency);
            tv_email = itemView.findViewById(R.id.tv_email_id);
            sp_action = itemView.findViewById(R.id.sp_action);
            tv_textView2 = itemView.findViewById(R.id.textView2);
        }
    }
}
