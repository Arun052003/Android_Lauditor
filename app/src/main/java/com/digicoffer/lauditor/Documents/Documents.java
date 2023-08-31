package com.digicoffer.lauditor.Documents;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.DocumentsListAdapter;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.GroupsListAdapter;
import com.digicoffer.lauditor.Documents.DocumentsListAdpater.View_documents_adapter;
import com.digicoffer.lauditor.Documents.models.ClientsModel;
import com.digicoffer.lauditor.Documents.models.DocumentsModel;
import com.digicoffer.lauditor.Documents.models.MattersModel;
import com.digicoffer.lauditor.Documents.models.ViewDocumentsModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.BottomSheetUploadFile;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Documents extends Fragment implements BottomSheetUploadFile.OnPhotoSelectedListner, AsyncTaskCompleteListener, DocumentsListAdapter.EventListener,View_documents_adapter.Eventlistner {
    Button btn_browse;
    private NewModel mViewModel;
    BottomSheetUploadFile bottommSheetUploadDocument;
    private Bitmap mSelectedBitmap;
    LinearLayout ll_added_tags, ll_matter, ll_category, ll_groups, ll_client_name,ll_view_docs,ll_upload_docs;
    TextInputEditText tv_tag_type, tv_tag_name;
    private ImageView imageView;
    boolean[] selectedLanguage;
    String DOCUMENT_TYPE_TAG = "client";
    String CONTENT_TYPE = "";
    String UPLOAD_TAG = "Client";
    DocumentsListAdapter adapter;
    ArrayList<ViewDocumentsModel> view_docs_list = new ArrayList<>();
    ShapeableImageView siv_upload_document,siv_view_document;
    ArrayList<Integer> langList = new ArrayList<>();
    ArrayList<DocumentsModel> groupsList = new ArrayList<>();
    ArrayList<DocumentsModel> tags_list = new ArrayList<>();
    private File mSelectedUri;
    String subtag = "";
    ArrayList<DocumentsModel> selected_documents_list = new ArrayList<>();
    LinearLayout ll_documents;
    boolean DOWNLOAD_TAG = false;
    String CATEGORY_TAG = "";
    CheckBox chk_select_all;
    String filename;
    int currentpoistion = 0;
    ArrayList<DocumentsModel> selected_groups_list = new ArrayList<>();
    RecyclerView rv_documents,rv_display_view_docs;
    ArrayList<DocumentsModel> docsList = new ArrayList<>();
    AlertDialog progress_dialog;
    TextView tv_add_tag, tv_client, tv_firm, tv_enable_download, tv_disable_download, tv_edit_meta, tv_name,tv_client_view,tv_firm_view;
    Button btn_upload, btn_add_tags;
    //    AutoCompleteTextView ;
    File file;
    String value = "";
    String entity_id = "";
    String matter_id = "";
    String client_id = "";
    LinearLayout ll_hide_document_details;
    TextView tv_tag_document_name, tv_select_groups;
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    Spinner sp_matter, sp_client;
    ArrayList<MattersModel> matterlist = new ArrayList<>();
    ArrayList<ClientsModel> updatedClients = new ArrayList<>();
    TextInputEditText tv_selected_file;
    TextInputLayout tl_selected_file;
    PDFView pdfView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.upload_document, container, false);
        try {
            mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
            mViewModel.setData("Documents");
            btn_browse = v.findViewById(R.id.btn_browse);
            tv_selected_file = v.findViewById(R.id.tv_selected_file);
            tl_selected_file = v.findViewById(R.id.tl_selected_file);
            sp_client = v.findViewById(R.id.at_search_client);
            sp_matter = v.findViewById(R.id.sp_matter);
            tv_add_tag = v.findViewById(R.id.tv_add_tag);
            tv_edit_meta = v.findViewById(R.id.tv_edit_meta);
            btn_upload = v.findViewById(R.id.btn_upload);
            tv_client = v.findViewById(R.id.tv_client);
            tv_firm = v.findViewById(R.id.tv_firm);
            tv_client_view = v.findViewById(R.id.tv_client_view);
            tv_name = v.findViewById(R.id.tv_name);
            ll_matter = v.findViewById(R.id.ll_matter);
            ll_category = v.findViewById(R.id.ll_category);
            rv_display_view_docs = v.findViewById(R.id.rv_display_view_docs);
            ll_groups = v.findViewById(R.id.ll_groups);
            tv_firm_view = v.findViewById(R.id.tv_firm_view);
            siv_upload_document = v.findViewById(R.id.upload_icon);
            siv_view_document = v.findViewById(R.id.view_icon);
            ll_upload_docs = v.findViewById(R.id.ll_upload_docs);
            ll_view_docs = v.findViewById(R.id.ll_view_docs);
            ll_client_name = v.findViewById(R.id.ll_client_name);
            chk_select_all = v.findViewById(R.id.chk_select_all);
            chk_select_all.getBackground().setAlpha(100);
            chk_select_all.setEnabled(false);
            btn_add_tags = v.findViewById(R.id.btn_add_tag);
            tv_enable_download = v.findViewById(R.id.tv_enable_download);
            tv_disable_download = v.findViewById(R.id.tv_disable_download);
            ll_hide_document_details = v.findViewById(R.id.ll_hide_doc_details);
            ll_hide_document_details.setVisibility(View.GONE);
            rv_documents = v.findViewById(R.id.rv_documents);
            siv_upload_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload_documents();
                }
            });
            siv_view_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view_document();
                }
            });
            tv_select_groups = v.findViewById(R.id.tv_select_groups);
            tv_select_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callGroupsWebservice();
                }
            });

            hidefirmBackground();
            tv_enable_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideEnableDownloadBackground();
                }
            });
            tv_disable_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDisableDownloadBackground();
                }
            });
            tv_client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rv_documents.removeAllViews();
                    hidefirmBackground();
                    callClientWebservice();

                }
            });
            tv_firm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rv_documents.removeAllViews();
                    hideClientBackground();

                }
            });

            tv_add_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddTag();
                }
            });
            tv_edit_meta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditMeta();
                }
            });

            btn_browse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissionREAD_EXTERNAL_STORAGE(getContext());
                }
            });
            tl_selected_file.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        tl_selected_file.setHint("");
                    else
                        tl_selected_file.setHint("Select Document");
                }
            });
            tv_selected_file.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        tv_selected_file.setHint("");
                    else
                        tv_selected_file.setHint("Select Document");
                }
            });
            btn_add_tags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_documents_list.clear();
                    for (int i = 0; i < adapter.getList_item().size(); i++) {
                        DocumentsModel documentsModel = adapter.getList_item().get(i);
                        if (documentsModel.isChecked()) {
                            ;
                            if (documentsModel.getTags() == null) {
                                selected_documents_list.add(documentsModel);
                            }
                        }
                    }
                    open_add_tags_popup();
                }
            });
            tv_client_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideviewFirmBackground();
                    rv_display_view_docs.removeAllViews();
                    view_docs_list.clear();
                    DOCUMENT_TYPE_TAG = "client";
                    callViewDocumentWebservice();
                }
            });
            tv_firm_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideviewClientBackground();
                    view_docs_list.clear();
                    rv_display_view_docs.removeAllViews();
                    DOCUMENT_TYPE_TAG = "firm";
                    callViewDocumentWebservice();
                }
            });
            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callUploadDocumentWebservice();
                }
            });
            callClientWebservice();
        } catch (Exception e) {
            e.printStackTrace();
        }
            return v;

    }

    private void view_document() {
        siv_view_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.eye_icon_white));
        siv_upload_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.upload_icon));
        ll_view_docs.setVisibility(View.VISIBLE);
        ll_upload_docs.setVisibility(View.GONE);
        rv_documents.removeAllViews();
        hideviewFirmBackground();

        callViewDocumentWebservice();
        clearListData();
        mViewModel.setData("View Documents");
    }

    private void callViewDocumentWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/documents/"+DOCUMENT_TYPE_TAG, "VIEW_DOCUMENT", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    private void hideviewFirmBackground() {

        tv_firm_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_client_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));

    }
    private void hideviewClientBackground(){
        tv_firm_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_client_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));

    }

    private void upload_documents() {
        siv_upload_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.upload_icon));
        siv_view_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.eye_icon_black));
        ll_upload_docs.setVisibility(View.VISIBLE);
        ll_view_docs.setVisibility(View.GONE);
        callClientWebservice();
        mViewModel.setData("Upload Documents");
    }

    private void callGroupsWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/groups", "Groups", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    private void open_add_tags_popup() {
        tags_list.clear();
        if (selected_documents_list.size() != 0) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.add_tag, null);
            tv_tag_type = (TextInputEditText) view.findViewById(R.id.tv_tag_type);
            tv_tag_name = view.findViewById(R.id.tv_tag_name);
            final Button btn_add = view.findViewById(R.id.btn_add_tags);
            final AppCompatButton btn_cancel = view.findViewById(R.id.btn_cancel_tag);
            final AppCompatButton btn_save_tag = view.findViewById(R.id.btn_save_tag);
            final ImageView iv_cancel = view.findViewById(R.id.close_tags);
            ll_added_tags = view.findViewById(R.id.ll_added_tags);
            final AlertDialog dialog = dialogBuilder.create();
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
//            tags_list.clear();
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    add_tags_listing();
                }
            });
            btn_save_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tags_list.size() != 0) {

                        subtag = "view_tags";
                    }
                    String tag = "add_tag";
