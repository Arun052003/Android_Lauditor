package com.digicoffer.lauditor.Documents.DocumentsListAdpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Documents.models.DocumentsModel;
import com.digicoffer.lauditor.R;

import java.util.ArrayList;

public class DocumentsListAdapter extends RecyclerView.Adapter<DocumentsListAdapter.ViewHolder> {
    ArrayList<DocumentsModel> itemsArrayList;
    ArrayList<DocumentsModel> list_item;
    boolean select_checked;
    boolean isfiledeleted = false;
    String tag = "";
    String subtag = "";
    Documents documents;
    DocumentsListAdapter.EventListener eventListener;

    public DocumentsListAdapter(ArrayList<DocumentsModel> itemsArrayList, String tag, String subtag, EventListener eventListener, Documents documents) {
        this.documents = documents;
        this.itemsArrayList = itemsArrayList;
        this.list_item = itemsArrayList;
        this.tag = tag;
        this.subtag = subtag;
        this.eventListener = eventListener;
    }

    public interface EventListener {

        void ViewTags(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList);

        void EditDocuments(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList);

        void RemoveDocument(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList, String tag);
    }

    @NonNull
    @Override
    public DocumentsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.displays_documents_list, parent, false);
        return new DocumentsListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsListAdapter.ViewHolder holder, int position) {
        DocumentsModel documentsModel = itemsArrayList.get(position);
        itemsArrayList = list_item;
//        if (subtag=="view_tags"){
//            holder.btn_view_tags.setVisibility(View.VISIBLE);
//        }
        if (documentsModel.getTags() != null && tag == "add_tag") {
            holder.btn_view_tags.setVisibility(View.VISIBLE);
        } else {
            holder.btn_view_tags.setVisibility(View.GONE);
        }
        holder.tv_document_name.setText(documentsModel.getName());
        holder.cb_documents_list.setChecked(itemsArrayList.get(position).isChecked());
        holder.cb_documents_list.setTag(position);
//            if(groupModel.isIsenabled()==null)
        if (itemsArrayList.get(position).isIsenabled()) {
            holder.cb_documents_list.setEnabled(true);
        } else {
            holder.cb_documents_list.setEnabled(false);
        }
        holder.cb_documents_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.cb_documents_list.getTag();
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
        holder.btn_view_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventListener.ViewTags(documentsModel, itemsArrayList);
            }
        });
        holder.iv_edit_meta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventListener.EditDocuments(documentsModel, itemsArrayList);
            }
        });
        holder.iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventListener.RemoveDocument(documentsModel, itemsArrayList, tag);

                //passing the value when the file is deleted....
                isfiledeleted = true;
                documents.remove_file(isfiledeleted);
            }
        });
//            holder.cb_team_members.setChecked(true);
//        holder.tv_tm_name.setText(groupModel.getName());
    }

    public ArrayList<DocumentsModel> getList_item() {
        return itemsArrayList;
    }

    public void selectall() {
        int i;
        for (i = 0; i < list_item.size(); i++) {
            list_item.get(i).setChecked(true);
            notifyDataSetChanged();
        }
    }

    public void deselectall() {
        for (int i = 0; i < list_item.size(); i++) {
            list_item.get(i).setChecked(false);
            notifyDataSetChanged();
        }
//        allselected();
    }

    public void check_allselected() {
        for (int i = 0; i < itemsArrayList.size(); i++) {
            if (!itemsArrayList.get(i).isChecked()) {
                select_checked = false;
                break;
//                Log.e("ivalue",".."+i);
            } else {
                select_checked = true;
            }
        }
        documents.check_select_all(select_checked);

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_documents_list;
        private TextView tv_document_name;
        private ImageView iv_cancel, iv_edit_meta;
        private Button btn_view_tags;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_documents_list = itemView.findViewById(R.id.chk_selected_documents);
            tv_document_name = itemView.findViewById(R.id.tv_document_name);
            iv_edit_meta = itemView.findViewById(R.id.iv_edit_meta);
            iv_edit_meta.setImageResource(R.drawable.edit_documents_icon);
            btn_view_tags = itemView.findViewById(R.id.btn_view_tags);
            btn_view_tags.setText("View Tags");
            iv_cancel = itemView.findViewById(R.id.iv_cancel);
            iv_cancel.setImageResource(R.drawable.cancel_red_icon);
            if (tag == "add_tag") {
                cb_documents_list.setVisibility(View.VISIBLE);
                iv_edit_meta.setVisibility(View.GONE);
                btn_view_tags.setVisibility(View.GONE);
            } else if (tag == "edit_meta") {
                iv_edit_meta.setVisibility(View.VISIBLE);
                cb_documents_list.setVisibility(View.GONE);
                btn_view_tags.setVisibility(View.GONE);
            } else {
                iv_edit_meta.setVisibility(View.GONE);
                cb_documents_list.setVisibility(View.GONE);
            }
        }
    }
}
