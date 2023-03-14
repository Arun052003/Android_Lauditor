package com.digicoffer.lauditor.Matter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ViewMatterAdapter extends RecyclerView.Adapter<ViewMatterAdapter.MyViewHolder> implements Filterable {
    ArrayList<ViewMatterModel> itemsArrayList;
    ArrayList<ViewMatterModel> list_item;
    ArrayList<ActionModel> actions_List = new ArrayList<ActionModel>();
    Context context;
    InterfaceListener eventListener;
    String[] items = {"Choose Actions", "View Details", "Edit Matter Info", "Update Group(s)", "Delete", "Close/ReOpen Matter"};

    ViewMatterModel new_view_model;

    public ViewMatterAdapter(ArrayList<ViewMatterModel> itemsArrayList, Context context, InterfaceListener eventListener) {
        this.itemsArrayList = itemsArrayList;
        this.list_item = itemsArrayList;
        this.context = context;
        this.eventListener = eventListener;

    }

    @NonNull
    @Override
    public ViewMatterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_matter_recyclerview_design, parent, false);
        return new ViewMatterAdapter.MyViewHolder(itemView);
    }

    public interface InterfaceListener {

        void View_Details(ViewMatterModel viewMatterModel);

        void DeleteMatter(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList);

        void Edit_Matter_Info(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList);

        void Update_Group(ViewMatterModel viewMatterModel);

        void Close_Matter(ViewMatterModel viewMatterModel);

        void ReopenMatter(ViewMatterModel viewMatterModel);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMatterAdapter.MyViewHolder holder, int position) {
        ViewMatterModel viewMatterModel = itemsArrayList.get(position);
//        new_view_model = viewMatterModel;
//        itemsArrayList = list_item;

        try {
            JSONObject owner = viewMatterModel.getOwner();
            String owner_name = owner.getString("name");
            String owner_id = owner.getString("id");
            holder.tv_matter_title.setText(viewMatterModel.getTitle());
            holder.tv_case_number.setText(viewMatterModel.getCaseNumber());
            holder.tv_owner_name.setText(owner_name);
            holder.tv_date_of_filling.setText(viewMatterModel.getDate_of_filling());
            if (viewMatterModel.getStatus().equals("Active")) {


                holder.tv_initiated.setText("Active");
                holder.iv_initiated.setImageDrawable(context.getResources().getDrawable(R.drawable.green_circular));

            } else if (viewMatterModel.getStatus().equals("Closed")) {

                holder.tv_initiated.setText("Closed");
                holder.iv_initiated.setImageDrawable(context.getResources().getDrawable(R.drawable.circular));
//
                actions_List.add(new ActionModel("Reopen Matter"));
            } else {


                String[] newItems = new String[items.length - 1];
                for (int i = 0, j = 0; i < items.length; i++) {
                    if (!items[i].equals("Close/ReOpen Matter")) {
                        newItems[j++] = items[i];
                    }
                }
                items = newItems;
//                for (int i = 0; i < items.length(); i++) {
//                    if (items.get(i).equals("Item 5")) {
//                        items.remove(i);
//                    }
//                }

//                spinner_adapter.
                holder.tv_initiated.setText("Pending");
                holder.iv_initiated.setImageDrawable(context.getResources().getDrawable(R.drawable.red_circular));
//               adapter.remove("");
//                actions_List.add(new ActionModel("Reopen Matter"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.sp_action.setAdapter(adapter);
//            holder.sp_action.setSelection(Spinner.INVALID_POSITION);
//            adapter.notifyDataSetChanged();

//            if ()
            holder.sp_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        String name = (String) adapterView.getItemAtPosition(i);

                        if (name.equals("View Details")) {
//                            AndroidUtils.showToast(new_view_model);
                            eventListener.View_Details(viewMatterModel);
                        } else if (name.equals("Delete")) {
                            eventListener.DeleteMatter(viewMatterModel, itemsArrayList);
                        } else if (name.equals("Edit Matter Info")) {
                            eventListener.Edit_Matter_Info(viewMatterModel, itemsArrayList);
                        } else if (name.equals("Update Group(s)")) {
                            eventListener.Update_Group(viewMatterModel);
                        } else if (name.equals("Close/ReOpen Matter")) {
                            eventListener.Close_Matter(viewMatterModel);
                        }
//                        else if (name.equals("Reopen Matter")){
//                            eventListener.ReopenMatter(viewMatterModel);
//                        }
//                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), context);
                    }
                }


                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
//            notifyDataSetChanged();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), context);
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsArrayList = list_item;
                } else {
                    ArrayList<ViewMatterModel> filteredList = new ArrayList<>();
                    for (ViewMatterModel row : list_item) {
//                            if (row.isChecked()){
//                                row.setChecked(false);
//                            }else
//                            {
//                                row.setChecked(true  );
//                            }
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getTitle()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    itemsArrayList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = itemsArrayList.size();
                filterResults.values = itemsArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsArrayList = (ArrayList<ViewMatterModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_matter_title, tv_case_number, tv_date_of_filling, tv_client_name, tv_owner_name, tv_initiated;
        ImageView iv_initiated;
        Spinner sp_action;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_matter_title = itemView.findViewById(R.id.tv_matter_title);
            tv_case_number = itemView.findViewById(R.id.tv_case_number);
            tv_date_of_filling = itemView.findViewById(R.id.tv_date_of_filling);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_owner_name = itemView.findViewById(R.id.tv_owner_name);
            tv_initiated = itemView.findViewById(R.id.tv_initiated);
            iv_initiated = itemView.findViewById(R.id.iv_initiated);
            sp_action = itemView.findViewById(R.id.sp_action);
        }
    }
}
