package com.digicoffer.lauditor.Calendar;

import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Calendar.Models.DocumentsDo;
import com.digicoffer.lauditor.Calendar.Models.RelationshipsDO;
import com.digicoffer.lauditor.Calendar.Models.TeamDo;
import com.digicoffer.lauditor.Matter.Adapters.GroupsAdapter;
import com.digicoffer.lauditor.Matter.Models.ClientsModel;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.TeamModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class CommonRelationshipsAdapter extends RecyclerView.Adapter<CommonRelationshipsAdapter.Viewholder> {
    ArrayList<TeamDo> sharedList = new ArrayList<>();
    ArrayList<TeamDo> list_item = new ArrayList<>();
    ArrayList<RelationshipsDO> individual_list = new ArrayList<>();
    ArrayList<TeamDo> tmList = new ArrayList<>();
    ArrayList<RelationshipsDO> entity_client_list = new ArrayList<>();
    ArrayList<DocumentsDo> documents_list = new ArrayList<>();
    String TAG = "TM";


    public CommonRelationshipsAdapter(ArrayList<TeamDo> teamList, String Tag, ArrayList<RelationshipsDO> individual_list, ArrayList<RelationshipsDO> entity_client_list, ArrayList<DocumentsDo> documents_list) {

        this.entity_client_list = entity_client_list;
        this.individual_list = individual_list;
        this.tmList = teamList;
        this.documents_list = documents_list;
        this.TAG = Tag;
    }

    @NonNull
    @Override
    public CommonRelationshipsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
        return new CommonRelationshipsAdapter.Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonRelationshipsAdapter.Viewholder holder, int position) {

        if (TAG == "TM") {
            TeamDo teamModel = tmList.get(position);
            holder.cb_documents.setChecked(tmList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(teamModel.getName());
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
        } else if (TAG == "INDIVIDUAL") {
            RelationshipsDO relationshipsDO = individual_list.get(position);
            holder.cb_documents.setChecked(individual_list.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(relationshipsDO.getName());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (individual_list.get(pos).isChecked()) {
                        individual_list.get(pos).setChecked(false);
                    } else {
                        individual_list.get(pos).setChecked(true);
                    }
                }
            });
        } else if (TAG == "Documents") {
            DocumentsDo documentsDo = documents_list.get(position);
            holder.cb_documents.setChecked(documents_list.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(documentsDo.getName());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (documents_list.get(pos).isChecked()) {
                        documents_list.get(pos).setChecked(false);
                    } else {
                        documents_list.get(pos).setChecked(true);
                    }
                }
            });
        } else {
            RelationshipsDO relationshipsDO = entity_client_list.get(position);
            holder.cb_documents.setChecked(entity_client_list.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(relationshipsDO.getName());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (entity_client_list.get(pos).isChecked()) {
                        entity_client_list.get(pos).setChecked(false);
                    } else {
                        entity_client_list.get(pos).setChecked(true);
                    }
                }
            });
        }

    }

    public ArrayList<RelationshipsDO> getIndividual_List() {
        return individual_list;
    }

    public ArrayList<TeamDo> getList_item() {
        return sharedList;
    }

    public ArrayList<TeamDo> getTmList() {
        return tmList;
    }

    public ArrayList<RelationshipsDO> getEntity_client_list() {
        return entity_client_list;
    }

    public ArrayList<DocumentsDo> getDocuments_list() {
        return documents_list;
    }

    @Override
    public int getItemCount() {
//        if (TAG == "Groups") {
//            return sharedList.size();
//        } else if (TAG == "Clients") {
//            return clientsList.size();
//        } else if(TAG == "UGM"){
//            return groupsList.size();
//        }
        if (TAG == "TM") {
            return tmList.size();
        } else if (TAG == "INDIVIDUAL") {
            return individual_list.size();
        } else if (TAG == "Documents") {
            return documents_list.size();
        } else {
            return entity_client_list.size();
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
        private View list_line;
        private LinearLayout select_tm_layout;
        private CheckBox cb_documents;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            cb_documents = itemView.findViewById(R.id.chk_selected);
            tv_tm_name = itemView.findViewById(R.id.tv_tm_name);
            select_tm_layout = itemView.findViewById(R.id.select_tm_layout);
            list_line = itemView.findViewById(R.id.list_line);
            list_line.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            select_tm_layout.setLayoutParams(params);
            select_tm_layout.setBackgroundResource(com.applandeo.materialcalendarview.R.drawable.background_transparent);
        }
    }
}
