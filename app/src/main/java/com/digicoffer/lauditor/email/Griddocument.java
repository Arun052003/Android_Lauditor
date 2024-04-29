package com.digicoffer.lauditor.email;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;

class Griddocument extends BaseAdapter {
    private Context mContext;
    private JSONArray mAttachments;
    private String selectedDocumentName;

    public Griddocument(Context context, JSONArray attachmentsArray, String selectedDocumentName) {
        mContext = context;
        mAttachments = attachmentsArray;
        this.selectedDocumentName = selectedDocumentName;
    }

    @Override
    public int getCount() {
        return mAttachments != null ? mAttachments.length() : 0;
    }

    @Override
    public Object getItem(int position) {
        try {
            return mAttachments != null ? mAttachments.get(position) : null;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View gridView = convertView;

        if (gridView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            gridView = inflater.inflate(R.layout.attachment_item, parent, false);


            holder = new ViewHolder();
            holder.attachmentFilename = gridView.findViewById(R.id.attachmentFilename);

            holder.attachmentImage = gridView.findViewById(R.id.attachmentImage);

            gridView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


//        String name = selectedDocumentName;
        String name = "";

        for (int i = 0; i < Constants.model.length(); i++) {
            try {
                name = Constants.model.getString(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        holder.attachmentFilename.setText(name);
        if (name.toLowerCase().endsWith(".pdf") ||
                name.toLowerCase().endsWith(".ics")) {

            holder.attachmentImage.setImageResource(R.drawable.pdf_icon2);
            holder.attachmentImage.setVisibility(View.VISIBLE);
        } else if (name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg")) {

            holder.attachmentImage.setImageResource(R.drawable.attachment_icons);
            holder.attachmentImage.setVisibility(View.VISIBLE);
        }

        return gridView;
    }

    static class ViewHolder {
        TextView attachmentFilename;
        ImageView attachmentImage;
    }
}
