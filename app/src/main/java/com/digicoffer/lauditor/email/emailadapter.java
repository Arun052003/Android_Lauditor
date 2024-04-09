package com.digicoffer.lauditor.email;

import static android.app.PendingIntent.getActivity;

import static com.digicoffer.lauditor.email.Email.progress_dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.GroupsListAdapter;
import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.Documents.models.DocumentsModel;
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

    boolean ischecked = true;


    String client_id = "";
    String msg_id = " ";
    String baseUrl = Constants.EMAIL_UPLOAD_URL;
    String token = Constants.TOKEN;
    String msgId = Constants.msg_id;
    String partId = Constants.part_id;


    public EmailAdapter(List<MessageModel> messages, Email email) {
        this.context_type = email.getContext();
        this.messages = messages;
        this.itemsList = messages;


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

    }


    class EmailViewHolder extends RecyclerView.ViewHolder implements AsyncTaskCompleteListener {
        TextView senderName;
        TextView subject;
        GridView gridView;
        TextView tvEmail;
        RecyclerView rv_display_upload_groups_docs;
        ListView list_client;
        ScrollView list_scroll_client;
        LinearLayout ll_select_groups, ll_select_grp;
        Button btn_group_cancel;
        LinearLayout ll_client_name, upload_group_layout;
        boolean ischecked = true;
        private TextView custom_client;
        AppCompatButton btn_upload_new, btn_cancel_save;
        TextView tv_select_groups;
        LinearLayout linearLayout2;
        ArrayList<DocumentsModel> groupsList = new ArrayList<>();
        boolean ischecked_group = true;
        Button btn_group_submit;
        ArrayList<ClientsModel> clientsList = new ArrayList<>();
        ConstraintLayout constraint_root;

        boolean[] selectedLanguage;
        ArrayList<DocumentsModel> selected_groups_list = new ArrayList<>();

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            subject = itemView.findViewById(R.id.subject);
            gridView = itemView.findViewById(R.id.gridView);


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

            TextView group_name = popupView.findViewById(R.id.group_name);
            group_name.setText("Select Group");
            TextView client_namee = popupView.findViewById(R.id.client_nameee);
            client_namee.setText("Client");
            client_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource

            client_namee.setTextColor(ContextCompat.getColor(context_type, R.color.white));
            TextView firm_namee = popupView.findViewById(R.id.firm_nameee);
            firm_namee.setText("Firm");
            firm_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.white)); // Assuming "green" is the desired color resource

            firm_namee.setTextColor(ContextCompat.getColor(context_type, R.color.black));
            custom_client = popupView.findViewById(R.id.custom_client);
            ScrollView list_scroll_client = popupView.findViewById(R.id.list_scroll_client);
            list_client = popupView.findViewById(R.id.list_client_email);
            btn_upload_new = popupView.findViewById(R.id.btn_upload_new);
            tv_select_groups = popupView.findViewById(R.id.tv_select_groups);
            rv_display_upload_groups_docs = popupView.findViewById(R.id.rv_display_upload_groups_docs);

            btn_group_cancel = popupView.findViewById(R.id.btn_group_cancel);
            btn_cancel_save = popupView.findViewById(R.id.btn_cancel_save);
            btn_group_submit = popupView.findViewById(R.id.btn_group_submit);
            ll_select_groups = popupView.findViewById(R.id.ll_select_groups);
            ll_client_name = popupView.findViewById(R.id.ll_client_name);
            linearLayout2 = popupView.findViewById(R.id.linearLayout2);
            ll_select_grp = popupView.findViewById(R.id.ll_select_grp);
            // constraint_root = popupView.findViewById(R.id.constraint_root);
            TextView document_upload = popupView.findViewById(R.id.document_upload);

            btn_upload_new.setText("Upload");
            rv_display_upload_groups_docs.setBackground(context_type.getDrawable(R.drawable.rectangle_light_grey_bg));


            tv_client_name.setText("Client Name");
            builder.setView(popupView);
            AlertDialog dialog = builder.create();
            dialog.show();
            btn_upload_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callUploadDocument(baseUrl, token, msgId, partId);
                }
            });
            btn_cancel_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    constraint_root.setVisibility(View.GONE);
                    document_upload.setVisibility(View.GONE);

                }
            });


            client_namee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_select_groups.setVisibility(View.GONE);
                    ll_client_name.setVisibility(View.VISIBLE);
                    ll_select_grp.setVisibility(View.GONE);
                    // Set background color
                    client_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource
                    // Set text color
                    client_namee.setTextColor(ContextCompat.getColor(context_type, R.color.white));
                    firm_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.white)); // Assuming "green" is the desired color resource
                    // Set text color
                    firm_namee.setTextColor(ContextCompat.getColor(context_type, R.color.black));// Assuming "black" is the desired color resource

                }
            });

            firm_namee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_client_name.setVisibility(View.GONE);
                    ll_select_groups.setVisibility(View.VISIBLE);
                    ll_select_grp.setVisibility(View.VISIBLE);
                    firm_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource
                    // Set text color
                    firm_namee.setTextColor(ContextCompat.getColor(context_type, R.color.white));
                    client_namee.setBackgroundColor(ContextCompat.getColor(context_type, R.color.white)); // Assuming "green" is the desired color resource
                    // Set text color
                    client_namee.setTextColor(ContextCompat.getColor(context_type, R.color.black));

                }
            });
            custom_client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callClientWebservice();


                    if (ischecked)
                        list_scroll_client.setVisibility(View.VISIBLE);
                    else
                        list_scroll_client.setVisibility(View.GONE);

                    ischecked = !ischecked;
                }
            });
            tv_select_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ischecked_group) {
                        callGroupsWebservice();
                        rv_display_upload_groups_docs.setVisibility(View.VISIBLE);
                        linearLayout2.setVisibility(View.VISIBLE);
                    } else {
                        rv_display_upload_groups_docs.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.GONE);
                    }
                    ischecked_group = !ischecked_group;
                }
            });


        }

        public void callUploadDocument(String uploadUrl, String token, String msgId, String partId) {
            try {
                progress_dialog = AndroidUtils.get_progress((Activity) context_type);
                try {
                    JSONArray clients = new JSONArray();
                    for (ClientsModel clientsModel : clientsList) {
                        if (clientsModel.getId().equals(client_id)) {
                            JSONObject jsonObject_client = new JSONObject();
                            jsonObject_client.put("id", clientsModel.getId());
                            jsonObject_client.put("type", clientsModel.getType());
                            clients.put(jsonObject_client);
                        }
                    }

                    JSONArray groups = new JSONArray();
                    for (DocumentsModel documentsModel : selected_groups_list) {
                        String groupId = documentsModel.getGroup_id();
                        if (groupId != null && !groupId.isEmpty()) {
                            groups.put(groupId);
                        }
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("category", "client");
                    jsonObject.put("clientids", clients);
                    jsonObject.put("groupids", groups);
                    jsonObject.put("enableDownload", true);

                    Log.d("Generated JSON", jsonObject.toString());

                    String url = uploadUrl  + token + "/" + msgId + "?" + partId;

                    // Make the web service call with the dynamically generated URL
                    WebServiceHelper.callHttpWebService(this, context_type, WebServiceHelper.RestMethodType.POST, url, "uploaded file", jsonObject.toString());
                    Log.d("json_value", url);

                } catch (Exception e) {
                    Log.e("callUploadDocument", "Error occurred while constructing request or sending request: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("callUploadDocument", "Error occurred while executing callUploadDocument: " + e.getMessage());
                if (progress_dialog != null && progress_dialog.isShowing()) {
                    AndroidUtils.dismiss_dialog(progress_dialog);
                }
                e.printStackTrace();
            }
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

        private void callGroupsWebservice() {
            try {
                progress_dialog = AndroidUtils.get_progress((Activity) context_type);
                JSONObject jsonObject = new JSONObject();
                WebServiceHelper.callHttpWebService(this, context_type, WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", jsonObject.toString());
            } catch (Exception e) {
                if (progress_dialog != null && progress_dialog.isShowing()) {
                    AndroidUtils.dismiss_dialog(progress_dialog);
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

        private void loadGroupsData(JSONArray data) {
            try {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    DocumentsModel documentsModel = new DocumentsModel();
                    documentsModel.setGroup_id(jsonObject.getString("id"));
                    documentsModel.setGroup_name(jsonObject.getString("name"));
                    groupsList.add(documentsModel);
                }
                selectedLanguage = new boolean[groupsList.size()];
//            GroupsAlert();
                GroupsPopup();
            } catch (JSONException e) {
                e.printStackTrace();
                AndroidUtils.showAlert(e.getMessage(), context_type);
            }
        }

        @SuppressLint("MissingInflatedId")
        private void GroupsPopup() {
            try {
                for (int i = 0; i < groupsList.size(); i++) {
                    for (int j = 0; j < selected_groups_list.size(); j++) {
                        if (groupsList.get(i).getGroup_id().matches(selected_groups_list.get(j).getGroup_id())) {
                            DocumentsModel documentsModel = groupsList.get(i);
                            documentsModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                        }
                    }
                }
                selected_groups_list.clear();

                //Upload Documents Group Selection
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context_type, LinearLayoutManager.VERTICAL, false);
                rv_display_upload_groups_docs.setLayoutManager(layoutManager);
                rv_display_upload_groups_docs.setHasFixedSize(true);

                GroupsListAdapter documentsAdapter = new GroupsListAdapter(groupsList, Documents.class.newInstance());
                rv_display_upload_groups_docs.setAdapter(documentsAdapter);

                btn_group_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rv_display_upload_groups_docs.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.GONE);
                        ischecked_group = true;
                    }
                });

                btn_group_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                            DocumentsModel documentsModel = documentsAdapter.getList_item().get(i);
                            if (documentsModel.isGroupChecked()) {
                                selected_groups_list.add(documentsModel);
                            }
                        }
                        String[] value = new String[selected_groups_list.size()];
                        for (int i = 0; i < selected_groups_list.size(); i++) {
                            value[i] = selected_groups_list.get(i).getGroup_name();

                        }
                        String str = String.join(",", value);
                        tv_select_groups.setText(str);
//                    dialog.dismiss();
                        rv_display_upload_groups_docs.setVisibility(View.GONE);
                        linearLayout2.setVisibility(View.GONE);
                        ischecked_group = true;
                    }
                });

                //...View Documents Group Selection


            } catch (Exception e) {
                e.printStackTrace();
                AndroidUtils.showAlert(e.getMessage(), context_type);
            }
        }

        private void initUI(ArrayList<ClientsModel> clientsList) {
            CommonSpinnerAdapter adapter = new CommonSpinnerAdapter((Activity) context_type, clientsList);
            list_client.setAdapter(adapter);
            list_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    client_id = "";
                    client_id = clientsList.get(position).getId();
                    String client_name = clientsList.get(position).getName();
                    Log.d("Client_value_name", client_name);
                    custom_client.setText(client_name);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }

        public void onAsyncTaskComplete(HttpResultDo httpResult) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);

            if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
                try {
                    JSONObject result = new JSONObject(httpResult.getResponseContent());
                    String requestType = httpResult.getRequestType();

                    if ("Clients List".equals(requestType)) {
                        JSONObject data = result.getJSONObject("data");
                        try {
                            loadClients(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ("Groups".equals(requestType)) {
                        JSONArray data = result.getJSONArray("data");
                        loadGroupsData(data);

                    } else if (httpResult.getRequestType().equals("uploaded file")) {
                        String msg = result.getString("msg");
                        AndroidUtils.showToast(msg, context_type);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


        }
    }
}




