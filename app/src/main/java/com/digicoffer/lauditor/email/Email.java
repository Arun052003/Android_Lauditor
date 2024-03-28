package com.digicoffer.lauditor.email;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.Documents.models.ViewDocumentsModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Email extends Fragment implements AsyncTaskCompleteListener {

    private static final int AUTH_REQUEST_CODE = 1001;

    private String URL = "";

    boolean ISCHECK_EMAIL = false;
    ArrayList<ViewDocumentsModel> view_docs_list = new ArrayList<>();
    boolean ISCHECK_AUTH = false;
    boolean ischecked = true;
    boolean ischeck_label, ischeck_auth;
    String nextPageToken = "";
    ImageView arrow_left;
    ImageView clear_search;
    TextView custom_client;
    private int currentPosition = 1;
    TextInputEditText et_Search;
    ArrayList<ClientsModel> clientsList = new ArrayList<>();

    public static AlertDialog progress_dialog;
    Stack<List<MessageModel>> pageStack = new Stack<>();

    boolean emailcheck;
    private List<MessageModel> messages = new ArrayList<>();
    private List<List<MessageModel>> totalMessageArray = new ArrayList<>();
    List<List<MessageModel>> nextMessages = new ArrayList<>();
    Integer pre_next_position = 0;
    private String requestType = null;
    HttpURLConnection httpURLConnection = null;
    TextView inbox_textViews;
    AppCompatButton first_button, search_email,sends_button;
    EditText to_input;
    ListView client_list_view;
    String client_id = "";



    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_layout, container, false);

        ImageView closeDocuments = view.findViewById(R.id.compose);

        ImageView arrow_right = view.findViewById(R.id.arrow_right);
        ImageView arrow_left = view.findViewById(R.id.arrow_left);
        arrow_left.setAlpha(0.3f);
        clear_search = view.findViewById(R.id.clear_search);
        clear_search.setVisibility(View.GONE);
        inbox_textViews = view.findViewById(R.id.inbox_textViews);
        first_button = view.findViewById(R.id.first_button);
        first_button.setAlpha(0.3f);
        et_Search = view.findViewById(R.id.et_Search);
        search_email = view.findViewById(R.id.search_email);
        search_email.setAlpha(0.3f);
        sends_button = view.findViewById(R.id.sends_button);
        ListView client_list_view = view.findViewById(R.id.client_list_view);

        closeDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openComposePopup();
            }
        });
        first_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!totalMessageArray.isEmpty()) {
                    pre_next_position = 0;
                    updateRecyclerView(totalMessageArray.get(pre_next_position));
                    arrow_left.setAlpha(0.3f);
                    first_button.setAlpha(0.3f);
                    arrow_right.setAlpha(1.0f);
                }
            }
        });



        arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (isMorePagesAvailable()) {

                        pre_next_position++;
                        callMessageListnext();
                        arrow_left.setAlpha(1.0f);
                        first_button.setAlpha(1.0f);

                    } else {

                        Toast.makeText(getContext(), "No more pages available", Toast.LENGTH_SHORT).show();
                    }
                }

        });


        arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pre_next_position  > 0) {
                    pre_next_position--;
                    List<MessageModel> previousPageMessages = totalMessageArray.get(pre_next_position);
                    updateRecyclerView(previousPageMessages);
                } else {

                    Toast.makeText(getContext(), "You are already on the first page", Toast.LENGTH_SHORT).show();
                    first_button.setAlpha(0.3f);
                    arrow_left.setAlpha(0.3f);
                }
            }
        });



//        emaiAPI();
        callLabel();


        return view;
    }
    private boolean isMorePagesAvailable() {

        return nextPageToken != null && !nextPageToken.isEmpty();
    }



    private void openComposePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.compose, null);

        ImageView attachmentImageView = view.findViewById(R.id.attachments);
        ImageView cross_icon = view.findViewById(R.id.attachment);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        attachmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder attachmentDialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater attachmentInflater = getActivity().getLayoutInflater();
                View attachmentView = attachmentInflater.inflate(R.layout.attach_document, null);

                TextView custom_client = attachmentView.findViewById(R.id.custom_client);
                ScrollView list_scroll_view = attachmentView.findViewById(R.id.list_scroll_view);
                ListView client_list_view = attachmentView.findViewById(R.id.client_list_view);
