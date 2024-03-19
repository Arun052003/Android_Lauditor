package com.digicoffer.lauditor.email;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
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
    static Context  context_type;

    private List<MessageModel> messages;



    public EmailAdapter(List<MessageModel> messages,Email email) {
        this.context_type=email.getContext();
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
            case VIEW_TYPE_ATTACHMENT:
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

        TextView tvEmail;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            subject = itemView.findViewById(R.id.subject);
            // Set up the GridView
            gridView = itemView.findViewById(R.id.gridView);


            gridView.setNumColumns(2);
        }
        public void bindEmail(MessageModel email) {
            senderName.setText(email.getFrom());
            subject.setText(email.getSubject());

            GridAdapter adapter = new GridAdapter(context_type, email.attachments);
            gridView.setAdapter(adapter);
            gridView.setNumColumns(2);

        }

        private Context getActivity() {
            return getActivity();
        }
    }



}