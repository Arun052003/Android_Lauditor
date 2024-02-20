package com.digicoffer.lauditor.Notifications;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Notifications.Models.NotificationsDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> implements Filterable {

    ArrayList<NotificationsDo> list_item;
    ArrayList<NotificationsDo> filtered_list;

    private EventListener context;

    public NotificationsAdapter(ArrayList<NotificationsDo> notificationList, EventListener mcontext) {
        this.list_item = notificationList;
        this.filtered_list = notificationList;
        this.context = mcontext;
    }


    public interface EventListener {
        void onEvent(String id);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view, parent, false);
        return new NotificationsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NotificationsAdapter.MyViewHolder holder, int i) {
        final NotificationsDo notifications = filtered_list.get(i);
        holder.chkSelected.setChecked(filtered_list.get(i).isSelected());
        holder.chkSelected.setTag(i);
        holder.chkSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.chkSelected.getTag();
                if (filtered_list.get(pos).isChecked()) {
                    filtered_list.get(pos).setChecked(false);
                } else {
                    filtered_list.get(pos).setChecked(true);
                }
            }

        });

        holder.tv_timestamp.setText(notifications.getTimestamp());
        holder.tv_message.setText(notifications.getMessage());
        if (notifications.getStatus().toLowerCase().equals("unread")) {
            holder.tv_message.setTypeface(null, Typeface.BOLD);
        } else
            holder.tv_message.setTypeface(null, Typeface.NORMAL);
        holder.ib_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onEvent(notifications.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filtered_list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_list = list_item;
                } else {
                    ArrayList<NotificationsDo> filteredList = new ArrayList<>();
                    for (NotificationsDo row : list_item) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getMessage()).toLowerCase().contains(charString.toLowerCase()) || AndroidUtils.isNull(row.getTimestamp()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }

                    filtered_list = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = filtered_list.size();
                filterResults.values = filtered_list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_list = (ArrayList<NotificationsDo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public void selectAllItems(boolean isSelected) {
        {
            for (int i = 0; i < list_item.size(); i++) {
//            if (list_item.get(i).isIsenabled())
//            if (list_item.get(i).isIsenabled()) {
                list_item.get(i).setChecked(isSelected);
//            }else {
//                list_item.get(i).setChecked(false);
//            }
            }
            notifyDataSetChanged();
        }
    }
    public ArrayList<NotificationsDo> getList_item() {
        return list_item;
    }

    public void selectOrDeselectAll(boolean isChecked) {
        for (NotificationsDo notification : filtered_list) {
            notification.setChecked(isChecked);
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_timestamp;
        private ImageButton ib_Delete;
        private TextView tv_message;
        public CheckBox chkSelected;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_timestamp = (TextView) itemView.findViewById(R.id.tv_timestampView);
            tv_message = (TextView) itemView.findViewById(R.id.tv_messageView);
            ib_Delete = (ImageButton) itemView.findViewById(R.id.btn_deleteNotification);
            chkSelected = (CheckBox) itemView.findViewById(R.id.chk_selected);


        }
    }
}
