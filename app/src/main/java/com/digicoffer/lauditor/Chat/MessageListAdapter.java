package com.digicoffer.lauditor.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Chat.Model.MessageDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.Constants;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<MessageDo> mMessageList;
    public String dateTimeHeader = "";

    public MessageListAdapter(Context context, List<MessageDo> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        MessageDo message = (MessageDo) mMessageList.get(position);

        if (message.getViewType().equals(Constants.chat_SENT)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageDo message = (MessageDo) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, id_dateTimeHeader;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            id_dateTimeHeader = (TextView) itemView.findViewById(R.id.id_dateTimeHeader);
        }

        void bind(MessageDo message) {
            /*Date date = AndroidUtils.stringToDateTimeDefault(message.getCreatedAt(), "hh:mm:a,MMMdd");
            String list_item_date = AndroidUtils.getDateToString(date, "dd-MM-yyyy");
            if (!list_item_date.equals(dateTimeHeader)) {
                dateTimeHeader = list_item_date;
                id_dateTimeHeader.setText(list_item_date);
                id_dateTimeHeader.setVisibility(View.VISIBLE);
            } else {*/
//            Log.d("History of Message Sent:","From:"+message.getMessage());
            id_dateTimeHeader.setVisibility(View.GONE);
//            }
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getCreatedAt());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, id_dateTimeHeader;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            id_dateTimeHeader = (TextView) itemView.findViewById(R.id.id_dateTimeHeader);
        }

        void bind(MessageDo message) {
            messageText.setText(message.getMessage());
            /*Date date = AndroidUtils.stringToDateTimeDefault(message.getCreatedAt(), "hh:mm:a,MMMdd");
            String list_item_date = AndroidUtils.getDateToString(date, "dd-MM-yyyy");
            if (!list_item_date.equals(dateTimeHeader)) {
                dateTimeHeader = list_item_date;
                id_dateTimeHeader.setText(list_item_date);
                id_dateTimeHeader.setVisibility(View.VISIBLE);
            } else {*/
//            Log.d("History of Message Receive:","From:"+message.getMessage());
            id_dateTimeHeader.setVisibility(View.GONE);
//            }

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getCreatedAt());

            nameText.setText(message.getSender().getNickname());

            // Insert the profile image from the URL into the ImageView.
//            Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    public void addMessage(MessageDo message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }
}
