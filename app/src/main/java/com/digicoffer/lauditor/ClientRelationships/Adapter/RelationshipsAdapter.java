package com.digicoffer.lauditor.ClientRelationships.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.ClientRelationships.Model.ProfileDo;
import com.digicoffer.lauditor.ClientRelationships.Model.RelationshipsModel;
import com.digicoffer.lauditor.ClientRelationships.Model.SharedDocumentsDo;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.Members.GroupsAdapter;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class RelationshipsAdapter extends RecyclerView.Adapter<RelationshipsAdapter.MyViewHolder> implements Filterable, AsyncTaskCompleteListener, SharedDocumentsAdapter.EventListener {
    ArrayList<RelationshipsModel> relationshipsList = new ArrayList<>();
    ArrayList<RelationshipsModel> itemsList = new ArrayList<>();
    ArrayList<ProfileDo> citizenList = new ArrayList<>();
    int position = 0;
    AlertDialog progress_dialog;
    Context mcontext;
    ArrayList<ViewGroupModel> updatedMembersList = new ArrayList<>();
    ArrayList<SharedDocumentsDo>shared_list = new ArrayList<>();
    RelationshipsAdapter.MyViewHolder mholder;
    RelationshipsModel relationshipsModel_new;
    ArrayList<ViewGroupModel> groupsList = new ArrayList<>();
    AlertDialog ad_dialog, ad_dialog_delete,ad_dialog_copy,ad_dialog_docs;
    ViewGroup mParent;
    String shared_tag = "";
    String doc_nature = "";
    private  String shared_relationship_id = "";
    FragmentActivity mActivity;
    RelationshipsAdapter.EventListener eventListener;
    private RelationshipsModel relationshipmodel_profile;
    private String FLAG = "";
    View view = null;
//    ScrollView sv_relationships;

    public RelationshipsAdapter(ArrayList<RelationshipsModel> relationshipsList, Context context, FragmentActivity activity, EventListener listener) {
        this.relationshipsList = relationshipsList;
        this.itemsList = relationshipsList;
        this.mcontext = context;
        this.mActivity = activity;
        this.eventListener = listener;
//        this.sv_relationships = sv_relationships;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    relationshipsList = itemsList;
                } else {
                    ArrayList<RelationshipsModel> filteredList = new ArrayList<>();
                    for (RelationshipsModel row : itemsList) {
//                            if (row.isChecked()){
//                                row.setChecked(false);
//                            }else
//                            {
//                                row.setChecked(true  );
//                            }
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (AndroidUtils.isNull(row.getName()).toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }
                    relationshipsList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.count = relationshipsList.size();
                filterResults.values = relationshipsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                relationshipsList = (ArrayList<RelationshipsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void CopyDocument(SharedDocumentsDo sharedDocumentsDo) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mcontext);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            tv_confirmation.setText("Are you sure you want to copy " + sharedDocumentsDo.getName() + "?");
            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            bt_yes.setText("Copy");
            btn_no.setText("Cancel");
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad_dialog_copy.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String action = "copy";
                   callCopyDocumentWebservice(sharedDocumentsDo,action);
                }
            });
            final AlertDialog dialog = dialogBuilder.create();
            ad_dialog_copy = dialog;
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), mcontext);
        }
    }

    @Override
    public void viewDocument(SharedDocumentsDo sharedDocumentsDo) {
        String action = "view";
       callCopyDocumentWebservice(sharedDocumentsDo, action);
    }

    private void callCopyDocumentWebservice(SharedDocumentsDo sharedDocumentsDo, String action) {
        JSONObject jsonObject = new JSONObject();
        String request = "";
        if (action=="copy"){
            request = "Copy Document";
        }else
        {
            request = "View Document";
        }
        WebServiceHelper.callHttpWebService(this,mcontext, WebServiceHelper.RestMethodType.GET,"v2/relationship/"+shared_relationship_id+"/"+sharedDocumentsDo.getId()+"/"+action,request,jsonObject.toString());
    }

    public interface EventListener {
        void RefreshViewRelationshipsData();
    }

    @NonNull
    @Override
    public RelationshipsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mParent = parent;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_relationships_design, parent, false);
        return new RelationshipsAdapter.MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RelationshipsAdapter.MyViewHolder holder, int position) {
//        updatedMembersList.clear();
        RelationshipsModel relationshipsModel = relationshipsList.get(position);
        position = position;
        mholder = holder;
//        notifyDataSetChanged();
        boolean isExpandable = relationshipsList.get(position).isExpandable();

        if (isExpandable){
            unhideProfileDetails(relationshipsModel,holder);
        }else
        {
            citizenList.clear();
            shared_list.clear();
//            holder.ll_expandable_layout.setVisibility(View.GONE);
//            holder.rg_shared_status.clearCheck();
//            holder.rg_profile.clearCheck();
        }
        holder.ll_expandable_layout.setVisibility(isExpandable?View.VISIBLE:View.GONE);
        if (relationshipsModel.isCanAccept()) {
            holder.btn_accept.setVisibility(View.VISIBLE);
            holder.iv_groups_relationships.setVisibility(View.GONE);
        }
        {
            holder.btn_accept.setVisibility(View.GONE);
            holder.iv_groups_relationships.setVisibility(View.VISIBLE);
//            ImageView iv_initiated = view.findViewById(R.id.iv_initiated);
            if (relationshipsModel.isAccepted()) {
                holder.tv_initiated.setText("Accepted");

                holder.iv_initiated.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.green_circular));
//                notifyDataSetChanged();
            } else {
                holder.tv_initiated.setText("Not Accepted");
                holder.iv_initiated.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.red_circular));
