package com.digicoffer.lauditor.email;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;

import java.util.List;

public class emailadapter extends RecyclerView.Adapter<emailadapter.MyViewHolder> {

    private List<MessageModel> messages;
    static Email email;

    public emailadapter(List<MessageModel> messages) {
        this.messages= messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.email_view, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel email = messages.get(position);
        holder.tvEmail.setText(email.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email_get);
           // email.makeApiCall();

        }
    }
}

