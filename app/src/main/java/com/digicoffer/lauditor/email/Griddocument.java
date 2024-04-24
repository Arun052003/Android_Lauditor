package com.digicoffer.lauditor.email;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digicoffer.lauditor.R;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

class Griddocument extends BaseAdapter {
    private Context mContext;
    private JSONArray mAttachments;

    public Griddocument(Context context, JSONArray attachments) {
        mContext = context;
        mAttachments = attachments;
    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        try {
            return mAttachments.get(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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


        JSONArray attachment = null;
        try {
            attachment = (JSONArray) mAttachments.get(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

for(int i=0;i<attachment.length();i++) {
    try {
        holder.attachmentFilename.setText(attachment.getString(i));
    } catch (JSONException e) {
        throw new RuntimeException(e);
    }

}
        return gridView;
    }

    // ViewHolder pattern to improve ListView performance
    static class ViewHolder {
        ImageView attachmentImage;
        TextView attachmentFilename;
    }
}