//                notifyDataSetChanged();
            }
        }
        Log.i("Tag", "Relationship:" + relationshipsModel.getAdminName());
        holder.tv_relationship_name.setText(relationshipsModel.getAdminName());
        holder.tv_created_date.setText("Created " + relationshipsModel.getCreated());
        holder.tv_consumer.setText(relationshipsModel.getClientType());
//                    if (relationshipsModel.isAccepted()){
//                        holder.tv_initiated.setText("Accepted");
//                    }else{
//                        holder.tv_initiated.setText("Not Accepted");
//                    }
        holder.iv_groups_relationships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    groupsList.clear();
                    updatedMembersList.clear();
                    callGroupsWebservice(holder, relationshipsModel);
//                    holder.tv_relationship_name.setText("Sai");
//                    holder.cv_documents.setVisibility(View.VISIBLE);
//                    holder.ll_documents.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    AndroidUtils.showToast(e.getMessage(), view.getContext());
                    e.printStackTrace();
                }
            }
        });
//        holder.iv_groups_relationships.setOnClickListener((View.OnClickListener) this);
        String text = "More Details";
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }
        };
        ss.setSpan(clickableSpan, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tv_more_details.setText(ss);
        holder.tv_more_details.setMovementMethod(LinkMovementMethod.getInstance());
        FLAG = "first_click";

        holder.iv_delete_relationships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRelationships(relationshipsModel, relationshipsModel.getName());
            }
        });
        holder.rg_profile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.rb_profile:
                        unhideProfileDetails(relationshipsModel, holder);

                        break;
                    case R.id.rb_share_button:
                        shared_tag = "withme";
                        holder.rb_share_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.radiobutton_centre_green_background));
                        holder.rb_profile.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_left_background));

                        unhideSharedDocumentsdata(holder,shared_tag,relationshipsModel);
//                        holder.rb_shared_with_us.setChecked(true);
//                               hideProfileData(holder);
                }
            }
        });
        holder.iv_share_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.iv_share_docs.setBackgroundColor(mcontext.getResources().getColor(R.color.green_count_color));
//
                holder.rb_shared_with_us.setBackground(mcontext.getResources().getDrawable(R.drawable.button_left_background));
                holder.rb_shared_by_us.setBackground(mcontext.getResources().getDrawable(R.drawable.button_right_background));
                holder.rg_document_type.setVisibility(View.VISIBLE);
                holder.rb_client_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_left_green_background));
                holder.rb_firm_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_right_background));
                shared_tag  = "client";
                callDocumentTypeWebservice(relationshipsModel.getId(),shared_tag,holder,doc_nature);

            }
        });
        holder.rg_shared_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                holder.nestedScrollView.setVisibility(View.VISIBLE);
                holder.ll_documents.setVisibility(View.VISIBLE);
                switch (i) {
                    case R.id.rb_shared_with_us:
                        try {
                            shared_tag = "withme";
                            holder.rb_shared_with_us.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_left_green_background));
                            holder.rb_shared_by_us.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_right_background));
                            unhideSharedDocumentsdata(holder, shared_tag, relationshipsModel);
                        } catch (Resources.NotFoundException e) {
                            AndroidUtils.showToast(e.getMessage(),mcontext);
                        }
                        break;
                    case R.id.rb_shared_by_us:
                        try {
                            holder.rb_shared_with_us.setBackground(mcontext.getResources().getDrawable(R.drawable.button_left_background));
                            holder.rb_shared_by_us.setBackground(mcontext.getResources().getDrawable(R.drawable.button_right_green_count));
                            shared_tag = "byme";

                            unhideSharedDocumentsdata(holder, shared_tag, relationshipsModel);
                        } catch (Resources.NotFoundException e) {
                            AndroidUtils.showToast(e.getMessage(),mcontext);
                        }
