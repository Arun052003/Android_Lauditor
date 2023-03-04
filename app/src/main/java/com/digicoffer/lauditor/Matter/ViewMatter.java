package com.digicoffer.lauditor.Matter;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Matter.Adapters.ViewMatterAdapter;
import com.digicoffer.lauditor.Matter.Models.HistoryModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ViewMatter extends Fragment implements AsyncTaskCompleteListener, ViewMatterAdapter.InterfaceListener {
    TextInputLayout search_matter;
    RecyclerView rv_matter_list;
    TextInputEditText et_search_matter;
    AlertDialog progressDialog;
    ArrayList<ViewMatterModel> matterList = new ArrayList<>();
    ArrayList<HistoryModel> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_matter, container, false);
        rv_matter_list = view.findViewById(R.id.rv_matter_list);
        search_matter = view.findViewById(R.id.search_matter);
        et_search_matter = view.findViewById(R.id.et_search_matter);
        callMatterListWebservice();
        return view;
    }

    private void callMatterListWebservice() {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/" + Constants.MATTER_TYPE.toLowerCase(Locale.ROOT), "Matter List", postdata.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog != null && progressDialog.isShowing())
            AndroidUtils.dismiss_dialog(progressDialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                boolean error = result.getBoolean("error");

                if (httpResult.getRequestType().equals("Matter List")) {
                    if (error) {
                        String msg = result.getString("msg");
                        AndroidUtils.showToast(msg, getContext());
                    } else {
                        JSONArray matters = result.getJSONArray("matters");
                        loadMattersList(matters);
                    }
                } else if (httpResult.getRequestType().equals("TimeLine")) {
                    if (error) {
                        String msg = result.getString("msg");
                        AndroidUtils.showToast(msg, getContext());
                    } else {
                        loadHistory(result);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadHistory(JSONObject result) {
        try {
            JSONArray jsonArray = result.getJSONArray("history");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HistoryModel historyModel = new HistoryModel();
                if (jsonObject.has("allday")){
                    historyModel.setAllday(jsonObject.getBoolean("allday"));
                }
                historyModel.setId(jsonObject.getString("id"));
                historyModel.setDescription(jsonObject.getString("description"));
                historyModel.setEvent_type(jsonObject.getString("event_type"));
                historyModel.setFrom_ts(jsonObject.getString("from_ts"));
                historyModel.setTo_ts(jsonObject.getString("to_ts"));
                historyModel.setTitle(jsonObject.getString("title"));
                historyModel.setNotes(jsonObject.getString("notes"));
                historyList.add(historyModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        openViewDetailsPopUp();
    }

    private void openViewDetailsPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_opponent_advocate, null);


    }

    private void loadMattersList(JSONArray matters) {
        try {
            for (int i = 0; i < matters.length(); i++) {
                JSONObject jsonObject = matters.getJSONObject(i);
                ViewMatterModel viewMatterModel = new ViewMatterModel();
                viewMatterModel.setId(jsonObject.getString("id"));
                viewMatterModel.setCaseNumber(jsonObject.getString("caseNumber"));
                viewMatterModel.setCasetype(jsonObject.getString("caseType"));
                viewMatterModel.setClients(jsonObject.getJSONArray("clients"));
                viewMatterModel.setCourtName(jsonObject.getString("courtName"));
                viewMatterModel.setDate_of_filling(jsonObject.getString("date_of_filling"));
                viewMatterModel.setDescription(jsonObject.getString("description"));
                viewMatterModel.setDocuments(jsonObject.getJSONArray("documents"));
                viewMatterModel.setGroupAcls(jsonObject.getJSONArray("groupAcls"));
                viewMatterModel.setGroups(jsonObject.getJSONArray("groups"));
                viewMatterModel.setHearingDateDetails(jsonObject.getJSONObject("hearingDateDetails"));
                viewMatterModel.setIs_editable(jsonObject.getBoolean("is_editable"));
                viewMatterModel.setJudges(jsonObject.getString("judges"));
                viewMatterModel.setMatterClosedDate(jsonObject.getString("matterClosedDate"));
                viewMatterModel.setMembers(jsonObject.getJSONArray("members"));
                viewMatterModel.setNextHearingDate(jsonObject.getString("nextHearingDate"));
                viewMatterModel.setOpponentAdvocates(jsonObject.getJSONArray("opponentAdvocates"));
                viewMatterModel.setOwner(jsonObject.getJSONObject("owner"));
                viewMatterModel.setPriority(jsonObject.getString("priority"));
                viewMatterModel.setStatus(jsonObject.getString("status"));
                viewMatterModel.setTags(jsonObject.getJSONObject("tags"));
                viewMatterModel.setTempClients(jsonObject.getJSONArray("tempClients"));
                viewMatterModel.setTemporaryClients(jsonObject.getJSONArray("temporaryClients"));
                viewMatterModel.setTitle(jsonObject.getString("title"));
                matterList.add(viewMatterModel);
            }
            loadMatterRecyclerview();
        } catch (JSONException e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
            e.printStackTrace();
        }
    }

    private void loadMatterRecyclerview() {
        try {
            rv_matter_list.setLayoutManager(new GridLayoutManager(getContext(), 1));
            ViewMatterAdapter viewMatterAdapter = new ViewMatterAdapter(matterList, getContext(), this);
            rv_matter_list.setAdapter(viewMatterAdapter);
            rv_matter_list.setHasFixedSize(true);
            et_search_matter.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    viewMatterAdapter.getFilter().filter(s);
                }

            });
            viewMatterAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    @Override
    public void View_Details(ViewMatterModel viewMatterModel) {
        callTimeLineWebservice(viewMatterModel.getId());
    }

    private void callTimeLineWebservice(String id) {
        try {
            progressDialog = AndroidUtils.get_progress(getActivity());
            JSONObject postdata = new JSONObject();
            Calendar calendar = new GregorianCalendar();
            TimeZone timeZone = calendar.getTimeZone();
            int offset = timeZone.getRawOffset();
            long hours = TimeUnit.MILLISECONDS.toMinutes(offset);
            long timezoneoffset = (-1) * (hours);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "matter/" + Constants.MATTER_TYPE + "/" + id + "/history/" + timezoneoffset, "TimeLine", postdata.toString());

        } catch (Exception e) {
            if (progressDialog != null || progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
            e.printStackTrace();
        }
    }

    @Override
    public void DeleteMatter(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList) {

    }

    @Override
    public void Edit_Matter_Info(ViewMatterModel viewMatterModel, ArrayList<ViewMatterModel> itemsArrayList) {

    }

    @Override
    public void Update_Group(ViewMatterModel viewMatterModel) {

    }

    @Override
    public void Close_Matter(ViewMatterModel viewMatterModel) {

    }
}
