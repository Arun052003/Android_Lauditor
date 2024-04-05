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

 class GridAdapter extends BaseAdapter {
    private Context mContext;
    private List<AttachmentModel> mAttachments;

    public GridAdapter(Context context, List<AttachmentModel> attachments) {
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
        View gridView = convertView;
        ViewHolder holder;

        if (gridView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            gridView = inflater.inflate(R.layout.attachment_item, parent, false);

            // Create a ViewHolder to store references to the views
            holder = new ViewHolder();
            holder.attachmentImage = gridView.findViewById(R.id.attachmentImage);
            holder.attachmentFilename = gridView.findViewById(R.id.attachmentFilename);


            // Set the ViewHolder as a tag for the convertView
            gridView.setTag(holder);

        }
        else {
            // If convertView is not null, retrieve the ViewHolder from its tag
            holder = (ViewHolder) convertView.getTag();
        }


        AttachmentModel attachment = mAttachments.get(position);


        holder.attachmentFilename.setText(attachment.getFilename());
        if (attachment.getFilename().toLowerCase().endsWith(".pdf") ||
                attachment.getFilename().toLowerCase().endsWith(".ics")) {

            holder.attachmentImage.setImageResource(R.drawable.pdf_icon2);
            holder.attachmentImage.setVisibility(View.VISIBLE);
        }
        else if (attachment.getFilename().toLowerCase().endsWith(".png") ||
                attachment.getFilename().toLowerCase().endsWith(".jpg") ||
                attachment.getFilename().toLowerCase().endsWith(".jpeg")) {

            holder.attachmentImage.setImageResource(R.drawable.attachment_icons);
            holder.attachmentImage.setVisibility(View.VISIBLE);
        }


        return gridView;
    }

    // ViewHolder pattern to improve ListView performance
    static class ViewHolder {
        ImageView attachmentImage;
        TextView attachmentFilename;
    }
}
