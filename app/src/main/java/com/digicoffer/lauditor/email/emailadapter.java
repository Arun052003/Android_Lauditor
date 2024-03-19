package com.digicoffer.lauditor.email;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;

import java.util.ArrayList;
import java.util.List;

class EmailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_EMAIL = 0;
    private static final int VIEW_TYPE_ATTACHMENT = 1 ;

    private List<MessageModel> messages;



    public EmailAdapter(List<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {

        if (messages.get(position).isAttachment()) {
            return VIEW_TYPE_ATTACHMENT;
        } else {
            return VIEW_TYPE_EMAIL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_EMAIL:
                view = inflater.inflate(R.layout.email_card_view, parent, false);
                return new EmailViewHolder(view);
            case VIEW_TYPE_ATTACHMENT:
                view = inflater.inflate(R.layout.attachment_item, parent, false);
                return new EmailViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EMAIL:
                ((EmailViewHolder) holder).bindEmail(message);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView senderName;
        TextView subject;
        GridView gridView;
        Attachmentadapter attachmentAdapter;
        TextView tvEmail;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            subject = itemView.findViewById(R.id.subject);
            // Set up the GridView
            gridView = itemView.findViewById(R.id.gridViewAttachments);
            //   attachmentAdapter =  List<AttachmentModel> attachments;
            gridView.setAdapter(attachmentAdapter);
            gridView.setNumColumns(2);
        }
        public void bindEmail(MessageModel email) {
            senderName.setText(email.getFrom());
            subject.setText(email.getSubject());

            gridView.setNumColumns(2);


            //   tvEmail.setText(email.getMessage());
        }
    }



}