//                     callSharedDocumentsWebservice(relationshipsModel.getId(),shared_tag,holder);
                        break;
                }
            }
        });
        holder.rg_document_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_client_document:
                        holder.rb_client_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_left_green_background));
                        holder.rb_firm_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_right_background));
                        shared_tag  = "client";

                        callDocumentTypeWebservice(relationshipsModel.getId(),shared_tag,holder,doc_nature);
                        break;
                    case R.id.rb_firm_document:
                        holder.rb_client_document.setBackground(mcontext.getResources().getDrawable(R.drawable.button_left_background));
                        holder.rb_firm_document.setBackground(mcontext.getResources().getDrawable(R.drawable.button_right_green_count));
                        shared_tag = "firm";

                        callDocumentTypeWebservice(relationshipsModel.getId(),shared_tag,holder,doc_nature);
//                        callSharedDocumentsWebservice();
                }
            }
        });
    }

    private void callDocumentTypeWebservice(String id, String shared_tag, MyViewHolder holder, String doc_nature) {
        progress_dialog = AndroidUtils.get_progress(mActivity);
        mholder = holder;
        shared_relationship_id = id;
        JSONObject jsonObject = new JSONObject();
        WebServiceHelper.callHttpWebService(this,mcontext, WebServiceHelper.RestMethodType.GET,"v3/documents/"+shared_tag,"Existing Documents",jsonObject.toString());
    }

    private void unhideSharedDocumentsdata(MyViewHolder holder, String shared_tag, RelationshipsModel relationshipsModel) {
        holder.cv_Profile.setVisibility(View.GONE);
         holder.rg_shared_status.setVisibility(View.VISIBLE);
//                        holder.ll_documents.setVisibility(View.VISIBLE);
        holder.nestedScrollView.setVisibility(View.GONE);
        holder.rg_document_type.setVisibility(View.GONE);
        holder.ll_documents.setVisibility(View.GONE);
        holder.iv_share_docs.setVisibility(View.VISIBLE);
        shared_list.clear();

        callSharedDocumentsWebservice(relationshipsModel.getId(), shared_tag,holder);
//
    }

    private void openSharedPopupWindow(ArrayList<SharedDocumentsDo> shared_list) {
        try {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mcontext);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.shared_document_recyclerview_popup, null);
//        sv_relationships.getForeground().setAlpha(220);
        RecyclerView rv_relationship_groups;
        TextInputEditText et_search_relationships,et_message;
            TextInputLayout el_search;
        TextView tv_header_name,tv_message;
        LinearLayoutCompat ll_buttons;
        LinearLayout ll_message,ll_hide_data;
        ImageView iv_cancel;
        AppCompatButton btn_send_request, btn_relationships_cancel,btn_cancel_message,btn_message_send_request;
        ll_message = view.findViewById(R.id.ll_message);
        et_message = view.findViewById(R.id.et_message);
        ll_hide_data = view.findViewById(R.id.ll_hide_data);
        el_search = view.findViewById(R.id.el_search);
        btn_cancel_message = view.findViewById(R.id.btn_message_cancel);
        btn_message_send_request = view.findViewById(R.id.btn_message_send_request);
        rv_relationship_groups = view.findViewById(R.id.rv_relationship_documents);
        et_search_relationships = view.findViewById(R.id.et_search_relationships);
        btn_send_request = view.findViewById(R.id.btn_send_request);
        ll_buttons = view.findViewById(R.id.ll_buttons);
        tv_header_name = view.findViewById(R.id.header_name);
        String header_name = "";