//                        for(int i=0;i<docsList.size();i++){
                    for (int j = 0; j < selected_documents_list.size(); j++) {
//                                if (docsList.get(i).getName().matches(selected_documents_list.get(j).getName())){
                        DocumentsModel documentsModel = selected_documents_list.get(j);
                        JSONObject tags = new JSONObject();
                        for (int t = 0; t < tags_list.size(); t++) {


                            try {
                                tags.put(tags_list.get(t).getTag_type(), tags_list.get(t).getTag_name());

//                                            selected_documents_list.add(documentsModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        documentsModel.setTags(tags);

                    }
                    for (int i = 0; i < docsList.size(); i++) {
                        for (int j = 0; j < selected_documents_list.size(); j++) {
                            if (docsList.get(i).getName().matches(selected_documents_list.get(j).getName())) {
                                DocumentsModel documentsModel = selected_documents_list.get(j);
                                documentsModel.setTags(selected_documents_list.get(j).getTags());
                                docsList.set(i, documentsModel);
                            }
                        }
                    }
//                    AndroidUtils.showAlert(selected_documents_list.toString(),getContext());
//                            }
//                        }
//                    adapter.getList_item().clear();
//                    chk_select_all.setChecked(false);
//                    for (int i=0;i<adapter.getList_item().size();i++){
////                        DocumentsModel documentsModel =
//                    }
                    loadRecyclerview(tag, subtag);
                    dialog.dismiss();

                }
            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } else {
            AndroidUtils.showToast("Please select atleast one document to add tags", getContext());
        }
    }

    private void add_tags_listing() {
        ll_added_tags.removeAllViews();
        DocumentsModel documentsModel = new DocumentsModel();
        documentsModel.setTag_type(tv_tag_type.getText().toString());
        documentsModel.setTag_name(tv_tag_name.getText().toString());
        tags_list.add(documentsModel);
        for (int i = 0; i < tags_list.size(); i++) {
            View view_added_tags = LayoutInflater.from(getContext()).inflate(R.layout.displays_documents_list, null);
            tv_tag_document_name = view_added_tags.findViewById(R.id.tv_document_name);
            ImageView iv_edit_tag = view_added_tags.findViewById(R.id.iv_edit_meta);
            ImageView iv_remove_tag = view_added_tags.findViewById(R.id.iv_cancel);
            iv_remove_tag.setTag(i);
            iv_remove_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (view.getTag() instanceof Integer) {
                        position = (Integer) view.getTag();
                        view = ll_added_tags.getChildAt(position);

                        ll_added_tags.removeView(view);
                        DocumentsModel documentsModel1 = tags_list.get(position);
                        documentsModel1.setTag_name("");
                        documentsModel1.setTag_type("");
                        tags_list.set(position, documentsModel1);
                        tags_list.remove(position);
//                        add_tags_listing();
//                                    ll_added_tags.removeAllViews();
                    }
                }
            });
            iv_edit_tag.setTag(i);
            iv_edit_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = 0;
                    if (view.getTag() instanceof Integer) {
                        position = (Integer) view.getTag();
                        view = ll_added_tags.getChildAt(position);
                        DocumentsModel documentsModel1 = tags_list.get(position);
                        edit_tags(documentsModel1.getTag_type(), documentsModel1.getTag_name(), position, view, tv_tag_document_name);
                    }
                }
            });
            iv_edit_tag.setVisibility(View.VISIBLE);
            tv_tag_document_name.setText(tags_list.get(i).getTag_type() + " - " + tags_list.get(i).getTag_name());
            ll_added_tags.addView(view_added_tags);
        }
    }

    private void edit_tags(String tag_type, String tag_name, int position, View view_tag, TextView tv_tag_document_name) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_tags = inflater.inflate(R.layout.edit_tag, null);
        TextInputEditText tv_edit_tag_type = view_edit_tags.findViewById(R.id.tv_edit_tag_type);
        TextInputEditText tv_edit_tag_nam = view_edit_tags.findViewById(R.id.tv_edit_tag_name);
        AppCompatButton btn_cancel = view_edit_tags.findViewById(R.id.btn_edit_cancel_tag);
        AppCompatButton btn_save_edited_tag = view_edit_tags.findViewById(R.id.btn_edit_save_tag);
        ImageView iv_close_edit_tags = view_edit_tags.findViewById(R.id.edit_close_tags);
        tv_edit_tag_type.setText(tag_type);
        tv_edit_tag_nam.setText(tag_name);
        final AlertDialog dialog = dialogBuilder.create();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_save_edited_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_edited_tags(tv_edit_tag_type.getText().toString(), tv_edit_tag_nam.getText().toString(), position, view_tag, dialog, tv_tag_document_name);
            }
        });
        iv_close_edit_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view_edit_tags);
        dialog.show();
    }

    private void save_edited_tags(String tag_type, String tag_name, int position, View view, AlertDialog dialog, TextView tv_edit_tag_document_name) {
        try {
            DocumentsModel documentsModel = new DocumentsModel();
            documentsModel.setTag_type(tag_type);
            documentsModel.setTag_name(tag_name);
            tags_list.set(position, documentsModel);
            tv_edit_tag_document_name = view.findViewById(R.id.tv_document_name);
            tv_edit_tag_document_name.setText(documentsModel.getTag_type() + " - " + documentsModel.getTag_name());
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void hidefirmBackground() {
        UPLOAD_TAG = "Client";
        ll_category.setVisibility(View.GONE);
        ll_matter.setVisibility(View.VISIBLE);
        ll_groups.setVisibility(View.GONE);
        ll_client_name.setVisibility(View.VISIBLE);

        clearListData();
        tv_firm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_client.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
    }

    private void hideClientBackground() {
        UPLOAD_TAG = "Firm";
        ll_matter.setVisibility(View.GONE);
        ll_category.setVisibility(View.VISIBLE);
        ll_groups.setVisibility(View.VISIBLE);
        ll_client_name.setVisibility(View.GONE);
        clearListData();
        tv_client.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_firm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
    }

    private void clearListData() {
        ll_hide_document_details.setVisibility(View.GONE);
        tv_selected_file.setText("");
        selected_groups_list.clear();
        selected_documents_list.clear();
        langList.clear();
        tags_list.clear();
        docsList.clear();
        groupsList.clear();
        clientsList.clear();
        matterlist.clear();
        tv_select_groups.setText("Select Groups");
    }

    private void callUploadDocumentWebservice() {
        try {
            if (docsList.size() == 0) {
                AndroidUtils.showToast("Please select atleast one document", getContext());
            } else {

                progress_dialog = AndroidUtils.get_progress(getActivity());
                if(UPLOAD_TAG=="Client"){
                for (int i = 0; i < docsList.size(); i++) {

                    JSONObject jsonObject = new JSONObject();
                    JSONArray clients = new JSONArray();
                    JSONObject clients_jobject = new JSONObject();
                    JSONArray matter = new JSONArray();
//                JSONArray tags = new JSONArray();
                    String docname = "";
                    DocumentsModel documentsModel = docsList.get(i);
                    filename = documentsModel.getName();
                    File new_file = documentsModel.getFile();
                    String doc_type = "pdf";
                    String content_string = new_file.getName().replace(".", "/");
                    String[] content_type = content_string.split("/");
                    if (content_type.length >= 2) {
                        doc_type = content_type[1];
                        docname = content_type[0];
                    }

                    for (int j = 0; j < clientsList.size(); j++) {
                        if (clientsList.get(j).getId().matches(client_id)) {
                            ClientsModel clientsModel = clientsList.get(j);
                            clients_jobject.put("id", clientsModel.getId());
                            clients_jobject.put("type", clientsModel.getType());
                            clients.put(clients_jobject);
                        }
                    }
                    matter.put(matter_id);
                    jsonObject.put("name", docsList.get(i).getName());
                    jsonObject.put("description", docsList.get(i).getDescription());
                    jsonObject.put("filename", docname);
                    jsonObject.put("category", "client");
                    jsonObject.put("clients", clients);
                    jsonObject.put("matters", matter);
                    jsonObject.put("downloadDisabled", DOWNLOAD_TAG);
                    if (docsList.get(i).getTags() == null) {
                        jsonObject.put("tags", "");
                    } else {
                        jsonObject.put("tags", docsList.get(i).getTags());
                    }

                    if (doc_type.equalsIgnoreCase("apng") || doc_type.equalsIgnoreCase("avif") || doc_type.equalsIgnoreCase("gif") || doc_type.equalsIgnoreCase("jpeg") || doc_type.equalsIgnoreCase("png") || doc_type.equalsIgnoreCase("svg") || doc_type.equalsIgnoreCase("webp") || doc_type.equalsIgnoreCase("jpg")) {
                        jsonObject.put("content_type", "image/" + doc_type);
                    } else {
                        jsonObject.put("content_type", "application/" + doc_type);
                    }
                    WebServiceHelper.callHttpUploadWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/document/upload", "Upload Document", new_file, jsonObject.toString());

//            AndroidUtils.showAlert(jsonObject.toString(),getContext());
                 }
                }
                else{
                    for (int i = 0; i < docsList.size(); i++) {
                        currentpoistion++;
                        JSONObject jsonObject = new JSONObject();
                        JSONArray clients = new JSONArray();
                        JSONObject clients_jobject = new JSONObject();

//                JSONArray tags = new JSONArray();
                        String docname = "";
                        DocumentsModel documentsModel = docsList.get(i);
                        filename = documentsModel.getName();
                        JSONArray groups = new JSONArray();
                        for (int k=0;k<selected_groups_list.size();k++){
                            DocumentsModel documentsModel1 = selected_groups_list.get(k);
                            groups.put(documentsModel1.getGroup_id());
                        }
                        File new_file = documentsModel.getFile();
                        String doc_type = "pdf";
                        String content_string = new_file.getName().replace(".", "/");
                        String[] content_type = content_string.split("/");
                        if (content_type.length >= 2) {
                            doc_type = content_type[1];
                            docname = content_type[0];
                        }

                        for (int j = 0; j < clientsList.size(); j++) {
                            if (clientsList.get(j).getId().matches(client_id)) {
                                ClientsModel clientsModel = clientsList.get(j);
                                clients_jobject.put("id", clientsModel.getId());
                                clients_jobject.put("type", clientsModel.getType());
                                clients.put(clients_jobject);
                            }
                        }


                        jsonObject.put("name", docsList.get(i).getName());
                        jsonObject.put("description", docsList.get(i).getDescription());
                        jsonObject.put("filename", docname);
                        jsonObject.put("category", "firm");
                        jsonObject.put("clients", "");
                        jsonObject.put("groups", groups);
                        jsonObject.put("downloadDisabled", DOWNLOAD_TAG);
                        if (docsList.get(i).getTags() == null) {
                            jsonObject.put("tags", "");
                        } else {
                            jsonObject.put("tags", docsList.get(i).getTags());
                        }

                        if (doc_type.equalsIgnoreCase("apng") || doc_type.equalsIgnoreCase("avif") || doc_type.equalsIgnoreCase("gif") || doc_type.equalsIgnoreCase("jpeg") || doc_type.equalsIgnoreCase("png") || doc_type.equalsIgnoreCase("svg") || doc_type.equalsIgnoreCase("webp") || doc_type.equalsIgnoreCase("jpg")) {
                            jsonObject.put("content_type", "image/" + doc_type);
                        } else {
                            jsonObject.put("content_type", "application/" + doc_type);
                        }

//            AndroidUtils.showAlert(jsonObject.toString(),getContext());
                        WebServiceHelper.callHttpUploadWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/document/upload", "Upload Document", new_file, jsonObject.toString());
                    }
                }

            }
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();

        }


    }

    private void callClientWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v2/relationship/client/list", "Clients List", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AndroidUtils.showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    123);
                }
                return false;
            } else {
                BottomSheetUploadfile();
                return true;
            }

        } else {
            return true;
        }
    }


    private void BottomSheetUploadfile() {
        bottommSheetUploadDocument = new BottomSheetUploadFile();
        bottommSheetUploadDocument.show(getParentFragmentManager(), "");
        bottommSheetUploadDocument.setTargetFragment(Documents.this, 1);
    }

    @SuppressLint("Range")
    @Override
    public void getImagepath(File imagepath, Uri ImageURI) throws IOException {
        if ((imagepath == null)) {
            mSelectedBitmap = null;
            mSelectedUri = imagepath;
            String uri = imagepath.toString();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
            imageLoader.displayImage(String.valueOf(Uri.fromFile(new File(uri))), imageView);
            file = imagepath;
            Cursor c = getContext().getContentResolver().query(ImageURI, null, null, null, null);
            c.moveToFirst();
            String[] content_type = file.getName().split(".");
            String file_name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            tv_selected_file.setText(file_name);

            load_documents(docsList, file_name, file);
        } else {
            file = getFile(getContext(), ImageURI);
            Log.i("FILE", "Info:" + file.toString());
            String file_name = file.getName();
            tv_selected_file.setText(file_name);
//            DocumentsModel documentsModel = new DocumentsModel();
//            documentsModel.setName(file.getName());
//            docsList.add(documentsModel);
            load_documents(docsList, file_name, file);
//            docsList.add()
        }

    }

    private void load_documents(ArrayList<DocumentsModel> docsList, String file_name, File file) {
//        for (int i = 0; i < docsList.size(); i++) {
//            View view = LayoutInflater.from(getContext()).inflate(R.layout.displays_documents_list, null);
//            TextView tv_docname = view.findViewById(R.id.tv_document_name);
//            tv_docname.setText(docsList.get(i).getName());
        String doc_type = "";
        String docname = "";
        String content_string = file_name.replace(".", "/");
        String[] content_type = content_string.split("/");
        if (content_type.length >= 2) {
            doc_type = content_type[1];
            docname = content_type[0];
        }
        DocumentsModel documentsModel = new DocumentsModel();
        documentsModel.setName(docname);
        documentsModel.setDescription(docname);
        documentsModel.setFile(file);
        documentsModel.setIsenabled(true);
        docsList.add(documentsModel);
        if (docsList.size() == 1) {
            ll_hide_document_details.setVisibility(View.VISIBLE);
            hideDisableDownloadBackground();
        } else if (docsList.size() < 1) {
            ll_hide_document_details.setVisibility(View.GONE);
//            hideDisableDownloadBackground();
        }
        String tag = "view_tags";
        loadRecyclerview(tag, subtag);

//            ll_documents.addView(view);
//        }
    }

    private void loadRecyclerview(String tag, String subtag) {
        rv_documents.setLayoutManager(new GridLayoutManager(getContext(), 1));
        adapter = new DocumentsListAdapter(docsList, tag, subtag, this);
        rv_documents.setAdapter(adapter);
        rv_documents.setHasFixedSize(true);
        chk_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapter.selectOrDeselectAll(isChecked);
            }
        });
    }

    private void hideDisableDownloadBackground() {
        DOWNLOAD_TAG = false;
        tv_enable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_disable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));

    }

    private void AddTag() {
        chk_select_all.setBackground(getContext().getResources().getDrawable(R.drawable.checkbox_background));
        chk_select_all.setEnabled(true);
        btn_upload.setVisibility(View.GONE);
        btn_add_tags.setVisibility(View.VISIBLE);
        tv_edit_meta.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        String tag = "add_tag";
        loadRecyclerview(tag, subtag);
    }

    private void EditMeta() {
        String tag = "edit_meta";
        btn_upload.setVisibility(View.VISIBLE);
        btn_add_tags.setVisibility(View.GONE);
        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_edit_meta.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        loadRecyclerview(tag, subtag);
    }

    private void hideEnableDownloadBackground() {
        DOWNLOAD_TAG = true;
        tv_disable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_enable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));

    }

    public static File getFile(Context context, Uri uri) throws IOException {
        File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        mSelectedBitmap = bitmap;
        mSelectedUri = null;
//        tv_upload_file.setEnabled(false);
        File filesDir = getContext().getFilesDir();
        File imageFile = new File(filesDir, "bitmap" + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            file = imageFile;
            tv_selected_file.setText(file.getName());
            DocumentsModel documentsModel = new DocumentsModel();
            documentsModel.setName(file.getName());
            docsList.add(documentsModel);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            file = new File(picturePath);
            filename = file.getName();
            tv_selected_file.setText(file.getName());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
//                    upload_file();
                } else {
                    AndroidUtils.showToast("GET_ACCOUNTS Denied", getContext());
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
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
                if (httpResult.getRequestType().equals("Clients List")) {
                    JSONObject data = result.getJSONObject("data");
                    try {
                        loadClients(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (httpResult.getRequestType().equals("Legal Matter")) {
                    JSONArray matters = result.getJSONArray("matterList");
                    loadMatters(matters);
                } else if (httpResult.getRequestType().equals("Upload Document")) {
                    String msg = result.getString("msg");
                    AndroidUtils.showToast(msg, getContext());
                    rv_documents.removeAllViews();
                    clearListData();
                } else if (httpResult.getRequestType().equals("Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadGroupsData(data);
                }else if(httpResult.getRequestType().equals("VIEW_DOCUMENT")){
                    JSONArray docs = result.getJSONArray("docs");
                    laodViewDocuments(docs);
                }else if(httpResult.getRequestType().equals("Update Documents")){
                    String msg = result.getString("msg");
                    AndroidUtils.showToast(msg,getContext());
                    view_docs_list.clear();
                    rv_display_view_docs.removeAllViews();
                    callViewDocumentWebservice();

                }else if(httpResult.getRequestType().equals("Delete Documents")){
                    String msg = result.getString("msg");
                    AndroidUtils.showToast(msg,getContext());
                    view_docs_list.clear();
                    rv_display_view_docs.removeAllViews();
                    callViewDocumentWebservice();
                }else if(httpResult.getRequestType().equals("Display Documents")){
                    JSONObject jsonObject = result.getJSONObject("data");
                    String url = jsonObject.getString("url");
                    loadDisplayDocuments(url);



                }
            } catch (JSONException e) {
                e.printStackTrace();
                AndroidUtils.showAlert(e.getMessage(),getContext());
            }
        }
        else{
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                AndroidUtils.showAlert(result.getString("msg"), getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDisplayDocuments(String url) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.view_documents, null);
        ImageView iv_image = view.findViewById(R.id.doc_image);
        pdfView = view.findViewById(R.id.pdfview);
        ImageView iv_close_edit_docs = view.findViewById(R.id.close_edit_docs);
        List<String> list = new ArrayList<String>(Arrays.asList(CONTENT_TYPE.split("/")));
        if (list.get(1).equalsIgnoreCase("pdf")){
            loadWeb(pdfView,iv_image);
        }
        else{
            pdfView.setVisibility(View.GONE);
            iv_image.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.progress_animation)
                    .centerCrop()
                    .into(iv_image);
        }
        final AlertDialog dialog = dialogBuilder.create();
        iv_close_edit_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void loadWeb(PDFView  pdf_view, ImageView image_view) {
        image_view.setVisibility(View.GONE);
        pdf_view.setVisibility(View.VISIBLE);

        try {
            String path = "";//result.getString("path");
            path= Constants.pdfFilePath;
            pdf_view.fromFile(new File(path))
                    .defaultPage(0)
                    .enableSwipe(true)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdf_view.setVisibility(View.VISIBLE);
                            image_view.setVisibility(View.GONE);
                        }
                    })
                    .onError(new OnErrorListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onError(Throwable t) {
                            pdf_view.setVisibility(View.GONE);
                            image_view.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(R.drawable.pdf_icon_documents)
                                    .placeholder(R.drawable.progress_animation)
                                    .centerCrop()
                                    .into(image_view);
                        }
                    })
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void laodViewDocuments(JSONArray docs) throws JSONException {
        try {
            view_docs_list.clear();
            for (int i = 0; i < docs.length(); i++) {
                ViewDocumentsModel viewDocumentsModel = new ViewDocumentsModel();
                JSONObject jsonObject = docs.getJSONObject(i);
                viewDocumentsModel.setCreated(jsonObject.getString("created"));
                viewDocumentsModel.setContent_type(jsonObject.getString("content_type"));
                viewDocumentsModel.setDescription(jsonObject.getString("description"));
                viewDocumentsModel.setExpiration_date(jsonObject.getString("expiration_date"));
                viewDocumentsModel.setFilename(jsonObject.getString("filename"));
                viewDocumentsModel.setId(jsonObject.getString("id"));
                viewDocumentsModel.setIs_disabled(jsonObject.getBoolean("is_disabled"));
                viewDocumentsModel.setIs_encrypted(jsonObject.getBoolean("is_encrypted"));
                viewDocumentsModel.setIs_password(jsonObject.getBoolean("is_password"));
                viewDocumentsModel.setName(jsonObject.getString("name"));
                viewDocumentsModel.setOrigin(jsonObject.getString("origin"));
                viewDocumentsModel.setUploaded_by(jsonObject.getString("uploaded_by"));
                view_docs_list.add(viewDocumentsModel);
            }
            loadViewDocumentsRecyclerview();
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(),getContext());
        }
    }

    private void loadViewDocumentsRecyclerview() {
        try {
            if (view_docs_list.size()==0){
                rv_display_view_docs.removeAllViews();
                AndroidUtils.showToast("No documents to display",getContext());
            }else {
                rv_display_view_docs.setLayoutManager(new GridLayoutManager(getContext(), 1));
                View_documents_adapter adapter = new View_documents_adapter(view_docs_list, this, getContext());
                rv_display_view_docs.setAdapter(adapter);
                rv_display_view_docs.setHasFixedSize(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(),getContext());
        }
    }

    private void loadGroupsData(JSONArray data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                DocumentsModel documentsModel = new DocumentsModel();
                documentsModel.setGroup_id(jsonObject.getString("id"));
                documentsModel.setGroup_name(jsonObject.getString("name"));
                groupsList.add(documentsModel);
            }
            selectedLanguage = new boolean[groupsList.size()];
//            GroupsAlert();
            GroupsPopup();
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void GroupsPopup() {
        try {

            for (int i = 0; i < groupsList.size(); i++) {
                for (int j = 0; j < selected_groups_list.size(); j++) {
                    if (groupsList.get(i).getGroup_id().matches(selected_groups_list.get(j).getGroup_id())) {
                        DocumentsModel documentsModel = groupsList.get(i);
                        documentsModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                    }
                }
            }
            selected_groups_list.clear();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.groups_list_adapter, null);
            RecyclerView rv_groups = view.findViewById(R.id.rv_relationship_documents);
            ImageView iv_cancel = view.findViewById(R.id.close_groups);
            AppCompatButton btn_groups_cancel = view.findViewById(R.id.btn_groups_cancel);
            AppCompatButton btn_save_group = view.findViewById(R.id.btn_save_group);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_groups.setLayoutManager(layoutManager);
            rv_groups.setHasFixedSize(true);
            GroupsListAdapter documentsAdapter = new GroupsListAdapter(groupsList);
            rv_groups.setAdapter(documentsAdapter);
            AlertDialog dialog = dialogBuilder.create();
            iv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btn_groups_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btn_save_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ArrayList<String>
                    for (int i = 0; i < documentsAdapter.getList_item().size(); i++) {
                        DocumentsModel documentsModel = documentsAdapter.getList_item().get(i);
                        if (documentsModel.isGroupChecked()) {
                            selected_groups_list.add(documentsModel);

//                           jsonArray.put(selected_documents_list.get(i).getGroup_name());


                        }
                    }

                    String[] value = new String[selected_groups_list.size()];
                    for (int i = 0; i < selected_groups_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
                        value[i] = selected_groups_list.get(i).getGroup_name();

                    }

                    String str = String.join(",", value);
                    tv_select_groups.setText(str);
                    dialog.dismiss();
                }

            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadMatters(JSONArray matters) throws JSONException {
        for (int i = 0; i < matters.length(); i++) {
            JSONObject jsonObject = matters.getJSONObject(i);
            MattersModel mattersModel = new MattersModel();
            mattersModel.setId(jsonObject.getString("id"));
            mattersModel.setTitle(jsonObject.getString("title"));
            mattersModel.setType(jsonObject.getString("type"));
            matterlist.add(mattersModel);
        }
        initMatter();
    }

    private void initMatter() {
        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), matterlist);
        Log.i("ArrayList", "Info:" + matterlist);
//        ArrayAdapter adaptador = new ArrayAdapter(User_Profile.this, android.R.layout.simple_spinner_item, sorted_countriesList);
//        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinner.setAdapter(adaptador);
        sp_matter.setAdapter(adapter);
        sp_matter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                matter_id = matterlist.get(position).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initUI(ArrayList<ClientsModel> clientsList) {
        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), this.clientsList);
