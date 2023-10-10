package com.digicoffer.lauditor.common_adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.digicoffer.lauditor.Calendar.Models.CalendarDo;
import com.digicoffer.lauditor.Calendar.Models.MinutesDO;
import com.digicoffer.lauditor.Calendar.Models.RelationshipsDO;
import com.digicoffer.lauditor.Calendar.Models.TaskDo;
import com.digicoffer.lauditor.Calendar.Models.TeamDo;
import com.digicoffer.lauditor.ClientRelationships.Model.CountriesDO;
import com.digicoffer.lauditor.ClientRelationships.Model.EntityModel;
import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.Documents.models.MattersModel;
import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.LoginActivity.FirmsDo;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectTMModel;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectsModel;
import com.digicoffer.lauditor.TimeSheets.Models.StatusModel;
import com.digicoffer.lauditor.TimeSheets.Models.TSMatterModel;
import com.digicoffer.lauditor.TimeSheets.Models.TasksModel;
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
        if (listData instanceof ActionModel){
            data = ((ActionModel)listData).getName();
        }
        if (listData instanceof ViewGroupModel){
            data = ((ViewGroupModel)listData).getGroup_name();
        } if (listData instanceof CountriesDO) {
            data = ((CountriesDO) listData).getName();
        }if (listData instanceof EntityModel){
            data = ((EntityModel)listData).getEntityID();
        }if (listData instanceof MattersModel){
            data = ((MattersModel)listData).getTitle();
        }if (listData instanceof ClientsModel){
            data = ((ClientsModel)listData).getName();
        }if(listData instanceof TSMatterModel){
            data = ((TSMatterModel)listData).getMattername();
        }if (listData instanceof TasksModel){
            data = ((TasksModel)listData).getDisplayValue();
        }if(listData instanceof StatusModel){
            data = ((StatusModel)listData).getName();
        }if (listData instanceof ProjectsModel){
            data  = ((ProjectsModel)listData).getProjectName();
        }if (listData instanceof ProjectTMModel){
            data  = ((ProjectTMModel)listData).getName();
        }if(listData instanceof CalendarDo){
            data = ((CalendarDo)listData).getProjectName();
        }if(listData instanceof ViewMatterModel){
            data = ((ViewMatterModel)listData).getTitle();
        }if(listData instanceof TaskDo){
            data = ((TaskDo)listData).getTaskName();
        }if(listData instanceof TeamDo){
            data = ((TeamDo)listData).getName();
        }if(listData instanceof RelationshipsDO) {
            data = ((RelationshipsDO)listData).getName();
        }if (listData instanceof MinutesDO){
            data = ((MinutesDO)listData).getName();
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

