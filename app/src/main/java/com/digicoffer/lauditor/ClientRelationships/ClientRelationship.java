package com.digicoffer.lauditor.ClientRelationships;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.ClientRelationships.Adapter.RelationshipsAdapter;
import com.digicoffer.lauditor.ClientRelationships.Model.CountriesDO;
import com.digicoffer.lauditor.ClientRelationships.Model.EntityModel;
import com.digicoffer.lauditor.ClientRelationships.Model.EntitySearchModel;
import com.digicoffer.lauditor.ClientRelationships.Model.RelationshipsModel;
import com.digicoffer.lauditor.ClientRelationships.Model.SearchModel;
import com.digicoffer.lauditor.Groups.GroupModels.ViewGroupModel;
import com.digicoffer.lauditor.Members.GroupsAdapter;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ClientRelationship extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, RelationshipsAdapter.EventListener {

    private RadioGroup rg_add_relationships, rg_individual_entity,rg_relationship;
    ArrayList<SearchModel> searchModelsList = new ArrayList<>();
    ArrayList<String> entityList = new ArrayList<>();
    ArrayList<ViewGroupModel> updatedMembersList = new ArrayList<>();
    ArrayList<RelationshipsModel> relationshipsList = new ArrayList<>();
    String TAG = "";
    private NewModel mViewModel;
    String value = "";
    String Relationship_Type = "";
    EntitySearchModel entitySearchModel;
    EntityModel entityModel;
    ArrayList<EntityModel> updatedEntityList = new ArrayList<>();
    RadioButton rb_add_relationship, rb_view_relationships, rb_individual, rb_entity,rb_individual_relationship,rb_business_relationship;
    LinearLayout ll_entity_name, ll_contact_person, ll_first_name, ll_last_name, ll_contatc_phone,ll_search_individual,ll_search_entity,ll_relationships,ll_select_all,ll_groups;
    TextInputEditText et_search_relationships, et_search_individual,et_search_view_relationships, tv_individual_email, tv_individual_confirm_email, tv_individual_firstname, tv_individual_last_name, tv_entity_name, tv_entity_contact_person, tv_entity_phone_number;
    Button btn_search_individual, btn_relationships_cancel, btn_send_request,btn_search_entity;
    TextView tv_response;
    AutoCompleteTextView at_search_entity;
    TextInputLayout tl_individual_country;
    String entity_id = "";
    Spinner sp_country;
    GroupsAdapter groupsAdapter;
    RecyclerView rv_relationship_groups,rv_relationships;
    SearchModel searchModel;
    private CheckBox chk_select_all;
    ArrayList<ViewGroupModel> groupsList = new ArrayList<>();
    ArrayList<CountriesDO> countriesList = new ArrayList<>();
    private static String RELATIONSHIP_TAG = "";
    CardView cv_details;
    private String country_name;
    private ScrollView sv_relationships;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_search_individual:
                try {
//                searchModelsList.clear();
//                tv_response.setText("");
                    callSearchIndividualWebservice();
                } catch (JSONException e) {
                    e.printStackTrace();
                    AndroidUtils.showToast(e.getMessage(), getContext());
                }
                break;
            case R.id.btn_search_entity:
                try{
                    String id = "";
                    callSearchEntityWebservice(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }

    private void callSearchEntityWebservice(String id) throws JSONException{
        JSONObject postdata = new JSONObject();
        if (entity_id == ""){
            tv_response.setText(at_search_entity.getText().toString()+"-not found.Please fill the below details to invite relationship");
            tv_response.setTextColor(getContext().getResources().getColor(R.color.Red));
            disableAlpha();
            enableIndividualData();
            clearIndividualData();
        }else {
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v2/relationship/entity/" + entity_id, "Search Entity", postdata.toString());
        }


//        postdata.put("email", et_search_individual.getText().toString());

    }


    private void callGroupsWebservice() {
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Get Groups", postdata.toString());


    }

    private void callSearchIndividualWebservice() throws JSONException {


        JSONObject postdata = new JSONObject();
        postdata.put("email", et_search_individual.getText().toString());
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v2/relationship/search/consumer", "Search Consumer", postdata.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        callCountriesWebService();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_relationships, container, false);
        callGroupsWebservice();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        String data = "Relationships";
        setViewModelData(data);
        rg_add_relationships = view.findViewById(R.id.rgTask);
        rg_relationship = view.findViewById(R.id.relationship);
        sv_relationships = (ScrollView) view.findViewById(R.id.sv_relationships);
        rb_add_relationship = view.findViewById(R.id.add_relationship);
        rb_view_relationships = view.findViewById(R.id.view_relationship);
        rg_individual_entity = view.findViewById(R.id.entity);
        rb_individual = view.findViewById(R.id.add_individiual);
        rv_relationship_groups = view.findViewById(R.id.rv_relationship_groups);
        rv_relationships = view.findViewById(R.id.rv_relationships);
        rb_entity = view.findViewById(R.id.add_entity);
        ll_search_entity = view.findViewById(R.id.ll_search_entity);
        ll_search_individual = view.findViewById(R.id.ll_search_individual);
        ll_entity_name = view.findViewById(R.id.ll_entity_name);
        ll_first_name = view.findViewById(R.id.ll_first_name);
        ll_last_name = view.findViewById(R.id.ll_last_name);
        ll_groups = view.findViewById(R.id.ll_groups);
        ll_select_all = view.findViewById(R.id.ll_select_all);
        ll_contact_person = view.findViewById(R.id.ll_contact_person);
        ll_contatc_phone = view.findViewById(R.id.ll_entity_number);
        sp_country = view.findViewById(R.id.sp_country);
        at_search_entity = view.findViewById(R.id.ac_search_entity);
        et_search_individual = view.findViewById(R.id.et_search_individual);
        btn_search_individual = view.findViewById(R.id.btn_search_individual);
        btn_search_entity = view.findViewById(R.id.btn_search_entity);
        et_search_relationships = view.findViewById(R.id.et_search_relationships);
        tv_response = view.findViewById(R.id.tv_response);
        cv_details = view.findViewById(R.id.cv_details);
        chk_select_all = view.findViewById(R.id.chk_select_all);
        tv_individual_email = view.findViewById(R.id.tv_individual_email);
        tv_entity_contact_person = view.findViewById(R.id.tv_entity_contact_person);
        tv_entity_name = view.findViewById(R.id.tv_entity_name);
        tv_entity_phone_number = view.findViewById(R.id.tv_entity_phone_number);
        ll_relationships = view.findViewById(R.id.ll_relationships);
        et_search_view_relationships = view.findViewById(R.id.et_search_view_relationships);
        tv_individual_confirm_email = view.findViewById(R.id.tv_individual_confirm_email);
        tv_individual_email = view.findViewById(R.id.tv_individual_email);
        tl_individual_country = view.findViewById(R.id.tl_individual_country);
        tv_individual_firstname = view.findViewById(R.id.tv_individual_firstname);
        tv_individual_last_name = view.findViewById(R.id.tv_individual_last_name);
        btn_send_request = view.findViewById(R.id.btn_send_request);
        btn_relationships_cancel = view.findViewById(R.id.btn_relationships_cancel);
        enableAlpha();
        btn_search_individual.setOnClickListener(this);
        rb_individual_relationship = view.findViewById(R.id.add_individiual_relationship);
        rb_business_relationship = view.findViewById(R.id.add_entity_relationship);
        btn_search_entity.setOnClickListener(this);
        rb_add_relationship.setTextColor(getContext().getResources().getColor(R.color.white));
        rb_view_relationships.setTextColor(getContext().getResources().getColor(R.color.black));
        rg_individual_entity.setVisibility(View.VISIBLE);
        rg_add_relationships.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.add_relationship:
                        rg_individual_entity.setVisibility(View.VISIBLE);
                        rg_relationship.setVisibility(View.GONE);
                        rb_add_relationship.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                        rb_add_relationship.setTextColor(getContext().getResources().getColor(R.color.white));
                        rb_view_relationships.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                        rb_add_relationship.setTextColor(getContext().getResources().getColor(R.color.white));
                        rb_view_relationships.setTextColor(getContext().getResources().getColor(R.color.black));
                        ll_relationships.setVisibility(View.GONE);
                        cv_details.setVisibility(View.VISIBLE);
                        ll_select_all.setVisibility(View.VISIBLE);
                        ll_groups.setVisibility(View.GONE);
                        relationshipsList.clear();
                        rv_relationships.removeAllViews();
                        break;
                    case R.id.view_relationship:
                        rb_individual_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                        rb_business_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_right_background));
                        Relationship_Type = "individuals";
                        viewRelationshipsData();

                        break;
                }
            }
        });
        rg_individual_entity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.add_individiual:
                        RELATIONSHIP_TAG = "INDIVIDUAL";
                        tv_response.setText("");
                        hideEntityData();
                        ll_search_individual.setVisibility(View.VISIBLE);
                        ll_search_entity.setVisibility(View.GONE);
                        ll_first_name.setVisibility(View.VISIBLE);
                        ll_last_name.setVisibility(View.VISIBLE);
                        rb_individual.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                        rb_entity.setTextColor(getContext().getResources().getColor(R.color.white));
                        rb_entity.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
                        rb_individual.setTextColor(getContext().getResources().getColor(R.color.white));
                        rb_entity.setTextColor(getContext().getResources().getColor(R.color.black));
                        break;
                    case R.id.add_entity:
                        RELATIONSHIP_TAG = "ENTITY";
                        callEntityWebService();
                       ll_search_individual.setVisibility(View.GONE);
                       ll_search_entity.setVisibility(View.VISIBLE);
                       ll_first_name.setVisibility(View.GONE);
                       ll_last_name.setVisibility(View.GONE);
                        enableAlpha();
                        callGroupsWebservice();
                        //end new code
                        tv_response.setText("");
                        unhideEntityData();
                        clearIndividualData();

                        rb_individual.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
                        rb_entity.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
                        rb_individual.setTextColor(getContext().getResources().getColor(R.color.black));
                        rb_entity.setTextColor(getContext().getResources().getColor(R.color.white));
                        break;
                }
            }
        });
        rg_relationship.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.add_individiual_relationship:
                        Relationship_Type = "individuals";
                        rb_individual_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
                        rb_business_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_right_background));
                        viewRelationshipsData();
                        break;
                    case R.id.add_entity_relationship:
                        Relationship_Type = "business";
                        rb_individual_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_left_background));
                        rb_business_relationship.setBackground(getContext().getResources().getDrawable(R.drawable.button_right_green_count));

                        viewRelationshipsData();
                        break;
                }
            }
        });
        tl_individual_country.setEnabled(false);
        sp_country.setEnabled(false);

    }

    private void viewRelationshipsData() {
        relationshipsList.clear();
        rv_relationships.removeAllViews();
        rg_individual_entity.setVisibility(View.GONE);
        rg_relationship.setVisibility(View.VISIBLE);
        rb_add_relationship.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        rb_view_relationships.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        rb_add_relationship.setTextColor(getContext().getResources().getColor(R.color.black));
        rb_view_relationships.setTextColor(getContext().getResources().getColor(R.color.white));
        ll_relationships.setVisibility(View.VISIBLE);
        cv_details.setVisibility(View.GONE);
        ll_select_all.setVisibility(View.GONE);
        ll_groups.setVisibility(View.GONE);
        callIndividualWebservice();
    }

    private void callIndividualWebservice() {
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.GET,"v2/relationship/"+Relationship_Type,"View Relationships",postdata.toString());
    }
    private void setViewModelData(String data) {
        mViewModel.setData(data);
    }
    private void callEntityWebService() {
        JSONObject postdata = new JSONObject();
        WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.GET,"v2/relationship/search/entity","Entities List",postdata.toString());

    }

    public void callCountriesWebService() {
        JSONObject jsonData = new JSONObject();
        try {

            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "countries", "COUNTRIES", jsonData.toString());
        } catch (Exception e) {

        }
    }
    private void disableAlpha() {

        tv_individual_email.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_individual_confirm_email.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_individual_firstname.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_individual_last_name.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_entity_phone_number.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_entity_contact_person.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        tv_entity_name.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));
        btn_relationships_cancel.setBackground(getContext().getResources().getDrawable(R.drawable.cancel_button_background));
        btn_send_request.setBackground(getContext().getResources().getDrawable(R.drawable.save_button_background));
        tl_individual_country.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_grey_background));

    }

    private void enableAlpha() {
        tv_individual_email.getBackground().setAlpha(100);
        tv_individual_confirm_email.getBackground().setAlpha(100);
        tv_individual_firstname.getBackground().setAlpha(100);
        tv_individual_last_name.getBackground().setAlpha(100);
        tv_entity_phone_number.getBackground().setAlpha(100);
        tv_entity_contact_person.getBackground().setAlpha(100);
        tv_entity_name.getBackground().setAlpha(100);
        btn_relationships_cancel.getBackground().setAlpha(100);
        btn_send_request.getBackground().setAlpha(100);
        tl_individual_country.getBackground().setAlpha(100);

//        rv_relationship_groups.getBackground().setAlpha(100);
    }

    private void hideEntityData() {
        ll_entity_name.setVisibility(View.GONE);
        ll_contact_person.setVisibility(View.GONE);
        ll_contatc_phone.setVisibility(View.GONE);
    }

    private void unhideEntityData() {
        ll_entity_name.setVisibility(View.VISIBLE);
        ll_contact_person.setVisibility(View.VISIBLE);
        ll_contatc_phone.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                Log.i("Tag", "Info:" + httpResult.getStatus_code());
                Log.i("Tag", "Info:" + result);

                if (httpResult.getRequestType().equals("Search Consumer")) {
                    loadIndividualData(result);

                } else if (httpResult.getRequestType().equals("Get Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadViewGroups(data);
                }else if (httpResult.getRequestType() == "COUNTRIES") {

                    JSONArray jsonArray = (new JSONObject(result.getString("data"))).getJSONArray("countries");
                    CountriesDO countriesDO;
                    countriesList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        countriesDO = new CountriesDO();
                        countriesDO.setName(String.valueOf(jsonArray.getJSONArray(i).get(1)));
                        countriesDO.setValue(String.valueOf(jsonArray.getJSONArray(i).get(0)));
                        countriesList.add(countriesDO);
                    }
                    loadCountryData();

//                    callHealthProfileWebservice();
                }else if(httpResult.getRequestType() == "Send Request"){
                    boolean error = result.getBoolean("error");
                    callGroupsWebservice();
                    if(error){

                        AndroidUtils.showToast(result.getString("msg"),getContext());
                    }else {
                        AndroidUtils.showToast(result.getString("msg"), getContext());
                        et_search_individual.setText("");

                        enableAlpha();
                        clearIndividualData();
                        disableIndividualData();
                        groupsList.clear();
                        searchModelsList.clear();

                    }
                }else if(httpResult.getRequestType() == "Send Entity Request"){
                    boolean error = result.getBoolean("error");
                    callGroupsWebservice();
                    if (error){
                        AndroidUtils.showToast(result.getString("msg"),getContext());
                    }
                    else{
                        AndroidUtils.showToast(result.getString("msg"),getContext());
                        at_search_entity.setText("");
                        entity_id = "";
                        value = "";
                        enableAlpha();
                        clearIndividualData();
                        disableIndividualData();
                        groupsList.clear();
                        tv_response.setText("");



                    }
                }
                else if(httpResult.getRequestType() == "Entities List"){
                    JSONArray entity = result.getJSONArray("data");
                    Log.i("Tag","Info:"+entity);
                    loadEntitydata(entity);
                }else if(httpResult.getRequestType() == "Search Entity"){
                    Log.i("TAG","EntityDATA:"+result.toString());
                    JSONObject data = result.getJSONObject("data");
                    loadSearchedEntityData(data);
//                    entity_id = "";
                    value = "";
                }else if(httpResult.getRequestType() == "View Relationships"){
                    JSONObject data = result.getJSONObject("data");
                    JSONArray relationships = data.getJSONArray("relationships");
                    loadRelationshipsData(relationships);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadRelationshipsData(JSONArray relationships) throws JSONException {
        RelationshipsModel relationshipsModel = null;
        for (int i = 0; i < relationships.length(); i++) {
             relationshipsModel = new RelationshipsModel();
            JSONObject jsonObject = relationships.getJSONObject(i);
            relationshipsModel.setAdminName(jsonObject.getString("adminName"));
            relationshipsModel.setCanAccept(jsonObject.getBoolean("canAccept"));
            relationshipsModel.setClientType(jsonObject.getString("clientType"));
            relationshipsModel.setClient_id(jsonObject.getString("client_id"));
            relationshipsModel.setConsent(jsonObject.getString("consent"));
            relationshipsModel.setCreated(jsonObject.getString("created"));
            relationshipsModel.setGroups(jsonObject.getJSONArray("groups"));
            relationshipsModel.setGuid(jsonObject.getString("guid"));
            relationshipsModel.setId(jsonObject.getString("id"));
            relationshipsModel.setAccepted(jsonObject.getBoolean("isAccepted"));
            relationshipsModel.setClient(jsonObject.getBoolean("isClient"));
            relationshipsModel.setEditable(jsonObject.getBoolean("isEditable"));
            relationshipsModel.setMatterList(jsonObject.getJSONArray("matterList"));
            relationshipsModel.setName(jsonObject.getString("name"));
            relationshipsList.add(relationshipsModel);
        }
        loadRelationshipsRecylerview(relationshipsModel);
    }

    private void loadRelationshipsRecylerview(RelationshipsModel relationshipsModel)throws JSONException {

            for (int j=0;j<relationshipsModel.getGroups().length();j++){
                ViewGroupModel viewGroupModel = new ViewGroupModel();
                JSONObject jsonObject = relationshipsModel.getGroups().getJSONObject(j);
                viewGroupModel.setGroup_id(jsonObject.getString("id"));
                viewGroupModel.setGroup_name(jsonObject.getString("name"));
                updatedMembersList.add(viewGroupModel);
            }

        rv_relationships.setLayoutManager(new GridLayoutManager(getContext(), 1));
        Log.i("Tag","Info:"+updatedMembersList.toString());
        RelationshipsAdapter adapter = new RelationshipsAdapter(relationshipsList,getContext(),getActivity(),this);
        rv_relationships.setAdapter(adapter);
        rv_relationships.setHasFixedSize(true);
        et_search_view_relationships.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s);
            }

        });
        adapter.notifyDataSetChanged();

    }

    private void loadSearchedEntityData(JSONObject data) throws JSONException{
        entitySearchModel = new EntitySearchModel();
        entitySearchModel.setEntityName(data.getString("entityName"));
        entitySearchModel.setEmail(data.getString("email"));
        entitySearchModel.setContactPerson(data.getString("contactPerson"));
        entitySearchModel.setContactPhone(data.getString("contactPhone"));
        entitySearchModel.setCountry(data.getString("country"));
        for (int i=0;i<countriesList.size();i++){
            if (countriesList.get(i).getName().equals(entitySearchModel.getCountry())){
                sp_country.setSelection(i);
            }
        }
        loadEntityUI(entitySearchModel);
    }

    private void loadEntityUI(EntitySearchModel entitySearchModel) {
        enableAlpha();
        disableIndividualData();

        btn_relationships_cancel.setBackground(getContext().getResources().getDrawable(R.drawable.cancel_button_background));
        btn_send_request.setBackground(getContext().getResources().getDrawable(R.drawable.save_button_background));
        btn_send_request.setEnabled(true);
        btn_relationships_cancel.setEnabled(true);
        tv_individual_email.setText(entitySearchModel.getEmail());
        tv_individual_confirm_email.setText(entitySearchModel.getEmail());
        tv_entity_contact_person.setText(entitySearchModel.getContactPerson());
        tv_entity_phone_number.setText(entitySearchModel.getContactPhone());
        tv_entity_name.setText(entitySearchModel.getEntityName());
        tv_response.setText("Entity "+ entitySearchModel.getEntityName()+"- found");
        tv_response.setTextColor(getContext().getResources().getColor(R.color.Blue_text_color));
    }

    private void loadUI(SearchModel searchModel) {
//        disableAlpha();
        enableAlpha();
        disableIndividualData();
        btn_relationships_cancel.setBackground(getContext().getResources().getDrawable(R.drawable.cancel_button_background));
        btn_send_request.setBackground(getContext().getResources().getDrawable(R.drawable.save_button_background));
        btn_send_request.setEnabled(true);
        btn_relationships_cancel.setEnabled(true);
        tv_individual_firstname.setText(searchModel.getFirstName());
        tv_individual_last_name.setText(searchModel.getLastName());
        tv_individual_email.setText(et_search_individual.getText().toString());
        tv_individual_confirm_email.setText(et_search_individual.getText().toString());

    }
    private void loadEntitydata(JSONArray entity) throws JSONException{
        entityList.clear();
        for (int i=0;i<entity.length();i++){
           JSONObject jsonObject = entity.getJSONObject(i);
            entityModel = new EntityModel();
           entityModel.setEntityID(jsonObject.getString("entityId"));
           entityModel.setName(jsonObject.getString("name"));
           entityModel.setContactName(jsonObject.getString("contactName"));
           entityList.add(entityModel.getName());
           updatedEntityList.add(entityModel);
//           entityList.add(entityModel.getEntityID());
        }
        Log.i("TAG","EntityList:"+entityList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_dropdown_item_1line, entityList);
        at_search_entity.setAdapter(adapter);
        at_search_entity.setThreshold(1);
      at_search_entity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  value = adapter.getItem(i);
              for (int j=0;j<updatedEntityList.size();j++){
//                  for (int k=0;k<entityList.size();k++){
                      if (value.equals(updatedEntityList.get(j).getName())){
                          entity_id = updatedEntityList.get(j).getEntityID();
//                      }
                  }
              }
          }
      });
        }


    private void loadCountryData() {
        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), countriesList);
        Log.i("ArrayList","Info:"+countriesList);
