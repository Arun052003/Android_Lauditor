package com.digicoffer.lauditor.Documents.DocumentsListAdpater;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.models.ViewDocumentsModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONArray;

import java.util.ArrayList;

public class view_document_emailadapter extends RecyclerView.Adapter<view_document_emailadapter.MyViewHolder> {
    //ArrayList<ViewDocumentsModel> docsList = new ArrayList<>();
    ArrayList<ViewDocumentsModel> list_item;
    ArrayList<ViewDocumentsModel> itemsArrayList;
    ArrayList<ViewDocumentsModel> docs_list=new ArrayList<>();
    private ArrayList<String> selectedDocumentNames = new ArrayList<>();
    private ArrayList<String> selectedDocumentIds = new ArrayList<>();
    private ArrayList<String> selectedDocumentPaths = new ArrayList<>();

    Context cContext;

    public view_document_emailadapter(ArrayList<ViewDocumentsModel> itemsArrayList, Context context) {
        this.itemsArrayList = itemsArrayList;

        this.cContext = context;
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
                    ArrayList<ViewDocumentsModel> filteredList = new ArrayList<>();
                    for (ViewDocumentsModel row : list_item) {
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

            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsArrayList = (ArrayList<ViewDocumentsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }




    @NonNull
    @Override
    public view_document_emailadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_client_group_document, parent, false);
        return new view_document_emailadapter.MyViewHolder(view);
    }


    public void onBindViewHolder(@NonNull view_document_emailadapter.MyViewHolder holder, int position) {
        ViewDocumentsModel viewDocumentsModel = itemsArrayList.get(position);
        try {
            holder.tv_client_name.setText(viewDocumentsModel.getUploaded_by());
//            holder.tv_client_name_one.setText(viewDocumentsModel.getUploaded_by());
            holder.tv_doc_description.setText(viewDocumentsModel.getDescription());
//            holder.tv_document_display_name.setText(viewDocumentsModel.getName());
            holder.tv_image_name.setText(viewDocumentsModel.getName());
//           holder.checkbox_id.setText(viewDocumentsModel.getId());
            // Assuming checkbox_id is a CheckBox variable declared at the class level
           holder. checkbox_id.setTag(viewDocumentsModel);

            holder.  checkbox_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ViewDocumentsModel documentModel = (ViewDocumentsModel) buttonView.getTag();
                        docs_list.add(documentModel);
                        if (documentModel != null) {
                            JSONArray docs = new JSONArray();
                            JSONArray doc_name=new JSONArray();
                            for (int k = 0; k < docs_list.size(); k++) {
                                ViewDocumentsModel documentsModel1 = docs_list.get(k);
                                docs.put(documentsModel1.getId());
                                doc_name.put(documentsModel1.getName());
//                                String documentId=documentModel.getId();
//                                String documentName = documentModel.getName();
//
//                                Log.d("DocumentInfo", "ID: " + documentId + ", Name: " + documentName);
                            }
                            try {
                                Constants.doc_id=docs;
                                Constants.model=doc_name;
                            }catch (Exception e)
                            {
                                e.fillInStackTrace();
                            }
                        }
                    }
                }
            });

//            Log.d("IMage_name", viewDocumentsModel.getName());
            holder.tv_created_date.setText(viewDocumentsModel.getCreated());
            holder.tv_Expiration_date.setText(viewDocumentsModel.getExpiration_date());
            holder.tv_client_name.setPaintFlags(holder.tv_client_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.tv_doc_description.setPaintFlags(holder.tv_doc_description.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), cContext);
        }
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        CheckBox checkbox_id;

        ViewDocumentsModel viewDocumentsModel;
        TextView tv_document_display_name, tv_Expiration, tv_client_name_one, tv_image_name, tv_Expiration_date, tv_client_name, tv_doc_description, tv_created_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_client_name.setTextSize(15);
            checkbox_id = itemView.findViewById(R.id.checkbox_id);
            checkbox_id.setVisibility(View.VISIBLE);





            tv_doc_description = itemView.findViewById(R.id.tv_doc_description);
            tv_doc_description.setTextSize(15);
            tv_created_date = itemView.findViewById(R.id.tv_created_date);
            tv_created_date.setText("Date");
            tv_Expiration = itemView.findViewById(R.id.tv_Expiration);
            tv_Expiration.setText("Expiration:");
            tv_Expiration_date = itemView.findViewById(R.id.tv_Expiration_date);
            tv_Expiration_date.setText("Expiration");

        }
    }
}
