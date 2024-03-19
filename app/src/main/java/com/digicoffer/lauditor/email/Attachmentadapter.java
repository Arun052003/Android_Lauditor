package com.digicoffer.lauditor.email;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digicoffer.lauditor.R;

import java.util.List;

public class Attachmentadapter extends BaseAdapter {
    private Context mContext;
    private List<AttachmentModel> mAttachments;

    public Attachmentadapter(Context context, List<AttachmentModel> attachments) {
        mContext = context;
        mAttachments = attachments;
    }

    @Override
    public int getCount() {
        return mAttachments.size();
    }

    @Override
    public Object getItem(int position) {
        return mAttachments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.attachment_item, null);
        } else {
            gridView = convertView;
        }

        ImageView imageView = gridView.findViewById(R.id.attachmentImage);
        TextView textView = gridView.findViewById(R.id.attachmentFilename);

        AttachmentModel attachment = mAttachments.get(position);

        textView.setText(attachment.getFilename());


         imageView.setImageResource(R.drawable.attachment_icons);

        return gridView;
    }

    public void setAttachments(List<AttachmentModel> attachments) {
        mAttachments = attachments;
        notifyDataSetChanged();
    }
}