//        String doctype = "";
        if (shared_tag=="withme"){
         header_name    = "Documents Shared With Us";
         ll_buttons.setVisibility(View.GONE);

//            https://apidev2.digicoffer.com/cors/professional/v2/relationship/6390695ea1db7204220cff88/docs/share
        }else if (shared_tag=="byme")
        {
            ll_buttons.setVisibility(View.VISIBLE);
            header_name = "Documents Shared By Us";

        }
        else if (shared_tag=="client"){
            header_name = "Client Documents";
            btn_message_send_request.setText("Share");
            btn_send_request.setText("Share");
        }
        else{
            header_name = "Firm Documents";
            btn_message_send_request.setText("Share");
            btn_send_request.setText("Share");
        }

        tv_header_name.setText(header_name);
        tv_message  = view.findViewById(R.id.message);
        if (shared_list.size()==0){
            tv_message.setVisibility(View.VISIBLE);
            ll_buttons.setVisibility(View.GONE);
            el_search.setVisibility(View.GONE);
            et_message.setVisibility(View.GONE);
        }
        btn_relationships_cancel = view.findViewById(R.id.btn_relationships_cancel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false);
        rv_relationship_groups.setLayoutManager(layoutManager);
        rv_relationship_groups.setHasFixedSize(true);
        SharedDocumentsAdapter documentsAdapter = new SharedDocumentsAdapter(shared_list, shared_tag, mcontext, this);
        rv_relationship_groups.setAdapter(documentsAdapter);
        et_search_relationships.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                documentsAdapter.getFilter().filter(et_search_relationships.getText().toString());
            }

        });
        final AlertDialog dialog = dialogBuilder.create();
        ad_dialog_docs = dialog;
        btn_relationships_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                shared_list.clear();
                dialog.dismiss();
            }
        });
        JSONArray remove = new JSONArray();
        iv_cancel =view.findViewById(R.id.close_documents);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                shared_list.clear();
                dialog.dismiss();
            }
        });
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                try {
                    for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                        SharedDocumentsDo sharedDocumentsDo = documentsAdapter.getList_item().get(i);
                        JSONObject jsonObject1 = new JSONObject();
                        if (sharedDocumentsDo.isChecked()) {
                            jsonObject1.put("docid", sharedDocumentsDo.getId());
                            jsonObject1.put("doctype", "general");
                            remove.put(jsonObject1);
                        }
                    }
                    if (remove.length()!=0) {
                        ll_buttons.setVisibility(View.GONE);
                        ll_hide_data.setVisibility(View.GONE);
                        ll_message.setVisibility(View.VISIBLE);
                    }else{
                        AndroidUtils.showToast("Please select atleast one document",mcontext);
                    }

