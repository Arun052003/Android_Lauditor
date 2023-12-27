package com.digicoffer.lauditor.Matter.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Groups.GroupModels.ActionModel;
import com.digicoffer.lauditor.Matter.MatterDocuments;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.Matter.ViewMatter;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ViewMatterAdapter extends RecyclerView.Adapter<ViewMatterAdapter.MyViewHolder> implements Filterable {
    ArrayList<ViewMatterModel> itemsArrayList;
    ArrayList<ActionModel> actions_List = new ArrayList();
    ArrayList<ViewMatterModel> list_item;
    //  ArrayList<ActionModel> actions_List = new ArrayList<ActionModel>();
    Context context;
    InterfaceListener eventListener;
    ViewMatter viewmatter;
    String[] items = { "View Details", "Edit Matter Info", "Update Group(s)", "Delete", "Close Matter/ReOpen Matter"};

    ViewMatterModel new_view_model;

    public ViewMatterAdapter(ArrayList<ViewMatterModel> itemsArrayList, Context context, InterfaceListener eventListener) {
        this.itemsArrayList = itemsArrayList;
        this.list_item = itemsArrayList;
        this.context = context;

        this.eventListener = eventListener;

    }

    public ViewMatterAdapter(ArrayList<ViewMatterModel> matterList, Context context, MatterDocuments matterDocuments) {
    }

    @NonNull
    @Override
    public ViewMatterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_matter_recyclerview_design, parent, false);
        return new ViewMatterAdapter.MyViewHolder(itemView);
    }

    public interface InterfaceListener {

        void View_Details(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList);

        void DeleteMatter(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList);

        void Edit_Matter_Info(MatterModel viewMatterModel);

        void Update_Group(ViewMatterModel viewMatterModel);

        void Close_Matter(ViewMatterModel viewMatterModel);

        void ReopenMatter(ViewMatterModel viewMatterModel);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewMatterAdapter.MyViewHolder holder, int position) {
        ViewMatterModel viewMatterModel = itemsArrayList.get(position);
        //  MatterModel matterModel = itemsArrayList.get(position);
//        new_view_model = viewMatterModel;
//        itemsArrayList = list_item;

        try {
            JSONObject owner = viewMatterModel.getOwner();
            String owner_name = owner.getString("name");
            String owner_id = owner.getString("id");
            holder.tv_matter_title.setText(viewMatterModel.getTitle());
            holder.tv_case_number.setText(viewMatterModel.getCaseNumber());
            holder.tv_owner_name.setText(owner_name);
            actions_List.add(new ActionModel("View Details"));
            actions_List.add(new ActionModel("Edit Matter Info"));
            actions_List.add(new ActionModel("Update Group(s)"));
            actions_List.add(new ActionModel("Delete"));
            actions_List.add(new ActionModel("Close Matter"));
            actions_List.add(new ActionModel("Reopen Matter"));
            holder.tv_date_of_filling.setText(viewMatterModel.getDate_of_filling());

            if (viewMatterModel.getStatus().equals("Active")) {


                holder.tv_initiated.setText("Active");
                holder.iv_initiated.setImageDrawable(context.getResources().getDrawable(R.drawable.green_circular));
                actions_List.add(new ActionModel("Close Matter"));
                actions_List.remove(new ActionModel("Reopen Matter"));


            } else if (viewMatterModel.getStatus().equals("Closed")) {

                holder.tv_initiated.setText("Closed");
                holder.iv_initiated.setImageDrawable(context.getResources().getDrawable(R.drawable.circular));
//
                actions_List.add(new ActionModel("Reopen Matter"));
                actions_List.add(new ActionModel("Close Matter"));
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

            holder.custom_spinner_cardview.setOnClickListener(new View.OnClickListener() {
                boolean ischecked = true;

                @Override
                public void onClick(View v) {
                    if (ischecked)
                        holder.sp_action.setVisibility(View.VISIBLE);
                    else
                        holder.sp_action.setVisibility(View.GONE);
                    ischecked = !ischecked;
                }
            });

            holder.sp_action.findFocus();
            holder.sp_action.setVisibility(View.GONE);

//            holder.sp_action.setSelection(Spinner.INVALID_POSITION);
//            adapter.notifyDataSetChanged();
            holder.sp_action.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int name = position;
                    if (name == 0) {

                        eventListener.View_Details(viewMatterModel, itemsArrayList);
                    } else if (name == 1) {


                        eventListener.Edit_Matter_Info(viewMatterModel);

                    } else if (name == 2) {
                        eventListener.Update_Group(viewMatterModel);
                    } else if (name == 3) {
                        eventListener.DeleteMatter(viewMatterModel, itemsArrayList);
                    } else if (name == 4) {
                        eventListener.Close_Matter(viewMatterModel);
                    }


                    holder.sp_action.setVisibility(View.GONE);
                }
            });


