package com.digicoffer.lauditor.Documents.DocumentsListAdpater;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.models.ViewDocumentsModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.pgpainless.key.selection.key.util.And;

import java.util.ArrayList;

public class View_documents_adapter extends RecyclerView.Adapter<View_documents_adapter.MyViewHolder> {
    ArrayList<ViewDocumentsModel> docsList = new ArrayList<>();
    View_documents_adapter.Eventlistner eventlistner;
    Context cContext;
    public View_documents_adapter(ArrayList<ViewDocumentsModel> docsList, Eventlistner eventlistner,Context context) {
        this.docsList = docsList;
        this.eventlistner = eventlistner;
        this.cContext = context;
    }

    public interface Eventlistner {
        void edit_document(ViewDocumentsModel viewDocumentsModel);

        void delete_document(ViewDocumentsModel viewDocumentsModel);

        void Display_Document(ViewDocumentsModel viewDocumentsModel);
    }

    @NonNull
    @Override
    public View_documents_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_documents_list, parent, false);
        return new View_documents_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_documents_adapter.MyViewHolder holder, int position) {
        ViewDocumentsModel viewDocumentsModel = docsList.get(position);
        try {
        holder.tv_client_name.setText(viewDocumentsModel.getUploaded_by());
        holder.tv_client_name_one.setText(viewDocumentsModel.getUploaded_by());
        holder.tv_doc_description.setText(viewDocumentsModel.getDescription());
        holder.tv_document_display_name.setText(viewDocumentsModel.getName());
        holder.tv_image_name.setText(viewDocumentsModel.getName());
        holder.tv_created_date.setText(viewDocumentsModel.getCreated());
        holder.tv_Expiration_date.setText(viewDocumentsModel.getExpiration_date());
            holder.tv_client_name.setPaintFlags(holder.tv_client_name.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            holder.tv_doc_description.setPaintFlags(holder.tv_doc_description.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
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
            holder.iv_view_document.setOnClickListener(new View.OnClickListener() {
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
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(),cContext);
        }
    }

    @Override
    public int getItemCount() {
        return docsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.imageview.ShapeableImageView siv_profile_icon;
        ImageView iv_doc_image, iv_edit_document, uv_delete_document,iv_view_document;
        TextView tv_document_display_name, tv_client_name_one, tv_image_name,tv_Expiration_date, tv_client_name, tv_doc_description,tv_created_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            siv_profile_icon = itemView.findViewById(R.id.iv_display_picture);
            iv_doc_image = itemView.findViewById(R.id.iv_doc_image);
            uv_delete_document = itemView.findViewById(R.id.uv_delete_document);
            iv_edit_document = itemView.findViewById(R.id.iv_edit_document);
            tv_document_display_name = itemView.findViewById(R.id.tv_document_display_name);
            tv_client_name_one = itemView.findViewById(R.id.tv_client_name_one);
            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_doc_description = itemView.findViewById(R.id.tv_doc_description);
            tv_created_date = itemView.findViewById(R.id.tv_created_date);
            tv_Expiration_date = itemView.findViewById(R.id.tv_Expiration_date);
            iv_view_document = itemView.findViewById(R.id.iv_edit_view);
        }
    }
}