//
//                    callUpdateGroupAcess(relationshipsModel_new.getId(), documentsAdapter.getList_item());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_cancel_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_buttons.setVisibility(View.VISIBLE);
                ll_message.setVisibility(View.GONE);
                ll_hide_data.setVisibility(View.VISIBLE);
                et_message.setText("");
            }
        });
        btn_message_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callUnshareDocumentWebservice(shared_relationship_id, remove,et_message.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.setView(view);
        dialog.show();
        } catch (Exception e) {
            AndroidUtils.showAlert(e.getMessage(),mcontext);
            e.printStackTrace();
        }
    }

    private void callUnshareDocumentWebservice(String id, JSONArray remove, String s) throws JSONException{
        progress_dialog = AndroidUtils.get_progress(mActivity);
        JSONObject jsonObject = new JSONObject();
        JSONArray add = new JSONArray();
//        JSONArray remove = new JSONArray();


        if(remove.length()!=0) {
            if (shared_tag == "byme"){
                jsonObject.put("remove", remove);
                jsonObject.put("add", add);
            }else {
                jsonObject.put("remove", add);
                jsonObject.put("add", remove);
            }

            jsonObject.put("message", s);

//        AndroidUtils.showAlert(jsonObject.toString(),mcontext);
            WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.PUT, "v2/relationship/" + id + "/docs/share", "UnshareDocuments", jsonObject.toString());
        }else{
            AndroidUtils.showToast("Please select atleast one document",mcontext);
        }
         }

    private void callSharedDocumentsWebservice(String id, String shared_tag, MyViewHolder holder) {
        progress_dialog = AndroidUtils.get_progress(mActivity);
        mholder = holder;
        shared_relationship_id = id;
        JSONObject jsonObject = new JSONObject();
        WebServiceHelper.callHttpWebService(this,mcontext, WebServiceHelper.RestMethodType.GET,"v2/relationship/"+id+"/docs/shared/"+shared_tag,"Shared Documents",jsonObject.toString());
    }

    private void hideProfileData(MyViewHolder holder) {


    }

    private void unhideProfileDetails(RelationshipsModel relationshipsModel, MyViewHolder holder) {
        mholder = holder;
        holder.rb_share_document.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.radiobutton_centre_background));
        holder.rb_profile.setBackgroundDrawable(mcontext.getResources().getDrawable(R.drawable.button_left_green_background));
        holder.rg_shared_status.setVisibility(View.GONE);
        relationshipmodel_profile = relationshipsModel;
        holder.nestedScrollView.setVisibility(View.GONE);
        holder.ll_documents.setVisibility(View.GONE);
        holder.iv_share_docs.setVisibility(View.GONE);
        holder.rg_document_type.setVisibility(View.GONE);
        callProfileWebservice(relationshipsModel.getId());
        shared_list.clear();
        shared_tag = "";
    }

    private void callProfileWebservice(String id) {
        JSONObject jsonObject = new JSONObject();
        WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.GET, "v2/relationship/" + id + "/profile", "Profile", jsonObject.toString());
    }


    @SuppressLint("SetTextI18n")
    private void deleteRelationships(RelationshipsModel relationshipsModel, String name) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mcontext);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            tv_confirmation.setText("You will be no longer be able to communicate and exchange information with " + name + ".Are you sure you want to end this digital relationship?");
            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad_dialog_delete.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callDeleteGroupsWebservice(relationshipsModel.getId());
                }
            });
            final AlertDialog dialog = dialogBuilder.create();
            ad_dialog_delete = dialog;
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), mcontext);
        }
    }

    private void callDeleteGroupsWebservice(String id) {
        try {
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.POST, "v2/relationship/" + id + "/terminate/request", "Delete Relationship", jsonObject.toString());
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), mcontext);
        }
    }

    private void callGroupsWebservice(MyViewHolder holder, RelationshipsModel relationshipsModel) {
        try {

            progress_dialog = AndroidUtils.get_progress(mActivity);
            for (int j = 0; j < relationshipsModel.getGroups().length(); j++) {
                ViewGroupModel viewGroupModel = new ViewGroupModel();
                JSONObject jsonObject = relationshipsModel.getGroups().getJSONObject(j);
                viewGroupModel.setGroup_id(jsonObject.getString("id"));
                viewGroupModel.setGroup_name(jsonObject.getString("name"));
                updatedMembersList.add(viewGroupModel);
            }
            relationshipsModel_new = relationshipsModel;
            mholder = holder;
            JSONObject postdata = new JSONObject();
            WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.GET, "v3/groups", "Get Groups", postdata.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return relationshipsList.size();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
            if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) ;
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
//            boolean error = result.getBoolean("error");
                String msg = "";


                if (httpResult.getRequestType().equals("Get Groups")) {
//                if (error) {
//                    msg = result.getString("msg");
//                    AndroidUtils.showToast(msg, mcontext);
//                } else {
                    JSONArray data = result.getJSONArray("data");
                    loadViewGroups(data);
//                }

                } else if (httpResult.getRequestType().equals("Update Group Access")) {
                    boolean error = result.getBoolean("error");
                    if (error) {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                    } else {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                        groupsList.clear();
                        relationshipsList.clear();
                        ad_dialog.dismiss();
                        eventListener.RefreshViewRelationshipsData();
                    }

                } else if (httpResult.getRequestType().equals("Delete Relationship")) {
                    boolean error = result.getBoolean("error");
                    if (error) {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                    } else {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                        groupsList.clear();
                        relationshipsList.clear();
                        ad_dialog_delete.dismiss();
                        eventListener.RefreshViewRelationshipsData();
                    }
                } else if (httpResult.getRequestType().equals("Profile")) {
                    boolean error = result.getBoolean("error");
                    if (error) {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                    } else {
                        mholder.rg_profile.setVisibility(View.VISIBLE);
                        JSONObject data = result.getJSONObject("data");
                        Log.d("TAG", "Data:" + data.toString());
                        loadProfile(data);
                    }
                } else if (httpResult.getRequestType().equals("Shared Documents")) {
                    boolean error = result.getBoolean("error");
                    if (error) {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                    } else {
                        JSONObject documents = result.getJSONObject("documents");
                        loadSharedWithmeDocuments(documents);
                    }
                } else if (httpResult.getRequestType().equals("Copy Document")) {
                    boolean error = result.getBoolean("error");
                    msg = result.getString("msg");
                    if (error) {
                        ad_dialog_copy.dismiss();
                        AndroidUtils.showAlert(msg, mcontext);
//                    AndroidUtils.showToast(msg, mcontext);
                    } else {
                        ad_dialog_copy.dismiss();
                        AndroidUtils.showToast(msg, mcontext);
                    }
                } else if (httpResult.getRequestType().equals("UnshareDocuments")) {
                    msg = result.getString("msg");
                    AndroidUtils.showToast(msg,mcontext);
                    shared_list.clear();
                    ad_dialog_docs.dismiss();
                }else if(httpResult.getRequestType().equals("Existing Documents")){
                    boolean error = result.getBoolean("error");
                    if (error) {
                        msg = result.getString("msg");
                        AndroidUtils.showToast(msg, mcontext);
                    } else {
                        JSONArray documents = result.getJSONArray("docs");
                        loadOtherDocs(documents);
//                        loadSharedWithmeDocuments(documents);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    private void loadOtherDocs(JSONArray docs) throws JSONException {


            for (int i=0;i<docs.length();i++){
                SharedDocumentsDo sharedDocumentsDo = new SharedDocumentsDo();
                JSONObject docs_new = docs.getJSONObject(i);
                sharedDocumentsDo.setContent_type(docs_new.getString("content_type"));
                sharedDocumentsDo.setCreated(docs_new.getString("created"));
                sharedDocumentsDo.setDescription(docs_new.getString("description"));
                sharedDocumentsDo.setExpiration_date(docs_new.getString("expiration_date"));
                sharedDocumentsDo.setFilename(docs_new.getString("filename"));
                sharedDocumentsDo.setId(docs_new.getString("id"));
                sharedDocumentsDo.setIs_disabled(docs_new.getBoolean("is_disabled"));
                sharedDocumentsDo.setIs_encrypted(docs_new.getBoolean("is_encrypted"));
                sharedDocumentsDo.setIs_password(docs_new.getBoolean("is_password"));
                sharedDocumentsDo.setName(docs_new.getString("name"));
                sharedDocumentsDo.setOrigin(docs_new.getString("origin"));
                sharedDocumentsDo.setUploaded_by(docs_new.getString("uploaded_by"));
                shared_list.add(sharedDocumentsDo);


            }
        openSharedPopupWindow(shared_list);
    }


    private void loadSharedWithmeDocuments(JSONObject documents)throws JSONException {
        if (shared_tag=="withme") {
            JSONArray identity = documents.getJSONArray("identity");
            for (int i = 0; i < identity.length(); i++) {
                SharedDocumentsDo sharedDocumentsDo = new SharedDocumentsDo();
                JSONObject docs = identity.getJSONObject(i);
                sharedDocumentsDo.setCategory(docs.getString("category"));
                sharedDocumentsDo.setCreated(docs.getString("created"));
                sharedDocumentsDo.setDescription(docs.getString("description"));
                sharedDocumentsDo.setDoctype(docs.getString("doctype"));
                sharedDocumentsDo.setId(docs.getString("id"));
                sharedDocumentsDo.setShareOn(docs.getString("sharedOn"));
                sharedDocumentsDo.setName(docs.getString("name"));
                shared_list.add(sharedDocumentsDo);
            }
        }else {
            JSONArray general = documents.getJSONArray("general");
            for (int i=0;i<general.length();i++){

                SharedDocumentsDo sharedDocumentsDo = new SharedDocumentsDo();
                JSONObject docs = general.getJSONObject(i);
                sharedDocumentsDo.setCategory(docs.getString("category"));
                sharedDocumentsDo.setCreated(docs.getString("created"));
                sharedDocumentsDo.setDescription(docs.getString("description"));
                sharedDocumentsDo.setDoctype(docs.getString("doctype"));
                sharedDocumentsDo.setId(docs.getString("id"));
                sharedDocumentsDo.setShareOn(docs.getString("sharedOn"));
                sharedDocumentsDo.setName(docs.getString("name"));
                shared_list.add(sharedDocumentsDo);
            }
        }

        openSharedPopupWindow(shared_list);
//        loadDocumentsRecyclerview();
    }

    private void loadDocumentsRecyclerview() {
        if (shared_list.size()==0){
            AndroidUtils.showToast("No Documents to display",mcontext);
            mholder.et_Search.setVisibility(View.GONE);
        }else {
            mholder.rv_documents.removeAllViews();
            mholder.rv_documents.setLayoutManager(new GridLayoutManager(mcontext, 1));

            SharedDocumentsAdapter adapter = new SharedDocumentsAdapter(shared_list, shared_tag, mcontext, this);
            mholder.rv_documents.setAdapter(adapter);
            mholder.rv_documents.setHasFixedSize(true);

            mholder.et_Search.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(mholder.et_Search.getText().toString());
                }

            });
        }
    }

    private void loadProfile(JSONObject data) throws JSONException {
        try {
            FLAG = "second_click";
            mholder.cv_Profile.setVisibility(View.VISIBLE);
            mholder.tv_first_name.setText(relationshipmodel_profile.getName());
            mholder.tv_email.setText(data.getString("email"));
            mholder.tv_contact_name.setText(data.getString("first_name"));
            mholder.tv_mobile.setText(data.getString("mobile"));
            JSONArray citizen = data.getJSONArray("citizen");
            for (int i = 0; i < citizen.length(); i++) {
                JSONObject jsonObject = citizen.getJSONObject(i);
                ProfileDo profileDo = new ProfileDo();
                profileDo.setIndex(jsonObject.getString("index"));
                profileDo.setCountry(jsonObject.getString("country"));
                citizenList.add(profileDo);
            }
            for (int i = 0; i < citizenList.size(); i++) {
                if (citizenList.get(i).getIndex().equals("citizen_primary")) {
                    mholder.tv_country.setText(citizenList.get(i).getCountry());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showToast(e.getMessage(), mcontext);
        }
    }

    private void loadViewGroups(JSONArray data) throws JSONException {

        ViewGroupModel viewGroupModel;
        groupsList.clear();
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            viewGroupModel = new ViewGroupModel();
            viewGroupModel.setId(jsonObject.getString("id"));
            String date = jsonObject.getString("created");
            Date date_new = AndroidUtils.stringToDateTimeDefault(date, "yyyy-MM-dd'T'HH:mm:ss.SSS");
            String created = AndroidUtils.getDateToString(date_new, "MMM dd YYYY");
            viewGroupModel.setCreated(created);
            JSONArray members = jsonObject.getJSONArray("members");
            viewGroupModel.setMembers(members);
            viewGroupModel.setDescription(jsonObject.getString("description"));
            viewGroupModel.setName(jsonObject.getString("name"));
            JSONObject group_head = jsonObject.getJSONObject("groupHead");
            viewGroupModel.setGroup_head_id(group_head.getString("id"));
            viewGroupModel.setGroup_head_name(group_head.getString("name"));
            viewGroupModel.setOwner_name(group_head.getString("name"));
            for (int j = 0; j < data.length(); j++) {
                for (int k = 0; k < updatedMembersList.size(); k++) {
                    if (viewGroupModel.getId().matches(updatedMembersList.get(k).getGroup_id())) {
                        viewGroupModel.setChecked(true);
                    }
                }
            }
            groupsList.add(viewGroupModel);
        }
        Log.i("ArrayList", "info" + updatedMembersList.size());
        String mtag = "VG";
        loadGroupsRecylerview();

    }

    private void loadGroupsRecylerview() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mcontext);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.groups_recylerview_popup, null);
        RecyclerView rv_relationship_groups;
        TextInputEditText et_search_relationships;
        ImageView iv_close;
        AppCompatButton btn_send_request, btn_relationships_cancel;
        rv_relationship_groups = view.findViewById(R.id.rv_relationship_groups);
        et_search_relationships = view.findViewById(R.id.et_search_relationships);
        btn_send_request = view.findViewById(R.id.btn_send_request);
        btn_relationships_cancel = view.findViewById(R.id.btn_relationships_cancel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false);
        rv_relationship_groups.setLayoutManager(layoutManager);
        rv_relationship_groups.setHasFixedSize(true);
        GroupsAdapter groupsAdapter = new GroupsAdapter(groupsList);
        rv_relationship_groups.setAdapter(groupsAdapter);
        et_search_relationships.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groupsAdapter.getFilter().filter(et_search_relationships.getText().toString());
            }

        });
        final AlertDialog dialog = dialogBuilder.create();
        ad_dialog = dialog;
        btn_relationships_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                groupsList.clear();
                updatedMembersList.clear();
                dialog.dismiss();
            }
        });
        iv_close = view.findViewById(R.id.close_groups);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                groupsList.clear();
                updatedMembersList.clear();
                dialog.dismiss();
            }
        });
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                try {
                    callUpdateGroupAcess(relationshipsModel_new.getId(), groupsAdapter.getList_item());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    private void callUpdateGroupAcess(String id, ArrayList<ViewGroupModel> list_item) throws JSONException {
        JSONObject postData = new JSONObject();
        JSONArray acls = new JSONArray();
        if (list_item.size() != 0) {
            for (int i = 0; i < list_item.size(); i++) {
                ViewGroupModel viewGroupModel = list_item.get(i);
                if (viewGroupModel.isChecked()) {
                    acls.put(viewGroupModel.getId());
                }
            }
            postData.put("acls", acls);
            WebServiceHelper.callHttpWebService(this, mcontext, WebServiceHelper.RestMethodType.PUT, "v2/relationship/" + id + "/acls", "Update Group Access", postData.toString());
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_relationship_name, tv_created_date, tv_consumer, tv_more_details, tv_initiated, tv_first_name, tv_email, tv_country, tv_contact_name, tv_mobile, tv_website, tv_billing_currency;
        ImageView iv_initiated, iv_groups_relationships, iv_clock_relationships, iv_delete_relationships;
        RadioGroup rg_profile, rg_shared_status, rg_shared_type, rg_document_type;
        CardView cv_Profile,cv_relationships;
        Button btn_accept;
        LinearLayout ll_documents,ll_expandable_layout,nestedScrollView;
        RecyclerView rv_documents;
        RadioButton rb_share_document, rb_profile, rb_shared_by_us, rb_shared_with_us,rb_client_document,rb_firm_document;
//        NestedScrollView ;
        TextInputEditText et_Search;
        com.google.android.material.imageview.ShapeableImageView iv_share_docs;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_relationship_name = itemView.findViewById(R.id.tv_relationship_name);
            tv_created_date = itemView.findViewById(R.id.tv_created_date);
            tv_consumer = itemView.findViewById(R.id.tv_consumer);
//            cv_documents = itemView.findViewById(R.id.cv_documents);
            tv_more_details = itemView.findViewById(R.id.tv_more_details);
            tv_initiated = itemView.findViewById(R.id.tv_initiated);
            iv_initiated = itemView.findViewById(R.id.iv_initiated);
            iv_groups_relationships = itemView.findViewById(R.id.iv_groups_relationships);
            iv_clock_relationships = itemView.findViewById(R.id.iv_clock_relationships);
            iv_delete_relationships = itemView.findViewById(R.id.iv_delete_relationships);
            rg_profile = itemView.findViewById(R.id.profile);
            rg_shared_status = itemView.findViewById(R.id.shared_status);
            rg_shared_type = itemView.findViewById(R.id.shared_type);
            rg_document_type = itemView.findViewById(R.id.document_type);
            tv_first_name = itemView.findViewById(R.id.tv_first_name);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_country = itemView.findViewById(R.id.tv_country);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            tv_website = itemView.findViewById(R.id.tv_website);
            tv_billing_currency = itemView.findViewById(R.id.tv_billing_currency);
            cv_Profile = itemView.findViewById(R.id.cv_profile);
            btn_accept = itemView.findViewById(R.id.accept);
            rb_profile = itemView.findViewById(R.id.rb_profile);
            iv_share_docs = itemView.findViewById(R.id.iv_share_docs);
            rb_share_document = itemView.findViewById(R.id.rb_share_button);
            rb_shared_by_us = itemView.findViewById(R.id.rb_shared_by_us);
            rb_shared_with_us = itemView.findViewById(R.id.rb_shared_with_us);
            nestedScrollView = itemView.findViewById(R.id.nestedScrollView);
            ll_documents = itemView.findViewById(R.id.ll_documents);
            rv_documents = itemView.findViewById(R.id.rv_shared_documents);
            et_Search = itemView.findViewById(R.id.et_search_documents);
            ll_expandable_layout = itemView.findViewById(R.id.ll_expandable_layout);
            rb_client_document = itemView.findViewById(R.id.rb_client_document);
            rb_firm_document = itemView.findViewById(R.id.rb_firm_document);
            tv_more_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelationshipsModel relationshipsModel = relationshipsList.get(getAdapterPosition());
                    relationshipsModel.setExpandable(!relationshipsModel.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
