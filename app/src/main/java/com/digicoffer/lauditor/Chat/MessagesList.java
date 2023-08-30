package com.digicoffer.lauditor.Chat;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Chat.Model.MessageDo;
import com.digicoffer.lauditor.Chat.Model.User;
import com.digicoffer.lauditor.LoginActivity.LoginActivity;
import com.digicoffer.lauditor.MainActivity;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.chatservice.ChatConnection;
import com.digicoffer.lauditor.chatservice.ChatConnectionService;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessagesList extends Fragment {
    RecyclerView rv_messagesList;
    ArrayList<MessageDo> message_list = new ArrayList<>();
    Button bt_sendMessage, bt_receiveMessage;
    EditText mChatView;
    TextView tv_contactName;
    AlertDialog progress_dialog;
    private static ChatConnection mConnection;
    private static ChatConnectionService chatConnectionService;
    private String contactJid, contact_name, currentJid, current_contact_name;
    private BroadcastReceiver mBroadcastReceiver;
    MessageListAdapter adapter;
    FloatingActionButton fb_chat;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_message_list, container, false);
        rv_messagesList = v.findViewById(R.id.reyclerview_message_list);
        tv_contactName = v.findViewById(R.id.tv_contactName);
//        fb_chat = (FloatingActionButton) getActivity().findViewById(R.id.fb_chat);
//        fb_chat.hide();
        mChatView = (EditText) v.findViewById(R.id.edittext_chatbox);
        bt_sendMessage = (Button) v.findViewById(R.id.button_chatbox_send);
        bt_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ChatConnectionService.getState().equals(ChatConnection.ConnectionState.CONNECTED)) {
                    Log.d(TAG, "The client is connected to the server,Sending Message - "+mChatView.getText());
                    //Send the message to the server
                    Intent intent = new Intent(ChatConnectionService.SEND_MESSAGE);
                    intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_BODY,
                            mChatView.getText().toString());
                    intent.putExtra(ChatConnectionService.BUNDLE_TO, contactJid);
                    String name = Constants.USER_ID.equals("admin") ? Constants.FIRM_NAME : Constants.NAME;
                    String subject = name + " ##" + currentJid + "## " + "#N#" + name + "#N#";
                    intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_SUBJECT, subject);
                    getActivity().sendBroadcast(intent);
                    MessageDo chatMessage = new MessageDo();
                    chatMessage.setMessage(mChatView.getText().toString());
                    chatMessage.setViewType("SENT");
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:aaa, MMMdd", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getDefault());

                    final String utcTime = sdf.format(new Date());

//                    chatMessage.setCreatedAt(AndroidUtils.getDateToString(Calendar.getInstance().getTime(), "hh:mm:aaa, MMMdd"));
                    chatMessage.setCreatedAt(utcTime);
                    User user = new User();
                    user.setNickname("");
                    chatMessage.setSender(user);
                    message_list.add(chatMessage);
                    mChatView.setText("");
//                    adapter.notifyDataSetChanged();
                    rv_messagesList.smoothScrollToPosition(message_list.size() - 1);
                    new ChatUnreadCountUpdateTask(currentJid, contactJid).execute("");
//                    new ChatHistoryTask(currentJid + File.separator + contactJid).execute("");
                } else {
                    Toast.makeText(getActivity(),
                            "Client not connected to server ,Message not sent! Please try after few seconds",
                            Toast.LENGTH_LONG).show();
                    AndroidUtils.reconnecXMPPServer(getActivity());
                }
            }
        });

        Bundle bundle = this.getArguments();
        contactJid = bundle.getString("EXTRA_CONTACT_JID");
        contact_name = bundle.getString("EXTRA_CONTACT_NAME");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jid = pref
                .getString("xmpp_jid", null);
        pref.edit().putString("CURRENTCHAT_JID", contactJid).commit();
        currentJid = jid;
        tv_contactName.setText(contact_name);
//        if (mConnection == null) {
//            mConnection = new ChatConnection(getContext());
//        }
//        if (chatConnectionService == null) {
//            chatConnectionService = new ChatConnectionService();
//        }
//        new JsonTask().execute(Constants.base_URL + "user/create/");

        new ChatHistoryTask(currentJid + File.separator + contactJid).execute("");

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        pref.edit().putString("CURRENTCHAT_JID", contactJid);
        if (!ChatConnectionService.getState().equals(ChatConnection.ConnectionState.CONNECTED)) {
            AndroidUtils.reconnecXMPPServer(getActivity());
        }