//        ArrayAdapter adaptador = new ArrayAdapter(User_Profile.this, android.R.layout.simple_spinner_item, sorted_countriesList);
//        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinner.setAdapter(adaptador);
        sp_country.setAdapter(adapter);
        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country_name = countriesList.get(position).getName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

            groupsList.add(viewGroupModel);
        }
        Log.i("ArrayList", "info" + groupsList.toString());
        String mtag = "VG";
        loadGroupsRecylerview();

    }

    private void loadGroupsRecylerview() {

//        if (groupsList.size() != 0) {

        rv_relationship_groups.setLayoutManager(new GridLayoutManager(getContext(), 1));
        groupsAdapter = new GroupsAdapter(groupsList);
        rv_relationship_groups.setAdapter(groupsAdapter);
        rv_relationship_groups.setHasFixedSize(true);
        et_search_relationships.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                groupsAdapter.getFilter().filter(s);
            }

        });
        btn_relationships_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_relationships.setText("");
                groupsList.clear();
                clearIndividualData();
                callGroupsWebservice();
            }
        });
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RELATIONSHIP_TAG == "INDIVIDUAL") {
                    if (!individualValidation()) {
                        try {

                            callIndividualRequestWebservice();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if (!individualValidation()) {
                        try {

                            callEntityRequestWebservice();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        chk_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                groupsAdapter.selectOrDeselectAll(isChecked);
            }
        });

//        }
//        else {
////            cv_members_details.setVisibility(View.GONE);
//        }
    }

    private void callEntityRequestWebservice() throws JSONException{
        Log.i("Tag","Country_Name:"+country_name);
        JSONObject postdata = new JSONObject();
        JSONArray groups = new JSONArray();
        if (groupsAdapter.getList_item().size()!=0) {
            for (int i = 0; i < groupsAdapter.getList_item().size(); i++) {
                ViewGroupModel viewGroupModel = groupsAdapter.getList_item().get(i);
                if (viewGroupModel.isChecked()) {
                    groups.put(viewGroupModel.getId());
                }
            }
            if (groups.length() == 0) {
                AndroidUtils.showToast("Please select atleast one group", getContext());

            }else{

                if(entity_id==""){
                    postdata.put("country",country_name);
                    postdata.put("email",tv_individual_email.getText().toString());
                    postdata.put("fullname",tv_entity_name.getText().toString());
                    postdata.put("contact_person",tv_entity_contact_person.getText().toString());
                    postdata.put("contactPhone",tv_entity_phone_number.getText().toString());
                    postdata.put("groupAcls",groups);
                    WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.POST,"v2/relationship/invite/entity","Send Entity Request",postdata.toString());
                }
                else {
                    postdata.put("entityId",entity_id);
                    postdata.put("description","Description");
                    postdata.put("groupAcls",groups);
                    WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.POST,"v2/relationship/request/entity","Send Entity Request",postdata.toString());
                }
            }
        }else{
            AndroidUtils.showToast("No groups selected",getContext());
        }

    }

    private void callIndividualRequestWebservice() throws JSONException {
        Log.i("Tag","Country_Name:"+country_name);
        JSONObject postdata = new JSONObject();
        JSONArray groups = new JSONArray();
        if (groupsAdapter.getList_item().size()!=0){
            for (int i=0;i<groupsAdapter.getList_item().size();i++){
                ViewGroupModel viewGroupModel = groupsAdapter.getList_item().get(i);
                if (viewGroupModel.isChecked()){
                    groups.put(viewGroupModel.getId());
                }
            }
            if (groups.length()==0){
                AndroidUtils.showToast("Please select atleast one group",getContext());

            }else{
                if(searchModel.getMsg().equals("Individual Not Found.")){
                    postdata.put("country",country_name);
                    postdata.put("email",tv_individual_email.getText().toString());
                    postdata.put("first_name",tv_individual_firstname.getText().toString());
                    postdata.put("last_name",tv_individual_last_name.getText().toString());
                    postdata.put("groupAcls",groups);
                    WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.POST,"v2/relationship/invite/consumer","Send Request",postdata.toString());
                }
                else {
                    postdata.put("consumerId",searchModel.getConsumerID());
                    postdata.put("description","Description");
                    postdata.put("groupAcls",groups);
                    WebServiceHelper.callHttpWebService(this,getContext(), WebServiceHelper.RestMethodType.POST,"v2/relationship/request/consumer","Send Request",postdata.toString());
                }


//                AndroidUtils.showValidationALert("Alert",postdata.toString(), getContext());
            }
         }else{
            AndroidUtils.showToast("No groups selected",getContext());
         }


    }

    private void loadIndividualData(JSONObject result) throws JSONException {

        searchModel= new SearchModel();
        searchModel.setMsg(result.getString("msg"));
        searchModel.setError(result.getBoolean("error"));
        if (searchModel.getError()) {
            Log.i("Tag", "Info:" + searchModel.getError());
            tv_response.setText(et_search_individual.getText().toString() + "-" + "not found. Please fill in the details below to send relationship invite");
            tv_response.setTextColor(getContext().getResources().getColor(R.color.Red));
            disableAlpha();
            enableIndividualData();
            clearIndividualData();
//                  et_search_individual.setText("");
        } else {
            Log.i("Tag", "Info:" + searchModel.getError());
            searchModel.setFirstName(result.getString("firstName"));
            searchModel.setLastName(result.getString("lastName"));
            searchModel.setCountry(result.getString("country"));
            searchModel.setConsumerID(result.getString("consumerId"));
            searchModel.setName(result.getString("name"));
            tv_response.setText(searchModel.getMsg());
            tv_response.setTextColor(getContext().getResources().getColor(R.color.Blue_text_color));
            for (int i=0;i<countriesList.size();i++){
                if (countriesList.get(i).getName().equals(searchModel.getCountry())){
                    sp_country.setSelection(i);
                }
            }
            loadUI(searchModel);
//
//                    et_search_individual.setText("");

        }
        searchModelsList.add(searchModel);
//        searchModelsList.add(searchModel);

    }

    private void clearIndividualData() {
        tv_individual_firstname.setText("");
        tv_individual_last_name.setText("");
        tv_individual_email.setText("");
        tv_individual_confirm_email.setText("");
        tv_entity_contact_person.setText("");
        tv_entity_name.setText("");
        tv_entity_phone_number.setText("");
        et_search_individual.setText("");


        for (int i = 0; i < countriesList.size(); i++) {
            if (countriesList.get(i).getName().equals("Choose country")) {
                sp_country.setSelection(i);
            }
        }
    }

    private void enableIndividualData() {

        tv_individual_firstname.setEnabled(true);
        tv_individual_last_name.setEnabled(true);
        tv_individual_email.setEnabled(true);
        tv_individual_confirm_email.setEnabled(true);
        tv_entity_phone_number.setEnabled(true);
        tv_entity_name.setEnabled(true);
        tv_entity_contact_person.setEnabled(true);
        btn_send_request.setEnabled(true);
        btn_relationships_cancel.setEnabled(true);
        tl_individual_country.setEnabled(true);
        sp_country.setEnabled(true);
//        rv_relationship_groups.setEnabled(true);
    }

    private void disableIndividualData() {
        tv_individual_firstname.setEnabled(false);
        tv_individual_last_name.setEnabled(false);
        tv_individual_email.setEnabled(false);
        tv_individual_confirm_email.setEnabled(false);
        tv_entity_phone_number.setEnabled(false);
        tv_entity_name.setEnabled(false);
        tv_entity_contact_person.setEnabled(false);
        btn_send_request.setEnabled(false);
        btn_relationships_cancel.setEnabled(false);
        sp_country.setEnabled(false);
//        rv_relationship_groups.setEnabled(false);
    }


    private boolean individualValidation(){
        boolean status = false;
        if(RELATIONSHIP_TAG =="INDIVIDUAL") {
            if (tv_individual_email.getText().toString().equals("")) {
                tv_individual_email.setError("Email is required");
                tv_individual_email.requestFocus();
                AndroidUtils.showToast("Email is required", getContext());
                status = true;
            } else if (tv_individual_confirm_email.getText().toString().equals("")) {
                tv_individual_confirm_email.setError("Confirm email is required");
                tv_individual_confirm_email.requestFocus();
                AndroidUtils.showToast("Confirm email is required", getContext());
                status = true;
            } else if (!tv_individual_email.getText().toString().equals(tv_individual_confirm_email.getText().toString())) {
                tv_individual_confirm_email.setError("Email and Confirm Email doesn't match");
                AndroidUtils.showToast("Email and Confirm Email doesn't match", getContext());
                status = true;
            } else if (tv_individual_firstname.getText().toString().equals("")) {
                tv_individual_firstname.setError("First Name is required");
                tv_individual_firstname.requestFocus();
                AndroidUtils.showToast("First Name is required", getContext());
                status = true;
            } else if (tv_individual_last_name.getText().toString().equals("")) {
                tv_individual_last_name.setError("Last Name is required");
                tv_individual_last_name.requestFocus();
                AndroidUtils.showToast("Last Name is required", getContext());
                status = true;
            } else if (country_name.equals("Choose country")) {
                AndroidUtils.showToast("Please select a country", getContext());
            } else {
                if (!tv_individual_email.getText().toString().equals("") && Patterns.EMAIL_ADDRESS.matcher(tv_individual_email.getText().toString()).matches()) {
                    status = false;
                } else {
                    tv_individual_email.setError("Enter a valid email address");
                    AndroidUtils.showToast("Enter a valid email address", getContext());
                    status = true;
                }
            }
        }else{
             if (tv_entity_name.getText().toString().equals("")) {
                 tv_entity_name.setError("Entity Name is required");
                 tv_entity_name.requestFocus();
                AndroidUtils.showToast("Entity Name is required", getContext());
                status = true;
            }else if (tv_entity_contact_person.getText().toString().equals("")) {
                 tv_entity_contact_person.setError("Contact Person is required");
                 tv_entity_contact_person.requestFocus();
                 AndroidUtils.showToast("Contact Person is required", getContext());
                 status = true;
             }else if(tv_entity_phone_number.getText().toString().equals("")){
                 tv_entity_phone_number.setError("Contact Phone Number is required");
                 tv_entity_phone_number.requestFocus();
                 AndroidUtils.showToast("Contact Phone Number is required",getContext());
             }
             else if (tv_individual_email.getText().toString().equals("")) {
                tv_individual_email.setError("Email is required");
                tv_individual_email.requestFocus();
                AndroidUtils.showToast("Email is required", getContext());
                status = true;
            } else if (tv_individual_confirm_email.getText().toString().equals("")) {
                tv_individual_confirm_email.setError("Confirm email is required");
                tv_individual_confirm_email.requestFocus();
                AndroidUtils.showToast("Confirm email is required", getContext());
                status = true;
            } else if (!tv_individual_email.getText().toString().equals(tv_individual_confirm_email.getText().toString())) {
                tv_individual_confirm_email.setError("Email and Confirm Email doesn't match");
                AndroidUtils.showToast("Email and Confirm Email doesn't match", getContext());
                status = true;
            }   else if (country_name.equals("Choose country")) {
                AndroidUtils.showToast("Please select a country", getContext());
            } else {
                if (!tv_individual_email.getText().toString().equals("") && Patterns.EMAIL_ADDRESS.matcher(tv_individual_email.getText().toString()).matches()) {
                    status = false;
                } else {
                    tv_individual_email.setError("Enter a valid email address");
                    AndroidUtils.showToast("Enter a valid email address", getContext());
                    status = true;
                }
            }
        }

        return status;
    }

    @Override
    public void RefreshViewRelationshipsData() {
        viewRelationshipsData();
    }
}
