package com.digicoffer.lauditor.Documents.DocumentsListAdpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Documents.models.DocumentsModel;
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.Viewholder> {
    ArrayList<DocumentsModel> sharedList = new ArrayList<>();
    ArrayList<DocumentsModel> list_item = new ArrayList<>();
    Documents documents1;

    public GroupsListAdapter(ArrayList<DocumentsModel> sharedList, Documents documents) {
        this.documents1 = documents;
        this.sharedList = sharedList;
        this.list_item = sharedList;
    }

    @NonNull
    @Override
    public GroupsListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
        return new GroupsListAdapter.Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsListAdapter.Viewholder holder, int position) {
        DocumentsModel groupsModel = sharedList.get(position);
        holder.cb_documents.setChecked(sharedList.get(position).isGroupChecked());
        holder.cb_documents.setTag(position);
        holder.tv_tm_name.setText(groupsModel.getGroup_name());
        holder.cb_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.cb_documents.getTag();
                if (sharedList.get(pos).isGroupChecked()) {
                    sharedList.get(pos).setGroupChecked(false);
                } else {
                    sharedList.get(pos).setGroupChecked(true);
                }
            }
        });
    }

    public ArrayList<DocumentsModel> getList_item() {
        return sharedList;
    }

    @Override
    public int getItemCount() {
        return sharedList.size();
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
