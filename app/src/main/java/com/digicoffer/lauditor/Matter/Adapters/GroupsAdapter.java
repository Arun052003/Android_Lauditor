package com.digicoffer.lauditor.Matter.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        if (TAG == "Groups") {
            GroupsModel groupsModel = sharedList.get(position);

            holder.cb_documents.setChecked(sharedList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(groupsModel.getGroup_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();

                    if (sharedList.get(pos).isChecked()) {
                        sharedList.get(pos).setChecked(false);
                    } else {
                        sharedList.get(pos).setChecked(true);
                    }
                }
            });
        } else if (TAG == "Clients") {
            ClientsModel clientsModel = clientsList.get(position);
            holder.cb_documents.setChecked(clientsList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(clientsModel.getClient_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (clientsList.get(pos).isChecked()) {
                       clientsList.get(pos).setChecked(false);

                    } else {
                        clientsList.get(pos).setChecked(true);

                    }
                }
            });
        } else if (TAG == "TM") {
            TeamModel teamModel = tmList.get(position);
            holder.cb_documents.setChecked(tmList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(teamModel.getTm_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (tmList.get(pos).isChecked()) {
                        tmList.get(pos).setChecked(false);
                    } else {
                        tmList.get(pos).setChecked(true);
                    }
                }
            });
        }else if(TAG == "UGM"){
            ViewMatterModel viewMatterModel = groupsList.get(position);
            holder.cb_documents.setChecked(groupsList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(viewMatterModel.getGroup_name());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (groupsList.get(pos).isChecked()) {
                        groupsList.get(pos).setChecked(false);
                    } else {
                        groupsList.get(pos).setChecked(true);
                    }
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
        if (TAG == "Groups") {
            return sharedList.size();
        } else if (TAG == "Clients") {
            return clientsList.size();
        } else if(TAG == "UGM"){
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

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            cb_documents = itemView.findViewById(R.id.chk_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
        }
    }
}
