package com.digicoffer.lauditor.Chat;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;

public class Teams extends Fragment implements AsyncTaskCompleteListener, TeamsAdapter.EventListener {
    AlertDialog progress_dialog;
    RecyclerView rv_Clientrelationships;
    TextInputEditText et_Search;
    AlertDialog ad_dialog;
    ArrayList<ClientRelationshipsDo> Clientlist = new ArrayList<ClientRelationshipsDo>();
    ArrayList<ChildDO> child_list = new ArrayList<>();
    @Override
    public void onClick(View view) {

    }
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
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "chatusers", "Team_Chat", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType() == "Team_Chat") {
                    if (!result.getBoolean("error")) {
//                        JSONObject jsonObject = result.getJSONObject("data");
                        JSONArray jsonArray = result.getJSONArray("groups");
                        Constants.teamResArray = jsonArray;
                        et_Search.setText("");
                        loadClientRelationshipsData(jsonArray);
                    } else {
                        AndroidUtils.showValidationALert("Alert", String.valueOf(result.get("msg")), getContext());
                    }
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
    }

    private void loadClientRelationshipsData(JSONArray jsonArray) {
        try {

            Clientlist.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                ClientRelationshipsDo clientRelationshipsDo = new ClientRelationshipsDo();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                clientRelationshipsDo.setId(jsonObject.getString("id"));
                clientRelationshipsDo.setName(jsonObject.getString("groupName"));
                clientRelationshipsDo.setClientType("Team");
                clientRelationshipsDo.setUsers(jsonObject.getJSONArray("users"));
//                clientRelationshipsDo.setGuid(jsonObject.getString("guid"));
//                clientRelationshipsDo.setExpanded(false);
//                if (clientRelationshipsDo.isAccepted()) {
                Clientlist.add(clientRelationshipsDo);
//                }
//                JSONArray user = jsonObject.getJSONArray("users");
//                child_list.clear();
//                for (int j = 0; j < user.length(); j++) {
//                    ChildDO childDO1 = new ChildDO();
//                    JSONObject jsonuser = user.getJSONObject(j);
//                    childDO1.setGuid(jsonuser.getString("guid"));
//                    childDO1.setName(jsonuser.getString("name"));
////                    childDO1.setId(jsonuser.getString("id"));
////                    childDO1.setUid(uid);
//                    child_list.add(childDO1);
//                }

                Constants.teamMapChatList.put(clientRelationshipsDo.getId(), child_list);
            }
            loadRecycleView();
        } catch (Exception e) {

        }
    }

    private void loadRecycleView() {
        rv_Clientrelationships.setLayoutManager(new GridLayoutManager(getContext(), 1));
        final TeamsAdapter adapter = new TeamsAdapter(Clientlist, this, getContext(), getActivity());

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
//                new Clients.ChatHistoryTask(currentJID, jid).execute("");
                move_message_fragment("", childDO.getName(), jid);

            } else {
//                new Clients.ChatHistoryTask(childDO.getId(), jid).execute("");
                move_message_fragment(childDO.getId(), childDO.getName(), jid);
            }
        } catch (Exception e) {
            Log.d("Error:", e.getMessage());
            e.fillInStackTrace();
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

    @Override
    public void Users(ClientRelationshipsDo clientRelationshipsDo, TeamsAdapter.MyViewHolder holder) {

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
}
