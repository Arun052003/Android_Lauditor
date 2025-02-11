package com.digicoffer.lauditor.Matter;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.HistoryModel;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
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

import java.util.ArrayList;

public class Matter extends Fragment implements AsyncTaskCompleteListener {

    com.google.android.material.imageview.ShapeableImageView siv_matter_icon, siv_groups, siv_documents;
    private HorizontalScrollView scrollView;
    private TextView tv_legal_matter, tv_general_matter;
    private TextView tv_create;
    private TextView tv_view;
    private NewModel mViewModel;
    AlertDialog progress_dialog;
    ViewMatter chk_viewMatter;
    JSONArray jsonArray = new JSONArray();
    TextView matter_info_txt;
    ArrayList<ViewMatterModel> itemsArrayList = new ArrayList<>();
    public ArrayList<MatterModel> matter_arraylist;
    public LinearLayoutCompat create_matter_view, ll_matter_type, ll_create_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_matter, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        matter_info_txt = view.findViewById(R.id.matter_info_txt);
        matter_info_txt.setText(R.string.matter_information);
        TextView matter_gct_txt = view.findViewById(R.id.matter_gct_txt);
        matter_gct_txt.setText(R.string.group_s_clients_amp_team_member_s);
        TextView matter_doc_txt = view.findViewById(R.id.matter_doc_txt);
        matter_doc_txt.setText(R.string.document_s);
        mViewModel.setData("Matter");
//        if(Constants.create_matter)
//            Constants.alert_name="Info";
//        else Constants.alert_name="";

        siv_matter_icon = view.findViewById(R.id.siv_matter_icon);
        create_matter_view = view.findViewById(R.id.create_matter_view);
        ll_matter_type = view.findViewById(R.id.ll_matter_type);
        ll_create_view = view.findViewById(R.id.ll_create_view);
        siv_groups = view.findViewById(R.id.siv_groups);
        siv_documents = view.findViewById(R.id.siv_documents);
        tv_legal_matter = view.findViewById(R.id.tv_legal_matter);

        tv_legal_matter.setText(R.string.legal_matter);
        tv_general_matter = view.findViewById(R.id.tv_general_matter);
        tv_general_matter.setText(R.string.general_matter);
        tv_create = view.findViewById(R.id.tv_create_matter);

        tv_create.setText(R.string.create);
        tv_view = view.findViewById(R.id.tv_view_matter);
        tv_view.setText(R.string.view);
//        siv_upload = view.findViewById(R.id.upload_icon);
//        siv_view = view.findViewById(R.id.view_icon);
        // loadViewUI();

        //Call The Group List API
        callGroupsWebservice();
        if (Constants.MATTER_TYPE.equals("General"))
            loadGeneralMatter();
        else
            loadLegalMatter();

        tv_legal_matter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Filled Data will be cleared when we click the legal matter view.
                matter_arraylist.clear();
                loadLegalMatter();
            }
        });
        tv_general_matter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Filled Data will be cleared when we click the general matter view.
                matter_arraylist.clear();
                loadGeneralMatter();
            }
        });
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.create_matter = true;
                Constants.Matter_CreateOrViewDetails = "Create";
                matter_arraylist.clear();
                loadCreateUI();
            }
        });

        tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewUI();
