package com.digicoffer.lauditor.email;

import static android.app.PendingIntent.getActivity;

import static com.digicoffer.lauditor.email.Email.progress_dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.digicoffer.lauditor.email.Email;
import com.digicoffer.lauditor.email.GridAdapter;
import com.digicoffer.lauditor.email.MessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class EmailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AsyncTaskCompleteListener {
    private static final int VIEW_TYPE_EMAIL = 0;
    private static final int VIEW_TYPE_ATTACHMENT = 1;
    static Context context_type;

    private List<MessageModel> messages;
    TextView tv_client_name;

    List<MessageModel> itemsList = new ArrayList<>();
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    boolean ischecked = true;

    ListView list_client;
    String client_id = "";
    String msg_id = " ";
    private TextView custom_client;

    public EmailAdapter(List<MessageModel> messages, Email email) {
        this.context_type = email.getContext();
        this.messages = messages;
        this.itemsList = messages;
        this.custom_client = custom_client;


    }
    private void callClientWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress((Activity) context_type);
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, context_type, WebServiceHelper.RestMethodType.GET, "v3/client/all/list", "Clients List", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }
    public void callUploadDocument(String msg_id) {
        try {
            progress_dialog = AndroidUtils.get_progress((Activity) context_type);



            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService(this, context_type, WebServiceHelper.RestMethodType.POST, Constants.EMAIL_BASE_URL + Constants.gmail_document + Constants.TOKEN + "/" + msg_id + "?partid=1" , "uploadedfile", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isAttachment()) {
            return VIEW_TYPE_ATTACHMENT;
        } else {
            return VIEW_TYPE_EMAIL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_EMAIL:
                view = inflater.inflate(R.layout.email_card_view, parent, false);
                return new EmailViewHolder(view);
            case VIEW_TYPE_ATTACHMENT:
                view = inflater.inflate(R.layout.attachment_item, parent, false);
                return new EmailViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    messages = itemsList;
                } else {
                    ArrayList<MessageModel> filteredList = new ArrayList<>();
                    for (MessageModel row : itemsList) {
                        if (AndroidUtils.isNull(row.getFrom()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    messages = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = messages.size();
                filterResults.values = messages;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                messages = (ArrayList<MessageModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EMAIL:
                ((EmailViewHolder) holder).bindEmail(message);
                break;
            case VIEW_TYPE_ATTACHMENT:
                ((EmailViewHolder) holder).bindEmail(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("Clients List")) {
                    JSONObject data = result.getJSONObject("data");
                    try {
                        loadClients(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (httpResult.getRequestType().equals("uploadedfile")) {
                    try {
                        JSONObject responseJson = new JSONObject(httpResult.getResponseContent());
                        String message = responseJson.getString("msg");
                        // Handle the message here as needed
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadClients(JSONObject data) throws JSONException {
        JSONArray relationships = data.getJSONArray("relationships");
        for (int i = 0; i < relationships.length(); i++) {
            JSONObject jsonObject = relationships.getJSONObject(i);
            ClientsModel clientsModel = new ClientsModel();
            clientsModel.setId(jsonObject.getString("id"));
            clientsModel.setName(jsonObject.getString("name"));
            clientsModel.setType(jsonObject.getString("type"));
            clientsList.add(clientsModel);
        }
        initUI(clientsList);
    }

    private void initUI(ArrayList<ClientsModel> clientsList) {
        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter((Activity) context_type, this.clientsList);
        list_client.setAdapter(adapter);
        list_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client_id = clientsList.get(position).getId();
                String client_name = clientsList.get(position).getName();
                Log.d("Client_value_name", client_name);
                custom_client.setText(client_name);
            }
        });
    }

    class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView senderName;
        TextView subject;
        GridView gridView;
        TextView tvEmail;
        ListView list_client;
        ScrollView list_scroll_client;
        LinearLayout ll_select_groups;
        LinearLayout ll_client_name;
        boolean ischecked = true; // Ensure this variable is properly initialized

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            subject = itemView.findViewById(R.id.subject);
            gridView = itemView.findViewById(R.id.gridView);
            list_scroll_client = itemView.findViewById(R.id.list_scroll_client); // Ensure this line is correct
            list_client = itemView.findViewById(R.id.list_client_email);

            gridView.setNumColumns(2);
        }

        public void bindEmail(MessageModel email) {
            senderName.setText(email.getFrom());
            subject.setText(email.getSubject());
            GridAdapter adapter = new GridAdapter(context_type, email.attachments);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    GridviewPopup(itemView);
                }
            });

        }

        private void GridviewPopup(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View popupView = inflater.inflate(R.layout.document_upload, null);
            TextView tv_client_name = popupView.findViewById(R.id.client_name);
            TextView client_namee = popupView.findViewById(R.id.client_nameee);
            TextView firm_namee = popupView.findViewById(R.id.firm_nameee);
            TextView custom_client = popupView.findViewById(R.id.custom_client);
            ScrollView list_scroll_client  = popupView.findViewById(R.id.list_scroll_client);

            tv_client_name.setText("Client Name");
            builder.setView(popupView);
            AlertDialog dialog = builder.create();
            dialog.show();

            client_namee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_select_groups.setVisibility(View.GONE);
                    ll_client_name.setVisibility(View.VISIBLE);
                }
            });
            firm_namee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_client_name.setVisibility(View.GONE);
                    ll_select_groups.setVisibility(View.VISIBLE);
                }
            });
            custom_client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callClientWebservice();


                    if (ischecked) {
                        list_scroll_client.setVisibility(View.VISIBLE);
                    } else {
                        list_scroll_client.setVisibility(View.GONE);
                    }
                    ischecked = !ischecked;
                }
            });
        }

    }





}