//            if ()
//            holder.sp_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    try {
//                        String name = (String) adapterView.getItemAtPosition(i);
//
//                        if (name == "View Details") {
////                            AndroidUtils.showToast(new_view_model);
//                            eventListener.View_Details(viewMatterModel);
//                        } else if (name == "Delete") {
//
//                            eventListener.DeleteMatter(viewMatterModel, itemsArrayList);
//                        } else if (name == "Edit Matter Info")
//                            eventListener.Edit_Matter_Info(viewMatterModel, itemsArrayList);
//                        else if (name == "Update Group(s)") {
//                            eventListener.Update_Group(viewMatterModel);
//                        } else if (name == "Close/ReOpen Matter") {
//                            eventListener.Close_Matter(viewMatterModel);
//                        }
////                        else if (name.equals("Reopen Matter")){
////                            eventListener.ReopenMatter(viewMatterModel);
////                        }
////                        notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        AndroidUtils.showAlert(e.getMessage(), context);
//                    }
//                }
//
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//
//                }
//            });
////            notifyDataSetChanged();
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
        TextView tv_matter_title, tv_case_number, tv_date_of_filling, tv_client_name, tv_owner_name, tv_initiated,filed,textView,owner;
        ImageView iv_initiated;
        private ListView sp_action;
        TextView custom_spinner;
        CardView custom_spinner_cardview;
        LinearLayout action_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_matter_title = itemView.findViewById(R.id.tv_matter_title);
            tv_matter_title.setText("Matter Title");
            tv_matter_title.setTextColor(Color.BLACK);

            tv_case_number = itemView.findViewById(R.id.tv_case_number);
            tv_case_number.setText("Case Number");
            action_layout = itemView.findViewById(R.id.action_layout);
            custom_spinner = itemView.findViewById(R.id.custom_spinner);
            custom_spinner_cardview = itemView.findViewById(R.id.custom_spinner_cardview);

            textView = itemView.findViewById(R.id.textView);
            textView.setText("Client:");
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(12);

            owner = itemView.findViewById(R.id.owner);
            owner.setText("Owner:");
            owner.setTextColor(Color.BLACK);
            owner.setTextSize(12);


            tv_date_of_filling = itemView.findViewById(R.id.tv_date_of_filling);
            tv_date_of_filling.setText("Date of Filing");
            tv_date_of_filling.setTextSize(12);
            tv_date_of_filling.setTextColor(Color.BLACK);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_client_name.setText("client");
            tv_client_name.setTextSize(12);
            tv_client_name.setTextColor(Color.BLACK);
            tv_owner_name = itemView.findViewById(R.id.tv_owner_name);
            tv_owner_name.setText("owner name");
            tv_owner_name.setTextSize(12);
            tv_owner_name.setTextColor(Color.BLACK);
            tv_initiated = itemView.findViewById(R.id.tv_initiated);
            tv_initiated.setText("Pending");
            tv_initiated.setTextSize(20);
            filed = itemView.findViewById(R.id.filed);
            filed.setText("Filed:");
            filed.setTextColor(Color.BLACK);
            filed.setTextSize(12);


            iv_initiated = itemView.findViewById(R.id.iv_initiated);
            sp_action = itemView.findViewById(R.id.list_client);
        }
    }
}