//                loadgeneralUI();
            }
        });

        siv_matter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constants.create_matter) {
                    chk_viewMatter.openViewDetailsPopUp();
                    Matter_Notes();
                }
                if (!matter_arraylist.isEmpty() && Constants.Matter_CreateOrViewDetails.equalsIgnoreCase("Create")) {
                    loadMatterInformation();
                }
            }
        });
        siv_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matter_arraylist.isEmpty()) {
                    AndroidUtils.showAlert("Please check the Matter Information section", getContext());
                } else if (!Constants.create_matter) {
                    Matter_Gct();
                } else {
                    loadGCT();
                }
            }
        });
        siv_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking Required information is filled in Matter Information page and if the One of the Groups are chosen in Gct page.
                if (!matter_arraylist.isEmpty()) {
                    for (int i = 0; i < matter_arraylist.size(); i++) {
                        MatterModel matterModel = matter_arraylist.get(i);
                        if (matterModel.getGroup_acls() != null) {
                            loadDocuments();
                        } else if (!Constants.create_matter) {
                            Matter_Doc();
                        } else {
                            AndroidUtils.showAlert("Please check Group(S),clients & Team member(S) section", getContext());
                        }
                    }
                } else {
                    AndroidUtils.showAlert("Please check the Matter Information section", getContext());
                }
            }
        });
        matter_arraylist = new ArrayList<>();
        return view;
    }

    public ArrayList<MatterModel> getMatter_arraylist() {
        return matter_arraylist;
    }

    //    public MatterModel matterModel
    void loadViewUI() {
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_create.setTextColor(Color.BLACK);
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_view.setTextColor(Color.WHITE);
        create_matter_view.setVisibility(View.GONE);
        ll_matter_type.setVisibility(View.VISIBLE);
        ll_create_view.setVisibility(View.VISIBLE);
        viewMatter();
        mViewModel.setData("View Legal Matter");
    }

    void display_timeline(ArrayList<HistoryModel> historyList, ViewMatter viewMatter, String header_name) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment childFragment = new timeline(historyList, viewMatter, header_name, this);
        ft.replace(R.id.child_container, childFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void viewMatter() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ViewMatter matterInformation = new ViewMatter();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void loadCreateUI() {
        create_matter_view.setVisibility(View.VISIBLE);
        if (Constants.Matter_CreateOrViewDetails.equalsIgnoreCase("Create")) {
            tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
            tv_create.setTextColor(Color.WHITE);
            tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
            tv_view.setTextColor(Color.BLACK);
            loadMatterInformation();
            mViewModel.setData("Create Legal Matter");
        }
    }


    void loadLegalMatter() {
        Constants.MATTER_TYPE = "Legal";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_legal_matter.setTextColor(Color.WHITE);
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_general_matter.setTextColor(Color.BLACK);
        loadMatterInformation();
        loadViewUI();
        mViewModel.setData(" View Legal Matter");
    }

    void loadGeneralMatter() {
        Constants.MATTER_TYPE = "General";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_legal_matter.setTextColor(Color.BLACK);
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_general_matter.setTextColor(Color.WHITE);
        loadMatterInformation();
        loadViewUI();
        mViewModel.setData(" View General Matter");
    }

    public void loadDocuments() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterDocuments matterInformation = new MatterDocuments();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void loadGCT() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.group_green_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment childFragment = new GCT();
        ft.replace(R.id.child_container, childFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    void Matter_Notes() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_green));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_groups.setClickable(true);
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        siv_documents.setClickable(true);
    }

    void Matter_Gct() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.group_green_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment childFragment = new GCT();
        ft.replace(R.id.child_container, childFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    void Matter_Doc() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterDocuments matterInformation = new MatterDocuments();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    void loadMatterInformation() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_groups.setClickable(true);
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        siv_documents.setClickable(true);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterInformation matterInformation = new MatterInformation();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void View_Details(ViewMatterModel viewMatterModel, ViewMatter viewMatter, ArrayList<HistoryModel> historyList, String header_name) {
        chk_viewMatter = viewMatter;
        Constants.create_matter = false;
        ll_matter_type.setVisibility(View.GONE);
        ll_create_view.setVisibility(View.GONE);
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_green));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_groups.setClickable(true);
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        siv_documents.setClickable(true);
        create_matter_view.setVisibility(View.VISIBLE);
        matter_info_txt.setText(R.string.time_line);
        matter_arraylist.add(0, viewMatterModel);
        jsonArray.put(viewMatterModel.getGroupAcls());
        Constants.ex_group_attachment = viewMatterModel.getGroupAcls();
        Constants.ex_client = viewMatterModel.getClients();
//        AndroidUtils.showAlert("" + Constants.ex_attachment, getContext());
        Constants.Matter_id = "";
        Constants.Matter_id = viewMatterModel.getId();
        display_timeline(historyList, viewMatter, header_name);
    }

    private void callGroupsWebservice() {
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", postdata.toString());
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
                if (httpResult.getRequestType().equals("Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadGroupsData(data);
                }
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
    }

    private void loadGroupsData(JSONArray data) {
        try {
            Constants.groupsList_Access.clear();
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                GroupsModel groupsModel = new GroupsModel();
                groupsModel.setGroup_id(jsonObject.getString("id"));
                groupsModel.setGroup_name(jsonObject.getString("name"));
                Constants.groupsList_Access.add(groupsModel);
            }
        } catch (JSONException e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }
}