//        ChatConnection.getUserStatus(contactJid);
        /*ChatConnection.mConnection.addSyncStanzaListener(new PacketListener() {
            @Override
            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {

            }
        }, "");*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case ChatConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(ChatConnectionService.BUNDLE_FROM_JID).split("@")[0];
                        String body = intent.getStringExtra(ChatConnectionService.BUNDLE_MESSAGE_BODY);

                        if (from.equals(contactJid)) {
                            MessageDo chatMessage = new MessageDo();
                            chatMessage.setMessage(body);

                            chatMessage.setViewType("RECEIVE");
                            chatMessage.setCreatedAt(AndroidUtils.getDateToString(Calendar.getInstance().getTime(), "hh:mm:a, MMMdd"));
                            User user = new User();
                            user.setNickname(contact_name);
                            chatMessage.setSender(user);
                            message_list.add(chatMessage);
                            adapter.notifyDataSetChanged();
                            rv_messagesList.smoothScrollToPosition(message_list.size() - 1);

                        } else {
                            Log.d(TAG, "Got a message from jid :" + from);
                        }

                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(ChatConnectionService.NEW_MESSAGE);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    void load_chat_history(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject history = jsonArray.getJSONObject(i);
                String message = history.getString("msg");
                String timestamp = history.getString("timestamp");
                Log.d("CHAT_HISTORY",message+"_"+timestamp);
            }

            MessageDo chatMessage;
            User user;
            message_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject history = jsonArray.getJSONObject(i);
                chatMessage = new MessageDo();
                chatMessage.setMessage(history.getString("msg"));
                chatMessage.setCreatedAt(history.getString("timestamp"));
                if (history.getString("from").equals(currentJid)) {
                    chatMessage.setViewType("SENT");
                    user = new User();
                    user.setNickname(Constants.FIRM_NAME);
                    chatMessage.setSender(user);
                } else {
                    chatMessage.setViewType("RECEIVE");
                    user = new User();
                    user.setNickname(contact_name);
                    chatMessage.setSender(user);
                }

                message_list.add(chatMessage);

            }
        } catch (Exception e) {
            AndroidUtils.logMsg(e.getMessage());
        }
        rv_messagesList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new MessageListAdapter(getContext(), message_list);
        rv_messagesList.setAdapter(adapter);
        if (message_list.size() != 0)
            rv_messagesList.smoothScrollToPosition(message_list.size() - 1);
    }

    class ChatHistoryTask extends AsyncTask<String, String, String> {
        String XMPP_DOMAIN = "https://" + Constants.XMPP_DOMAIN + "/history/";

        String url = "";
        ChatHistoryTask(String url) {

            super();

            this.url = url + "@" + Constants.XMPP_DOMAIN;
            Log.d("XMPP_DOMAIN",this.url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new ChatUnreadCountUpdateTask(currentJid, contactJid).execute("");
//            progress_dialog = AndroidUtils.get_progress(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(XMPP_DOMAIN + url).openConnection();
                Log.d("New_Data",XMPP_DOMAIN + url);
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
                    AndroidUtils.showAlert("Err connection, Please try again", getContext());
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

        protected void onPostExecute(String response) {
//            AndroidUtils.showAlert(response, getContext());
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
            Log.d("Response",response.toString());
            load_chat_history(response);

        }
    }


    class ChatUnreadCountUpdateTask extends AsyncTask<String, String, String> {
        String XMPP_DOMAIN = "https://" + Constants.XMPP_DOMAIN + "/";
        String url = "";

        ChatUnreadCountUpdateTask(String currentJID, String JID) {
            super();
            this.url = XMPP_DOMAIN + "unread-timestamp/" + currentJID + File.separator + JID ;
            Log.d("Unread_count",this.url);
            Log.d("TOKEN",Constants.TOKEN);
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

//            return response;

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
            Log.d("REQUEST_COUNT", String.valueOf(status_code));
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
//                AndroidUtils.showToast("Err connection, Please try again", getContext());
            }
        } catch (Exception e) {
            AndroidUtils.logMsg(e.getMessage());
        }
        return data;
//        load_chat_history();
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                mConnection.connect();

            } catch (IOException | SmackException | XMPPException e) {
                Log.d("Chat Error", "Something went wrong while connecting ,make sure the credentials are right and try again");
                e.printStackTrace();
                //Stop the service all together.
                chatConnectionService.stopSelf();
            }
            return "";
        }
    }



}
