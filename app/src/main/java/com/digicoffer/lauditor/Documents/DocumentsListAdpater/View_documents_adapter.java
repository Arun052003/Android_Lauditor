package com.digicoffer.lauditor.Documents.DocumentsListAdpater;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.models.DocumentsModel;
import com.digicoffer.lauditor.Documents.models.ViewDocumentsModel;
import com.digicoffer.lauditor.Members.MembersModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.pgpainless.key.selection.key.util.And;

import java.util.ArrayList;

public class View_documents_adapter extends RecyclerView.Adapter<View_documents_adapter.MyViewHolder> {
    //ArrayList<ViewDocumentsModel> docsList = new ArrayList<>();
    ArrayList<ViewDocumentsModel> list_item;
    ArrayList<ViewDocumentsModel> itemsArrayList;
    View_documents_adapter.Eventlistner eventlistner;
    Context cContext;
    boolean is_encrypted;

    public View_documents_adapter(ArrayList<ViewDocumentsModel> itemsArrayList, Eventlistner eventlistner, Context context) {
        this.itemsArrayList = itemsArrayList;
        this.eventlistner = eventlistner;
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


    public interface Eventlistner {
        void edit_document(ViewDocumentsModel viewDocumentsModel);

        void delete_document(ViewDocumentsModel viewDocumentsModel);

        void Display_Document(ViewDocumentsModel viewDocumentsModel);

        void encryption(ViewDocumentsModel viewDocumentsModel);

        void decryption(ViewDocumentsModel viewDocumentsModel);
    }

    @NonNull
    @Override
    public View_documents_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_documents_list, parent, false);
        return new View_documents_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_documents_adapter.MyViewHolder holder, int position) {
        ViewDocumentsModel viewDocumentsModel = itemsArrayList.get(position);
        try {
            holder.tv_client_name.setText(viewDocumentsModel.getUploaded_by());
            holder.tv_client_name_one.setText(viewDocumentsModel.getUploaded_by());
            holder.tv_doc_description.setText(viewDocumentsModel.getDescription());
            holder.tv_document_display_name.setText(viewDocumentsModel.getName());
            holder.tv_image_name.setText(viewDocumentsModel.getName());
            is_encrypted = viewDocumentsModel.isIs_encrypted();
            if (is_encrypted || viewDocumentsModel.isAdded_encryption()) {
                holder.lock_close.setVisibility(View.VISIBLE);
                holder.lock_open.setVisibility(View.GONE);
            } else {
                holder.lock_open.setVisibility(View.VISIBLE);
                holder.lock_close.setVisibility(View.GONE);
            }
            Log.d("IMage_name", viewDocumentsModel.getName());
            holder.tv_created_date.setText(viewDocumentsModel.getCreated());
            holder.tv_Expiration_date.setText(viewDocumentsModel.getExpiration_date());
            holder.tv_client_name.setPaintFlags(holder.tv_client_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.tv_doc_description.setPaintFlags(holder.tv_doc_description.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.lock_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventlistner.encryption(viewDocumentsModel);
                }
            });
            holder.lock_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventlistner.decryption(viewDocumentsModel);
                }
            });
            holder.iv_edit_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventlistner.edit_document(viewDocumentsModel);
                }
            });
            holder.uv_delete_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventlistner.delete_document(viewDocumentsModel);
                }
            });
//            holder.iv_view_document.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    eventlistner.Display_Document(viewDocumentsModel);
//                }
//            });
            holder.cv_view_documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventlistner.Display_Document(viewDocumentsModel);
                }
            });
//        if (viewDocumentsModel.getContent_type().equals("image/jpeg")) {
//
//            Glide.with(cContext)
//                    .load(url)
//                    .placeholder(R.drawable.progress_animation)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.iv_preview);
//
//
//        } else {
//
//            Glide.with(cContext)
//                    .load(url)
//                    .placeholder(R.drawable.pdf_icons_48)
//                    .centerCrop()
//                    .fitCenter()
//                    .into(holder.iv_preview);
////                Picasso.with(view.getContext()).load(url).placeholder(R.drawable.pdf_icons_48).centerCrop().fit().into(holder.iv_preview, getCallBack(holder.iv_preview));
//        }
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), cContext);
        }
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView siv_profile_icon;
        ImageView iv_doc_image, iv_edit_document, uv_delete_document, iv_view_document, lock_open, lock_close;
        CardView cv_view_documents;
        TextView tv_document_display_name, tv_Expiration, tv_client_name_one, tv_image_name, tv_Expiration_date, tv_client_name, tv_doc_description, tv_created_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //  siv_profile_icon = itemView.findViewById(R.id.iv_display_picture);
            iv_doc_image = itemView.findViewById(R.id.iv_doc_image);
            lock_open = itemView.findViewById(R.id.lock_open);
            lock_close = itemView.findViewById(R.id.lock_close);
            cv_view_documents = itemView.findViewById(R.id.cv_view_documents);
            uv_delete_document = itemView.findViewById(R.id.uv_delete_document);
            iv_edit_document = itemView.findViewById(R.id.iv_edit_document);
            tv_document_display_name = itemView.findViewById(R.id.tv_document_display_name);
            tv_document_display_name.setMaxLines(1);
            tv_document_display_name.setEllipsize(TextUtils.TruncateAt.END);
            tv_client_name_one = itemView.findViewById(R.id.tv_client_name_one);
            tv_client_name_one.setTextSize(15);
            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_image_name.setMaxLines(1);
            tv_image_name.setEllipsize(TextUtils.TruncateAt.END);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_client_name.setTextSize(15);

            tv_doc_description = itemView.findViewById(R.id.tv_doc_description);
            tv_doc_description.setTextSize(15);
            tv_created_date = itemView.findViewById(R.id.tv_created_date);
            tv_created_date.setText(R.string.date);
            tv_Expiration = itemView.findViewById(R.id.tv_Expiration);
            tv_Expiration.setText(R.string.expiration_);
            tv_Expiration_date = itemView.findViewById(R.id.tv_Expiration_date);
            tv_Expiration_date.setText(R.string.expiration);
//            iv_view_document = itemView.findViewById(R.id.iv_edit_view);
        }
    }
}