//        Log.i("ArrayList","Info:"+matterlist);
//        ArrayAdapter adaptador = new ArrayAdapter(User_Profile.this, android.R.layout.simple_spinner_item, sorted_countriesList);
//        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinner.setAdapter(adaptador);
        sp_client.setAdapter(adapter);
        sp_client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                matter_name = Documents.this.clientsList.get(position).getName();
                client_id = clientsList.get(position).getId();
                matterlist.clear();
                callLegalMatter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void loadClients(JSONObject data) throws JSONException {
        JSONArray relationships = data.getJSONArray("relationships");

        for (int i = 0; i < relationships.length(); i++) {
            JSONObject jsonObject = relationships.getJSONObject(i);
            ClientsModel clientsModel = new ClientsModel();
            clientsModel.setId(jsonObject.getString("id"));
            clientsModel.setName(jsonObject.getString("name"));
            clientsModel.setType(jsonObject.getString("type"));
            clientsList.add(clientsModel);
//                    updatedClients.add(clientsModel);
        }
        initUI(clientsList);
    }

    private void callLegalMatter() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/matter/all/" + client_id, "Legal Matter", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.printStackTrace();
        }
    }


    @Override
    public void ViewTags(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_tags = inflater.inflate(R.layout.edit_existing_tags, null);
        LinearLayout ll_existing_tags = view_edit_tags.findViewById(R.id.ll_view_tags);
        ImageView iv_close_existing_tags = view_edit_tags.findViewById(R.id.close_exisiting_tags);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(documentsModel.getTags());
        Iterator<String> iter = documentsModel.getTags().keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
//                String value = String.valueOf(documentsModel.getTags().get(key));
                View view_added_tags = LayoutInflater.from(getContext()).inflate(R.layout.displays_documents_list, null);
                TextView tv_tag_name = view_added_tags.findViewById(R.id.tv_document_name);
                ImageView iv_remove_tag = view_added_tags.findViewById(R.id.iv_cancel);
                iv_remove_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        documentsModel.getTags().remove()
                    }
                });

                tv_tag_name.setText(key + " - " + documentsModel.getTags().get(key));
                ll_existing_tags.addView(view_added_tags);
