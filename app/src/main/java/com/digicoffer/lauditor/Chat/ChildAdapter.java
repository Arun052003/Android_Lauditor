package com.digicoffer.lauditor.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Chat.Model.ChildDO;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.MyViewHolder>{


    ArrayList<ChildDO> filteredList = new ArrayList<>();
    //    ChildAdapter.EventListener context;
    Context cContext;
    ChildDO childDo;
    EventListener context;
    String relationship_id;
    View view;
    Activity Mactivity;

    public ChildAdapter(ArrayList<ChildDO> child_list, Context context, EventListener mcontext, Activity activity) {
        this.filteredList = child_list;
        this.cContext = context;
        this.context = mcontext;
        this.Mactivity = activity;
    }
    public interface EventListener {
        void Message(ChildDO childDO);
//        void Copy(ChildDo childDo);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub,parent,false);
        return new ChildAdapter.MyViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.MyViewHolder holder, int position) {
        ChildDO childDO = filteredList.get(position);
        holder.tv_name.setText(childDO.getName());
        holder.ll_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Message",childDO.getName());
                context.Message(childDO);
            }
        });

    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        LinearLayoutCompat ll_users,ll_clients;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_users = itemView.findViewById(R.id.ll_clients);
            ll_clients = itemView.findViewById(R.id.ll_clients);
        }
    }
}