//                initUI(client_list_view, clientsList);
                custom_client.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callClientWebservice();
                        if (ischecked) {
                            list_scroll_view.setVisibility(View.VISIBLE);
                        } else {
                            list_scroll_view.setVisibility(View.VISIBLE);
                        }
                        ischecked = !ischecked;
                    }
                });
                // Initialize custom_client here

                attachmentDialogBuilder.setView(attachmentView);
                AlertDialog attachmentDialog = attachmentDialogBuilder.create();
                attachmentDialog.show();


            }
        });


        cross_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the AlertDialog
                dialog.dismiss();
            }
        });

    }



    private void load_view_doc(JSONArray docs) throws JSONException {
        try {
            view_docs_list.clear();
            for (int i = 0; i < docs.length(); i++) {
                ViewDocumentsModel viewDocumentsModel = new ViewDocumentsModel();
                JSONObject jsonObject = docs.getJSONObject(i);
                viewDocumentsModel.setCreated(jsonObject.getString("created"));
                viewDocumentsModel.setContent_type(jsonObject.getString("content_type"));
                viewDocumentsModel.setDescription(jsonObject.getString("description"));
                viewDocumentsModel.setExpiration_date(jsonObject.getString("expiration_date"));
                viewDocumentsModel.setFilename(jsonObject.getString("filename"));
                viewDocumentsModel.setId(jsonObject.getString("id"));
                viewDocumentsModel.setIs_disabled(jsonObject.getBoolean("is_disabled"));
                viewDocumentsModel.setIs_encrypted(jsonObject.getBoolean("is_encrypted"));
                viewDocumentsModel.setIs_password(jsonObject.getBoolean("is_password"));
                viewDocumentsModel.setName(jsonObject.getString("name"));
//                viewDocumentsModel.setOrigin(jsonObject.getString("origin"));
                viewDocumentsModel.setUploaded_by(jsonObject.getString("uploaded_by"));
                view_docs_list.add(viewDocumentsModel);
                Log.d("VIEW_POSITION", view_docs_list.get(i).toString());
            }
           // loadViewDocumentsRecyclerview();
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }














    private void loadRowDatas(JSONObject jsonObject) throws JSONException {

        JSONArray messagesArray = jsonObject.getJSONArray("messages");
        List<MessageModel> messages = new ArrayList<>();
        for (int i = 0; i < messagesArray.length(); i++) {
            JSONObject messageObject = messagesArray.getJSONObject(i);
            MessageModel message = new MessageModel();
            message.setMsgId(messageObject.getString("msgId"));
            message.setSubject(messageObject.getString("subject"));
            message.setFrom(messageObject.getString("from"));
            message.setTo(messageObject.getString("to"));

            JSONArray attachmentsArray = messageObject.getJSONArray("attachments");
            List<AttachmentModel> attachments = new ArrayList<>();
            for (int j = 0; j < attachmentsArray.length(); j++) {
                JSONObject attachmentObject = attachmentsArray.getJSONObject(j);
                AttachmentModel attachment = new AttachmentModel();
                if (!attachmentObject.getString("filename").isEmpty() && !attachmentObject.getString("partId").isEmpty()) {
                    attachment.setPartId(attachmentObject.getString("partId"));
                    attachment.setMimeType(attachmentObject.getString("mimeType"));
                    attachment.setFilename(attachmentObject.getString("filename"));


                    JSONArray headersArray = attachmentObject.getJSONArray("headers");
                    List<Header> headers = new ArrayList<>();
                    for (int k = 0; k < headersArray.length(); k++) {
                        JSONObject headerObject = headersArray.getJSONObject(k);
                        Header header = new Header();
                        header.setName(headerObject.getString("name"));
                        header.setValue(headerObject.getString("value"));
                        headers.add(header);
                    }
                    attachment.setHeaders(headers);


                    JSONObject bodyObject = attachmentObject.getJSONObject("body");
                    Body body = new Body();
                    body.setSize(bodyObject.getInt("size"));
                    attachment.setBody(body);

                    attachments.add(attachment);
                    message.setAttachments(attachments);
                }

            }
            messages.add(message);
        }
        totalMessageArray.add(pre_next_position, messages);
        // Access other fields like nextPageToken and resultSizeEstimation
        nextPageToken = jsonObject.optString("nextPageToken");
        Log.d("Nextpagetoken", nextPageToken);
        int resultSizeEstimate = jsonObject.optInt("resultSizeEstimate");
        if (messages.size() > 0) {
            updateRecyclerView(messages);


        }

    }


    @Override
    public void onClick(View view) {

    }

    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing()) {
            AndroidUtils.dismiss_dialog(progress_dialog);
        }
        String success = String.valueOf(httpResult.getResult());
        Log.d("Succ", success);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());

                if (httpResult.getRequestType().equals("Label")) {
                    JSONObject data = result.getJSONObject("data");
                    if (data.length() > 0) {
                        loadLabelData(data);
                        ISCHECK_EMAIL = true;
                        callMessageList();
                    }
                } else if (httpResult.getRequestType().equals("messages_rows")) {
                    // Parse the response for messages
                    loadRowDatas(result);


                }   else if (httpResult.getRequestType().equals("Clients List")) {
                    JSONObject data = result.getJSONObject("data");
                    try {
                        loadClients(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (httpResult.getRequestType().equals("auth")) {
                    String url = result.getString("url");
                    Log.d("Value_token", url);
                    ISCHECK_AUTH = true;
                    emaiAPI();
                    launchAuthUrl(url);
                } else {

                    System.out.println("Failed to obtain authentication URL");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (httpResult.getRequestType().equals("Label") || (httpResult.getRequestType().equals("auth"))) {
                emaiAPI();
            }


        }
    }

    private void loadLabelData(JSONObject data) {
        try {
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject jsonObject = data.getJSONObject(String.valueOf(i));
            EmailModel emailModel = new EmailModel();
            emailModel.setId(data.getString("id"));
            emailModel.setName(data.getString("name"));
            emailModel.setType(data.getString("type"));
            emailModel.setMessagesTotal(data.getInt("messagesTotal"));
            emailModel.setMessagesUnread(data.getInt("messagesUnread"));
            emailModel.setThreadsTotal(data.getInt("threadsTotal"));
            emailModel.setThreadsUnread(data.getInt("threadsUnread"));
            Log.e("Email Inbox", String.valueOf(emailModel.getMessagesTotal()));
            inbox_textViews.setText("Indox " + String.valueOf(emailModel.getMessagesTotal()));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void launchAuthUrl(String authUrl) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(authUrl));
        startActivityForResult(intent, AUTH_REQUEST_CODE);
        ischeck_auth = true;
    }


    public void emaiauth() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("authtoken",Constants.TOKEN);
            WebServiceHelper.callHttpWebService(this,
                    getContext(),
                    WebServiceHelper.RestMethodType.GET,
                    Constants.EMAIL_BASE_URL + "gmail/authurl?authtoken=" + Constants.TOKEN,
                    "auth",
                    jsonObject.toString());
            Log.d("C12Token", Constants.EMAIL_BASE_URL + "gmail/authurl?authtoken=" + Constants.TOKEN);
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();
        }
    }

    public void callMessageList() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, Constants.EMAIL_BASE_URL + Constants.gmail_messages + Constants.TOKEN + "?rows=10", "messages_rows", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();

            Log.e("API Error", "Failed to call API: " + e.getMessage());
        }
    }
    private void loadClients(JSONObject data) throws JSONException {
        JSONArray relationships = data.getJSONArray("relationships");
        //Adding a list first value as empty...
//        clientsList.add(0, new ClientsModel());
        for (int i = 0; i < relationships.length(); i++) {
            JSONObject jsonObject = relationships.getJSONObject(i);
            ClientsModel clientsModel = new ClientsModel();
            clientsModel.setId(jsonObject.getString("id"));
            clientsModel.setName(jsonObject.getString("name"));
            clientsModel.setType(jsonObject.getString("type"));
            clientsList.add(clientsModel);
//                    updatedClients.add(clientsModel);
        }
        initUI( clientsList);
        // intUI(clientsList);
    }
    private void initUI( ArrayList<ClientsModel> clientsList) {


        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), clientsList);
       client_list_view.setAdapter(adapter);

       client_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client_id = clientsList.get(position).getId();
                String client_name = clientsList.get(position).getName();
                Log.d("Client_value_name", client_name);
                custom_client.setText(client_name);
                ischecked = true;
            }
        });
    }
    public void callMessageListnext() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, Constants.EMAIL_BASE_URL + Constants.gmail_messages + Constants.TOKEN + "?rows=10&nextpagetoken=" + nextPageToken, "messages_rows", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();

            Log.e("API Error", "Failed to call API: " + e.getMessage());
        }
    }


    public void callLabel() {
        try {

            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, Constants.EMAIL_BASE_URL + Constants.gmail_label + Constants.TOKEN + "?labelid=INBOX", "Label", jsonObject.toString());
            Log.d("Label_value", Constants.EMAIL_BASE_URL + "gmail/label/" + Constants.TOKEN + "?labelid=INBOX");

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();

        }
    }


    public void emaiAPI() {

        if (!ISCHECK_AUTH) {
            emaiauth();
        } else {

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Do something here
                    if (!ISCHECK_EMAIL) {
                        callLabel();
                    }

                }
            }, 10000);
        }

    }
    private void callClientWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/client/all/list", "Clients List", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }


    private void updateRecyclerView(List<MessageModel> messages) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        EmailAdapter adapter = new EmailAdapter(messages, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getFilter().filter(et_Search.getText().toString());
            }
        });
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    clear_search.setVisibility(View.GONE);
                else
                    clear_search.setVisibility(View.VISIBLE);
                search_email.setAlpha(1.0f);
            }
        });
        clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_Search.setText("");
                search_email.setAlpha(0.3f);
                adapter.getFilter().filter(et_Search.getText().toString());
            }
        });
    }
}


