package com.digicoffer.lauditor.Dashboard;

import static com.digicoffer.lauditor.common.Constants.clientTeamApiEndpoint;
import static com.digicoffer.lauditor.common.Constants.groupsEndpoint;
import static com.digicoffer.lauditor.common.Constants.hiringEndpoint;
import static com.digicoffer.lauditor.common.Constants.hoursEndpoint;
import static com.digicoffer.lauditor.common.Constants.matterEndpoint;
import static com.digicoffer.lauditor.common.Constants.meetingApiEndpoint;
import static com.digicoffer.lauditor.common.Constants.newclientEndpoint;
import static com.digicoffer.lauditor.common.Constants.notificationEndpoint;
import static com.digicoffer.lauditor.common.Constants.relationshipsEndpoint;
import static com.digicoffer.lauditor.common.Constants.storageEndpoint;
import static com.digicoffer.lauditor.common.Constants.subscriptionEndpoint;
import static com.digicoffer.lauditor.common.Constants.timesheetEndpoint;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.ClientChatModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.EmailModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.Item;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.MeetingModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.NotificationModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.RelationshipRequestModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.MydayModels.TeamChatModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.ActiveModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.ApproxRevenueModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.AverageBillingRateModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.BillableModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.ClosedModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.GroupsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.HiringModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.NewClientsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.NonBillableModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.PendingTimeSheetsModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.PracticeModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.RelationshipModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.StorageModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.SubScriptionModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.SubmittedTimesheetModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.TeamModel;
import com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels.TimeSheetModel;
import com.digicoffer.lauditor.MainActivity;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.minidns.record.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dashboard extends Fragment implements AsyncTaskCompleteListener {

    private NewModel mViewModel;
    private AlertDialog progressDialog;
    private TextClock tv_date_time, tv_time, tv_am_pm;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private ExecutorService executorService;
    private Button bt_MyDay, bt_KPI;
    MainActivity mainActivity;
    private static String KPI_DATA = "";
    private RecyclerView rv_myday;
    ArrayList<Item> itemArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callMultipleAPI();

        executorService = Executors.newFixedThreadPool(2);
    }

    @SuppressLint({"SimpleDateFormat", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dashboard_screen, container, false);
        KPI_DATA = Constants.ROLE;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        try {
            mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
            mViewModel.setData("Dashboard");
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            date = dateFormat.format(calendar.getTime());
            try {
//            mainActivity = (MainActivity) getActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bt_KPI = v.findViewById(R.id.bt_kpi);
            bt_MyDay = v.findViewById(R.id.bt_Myday);
            bt_MyDay.setBackgroundColor(getContext().getResources().getColor(R.color.grey_color_dark));
            Date date_new = AndroidUtils.stringToDateTimeDefault(date, "MMM dd YYYY HH:mm:ss aa");
            String time = AndroidUtils.getDateToString(date_new, "HH:mm");
            String date = AndroidUtils.getDateToString(date_new, "MMM dd YYYY");
            String am_pm = AndroidUtils.getDateToString(date_new, "aa");
            tv_am_pm = v.findViewById(R.id.am_pm);
            tv_time = v.findViewById(R.id.time);
            tv_date_time = v.findViewById(R.id.tv_date);
            rv_myday = v.findViewById(R.id.rv_myday);
            itemArrayList.clear();

            try {
                bt_MyDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemArrayList.clear();
                        KPI_DATA = "";
                        bt_MyDay.setBackgroundColor(getContext().getResources().getColor(R.color.grey_color_dark));
                        bt_KPI.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        callMultipleAPI();
                    }
                });
                bt_KPI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KPI_DATA = "";
                        itemArrayList.clear();
                        bt_MyDay.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                        bt_KPI.setBackgroundColor(getContext().getResources().getColor(R.color.grey_color_dark));
//                        load_TM_KPI_data();
                        callMultipleKpiAPI();
                    }

                });

            } catch (Exception e) {
                Log.e("Error", "Info" + e.getMessage());
            }


        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void callMultipleKpiAPI() {
        clearData();
        if (Constants.ROLE.equalsIgnoreCase("AAM")){
            callGroupsWebservice(groupsEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("SU")){
            callHoursWebservice(hoursEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("GH")){
            callHoursWebservice(hoursEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("TM")){
            callHoursWebservice(hoursEndpoint);
        }
    }

    private void clearData() {
        itemArrayList.clear();
        rv_myday.removeAllViews();
    }

    private void callExecutorAPIs(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {

            }
        });
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    private void loadMyDayData() {
        callMultipleAPI();
    }

    private void callMultipleAPI() {
        itemArrayList.clear();
//        if (Constants.ROLE.equalsIgnoreCase("AAM")){
            Admin_Myday(meetingApiEndpoint,clientTeamApiEndpoint,notificationEndpoint);
//        }
    }

    private void Admin_Myday(String meetingApiEndpoint, String clientTeamApiEndpoint, String notificationEndpoint) {
        callMeetingWebservice(meetingApiEndpoint);
    }

    private void callChatRelationshipWebservice(String chatRelationshipApiEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, chatRelationshipApiEndpoint, "CHAT_RELATIONSHIP", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callExternalCounselsWebservice(String externalCounselsEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, externalCounselsEndpoint, "EXTERNAL_COUNSEL", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callNewClientWebservice(String newclientEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, newclientEndpoint, "NEW_CLIENT", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callGroupsWebservice(String groupsEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, groupsEndpoint, "GROUPS", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callSubscriptionWebservice(String subscriptionEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, subscriptionEndpoint, "SUBSCRIPTION", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callMatterWebservice(String matterEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, matterEndpoint, "MATTER", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callTimesheetWebservice(String timesheetEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, timesheetEndpoint, "TIMESHEET", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callStorageWebservice(String storageEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, storageEndpoint, "STORAGE", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callHiringWebservice(String hiringThread) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, hiringThread, "HIRING", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callHoursWebservice(String hoursEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, hoursEndpoint, "HOURS", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callNotificationWebservice(String notificationEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, notificationEndpoint, "NOTIFICATION", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callEmailWebservice(String emailApiEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, emailApiEndpoint, "EMAIL", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callClientTeamWebservice(String clientTeamApiEndpoint) {
        try {


            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, clientTeamApiEndpoint, "CLIENT_TEAM", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callRelationshipWebservice(String relationshipApiEndpoint) {
        try {


            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, relationshipApiEndpoint, "RELATIONSHIP", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    private void callMeetingWebservice(String meetingApiEndpoint) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postData = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, meetingApiEndpoint, "MEETING", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }


    private void loadKPIdata() {
//        for (int i=0;i<=8;i++){
        PracticeModel practiceModel_tbh = new PracticeModel(800, 72, 0);
        itemArrayList.add(new Item(0, practiceModel_tbh));
//        }
        PracticeModel practiceModel_nbh = new PracticeModel(140, 28, 0);
        itemArrayList.add(new Item(1, practiceModel_nbh));
        PracticeModel practiceModel_ar = new PracticeModel(10000, 0, 0);
        itemArrayList.add(new Item(2, practiceModel_ar));
        PracticeModel practiceModel_abr = new PracticeModel(200, 0, 0);
        itemArrayList.add(new Item(3, practiceModel_abr));
        PracticeModel practiceModel_ts = new PracticeModel(30, 3, 27);
        itemArrayList.add(new Item(4, practiceModel_ts));
        PracticeModel practiceModel_matter = new PracticeModel(128, 4, 0);
        itemArrayList.add(new Item(5, practiceModel_matter));
        PracticeModel practiceModel_usl = new PracticeModel(4, 0, 0);
        itemArrayList.add(new Item(6, practiceModel_usl));
        PracticeModel practiceModel_cr = new PracticeModel(128, 4, 3);
        itemArrayList.add(new Item(7, practiceModel_cr));
        KPI_DATA = "PH_KPI";
        loadRecyclerview(KPI_DATA);

    }

    private void loadSuperKPIdata() {
        PracticeModel practiceModel_tbh = new PracticeModel(800, 72, 0);
        itemArrayList.add(new Item(0, practiceModel_tbh));
//        }
        PracticeModel practiceModel_nbh = new PracticeModel(140, 28, 0);
        itemArrayList.add(new Item(1, practiceModel_nbh));
        PracticeModel practiceModel_ar = new PracticeModel(10000, 0, 0);
        itemArrayList.add(new Item(2, practiceModel_ar));
        PracticeModel practiceModel_abr = new PracticeModel(200, 0, 0);
        itemArrayList.add(new Item(3, practiceModel_abr));
        PracticeModel practiceModel_ts = new PracticeModel(30, 3, 27);
        itemArrayList.add(new Item(4, practiceModel_ts));
        PracticeModel practiceModel_matter = new PracticeModel(128, 4, 0);
        itemArrayList.add(new Item(5, practiceModel_matter));
        PracticeModel practiceModel_usl = new PracticeModel(4, 0, 0);
        itemArrayList.add(new Item(6, practiceModel_usl));
        PracticeModel practiceModel_cr = new PracticeModel(128, 4, 3);
        itemArrayList.add(new Item(7, practiceModel_cr));
        KPI_DATA = "SU_KPI";
        loadRecyclerview(KPI_DATA);
    }

    private void load_TM_KPI_data() {
        PracticeModel practiceModel_tbh = new PracticeModel(800, 72, 0);
        itemArrayList.add(new Item(0, practiceModel_tbh));
        PracticeModel practiceModel_nbh = new PracticeModel(140, 28, 0);
        itemArrayList.add(new Item(1, practiceModel_nbh));
        PracticeModel practiceModel_ar = new PracticeModel(10000, 0, 0);
        itemArrayList.add(new Item(2, practiceModel_ar));
        PracticeModel practiceModel_abr = new PracticeModel(200, 0, 0);
        itemArrayList.add(new Item(3, practiceModel_abr));
        PracticeModel practiceModel_ts = new PracticeModel(30, 3, 27);
        itemArrayList.add(new Item(4, practiceModel_ts));
        KPI_DATA = "TM_KPI";
        loadRecyclerview(KPI_DATA);
    }

    private void load_Admin_data() {
        PracticeModel practiceModel_tbh = new PracticeModel(800, 72, 0);
        itemArrayList.add(new Item(0, practiceModel_tbh));
        PracticeModel practiceModel_nbh = new PracticeModel(140, 28, 0);
        itemArrayList.add(new Item(1, practiceModel_nbh));
        PracticeModel practiceModel_ar = new PracticeModel(10000, 0, 0);
        itemArrayList.add(new Item(2, practiceModel_ar));
        PracticeModel practiceModel_abr = new PracticeModel(200, 0, 0);
        itemArrayList.add(new Item(3, practiceModel_abr));
        PracticeModel practiceModel_ts = new PracticeModel(30, 3, 27);
        itemArrayList.add(new Item(4, practiceModel_ts));
        KPI_DATA = "Admin";
        loadRecyclerview(KPI_DATA);
    }

    private void setViewModelData(String data) {
        mViewModel.setData(data);
    }

    private void loadRecyclerview(String data) {
        if (progressDialog.isShowing() && progressDialog != null) {
            AndroidUtils.dismiss_dialog(progressDialog);
        }
        //Meetings
        rv_myday.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_myday.setAdapter(new MyDayAdapter(itemArrayList, data));
        rv_myday.scrollToPosition(-1);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog.isShowing() && progressDialog != null) {
            AndroidUtils.dismiss_dialog(progressDialog);
        }
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equalsIgnoreCase("MEETING")){
                    if (result.getBoolean("error")){
                        MeetingModel meetingModel = new MeetingModel("","00:00","00:00",result.getString("message"));
                        itemArrayList.add(new Item(0, meetingModel));
                        callClientTeamWebservice(clientTeamApiEndpoint);
                    }else {
                        JSONObject data = result.getJSONObject("data");
                        Log.d("Meeting_Data", data.toString());
                        loadMeetingData(data);
                    }
                }else if(httpResult.getRequestType().equalsIgnoreCase("CHAT_RELATIONSHIP")){
                    String data = result.getString("data");
                }else if(httpResult.getRequestType().equalsIgnoreCase("CLIENT_TEAM")){
                    if (result.getBoolean("error")){
                        ClientChatModel clientChatModel = new ClientChatModel("00:00","",result.getString("message"));
                        itemArrayList.add(new Item(1, clientChatModel));
                        TeamChatModel teamChatModel = new TeamChatModel("00:00","",result.getString("message"));
                        itemArrayList.add(new Item(2, teamChatModel));
                        callNotificationWebservice(notificationEndpoint);
                    }else {
                        JSONObject data = result.getJSONObject("data");
                        Log.d("Client_Data", data.toString());
                        loadClientdata(data);
                    }
                }else if(httpResult.getRequestType().equalsIgnoreCase("EMAIL")){
                    JSONObject data = result.getJSONObject("data");
                    loadEmaildata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("NOTIFICATION")){
                    if (result.getBoolean("error")){
                        NotificationModel notificationModel = new NotificationModel("00:00",result.getString("message"),"");
                        itemArrayList.add(new Item(3, notificationModel));
                        loadRecyclerview("MyDay_AAM");
                    }else{
                    JSONObject data = result.getJSONObject("data");
                    Log.d("Notification_Data",data.toString());
                    loadNotificationdata(data);
                    }
                }else if(httpResult.getRequestType().equalsIgnoreCase("HOURS")){
                    JSONObject data = result.getJSONObject("data");
                    loadHoursdata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("HIRING")){
                    JSONArray data = result.getJSONArray("data");
                    loadHiringData(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("STORAGE")){
                    JSONObject data = result.getJSONObject("data");
                    loadStoragedata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("TIMESHEET")){
                    JSONObject data = result.getJSONObject("data");
                    loadTimesheetsdata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("MATTER")){
                    JSONObject data = result.getJSONObject("data");
                    loadMatterdata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("SUBSCRIPTION")){
                    JSONObject data = result.getJSONObject("data");
                    loadSubscriptiondata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("GROUPS")){
                    JSONObject data = result.getJSONObject("data");
                    loadGroupsdata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("NEW_CLIENT")){
                    JSONArray data = result.getJSONArray("data");
                    loadNewClientsdata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("EXTERNAL_COUNSEL")){
                    JSONArray data = result.getJSONArray("data");
                    loadExternalCounseldata(data);
                }else if(httpResult.getRequestType().equalsIgnoreCase("RELATIONSHIP")){
                    JSONObject data = result.getJSONObject("data");
                    loadRelationshipdata(data);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void loadRelationshipdata(JSONObject data) throws JSONException {
        String accepted = data.getString("accepted");
        String pending = data.getString("pending");
        RelationshipModel relationshipModel = new RelationshipModel(accepted,pending);
        itemArrayList.add(new Item(6,relationshipModel));
        callStorageWebservice(storageEndpoint);
    }

    private void loadNotificationdata(JSONObject data) throws JSONException {
        String timeStamp = data.getString("timestamp");
        String message = data.getString("message");
        String date = data.getString("date");
        Log.d("DATA",timeStamp+message+date);
        NotificationModel notificationModel = new NotificationModel(timeStamp,message,date);

        itemArrayList.add(new Item(3,notificationModel));
//        if (Constants.ROLE.equalsIgnoreCase("AAM")){
            loadRecyclerview("MyDay_AAM");
//        }else{
//            loadRecyclerview("MyDay_Others");
//        }

        for (int i=0;i<itemArrayList.size();i++){
            String new_message  = itemArrayList.get(i).toString();
            Log.d("ItemArrayList",new_message);
        }
    }

    private void loadExternalCounseldata(JSONArray data)throws JSONException {
        for (int i=0;i<data.length();i++){
            JSONObject jsonObject = data.getJSONObject(i);
        }
    }

    private void loadNewClientsdata(JSONArray data) throws JSONException {
        Log.d("NewClient","NewClient Successfull");
        String corporateType = "";
        int corporateCount = 0;
        String criminalType = "";
        int criminalCount = 0;
        for (int i=0;i<data.length();i++){
            JSONObject jsonObject = data.getJSONObject(i);
            String type = jsonObject.getString("type");
            int count = jsonObject.getInt("count");
            if (i == 0) { // Assuming the first entry is for corporate
                corporateType = type;
                corporateCount = count;
            } else if (i == 1) { // Assuming the second entry is for criminal
                criminalType = type;
                criminalCount = count;
            }
        }
        NewClientsModel newClientsModel = new NewClientsModel(corporateType, corporateCount, criminalType, criminalCount);
        itemArrayList.add(new Item(5,newClientsModel));
        callHiringWebservice(hiringEndpoint);
    }

    private void loadGroupsdata(JSONObject data) throws JSONException{
        Log.d("Groups","Groups Successfull");
        String totalGroups = data.getString("totalGroups");
        GroupsModel groupsModel = new GroupsModel(totalGroups);
        itemArrayList.add(new Item(0,groupsModel));
        String totalTms = data.getString("totalTms");
        TeamModel teamModel = new TeamModel(totalTms);
        itemArrayList.add(new Item(1,teamModel));
        callHiringWebservice(hiringEndpoint);
    }

    private void loadSubscriptiondata(JSONObject data) throws JSONException {
        Log.d("SubScription","SubScription_Successful");
        boolean is_paid_subscriber = data.getBoolean("is_paid_sub");
        boolean is_active_subscriber = data.getBoolean("is_active_sub");
        boolean is_active_pay_btn_enabled = data.getBoolean("active_pay_btn");
        SubScriptionModel subScriptionModel = new SubScriptionModel(is_active_pay_btn_enabled,is_active_subscriber,is_paid_subscriber);
        itemArrayList.add(new Item(4,subScriptionModel));
//        for (int i=0;i<itemArrayList.size();i++){
//            Item item = itemArrayList.get(i);
//            String message = item.getViewtype();
//            Log.d("Sub_Message",message.toString());
//        }
        loadRecyclerview("Kpi_AAM");
    }

    private void loadMatterdata(JSONObject data) throws JSONException{
        Log.d("KPIMatter","KPIMatter Successfull");
        JSONObject active = data.getJSONObject("active");
        JSONObject closed = data.getJSONObject("closed");
        String activeTotal = active.getString("total");
        String closedTotal = closed.getString("total");
        String active_general_type = "";
        int active_general_count = 0;
        String active_legal_type = "";
        int active_legal_count = 0;
        String closed_general_type = "";
        int closed_general_count = 0;
        String closed_legal_type = "";
        int closed_legal_count = 0;
        JSONArray activeCounts = active.getJSONArray("countsByType");
        JSONArray closeCounts = closed.getJSONArray("countsByType");
        for (int i=0;i<activeCounts.length();i++){
            JSONObject jsonObject = activeCounts.getJSONObject(i);
            String type = jsonObject.getString("type");
            int count = jsonObject.getInt("count");
            if (i==0){
                active_legal_type = type;
                active_legal_count = count;
            }else if(i==1){
                active_general_type = type;
                active_general_count = count;
            }
        }
//        ActiveModel activeModel = new ActiveModel(activeTotal,active_legal_type,active_legal_count,active_general_type,active_general_count);
//        itemArrayList.add(new Item(4,activeModel));
        for (int i=0;i<closeCounts.length();i++){
            JSONObject jsonObject = closeCounts.getJSONObject(i);
            String type = jsonObject.getString("type");
            int count = jsonObject.getInt("count");
            if (i==0){
                closed_legal_type = type;
                closed_legal_count = count;
            }else if(i==1){
                closed_general_type = type;
                closed_general_count = count;
            }
        }
        ActiveModel activeModel = new ActiveModel(activeTotal,active_legal_type,active_legal_count,active_general_type,active_general_count,closedTotal,closed_legal_type,closed_legal_count,closed_general_type,closed_general_count);

        Log.d("MatterDataLoaded","MatterDataLoaded Successfull");
        if (Constants.ROLE.equalsIgnoreCase("SU")){
            itemArrayList.add(new Item(4,activeModel));
            callNewClientWebservice(newclientEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("GH")){
            itemArrayList.add(new Item(5,activeModel));
            callRelationshipWebservice(relationshipsEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("TM")){
            itemArrayList.add(new Item(4,activeModel));
         loadRecyclerview("TM_KPI");
        }


    }

    private void loadTimesheetsdata(JSONObject data) throws JSONException {
        JSONObject submittedDates = data.getJSONObject("submittedDates");
        JSONObject pendingDates = data.getJSONObject("pendingDates");
        String startDate = pendingDates.getString("startDate");
        String endDate = pendingDates.getString("endDate");
        String submitted_start_date = submittedDates.getString("startDate");
        String submitted_end_date = submittedDates.getString("endDate");
        String pending_start_date = pendingDates.getString("startDate");
        String pending_end_date = pendingDates.getString("endDate");
        if (Constants.ROLE.equalsIgnoreCase("GH")) {
            TimeSheetModel timeSheetModel = new TimeSheetModel(submitted_start_date, submitted_end_date, pending_start_date, pending_end_date);
            itemArrayList.add(new Item(4, timeSheetModel));

        }else if(Constants.ROLE.equalsIgnoreCase("TM")){
            SubmittedTimesheetModel submittedTimesheetModel = new SubmittedTimesheetModel(submitted_start_date,submitted_end_date);
            itemArrayList.add(new Item(2,submittedTimesheetModel));
            PendingTimeSheetsModel pendingTimeSheetsModel = new PendingTimeSheetsModel(pending_start_date,pending_end_date);
            itemArrayList.add(new Item(3,pendingTimeSheetsModel));
        }
        callMatterWebservice(matterEndpoint);

    }

    private void loadStoragedata(JSONObject data)throws JSONException {
        Log.d("Storage","Storage_Successful");
        int balanceStorage = data.getInt( "balanceStorage");
        int currentStorage = data.getInt("currentStorage");
        int totalStorage = data.getInt("totalStorage");
        StorageModel storageModel = new StorageModel(balanceStorage,currentStorage,totalStorage);
        if (Constants.ROLE.equalsIgnoreCase("AAM")) {
            itemArrayList.add(new Item(3,storageModel));
            callSubscriptionWebservice(subscriptionEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("SU")){
            itemArrayList.add(new Item(7,storageModel));
            Log.d("KPI_Storage","Storage_Successful");
            loadRecyclerview("SU_KPI");
        }else if(Constants.ROLE.equalsIgnoreCase("GH")){
            itemArrayList.add(new Item(7,storageModel));
            loadRecyclerview("GH_KPI");
        }



    }

    private void loadHiringData(JSONArray data)throws JSONException {
        Log.d("Hiring","Hiring Successfull");
        String corporateType = "";
        int corporateCount = 0;
        String criminalType = "";
        int criminalCount = 0;
        for (int i=0;i<data.length();i++){
            JSONObject jsonObject = data.getJSONObject(i);
            String type = jsonObject.getString("type");
            int count = jsonObject.getInt("count");
            if (i == 0) { // Assuming the first entry is for corporate
                corporateType = type;
                corporateCount = count;
            } else if (i == 1) { // Assuming the second entry is for criminal
                criminalType = type;
                criminalCount = count;
            }
        }
        HiringModel hiringModel = new HiringModel(corporateType, corporateCount, criminalType, criminalCount);
        if (Constants.ROLE.equalsIgnoreCase("AAM")){
            itemArrayList.add(new Item(2, hiringModel));
        }else if (Constants.ROLE.equalsIgnoreCase("SU")){
            itemArrayList.add(new Item(6, hiringModel));
        }

        callStorageWebservice(storageEndpoint);
    }

    private void loadHoursdata(JSONObject data)throws JSONException {
        Log.d("Hours","Hours Sucessfull");
        String approxRevenue = data.getString("approxRevenue");
        String averageBillingRate = data.getString("averageBillingRate");
        String billableHours = data.getString("billableHours");
        String billablePercentage = data.getString("billablePercentage");
        String nonBillableHours = data.getString("nonBillableHours");
        String nonBillablePercentage = data.getString("nonBillablePercentage");
        ApproxRevenueModel approxRevenueModel = new ApproxRevenueModel(approxRevenue);
        AverageBillingRateModel averageBillingRateModel = new AverageBillingRateModel(averageBillingRate);
        BillableModel billableModel = new BillableModel(billableHours,billablePercentage);
        NonBillableModel nonBillableModel = new NonBillableModel(nonBillableHours,nonBillablePercentage);

        if (Constants.ROLE.equalsIgnoreCase("SU")){

            itemArrayList.add(new Item(0,billableModel));
            itemArrayList.add(new Item(1,nonBillableModel));
            itemArrayList.add(new Item(2,approxRevenueModel));
            itemArrayList.add(new Item(3,averageBillingRateModel));
            callMatterWebservice(matterEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("GH")){

            itemArrayList.add(new Item(0,billableModel));
            itemArrayList.add(new Item(1,nonBillableModel));
            itemArrayList.add(new Item(2,approxRevenueModel));
            itemArrayList.add(new Item(3,averageBillingRateModel));
            callTimesheetWebservice(timesheetEndpoint);
        }else if(Constants.ROLE.equalsIgnoreCase("TM")){
            itemArrayList.add(new Item(0,billableModel));
            itemArrayList.add(new Item(1,nonBillableModel));
            callTimesheetWebservice(timesheetEndpoint);

        }

    }

    private void loadEmaildata(JSONObject data) throws JSONException{
        String timeStamp = data.getString("timestamp");
        String message  = data.getString("message");
        String subject = data.getString("user");
    }

    private void loadTeamdata(JSONObject data) throws JSONException {
        JSONObject client = data.getJSONObject("team");
        String timeStamp = client.getString("timestamp");
        String message  = client.getString("message");
        String user = client.getString("user");
//        String originalDateString = "202207011230";

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        SimpleDateFormat desiredFormat = new SimpleDateFormat("E hh:mm a", Locale.getDefault());

        try {
            Date date = originalFormat.parse(timeStamp);
            String formattedDate = desiredFormat.format(date);
            TeamChatModel teamChatModel = new TeamChatModel(formattedDate,user,message);
//            if (Constants.ROLE.equalsIgnoreCase("AAM")){
                itemArrayList.add(new Item(2,teamChatModel));
//            }else{
//                itemArrayList.add(new Item(1,teamChatModel));
//            }
//            System.out.println(formattedDate); // Output: Fri 11:30 AM
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception
        }

//        itemArrayList
//        teamChatModel.setTime(timeStamp);
//        teamChatModel.setTm_message(message);
//        teamChatModel.setTm_name(user);



       callNotificationWebservice(notificationEndpoint);
    }

    private void loadClientdata(JSONObject  data) throws JSONException {
        JSONObject team = data.getJSONObject("client");
        String timeStamp = team.getString("timestamp");
        String message  = team.getString("message");
        String user = team.getString("user");
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        SimpleDateFormat desiredFormat = new SimpleDateFormat("E hh:mm a", Locale.getDefault());

        try {
            Date date = originalFormat.parse(timeStamp);
            String formattedDate = desiredFormat.format(date);
            ClientChatModel clientChatModel = new ClientChatModel(formattedDate,user,message);
            if (Constants.ROLE.equalsIgnoreCase("AAM")){

            }else{
                itemArrayList.add(new Item(1,clientChatModel));
            }
//            System.out.println(formattedDate); // Output: Fri 11:30 AM
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception
        }


    loadTeamdata(data);
    }

    private void loadMeetingData(JSONObject data)  throws JSONException{
        String date  = data.getString("date");
        String from_ts = data.getString("fromTs");
        String to_ts = data.getString("toTs");
        String subject = data.getString("subject");
        MeetingModel meetingModel = new MeetingModel(date,from_ts,to_ts,subject);
        itemArrayList.add(new Item(0, meetingModel));

        callClientTeamWebservice(clientTeamApiEndpoint);
    }
}
