package com.digicoffer.lauditor.email;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class Email extends Fragment {

    private static final int AUTH_REQUEST_CODE = 1001;

    boolean isAuthInProgress = false;

    AlertDialog progress_dialog;
    String GoogleSignInAccount ;
    private List<MessageModel> messages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_layout, container, false);

        ImageView closeDocuments = view.findViewById(R.id.compose);
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
       // makeApiCall();
        callLabel();
       // emaiauth();

        // onResume();
        emailAPI("Label","GET");


        return view;
    }



    public boolean emaiauth() {
        try {
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuser.emails.read%20https%3A%2F%2Fmail.google.com%2F&state=ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SjFhV1FpT2lKQlEwSXhNRUUyUWpVelJqbEVNamRFSWl3aVlXUnRhVzRpT21aaGJITmxMQ0p3YkdGdUlqb2liR0YxWkdsMGIzSWlMQ0p5YjJ4bElqb2lVMVVpTENKdVlXMWxJam9pVTI5MWJtUmhjbmxoSUVSTVJpQlRWU0lzSW5WelpYSmZhV1FpT2lJMk0ySm1aRGxpTjJFeFpHSTNNakJtTW1Rek9HUTBaRElpTENKbGVIQWlPakUzTVRBek5ERXpNemg5LnJjR0xFWTJ5dmk4YlB4R2R1REFJTzZQa1JPWlMxQlA0aUxudV85c0RmOXc%3D&response_type=code&client_id=1074057396282-o970s9o11m4g3dla4g0lokc5k4e7o8g5.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fdev.utils.mail.digicoffer.com%2Foauth2callback";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(authUrl));
            startActivityForResult(intent, AUTH_REQUEST_CODE);

            progress_dialog = AndroidUtils.get_progress(getActivity());


            JSONObject jsonObject = new JSONObject();


            WebServiceHelper.callHttpWebService((AsyncTaskCompleteListener) this,
                    getContext(),
                    WebServiceHelper.RestMethodType.GET,
                    Constants.EMAIL_BASE_URL + "gmail/authurl?authtoken=" + Constants.TOKEN,
                    "auth",
                    jsonObject.toString());
return true;
        } catch (Exception e) {

            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }

            e.printStackTrace();
        }
        return false;
    }


    public void onResume() {
        super.onResume();

        if (isAuthInProgress && isAuthenticationSuccessful()) {
            callauth();
            isAuthInProgress = false;
        }
    }
    private boolean isAuthenticationSuccessful() {

        return false;
    }




    public void callauth() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService((AsyncTaskCompleteListener) this, getContext(), WebServiceHelper.RestMethodType.GET, Constants.EMAIL_BASE_URL + "gmail/messages/" + Constants.TOKEN + "?rows=10", "rows", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();
            // Add log statement to identify the issue
            Log.e("API Error", "Failed to call API: " + e.getMessage());
        }
    }
////void makeApiCall() {
////    new Thread(new Runnable() {
////        @Override
////        public void run() {
////            try {
////
////                OkHttpClient client = new OkHttpClient();
////                MediaType mediaType = MediaType.parse("text/plain");
////                RequestBody body = RequestBody.create(mediaType, "");
////                Request request =new Request.Builder()
////                        .url("https://dev.utils.mail.digicoffer.com/api/v1/gmail/messages/"+Constants.TOKEN+"?rows=10")
////                        .method("GET", body)
////                        .build();
////                Response response = client.newCall(request).execute();
////                final String responseData = response.body().string();
////
////                // Update UI with response data (e.g., using runOnUiThread)
////                getActivity().runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////
////                    }
////                });
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////    }).start();
////}
//
//
    private void loadGroupsDatas(JSONObject jsonObject) throws JSONException {

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
            }
            message.setAttachments(attachments);

            messages.add(message);
        }

        // Access other fields like nextPageToken and resultSizeEstimate
        String nextPageToken = jsonObject.optString("nextPageToken");
        int resultSizeEstimate = jsonObject.optInt("resultSizeEstimate");

        // Do something with parsed data
    }


    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing()) {
            AndroidUtils.dismiss_dialog(progress_dialog);
        }
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());

                if (httpResult.getRequestType().equals("Label")) {

                    JSONObject data = new JSONObject();
                    loadGroupsData(data);

                } else if (httpResult.getRequestType().equals("rows")) {
                    loadGroupsDatas(result);
                    updateRecyclerView(messages);
                }    else if (httpResult.getRequestType().equals("auth")) {

                    String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuser.emails.read%20https%3A%2F%2Fmail.google.com%2F&state=ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SjFhV1FpT2lKQlEwSXhNRUUyUWpVelJqbEVNamRFSWl3aVlXUnRhVzRpT21aaGJITmxMQ0p3YkdGdUlqb2liR0YxWkdsMGIzSWlMQ0p5YjJ4bElqb2lVMVVpTENKdVlXMWxJam9pVTI5MWJtUmhjbmxoSUVSTVJpQlRWU0lzSW5WelpYSmZhV1FpT2lJMk0ySm1aRGxpTjJFeFpHSTNNakJtTW1Rek9HUTBaRElpTENKbGVIQWlPakUzTVRBek5ERXpNemg5LnJjR0xFWTJ5dmk4YlB4R2R1REFJTzZQa1JPWlMxQlA0aUxudV85c0RmOXc%3D&response_type=code&client_id=1074057396282-o970s9o11m4g3dla4g0lokc5k4e7o8g5.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fdev.utils.mail.digicoffer.com%2Foauth2callback";


                    if (authUrl != null && !authUrl.isEmpty()) {

                        launchAuthUrl(authUrl);
                    } else {

                        Toast.makeText(getActivity(), "Failed to obtain authentication URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void launchAuthUrl(String authUrl) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(authUrl));
        startActivityForResult(intent, AUTH_REQUEST_CODE);
    }




    private void loadGroupsData(JSONObject data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(String.valueOf(i));
                EmailModel emailModel = new EmailModel();
                emailModel.setId(jsonObject.getString("id"));
                emailModel.setName(jsonObject.getString("name"));
                emailModel.setType(jsonObject.getString("type"));
                emailModel.setMessagesTotal(jsonObject.getInt("messagesTotal"));
                emailModel.setMessagesUnread(jsonObject.getInt("messagesUnread"));
                emailModel.setThreadsTotal(jsonObject.getInt("threadsTotal"));
                emailModel.setThreadsUnread(jsonObject.getInt("threadsUnread"));


            }

        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }


    public void callLabel() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();

            WebServiceHelper.callHttpWebService((AsyncTaskCompleteListener) this, getContext(), WebServiceHelper.RestMethodType.GET, Constants.EMAIL_BASE_URL + "gmail/label/" +Constants.TOKEN+ "?labelid=INBOX", "Label", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();
        }
    }

    public void emailAPI(String actionType, String requestMethod) {

        if (actionType.equals("Label") && requestMethod.equals("GET")) {

          emaiauth();
        } else {

            if (emaiauth()) {
              //  makeApiCall();
//                try {
//                    callauth(com.google.android.gms.auth.api.signin.GoogleSignInAccount.zab(GoogleSignInAccount));
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
                callauth();

            } else {
                // emailAuth was not successful
                // Handle the failure case as needed
            }
        }
    }
    private void updateRecyclerView(List<MessageModel> messages) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
       emailadapter adapter = new emailadapter(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


}