//                Object value = documentsModel.getTags().get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        AlertDialog dialog = dialogBuilder.create();
        iv_close_existing_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view_edit_tags);
        dialog.show();
    }

    @Override
    public void EditDocuments(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_documents = inflater.inflate(R.layout.edit_meta_data, null);
        ImageView iv_cancel_edit_doc = view_edit_documents.findViewById(R.id.close_edit_docs);
        AppCompatButton btn_close_edit_docs = view_edit_documents.findViewById(R.id.btn_cancel_edit_docs);
        TextInputEditText tv_doc_name = view_edit_documents.findViewById(R.id.edit_doc_name);
        TextInputEditText tv_description = view_edit_documents.findViewById(R.id.edit_description);
        AppCompatButton tv_exp_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
        tv_doc_name.setText(documentsModel.getName());
        tv_description.setText(documentsModel.getDescription());
        Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_exp_date.setText(sdf.format(myCalendar.getTime()));
            }
        };
        tv_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            tv_exp_date.setText(formattedDate);

        AppCompatButton btn_save_tag = view_edit_documents.findViewById(R.id.btn_save_tag);
        final AlertDialog dialog = dialogBuilder.create();
        iv_cancel_edit_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_close_edit_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_save_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < itemsArrayList.size(); i++) {

                    if (documentsModel.getName().matches(itemsArrayList.get(i).getName())) {
                        DocumentsModel documentsModel1 = itemsArrayList.get(i);
                        documentsModel1.setName(tv_doc_name.getText().toString());
                        documentsModel1.setDescription(tv_description.getText().toString());
                        itemsArrayList.set(i, documentsModel1);
                        dialog.dismiss();
                        String tag = "edit_meta";
                        loadRecyclerview(tag, "");

                    }
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view_edit_documents);
        dialog.show();

    }

    @Override
    public void RemoveDocument(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList, String tag) {
        for (int i = 0; i < itemsArrayList.size(); i++) {
            if (documentsModel.getName().matches(itemsArrayList.get(i).getName())) {
                DocumentsModel documentsModel1 = itemsArrayList.get(i);
                itemsArrayList.remove(i);
//                dialog.dismiss();
//                String tag = "edit_meta";
                loadRecyclerview(tag, "");
            }
        }
    }

    @Override
    public void edit_document(ViewDocumentsModel viewDocumentsModel) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_documents = inflater.inflate(R.layout.edit_meta_data, null);
        ImageView iv_cancel_edit_doc = view_edit_documents.findViewById(R.id.close_edit_docs);
        AppCompatButton btn_close_edit_docs = view_edit_documents.findViewById(R.id.btn_cancel_edit_docs);
        TextInputEditText tv_doc_name = view_edit_documents.findViewById(R.id.edit_doc_name);
        TextInputEditText tv_description = view_edit_documents.findViewById(R.id.edit_description);
        AppCompatButton tv_exp_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
