package com.digicoffer.lauditor.email;

import static com.digicoffer.lauditor.email.EmailAdapter.context_type;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Documents.Documents;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.GroupsListAdapter;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.view_document_emailadapter;
import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.Documents.models.DocumentsModel;
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
    private static final int VIEW_TYPE_DOCUMENT= 0;
    private static final int VIEW_TYPE_ATTACHMENT = 1;

    boolean ISCHECK_EMAIL = false;
    int doc_count=0;
    boolean ischecked_group = true;
    boolean[] selectedLanguage;
    Button btn_group_cancel, btn_group_submit;
    AppCompatButton btn_create;
    LinearLayout upload_group_layout;
    TextInputEditText et_Search_email_document;
    TextView tv_select_groups;
    ArrayList<DocumentsModel> selected_groups_list = new ArrayList<DocumentsModel>();
    ArrayList<ViewDocumentsModel> view_docs_list = new ArrayList<>();
    boolean ISCHECK_AUTH = false;
    boolean ischecked = true;
    boolean ischeck_label, ischeck_auth;
    String nextPageToken = "";
    LinearLayout ll_attach_grp;

    ArrayList<DocumentsModel> groupsList = new ArrayList<>();
    ImageView arrow_left;
    ImageView clear_search;
    TextView custom_client;
    private int currentPosition = 1;
    TextInputEditText et_Search;
    RecyclerView rv_documents_email;
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
    AppCompatButton first_button, search_email, sends_button;
    EditText to_input;
    ListView client_list_view;
    TextView grp_name;
    String client_id = "";
    RecyclerView rv_display_upload_groups_docs;
    String CATEGORY_TAG = "";
    LinearLayout linearLayout2;
    JSONArray array_group = new JSONArray();
    GridView yourGridView;
    ImageView closeDocuments;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_layout, container, false);
        closeDocuments = view.findViewById(R.id.compose);
        closeDocuments.setAlpha(0.3f);

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
                if (pre_next_position > 0) {
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



    private void openComposePopup( ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.compose, null);
        ViewDocumentsModel viewDocumentsModel = new ViewDocumentsModel();
        ImageView attachmentImageView = view.findViewById(R.id.attachments);
        ImageView cross_icon = view.findViewById(R.id.attachment);
        to_input = view.findViewById(R.id.to_input);
        AppCompatButton sends_button = view.findViewById(R.id.sends_button);

       yourGridView = view.findViewById(R.id. gridView);
        builder.setView(view);
        AlertDialog dialog = builder.create();


        dialog.show();
        to_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail(to_input.getText().toString());
                }
            }
        });
        sends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_email();

            }
        });
        Griddocument adapter = new Griddocument(context_type, Constants.doc_id);
        yourGridView.setAdapter(adapter);


        attachmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder attachmentDialogBuilder = new AlertDialog.Builder(getContext());
                LayoutInflater attachmentInflater = getActivity().getLayoutInflater();
                View attachmentView = attachmentInflater.inflate(R.layout.attach_document, null);
                TextView client_name = attachmentView.findViewById(R.id.client_name);
                client_name.setText("Client Name");

                custom_client = attachmentView.findViewById(R.id.custom_client);
                TextView compose_client_name = attachmentView.findViewById(R.id.compose_client_name);
                compose_client_name.setText("Client");
                compose_client_name.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource

                compose_client_name.setTextColor(ContextCompat.getColor(context_type, R.color.white));

                TextView compose_firm_name = attachmentView.findViewById(R.id.compose_firm_name);
                compose_firm_name.setText("Firm");
                rv_documents_email = attachmentView.findViewById(R.id.rv_documents_email);
                ScrollView list_scroll_view = attachmentView.findViewById(R.id.list_scroll_view);
                client_list_view = attachmentView.findViewById(R.id.client_list_view);
                rv_display_upload_groups_docs = attachmentView.findViewById(R.id.rv_display_upload_groups_docs);
                btn_group_cancel = attachmentView.findViewById(R.id.btn_group_cancel);
                btn_group_cancel.setVisibility(View.GONE);
                upload_group_layout = attachmentView.findViewById(R.id.upload_group_layout);
                btn_group_submit = attachmentView.findViewById(R.id.btn_group_submit);
                btn_group_submit.setVisibility(View.GONE);
                tv_select_groups = attachmentView.findViewById(R.id.tv_select_groups);
                ll_attach_grp = attachmentView.findViewById(R.id.ll_attach_grp);
                grp_name = attachmentView.findViewById(R.id.grp_name);
                grp_name.setText("Select Group");
                et_Search_email_document = attachmentView.findViewById(R.id.et_Search_email_document);
                LinearLayout ll_select_groups = attachmentView.findViewById(R.id.ll_select_groups);
                LinearLayout ll_client_name = attachmentView.findViewById(R.id.ll_client_name);
                linearLayout2 = attachmentView.findViewById(R.id.linearLayout2);
                btn_create = attachmentView.findViewById(R.id.btn_create);
                btn_create.setText("Attach");
                btn_create.setAlpha(0.5f);

                compose_client_name.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SuspiciousIndentation")
                    @Override
                    public void onClick(View v) {
                        ll_select_groups.setVisibility(View.GONE);
                        ll_client_name.setVisibility(View.VISIBLE);
                        custom_client.setText("");
                        list_scroll_view.setVisibility(View.GONE);
                        rv_documents_email.clearFocus();
                        ll_attach_grp.setVisibility(View.GONE);
                        compose_client_name.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource

                        compose_client_name.setTextColor(ContextCompat.getColor(context_type, R.color.white));
                        compose_firm_name.setBackgroundColor(ContextCompat.getColor(context_type, R.color.white)); // Assuming "green" is the desired color resource

                        compose_firm_name.setTextColor(ContextCompat.getColor(context_type, R.color.black));

                    }
                });
                btn_create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String doc1_id;
                            doc1_id = Constants.doc_id.getString(0);
                            view_document(doc1_id);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });



                compose_firm_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CATEGORY_TAG = "firm";
                        ll_client_name.setVisibility(View.GONE);
                        ll_select_groups.setVisibility(View.VISIBLE);
                        view_docs_list.clear();
                        groupsList.clear();

                        ischecked_group = true;
                        selected_groups_list.clear();
                        tv_select_groups.setText("");
                        rv_documents_email.clearFocus();
                        ll_attach_grp.setVisibility(View.VISIBLE);
                        linearLayout2.setVisibility(View.GONE);

                        compose_firm_name.setBackgroundColor(ContextCompat.getColor(context_type, R.color.green_count_color)); // Assuming "green" is the desired color resource

                        compose_firm_name.setTextColor(ContextCompat.getColor(context_type, R.color.white));
                        compose_client_name.setBackgroundColor(ContextCompat.getColor(context_type, R.color.white)); // Assuming "green" is the desired color resource

                        compose_client_name.setTextColor(ContextCompat.getColor(context_type, R.color.black));
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


