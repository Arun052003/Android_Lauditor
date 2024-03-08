package com.digicoffer.lauditor.Notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.Notifications.Models.NotificationsDo;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notifications extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, NotificationsAdapter.EventListener {
    RecyclerView rv_notifications;
    private NewModel mViewModel;
    ArrayList<NotificationsDo> notificationList = new ArrayList<NotificationsDo>();
    AlertDialog progress_dialog;
    private CheckBox chk_select_all;
    private ImageView delete_all;
    private TextView ib_read;
    TextInputEditText et_Search;
    TextView tv_notification_count;
    public Notifications() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notifications, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Notifications");
        chk_select_all = (CheckBox) v.findViewById(R.id.chk_select_all);
        delete_all = (ImageView) v.findViewById(R.id.btn_delete_all);
        ib_read = (TextView) v.findViewById(R.id.ib_read);
        rv_notifications = v.findViewById(R.id.rv_list1);
        rv_notifications.setHasFixedSize(true);
        tv_notification_count = v.findViewById(R.id.tv_notification_count);
        callWebservice();
        et_Search = v.findViewById(R.id.et_Search);
        et_Search.setHint("Search");
        loadRecycleView();
        return v;
    }

    private void loadRecycleView() {
        rv_notifications.setLayoutManager(new GridLayoutManager(getContext(), 1));
        final NotificationsAdapter adapter = new NotificationsAdapter(notificationList, this);
        rv_notifications.setAdapter(adapter);
     rv_notifications.setHasFixedSize(true);
        et_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(et_Search.getText().toString());
            }

        });
        chk_select_all.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (chk_select_all.isChecked()) {
                adapter.selectOrDeselectAll(true);
            } else {
                adapter.selectOrDeselectAll(false);
            }
        }
    });


    }



    private void callWebservice() {
        JSONObject postData = new JSONObject();
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "notification", "NOTIFICATIONS", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }


    public void callDeleteNotificationsWebservice(String str_id) {

        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(str_id);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "notification/delete", "DELETE_NOTIFICATIONS", jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    public void loadNotificationsData(JSONObject data) {
        try {
            final JSONArray jsonArray = data.getJSONArray("notifications");
            NotificationsDo Notifications;
            notificationList.clear();
            ArrayList<NotificationsDo> delete_list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Notifications = new NotificationsDo();
                Notifications.setMessage(obj.getString("message"));
                Notifications.setTimestamp(obj.getString("timestamp"));
                Notifications.setId(obj.getString("id"));
                Notifications.setStatus(obj.getString("status"));
                notificationList.add(Notifications);
            }
            tv_notification_count.setText("Notifications:"+"("+"Total "+notificationList.size()+")");
            final NotificationsAdapter adapter = new NotificationsAdapter(notificationList, this);

            delete_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(getContext());
                    dlgAlert.setMessage("Are you sure to delete the notifications?");
                    dlgAlert.setTitle("Alert");
                    dlgAlert.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    delete_all_notifications(adapter.getList_item());
                                }
                            });
                    dlgAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dlgAlert.setCancelable(true);
                    dlgAlert.show();
                }
            });

            ib_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    read_all_notifications(adapter.getList_item());
                }
            });
            loadRecycleView();
        } catch (Exception e) {

        }
    }

    private void read_all_notifications(ArrayList<NotificationsDo> notificationsArray) {
        try {
            JSONArray notification_list = new JSONArray();
            for (int i = 0; i < notificationsArray.size(); i++) {

                NotificationsDo notificationsDo = notificationsArray.get(i);
                if (notificationsDo.isChecked()) {
                    notification_list.put(notificationsDo.getId());
                }
            }
            if (notification_list.length() == 0) {
                AndroidUtils.showToast("Select atleast 1 notification", getActivity());
                return;
            }
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "notification/read", "READ_NOTIFICATIONS", notification_list.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delete_all_notifications(ArrayList<NotificationsDo> notificationsarray) {
        try {
            JSONArray notification_list = new JSONArray();
            for (int i = 0; i < notificationsarray.size(); i++) {

                NotificationsDo notificationsDo = notificationsarray.get(i);
                if (notificationsDo.isChecked()) {
                    notification_list.put(notificationsDo.getId());


                }
            }
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "notification/delete", "DELETE_NOTIFICATIONS", notification_list.toString());

        } catch (Exception e) {
            e.printStackTrace();
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
                if (httpResult.getRequestType().equals("NOTIFICATIONS")) {
                    if (!result.getBoolean("error")) {
                        JSONObject response = new JSONObject(result.getString("data"));
//                        JSONArray jsonArray = response.getJSONArray("notifications");
                        loadNotificationsData(response);
                    } else {
                        AndroidUtils.showValidationALert("Alert", String.valueOf(result.get("msg")), getContext());
                    }
                } else if (httpResult.getRequestType().equals("DELETE_NOTIFICATIONS")) {
                    if (!result.getBoolean("error")) {
                        AndroidUtils.showAlert(result.getString("msg"), getContext());
                        callWebservice();
                    } else {
                        AndroidUtils.showAlert(result.getString("msg"), getContext());
                    }
                } else if (httpResult.getRequestType().equals("READ_NOTIFICATIONS")) {
                    if (!result.getBoolean("error")) {
                        AndroidUtils.showToast(result.getString("msg"), getContext());
                        callWebservice();
                    } else {
                        AndroidUtils.showAlert(result.getString("msg"), getContext());
                    }
                }
            } catch (Exception e) {
                AndroidUtils.logMsg(e.getMessage());
            }

        } else
            AndroidUtils.showAlert((httpResult.getResponseContent()), getActivity());

    }

    public void onEvent(String notifications_id) {
        confirmations(notifications_id);
    }

    @Override
    public void onSelectAllChanged(boolean allSelected) {
        chk_select_all.setChecked(allSelected);

    }

    private void confirmations(final String notifications_id) {
        final android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(getContext());

        dlgAlert.setMessage("Are you sure to delete the notification?");
        dlgAlert.setTitle("Alert");
        final String id = notifications_id;
        dlgAlert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callDeleteNotificationsWebservice(notifications_id);
                    }
                });
        dlgAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.show();
    }


}
