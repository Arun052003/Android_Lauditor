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
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.Viewholder> {

    ArrayList<DocumentsModel> documentsList = new ArrayList<>();




    public DocumentsAdapter( ArrayList<DocumentsModel> documentsList) {

        this.documentsList = documentsList;

    }

    @NonNull
    @Override
    public DocumentsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_team_members, parent, false);
        return new DocumentsAdapter.Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsAdapter.Viewholder holder, int position) {

            DocumentsModel documentsModel = documentsList.get(position);
            holder.cb_documents.setChecked(documentsList.get(position).isChecked());
            holder.cb_documents.setTag(position);
            holder.tv_tm_name.setText(documentsModel.getName());
            holder.cb_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer pos = (Integer) holder.cb_documents.getTag();
                    if (documentsList.get(pos).isChecked()) {
                        documentsList.get(pos).setChecked(false);
                    } else {
                        documentsList.get(pos).setChecked(true);
                    }
                }
            });
        }




    public ArrayList<DocumentsModel> getDocumentsList() {
        return documentsList;
    }

    @Override
    public int getItemCount() {

            return documentsList.size();


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