//        TextInputEditText tv_expiration_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
        Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_exp_date.setText(sdf.format(myCalendar.getTime()));
            }
        };
        tv_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });if (viewDocumentsModel.getExpiration_date().equalsIgnoreCase("NA")||viewDocumentsModel.getExpiration_date().equalsIgnoreCase("")||viewDocumentsModel.getExpiration_date().equalsIgnoreCase(null)){
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            tv_exp_date.setText(formattedDate);
        }else
        {
            String exp_date = viewDocumentsModel.getExpiration_date();
            Date date_new = AndroidUtils.stringToDateTimeDefault(exp_date, "MMM dd YYYY");
            String created = AndroidUtils.getDateToString(date_new, "dd-MM-yyyy");
            tv_exp_date.setText(created);
        }
        tv_doc_name.setText(viewDocumentsModel.getName());
        tv_description.setText(viewDocumentsModel.getDescription());

        AppCompatButton btn_save_tag = view_edit_documents.findViewById(R.id.btn_save_tag);
        final AlertDialog dialog = dialogBuilder.create();
        iv_cancel_edit_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_close_edit_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_save_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callUpdateDocumentWebservice(tv_doc_name.getText().toString(),tv_description.getText().toString(),tv_exp_date.getText().toString(),viewDocumentsModel.getId());
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view_edit_documents);
        dialog.show();
    }

    private void callUpdateDocumentWebservice(String name, String description, String expiration_date, String id) {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",name);
            jsonObject.put("description",description);
            jsonObject.put("expiration_date",expiration_date);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/"+id, "Update Documents", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    @Override
    public void delete_document(ViewDocumentsModel viewDocumentsModel) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            tv_confirmation.setText("Are you sure you want to delete " +viewDocumentsModel.getName() + "?");
            AppCompatButton bt_yes = view.findViewById(R.id.btn_yes);
            AppCompatButton btn_no = view.findViewById(R.id.btn_No);
            final AlertDialog dialog = dialogBuilder.create();
//            ad_dialog_delete = dialog;
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            bt_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    callDeleteDocumentWebservice(viewDocumentsModel.getId());
                }
            });

            dialog.setView(view);
            dialog.show();
        } catch (Exception e) {
            AndroidUtils.showToast(e.getMessage(), getContext());
        }
    }

    @Override
    public void Display_Document(ViewDocumentsModel viewDocumentsModel) {
        CONTENT_TYPE = viewDocumentsModel.getContent_type();
        callDisplayDocumentWebservice(viewDocumentsModel.getId());


    }

    private void callDisplayDocumentWebservice(String id) {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/document/"+id+"/view", "Display Documents", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    private void callDeleteDocumentWebservice(String id) {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(id);
            jsonObject.put("docids",jsonArray);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/document/delete", "Delete Documents", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }
}



