package com.digicoffer.lauditor.common_adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.digicoffer.lauditor.LoginActivity.FirmsDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common_objects.TimeZonesDO;
import com.digicoffer.lauditor.common_objects.UsersDO;


import java.util.ArrayList;

public class CommonSpinnerAdapter<Object> extends BaseAdapter {
    Activity context = null;
    ArrayList<Object> listArrayData = null;
    String master;
    Object listData;

    public CommonSpinnerAdapter(Activity context, ArrayList<Object> listArrayData) {
        this.context = context;
        this.listArrayData = listArrayData;
    }

    public View getView(int pos, View view1, ViewGroup arg2) {
        View view = view1;
        listData = listArrayData.get(pos);
        String data = "";

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.spinnerdropdownview, null, true);
        }
        TextView listTextView = (TextView) view.findViewById(R.id.spinnerDropDownTextview);
        if (listData instanceof String) {
            data = listData.toString();
        }
        if (listData instanceof UsersDO) {
            data = ((UsersDO) listData).getName();
        }
        if(listData instanceof FirmsDo) {
            data = ((FirmsDo) listData).getName();
        }
        if (listData instanceof TimeZonesDO){
            data = ((TimeZonesDO)listData).getNAME();
        }
        listTextView.setText(data);

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listArrayData.size();
    }

    @Override
    public Object getItem(int i) {
        // TODO Auto-generated method stub
        return listArrayData.get(i);
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
      return 0;
    }

}

