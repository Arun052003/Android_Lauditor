package com.digicoffer.lauditor.Chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Chat.Model.ChildDO;
import com.digicoffer.lauditor.Chat.Model.ClientRelationshipsDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> implements Filterable, View.OnClickListener, AsyncTaskCompleteListener, ChildAdapter.EventListener {
    ArrayList<ClientRelationshipsDo> list_item = new ArrayList<ClientRelationshipsDo>();
    ArrayList<ClientRelationshipsDo> filtered_list = new ArrayList<ClientRelationshipsDo>();
    private ChatAdapter.EventListener context;
    ArrayList<ChildDO> child_list = new ArrayList<>();
    private Context frag_context;
    LinearLayoutCompat ll_clients;
    MyViewHolder new_holder;
    int item_position = 0;
    private boolean isExpandable;
    ClientRelationshipsDo relationshipsDoRow;
    String User_Name = "";
    TextView tv_name_users;
    Activity activity;
    AlertDialog progress_dialog;


    public ChatAdapter(ArrayList<ClientRelationshipsDo> contactsList, EventListener mcontext, Context context, FragmentActivity activity) {
        this.list_item = contactsList;
        this.filtered_list = contactsList;
        this.context = mcontext;
        this.frag_context = context;
        this.activity = activity;
        isExpandable = false;

    }

    @Override
    public void Message(ChildDO childDO) {
        context.Message(childDO);
    }

    public interface EventListener {
        void  Message(ChildDO childDO);
        void view_users(String uid, String name) throws JSONException;


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        Log.d("SERVICECALLSTATUS", httpResult.getResult().toString());
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType() == "USERS_CHAT_LIST") {
                    if (!result.getBoolean("error")) {
                        JSONObject jsonObj = result.getJSONObject("data");
                        Log.d("JSONobj", relationshipsDoRow.getClientType().toString());
                        if (relationshipsDoRow.getClientType().equals("Entity")) {
////                        move_message_fragment(jsonObj);
                            User_Name = relationshipsDoRow.getName();
                            ViewUserList(jsonObj, User_Name);
                        } else {
                            move_message_consumer_fragment(jsonObj);
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void move_message_consumer_fragment(JSONObject jsonObj) throws JSONException{
        String uid = jsonObj.getString("uid");
        context.view_users(uid,relationshipsDoRow.getName());
    }

    private void ViewUserList(JSONObject jsonObj, String user_name) throws JSONException {

        try {
            String uid = jsonObj.getString("uid");
            JSONArray user = jsonObj.getJSONArray("users");

            ChildDO childDO = new ChildDO();
            childDO.setGuid(relationshipsDoRow.getGuid());
            childDO.setName(relationshipsDoRow.getName()+" "+"(Admin)");
            childDO.setUid(uid);
            Log.d("Child_Position", String.valueOf(relationshipsDoRow.getGuid())+relationshipsDoRow.getName());
            child_list.add(childDO);
                for (int i = 0; i < user.length(); i++) {
                    ChildDO childDO1 = new ChildDO();
                    JSONObject jsonObject = user.getJSONObject(i);
                    childDO1.setGuid(jsonObject.getString("guid"));
                    childDO1.setName(jsonObject.getString("name"));
                    childDO1.setId(jsonObject.getString("id"));
                    childDO1.setUid(uid);
                    child_list.add(childDO1);
                }


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(frag_context, LinearLayoutManager.VERTICAL, false);
            new_holder.rv_users.setLayoutManager(layoutManager);
            new_holder.rv_users.setHasFixedSize(true);
            ChildAdapter childRecyclerViewAdapter = new ChildAdapter(child_list, new_holder.rv_users.getContext(), this,activity);
            new_holder.rv_users.setAdapter(childRecyclerViewAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ChatHistoryTask extends AsyncTask<String, String, String> {
        String XMPP_DOMAIN = "https://" + Constants.XMPP_DOMAIN + "/";
        String url = "";
        TextView tv_count;

        ChatHistoryTask(String currentJID, String JID, TextView tv_count) {
            super();

            this.url = XMPP_DOMAIN + "unread/" + currentJID + File.separator + JID;
            this.tv_count = tv_count;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress_dialog = AndroidUtils.get_progress(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            data = requestUnreadCount(url);
            return data;
        }

        protected void onPostExecute(String response) {
//            AndroidUtils.showAlert(response, getContext());
//            if (progress_dialog != null && progress_dialog.isShowing())
//                AndroidUtils.dismiss_dialog(progress_dialog);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (!jsonResponse.getBoolean("error")) {
                    tv_count.setText((jsonResponse.getJSONObject("data")).getString("count"));
                }
            } catch (Exception e) {
                e.getMessage();
            }

        }
    }

    private String requestUnreadCount(String url) {
        String data = "";
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty("Authorization", "Bearer " + Constants.TOKEN);
            httpURLConnection.setRequestMethod("GET");
            int status_code = httpURLConnection.getResponseCode();
            if (status_code == 200) {
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } else {
                AndroidUtils.showAlert("Err connection, Please try again", frag_context);
            }
        } catch (Exception e) {
            AndroidUtils.logMsg(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_list = list_item;

                } else {
                    ArrayList<ClientRelationshipsDo> filteredList = new ArrayList<>();
                    for (ClientRelationshipsDo row : list_item) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getName()).toLowerCase().contains(charString.toLowerCase()) || AndroidUtils.isNull(row.getClientType()).toLowerCase().contains(charString.toLowerCase()) || AndroidUtils.isNull(row.getCreated()).toLowerCase().contains(charString.toLowerCase()) || AndroidUtils.isNull(row.getConsent()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtered_list = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence cha, FilterResults filterResults) {
                filtered_list = (ArrayList<ClientRelationshipsDo>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_list, parent, false);
        return new ChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ClientRelationshipsDo clientRelationshipsDo = filtered_list.get(position);
        clientRelationshipsDo.setRecyclerview_position(position);
        filtered_list.set(position, clientRelationshipsDo);
        boolean isExpandable = clientRelationshipsDo.isExpanded();
//                for (int i=0;i<filtered_list.size();i++){
//                    ClientRelationshipsDo clientRelationshipsDo1 = filtered_list.get(i);
        Log.d("Position", String.valueOf(clientRelationshipsDo.getRecyclerview_position()) + String.valueOf(clientRelationshipsDo.getName()));
//                }
        new_holder = holder;
        holder.tv_name.setText(clientRelationshipsDo.getName());
        if (clientRelationshipsDo.getClientType().equalsIgnoreCase("Consumer")) {
            holder.plus_icon.setVisibility(View.GONE);
        } else {
            holder.plus_icon.setVisibility(View.VISIBLE);
        }
        holder.ll_users.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable) {
            holder.plus_icon.setImageResource(R.drawable.minus_icon);

        } else {
            holder.plus_icon.setImageResource(R.drawable.plus_icon);
        }
        holder.plus_icon.setOnClickListener(v -> {
            child_list.clear();
//            if (child_list.size() == 0) {
//
//                holder.ll_users.setVisibility(View.GONE);
//
//
//            } else {
//                holder.ll_users.setVisibility(View.VISIBLE);
//            }

            //            item_position = position;
            if (clientRelationshipsDo.isExpanded()) {
                // If already expanded, remove the sub-items and update the button icon
                clientRelationshipsDo.setExpanded(false);

//                holder.plus_icon.setImageResource(R.drawable.plus_icon);

            } else {
                // If not expanded, add the sub-items and update the button icon
                clientRelationshipsDo.setExpanded(true);
//                holder.plus_icon.setImageResource(R.drawable.minus_icon);
                relationshipsDoRow = clientRelationshipsDo;
                callChatUsersListWebservice(clientRelationshipsDo.getId(), User_Name);

            }
//
//            clientRelationshipsDo.setExpanded(!clientRelationshipsDo.isExpanded());

            notifyItemChanged(holder.getAdapterPosition());

//            notifyDataSetChanged();
        });
        if (clientRelationshipsDo.getClientType().equalsIgnoreCase("Consumer")){
            holder.ll_client_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        context.view_users(clientRelationshipsDo.getGuid(), clientRelationshipsDo.getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void callChatUsersListWebservice(String id, String User_Name) {
        progress_dialog = AndroidUtils.get_progress(activity);
        JSONObject postData = new JSONObject();
        try {
            Log.d("Client_id", id.toString());
            WebServiceHelper.callHttpWebService(this, frag_context, WebServiceHelper.RestMethodType.GET, "relationship/" + id + "/users/notify", "USERS_CHAT_LIST", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    @Override
    public int getItemCount() {
//        notifyDataSetChanged();
        return filtered_list.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView plus_icon;
        LinearLayoutCompat ll_users;
        LinearLayout ll_client_card;
        RecyclerView rv_users;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            plus_icon = itemView.findViewById(R.id.plus_icon);
            ll_users = itemView.findViewById(R.id.ll_users);
            rv_users = itemView.findViewById(R.id.rv_users);
            ll_client_card = itemView.findViewById(R.id.ll_client_card);
        }
    }
}
