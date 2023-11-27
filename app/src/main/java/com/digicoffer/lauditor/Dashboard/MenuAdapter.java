package com.digicoffer.lauditor.Dashboard;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.ClientRelationships.Adapter.RelationshipsAdapter;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MenuModels;
import com.digicoffer.lauditor.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    ArrayList<MenuModels> filtered_list = new ArrayList<>();
    public MenuAdapter(ArrayList<MenuModels> menuList) {
        this.filtered_list = menuList;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_recyclerview, parent, false);
        return new MenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        MenuModels menuModels = filtered_list.get(position);
        holder.tv_item_name.setText(menuModels.getName());
        holder.fab_button.setImageResource(menuModels.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return filtered_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name;
        FloatingActionButton fab_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_item);
            fab_button = itemView.findViewById(R.id.fb_items);
        }
    }
}
