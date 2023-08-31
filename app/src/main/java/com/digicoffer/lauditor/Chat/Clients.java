package com.digicoffer.lauditor.Chat;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Chat.Model.ChildDO;
import com.digicoffer.lauditor.Chat.Model.ClientRelationshipsDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Clients extends Fragment implements AsyncTaskCompleteListener, ChatAdapter.EventListener {
    AlertDialog progress_dialog;
    RecyclerView rv_Clientrelationships;
    TextInputEditText et_Search;
    AlertDialog ad_dialog;
    ArrayList<ClientRelationshipsDo> Clientlist = new ArrayList<ClientRelationshipsDo>();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client, container, false);
        rv_Clientrelationships = view.findViewById(R.id.rv_clientrelationships);
        et_Search = view.findViewById(R.id.et_Search);
        callWebservice();
        return view;
    }

    private void callWebservice() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postData = new JSONObject();
        try {
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v2/relationship", "CLIENT_RELATIONSHIP", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
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
                if (httpResult.getRequestType() == "CLIENT_RELATIONSHIP") {
                    if (!result.getBoolean("error")) {
                        JSONObject jsonObject = result.getJSONObject("data");
                        JSONArray jsonArray = jsonObject.getJSONArray("relationships");
                        et_Search.setText("");
                        loadClientRelationshipsData(jsonArray);
                    } else {
                        AndroidUtils.showValidationALert("Alert", String.valueOf(result.get("msg")), getContext());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadClientRelationshipsData(JSONArray jsonArray) {
        try {

            Clientlist.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                ClientRelationshipsDo clientRelationshipsDo = new ClientRelationshipsDo();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                clientRelationshipsDo.setAdminName(jsonObject.getString("adminName"));
                clientRelationshipsDo.setCanAccept(jsonObject.getBoolean("canAccept"));
                clientRelationshipsDo.setClientType(jsonObject.getString("clientType"));
                clientRelationshipsDo.setConsent(jsonObject.getString("consent"));
                clientRelationshipsDo.setCreated(jsonObject.getString("created"));
                clientRelationshipsDo.setGroups(jsonObject.getJSONArray("groups"));
                clientRelationshipsDo.setGuid(jsonObject.getString("guid"));
                clientRelationshipsDo.setId(jsonObject.getString("id"));
                clientRelationshipsDo.setAccepted(jsonObject.getBoolean("isAccepted"));
                clientRelationshipsDo.setClient(jsonObject.getBoolean("isClient"));
                clientRelationshipsDo.setEditable(jsonObject.getBoolean("isEditable"));
                clientRelationshipsDo.setMatterList(jsonObject.getJSONArray("matterList"));
                clientRelationshipsDo.setName(jsonObject.getString("name"));
//                clientRelationshipsDo.setExpanded(false);
                if (clientRelationshipsDo.isAccepted()) {
                    Clientlist.add(clientRelationshipsDo);
                }

            }


            loadRecycleView();
        } catch (Exception e) {

        }
    }

    private void loadRecycleView() {
        rv_Clientrelationships.setLayoutManager(new GridLayoutManager(getContext(), 1));
        final ChatAdapter adapter = new ChatAdapter(Clientlist, this, getContext(), getActivity());
        Log.d("Client_list", String.valueOf(Clientlist.size()));
        rv_Clientrelationships.setAdapter(adapter);
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(et_Search.getText().toString());
            }

        });

    }

    @Override
    public void Message(ChildDO childDO) {
        try {
            String jid = childDO.getUid();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String currentJID = pref
                    .getString("xmpp_jid", null);
            Log.d("jid+currentJID", jid + "_" + currentJID);
            if (childDO.getId() == "" || childDO.getId() == null) {
                new ChatHistoryTask(currentJID, jid).execute("");
                move_message_fragment("", childDO.getName(), jid);

            } else {
                new ChatHistoryTask(childDO.getId(), jid).execute("");
                move_message_fragment(childDO.getId(), childDO.getName(), jid);
            }
        } catch (Exception e) {
            Log.d("Error:", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void view_users(String jid, String name) throws JSONException {
        MessagesList frag = new MessagesList();
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_CONTACT_JID", jid);
        bundle.putString("EXTRA_CONTACT_NAME", name);
        frag.setArguments(bundle);
        FragmentManager fragmentManager11 = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction11 = fragmentManager11.beginTransaction();
        fragmentTransaction11.replace(R.id.id_framelayout, frag);
        fragmentTransaction11.addToBackStack(null);
        fragmentTransaction11.commit();
//        ad_dialog.dismiss();
    }

    private void move_message_fragment(String tmid, String name, String jid) {
        try {
            String xmpp_jid = tmid.equals("") ? jid : (jid + "_" + tmid);
            Log.d("xmpp_jid", xmpp_jid);
            MessagesList frag = new MessagesList();
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_CONTACT_JID", xmpp_jid);
            bundle.putString("EXTRA_CONTACT_NAME", name);
            frag.setArguments(bundle);
            FragmentManager fragmentManager11 = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction11 = fragmentManager11.beginTransaction();
            fragmentTransaction11.replace(R.id.id_framelayout, frag);
            fragmentTransaction11.addToBackStack(null);
            fragmentTransaction11.commit();
//            ad_dialog.dismiss();
        } catch (Exception e) {
            AndroidUtils.logMsg(e.getMessage());
        }
    }

    class ChatHistoryTask extends AsyncTask<String, String, String> {
        String XMPP_DOMAIN = "https://" + Constants.XMPP_DOMAIN + "/";
        String url = "";
        TextView tv_count;

        ChatHistoryTask(String currentJID, String JID) {
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
}
