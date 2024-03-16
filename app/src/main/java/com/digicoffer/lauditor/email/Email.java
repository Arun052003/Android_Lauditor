package com.digicoffer.lauditor.email;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class Email extends Fragment implements AsyncTaskCompleteListener {

    private static final int AUTH_REQUEST_CODE = 1001;

    private String URL = "";

    boolean ISCHECK_EMAIL = false;
    boolean ISCHECK_AUTH = false;
    boolean ischeck_label, ischeck_auth;


    static AlertDialog progress_dialog;

    boolean emailcheck;
    private List<MessageModel> messages;
    private String requestType = null;
    HttpURLConnection httpURLConnection = null;
    TextView inbox_textViews;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_layout, container, false);

        ImageView closeDocuments = view.findViewById(R.id.compose);
        inbox_textViews = view.findViewById(R.id.inbox_textViews);
        closeDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.child_container, new ComposeFragment())
                            .addToBackStack(null) // Adds the transaction to the back stack
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to open compose fragment", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        emaiAPI();
      callLabel();


        return view;
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
                if (attachmentObject.getString("filename").isEmpty()) {
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


        // Access other fields like nextPageToken and resultSizeEstimate
        String nextPageToken = jsonObject.optString("nextPageToken");
        int resultSizeEstimate = jsonObject.optInt("resultSizeEstimate");
        if (messages.size() > 0){
            updateRecyclerView(messages);
        }
        // Do something with parsed data
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
                    loadRowDatas(result);
                   // updateRecyclerView(messages);
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
        }
        else{
            if (httpResult.getRequestType().equals("Label") || (httpResult.getRequestType().equals("auth")) ){
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
            inbox_textViews.setText("Indox "+String.valueOf(emailModel.getMessagesTotal()));
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

        if (!ISCHECK_AUTH ) {
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

    private void updateRecyclerView(List<MessageModel> messages) {

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        emailadapter adapter = new emailadapter(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}