//                initUI(client_list_view, clientsList);
                custom_client.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SuspiciousIndentation")
                    @Override
                    public void onClick(View v) {
                        clientsList.clear();
                        callClientWebservice();
                        list_scroll_view.setVisibility(View.GONE);
                        if (ischecked)
                            list_scroll_view.setVisibility(View.VISIBLE);
                        else
                            list_scroll_view.setVisibility(View.GONE);

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_DOCUMENT:
                view = inflater.inflate(R.layout.grid_view, parent, false);
               openComposePopup();
            case VIEW_TYPE_ATTACHMENT:
                view = inflater.inflate(R.layout.attachment_item, parent, false);
              openComposePopup();
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }


    private void validateEmail(String email) {
        if (isValidEmail(email)) {
            AndroidUtils.showToast("Valid Email", getContext());

        } else {

            AndroidUtils.showToast("Invalid Email", getContext());
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void view_document(String doc_id) {
        try {
            doc_count++;
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            //https://api.staging.digicoffer.com/professional/v3/document/64ca0bd9fffd8f083fafaa22/view
            String baseUrl = "https://api.staging.digicoffer.com/professional/v3/document/";
            String url = baseUrl  + doc_id+ "/view";
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET,"v3/document/"+doc_id+"/view"  , "view_document", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    public void send_email() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, Constants.EMAIL_BASE_URL + Constants.sending_email + Constants.TOKEN, "sending_email", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }


    public void callfilter_client_webservices() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
//            JSONArray groups1 = new JSONArray();
            if (CATEGORY_TAG == "client") {
                jsonObject.put("category", "client");
                jsonObject.put("clients", client_id);
                jsonObject.put("showPdfDocs", false);
                jsonObject.put("groups", null);
                //When the matter is chosen by the user.....
//                if (!matter_id.equals("")) {
//                    jsonObject.put("matters", matter_id);
//                }
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display clientDocuments", jsonObject.toString());
                Log.d("Group_doc_view1", jsonObject.toString());
            } else if (CATEGORY_TAG == "firm") {
                jsonObject.put("category", "firm");
                jsonObject.put("clients", "");
                jsonObject.put("matters", "");
                jsonObject.put("showPdfDocs", false);
                jsonObject.put("groups", array_group);
                Log.d("Group_value_num", array_group.toString());

                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display firmDocuments", jsonObject.toString());
                Log.d("Group_doc_view1", jsonObject.toString());
            }
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
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
            loadViewDocumentsRecyclerview();
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadViewDocumentsRecyclerview() {
        try {
            if (view_docs_list.size() == 0) {
                rv_documents_email.removeAllViews();
                AndroidUtils.showToast("No documents to display", getContext());
                rv_documents_email.setVisibility(View.GONE);
            } else {
                rv_documents_email.setVisibility(View.VISIBLE);
                rv_documents_email.setLayoutManager(new GridLayoutManager(getContext(), 1));
                view_document_emailadapter adapter = new view_document_emailadapter(view_docs_list, context_type);
                rv_documents_email.setAdapter(adapter);
                rv_documents_email.setHasFixedSize(true);
                et_Search_email_document.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        adapter.getFilter().filter(et_Search_email_document.getText().toString());
                    }
                });


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    Log.e("Email_Res", "File-" + attachmentObject.getString("filename") + ": Partid-" + attachmentObject.getString("partId"));
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


                } else if (httpResult.getRequestType().equals("Display clientDocuments")) {
                    JSONArray data = result.getJSONArray("data");


                    load_view_doc(data);
                    Log.d("TAG_VIEW_CLIENT", data.toString());
                } else if (httpResult.getRequestType().equals("Display firmDocuments")) {
                    JSONArray data = result.getJSONArray("data");
                    load_view_doc(data);
                    Log.d("TAG_VIEW_CLIENT", data.toString());
                } else if (httpResult.getRequestType().equals("Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadGroupsData(data);
                } else if (httpResult.getRequestType().equals("Clients List")) {
                    JSONObject data = result.getJSONObject("data");
                    try {
                        loadClients(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (httpResult.getRequestType().equals("view_document")) {
                    JSONObject data = result.getJSONObject("data");
                    String url=data.getString("url");
                    Log.d("Doc_url",url);
                    if(!(doc_count==Constants.doc_id.length())) {
                        for (int i = 1; i < Constants.doc_id.length(); i++) {
                            try {
                                String doc1_id = Constants.doc_id.getString(i);
                                view_document(doc1_id);
                                Log.d("Const_doc", "count....."+doc_count+".....len.." + Constants.doc_id.length() + " .. " + doc1_id);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {
                        AndroidUtils.showAlert("Documents attached....");
                        openComposePopup();
                    }
                } else if (httpResult.getRequestType().equals("sending_email")) {
                    String msg = result.getString("msg");
                    Log.d("msg_details", msg);

                } else if (httpResult.getRequestType().equals("auth")) {
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

    private void callGroupsWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
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
            closeDocuments.setAlpha(1.0f);

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
        clientsList = new ArrayList<>();
        for (int i = 0; i < relationships.length(); i++) {
            JSONObject jsonObject = relationships.getJSONObject(i);
            ClientsModel clientsModel = new ClientsModel();
            clientsModel.setId(jsonObject.getString("id"));
            clientsModel.setName(jsonObject.getString("name"));
            clientsModel.setType(jsonObject.getString("type"));
            clientsList.add(clientsModel);
//                    updatedClients.add(clientsModel);
        }
        initUI(clientsList);
        // intUI(clientsList);
    }

    private void initUI(ArrayList<ClientsModel> clientsList) {


        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), clientsList);
        client_list_view.setAdapter(adapter);

        client_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client_id = clientsList.get(position).getId();
                String client_name = clientsList.get(position).getName();
                Log.d("Client_value_name", client_name);
                custom_client.setText(client_name);
                CATEGORY_TAG = "client";
                callfilter_client_webservices();
                btn_create.setAlpha(1.0f);
                ischecked = true;
            }
        });
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
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }


    private void GroupsPopup() {
        try {
            selected_groups_list.clear();

            // Upload Documents Group Selection
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context_type, LinearLayoutManager.VERTICAL, false);
            rv_display_upload_groups_docs.setLayoutManager(layoutManager);
            rv_display_upload_groups_docs.setHasFixedSize(true);

            GroupsListAdapter documentsAdapter = new GroupsListAdapter(groupsList, Documents.class.newInstance(), new GroupsListAdapter.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(DocumentsModel documentsModel) {
                    // Update selected groups list
                    if (documentsModel.isGroupChecked()) {
                        selected_groups_list.add(documentsModel);
                        callfilter_client_webservices();
                    }

                    // Update TextView with selected groups
                    String[] value = new String[selected_groups_list.size()];
                    for (int i = 0; i < selected_groups_list.size(); i++) {
                        value[i] = selected_groups_list.get(i).getGroup_name();
                    }
                    String str = TextUtils.join(",", value);
                    tv_select_groups.setText(str);
                }
            });

            rv_display_upload_groups_docs.setAdapter(documentsAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), context_type);
        }
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

                search_email.setAlpha(0.3f);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    clear_search.setVisibility(View.GONE);
                    search_email.setAlpha(0.3f);
                } else {
                    clear_search.setVisibility(View.VISIBLE);
                    search_email.setAlpha(1.0f);
                }
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


