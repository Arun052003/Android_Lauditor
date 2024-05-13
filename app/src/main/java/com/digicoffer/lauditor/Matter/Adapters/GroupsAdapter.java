package com.digicoffer.lauditor.Matter.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Matter.Models.ClientsModel;
import com.digicoffer.lauditor.Matter.Models.DocumentsModel;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.TeamModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;

import org.minidns.record.A;

import java.util.ArrayList;
import java.util.Objects;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.Viewholder> {
    ArrayList<GroupsModel> sharedList = new ArrayList<>();
    ArrayList<GroupsModel> list_item = new ArrayList<>();
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<TeamModel> tmList = new ArrayList<>();
    ArrayList<ViewMatterModel> groupsList = new ArrayList<>();

    String TAG = "Groups";


    public GroupsAdapter(ArrayList<GroupsModel> sharedList, ArrayList<ClientsModel> clientsList, ArrayList<TeamModel> teamList,ArrayList<ViewMatterModel> groups_list,  String Tag) {
        this.sharedList = sharedList;
        this.list_item = sharedList;
        this.clientsList = clientsList;
        this.tmList = teamList;
        this.groupsList = groups_list;
        this.TAG = Tag;
    }

    @NonNull
    @Override
    public GroupsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
        return new GroupsAdapter.Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsAdapter.Viewholder holder, int position) {
        if (Objects.equals(TAG, "Groups")) {
            GroupsModel groupsModel = sharedList.get(position);
            holder.cb_documents.setChecked(sharedList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(groupsModel.getGroup_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    sharedList.get(pos).setChecked(!sharedList.get(pos).isChecked());
                }
            });
        } else if (Objects.equals(TAG, "Clients")) {
            ClientsModel clientsModel = clientsList.get(position);
            holder.cb_documents.setChecked(clientsList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(clientsModel.getClient_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    clientsList.get(pos).setChecked(!clientsList.get(pos).isChecked());
                }
            });
        } else if (Objects.equals(TAG, "TM")) {
            TeamModel teamModel = tmList.get(position);
            holder.cb_documents.setChecked(tmList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(teamModel.getTm_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    tmList.get(pos).setChecked(!tmList.get(pos).isChecked());
                }
            });
        }else if(Objects.equals(TAG, "UGM")){
            ViewMatterModel viewMatterModel = groupsList.get(position);
            holder.cb_documents.setChecked(groupsList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(viewMatterModel.getGroup_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    groupsList.get(pos).setChecked(!groupsList.get(pos).isChecked());
                }
            });
        }
    }


    public ArrayList<ClientsModel> getClientsList_item() {
        return clientsList;
    }

    public ArrayList<GroupsModel> getList_item() {
        return sharedList;
    }

    public ArrayList<TeamModel> getTmList() {
        return tmList;
    }

    public ArrayList<ViewMatterModel> getGroupsList(){
        return groupsList;
    }

    @Override
    public int getItemCount() {
        if (Objects.equals(TAG, "Groups")) {
            return sharedList.size();
        } else if (Objects.equals(TAG, "Clients")) {
            return clientsList.size();
        } else if(Objects.equals(TAG, "UGM")){
            return groupsList.size();
        }else  {
            return tmList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView tv_tm_name;
        private CheckBox cb_documents;
        private View list_line;
        private LinearLayout select_tm_layout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            cb_documents = itemView.findViewById(R.id.chk_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
            select_tm_layout=itemView.findViewById(R.id.select_tm_layout);
            list_line = itemView.findViewById(R.id.list_line);
            list_line.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            select_tm_layout.setLayoutParams(params);
            select_tm_layout.setBackgroundResource(com.applandeo.materialcalendarview.R.drawable.background_transparent);
        }
    }
}
