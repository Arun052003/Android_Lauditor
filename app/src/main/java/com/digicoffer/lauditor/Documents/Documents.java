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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
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
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.github.barteksc.pdfviewer.PDFView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class Documents extends Fragment implements BottomSheetUploadFile.OnPhotoSelectedListner, AsyncTaskCompleteListener, DocumentsListAdapter.EventListener, View_documents_adapter.Eventlistner, GroupsListAdapter.OnCheckedChangeListener {
    Button btn_browse, btn_group_cancel, btn_group_submit, btn_group_view_cancel, btn_group_view_submit;

    //Initialize a file count to Zero
    String exp_date = "";
    //....
    TextView custom_spinner, custom_spinner2, custom_spinner3, custom_spinner4, custom_spinner_group, tv_select_groups_view;
    CardView cv_view_doc;
    LinearLayout chk_box_layout;
    boolean isselect_all_checked = true;

    JSONArray array_group = new JSONArray();
    boolean is_clicked_add = true;
    boolean ischecked_group = true;
    boolean ischecked_group_view = true;
    boolean is_clicked_edit = true;
    boolean ischecked = true;
    ListView list_client, list_matter, list_client_view, list_matter_view, list_group;
    ScrollView list_scroll, list_scroll2, list_scroll3, list_scroll4, list_scroll_group;

    //...
    int count_file = 0;
    private NewModel mViewModel;
    RelativeLayout spinnerLayout;
    private boolean ismatter_chosen = true;

    BottomSheetUploadFile bottommSheetUploadDocument;
    private Bitmap mSelectedBitmap;
    LinearLayout ll_added_tags, ll_matter, ll_category, ll_groups, ll_client_name, ll_view_docs, ll_upload_docs, upload_group_layout, view_group_layout;
    LinearLayout ll_matter_view, ll_client_name_view, ll_categories_layout, ll_document_type_view, ll_search_client_view;
    TextInputEditText tv_tag_type, tv_tag_name, tv_search_client_view;
    private ImageView imageView;
    boolean[] selectedLanguage;
    String DOCUMENT_TYPE_TAG = "client";
    String CONTENT_TYPE = "";
    String UPLOAD_TAG = "Client";
    String VIEW_TAG = "Client";
    DocumentsListAdapter adapter;
    CardView cv_view_documents;
    ArrayList<ViewDocumentsModel> view_docs_list = new ArrayList<>();
    ShapeableImageView siv_upload_document, siv_view_document;
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
    ArrayList<ClientsModel> clientsList = new ArrayList<>();
    ArrayList<MattersModel> matterlist = new ArrayList<>();
    ArrayList<ClientsModel> updatedClients = new ArrayList<>();
    ArrayList<DocumentsModel> selected_groups_list = new ArrayList<>();
    ArrayList<DocumentsModel> docsList = new ArrayList<>();
    RecyclerView rv_documents, rv_display_view_docs, rv_display_upload_groups_docs, rv_display_view_groups_docs;
    AlertDialog progress_dialog;

    TextView tv_matter, select_documents, tv_select_group_name, upload_name, view_name, tag_type_name, tag_name, header_name, header_name_group;
    TextView category_name, tv_category, tv_selected_file;

    //edit_tag..
    TextView header_name_edit, tag_type_edit, tag_edit;
    TextInputEditText tv_edit_tag_type, tv_edit_tag_name;
    Button btn_upload, btn_add_tags, btn_cancel;
    TextView tv_add_tag, tv_client, tv_firm, tv_enable_download, tv_disable_download, tv_edit_meta, tv_name, tv_client_view, tv_firm_view, tv_name_view, matter_name, category_name_id, select_doc_type, tv_document_name, description;
    //    AutoCompleteTextView ;
    File file;
    String value = "";
    String entity_id = "";
    String matter_id = "";
    String client_id = "";
    String group_id = "";
    TextView tv_tag_document_name, tv_select_groups, merge_pdf;
    ;
    //    TextInputLayout tl_selected_file;
    LinearLayout ll_hide_document_details, ll_search_client_views;
    Spinner sp_matter, sp_client, tv_search_client, sp_matter_view;
    TextInputEditText tv_search_client_views;
    TextInputLayout tl_selected_file;
    PDFView pdfView;
    private boolean ischecked_matter = true;
    private boolean ischecked_matter2 = true;
    private boolean ischecked2 = true;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.upload_document, container, false);
        try {
            mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
            mViewModel.setData("Document Upload");
            tv_client_view = v.findViewById(R.id.tv_client_view);
            tv_client_view.setText(R.string.client);
//            tv_client_view.setPadding(20,10,20,10);
            tv_firm_view = v.findViewById(R.id.tv_firm_view);
            tv_firm_view.setText(R.string.firm);
//            tv_firm_view.setPadding(20,10,20,10);
//            sp_client = v.findViewById(R.id.at_search_client);
//            sp_client.isShown();
//            tv_search_client = v.findViewById(R.id.tv_search_client);
            cv_view_documents = v.findViewById(R.id.cv_view_documents);
            cv_view_documents.setVisibility(View.GONE);
            tv_search_client_view = v.findViewById(R.id.tv_search_client_view);

            custom_spinner = v.findViewById(R.id.custom_spinner);

//            custom_spinner_group=v.findViewById(R.id.custom_spinner_group);
//            list_group=v.findViewById(R.id.list_group);
//            list_scroll_group=v.findViewById(R.id.list_scroll_group);

            list_client = v.findViewById(R.id.list_client);
//            list_scroll = v.findViewById(R.id.list_scroll);
            custom_spinner2 = v.findViewById(R.id.custom_spinner2);
            list_matter = v.findViewById(R.id.list_matter);
//            list_scroll2 = v.findViewById(R.id.list_scroll2);

            custom_spinner3 = v.findViewById(R.id.custom_spinner3);
            custom_spinner4 = v.findViewById(R.id.custom_spinner4);
            list_client_view = v.findViewById(R.id.list_client_view);
            list_matter_view = v.findViewById(R.id.list_matter_view);
//            list_scroll3 = v.findViewById(R.id.list_scroll3);
//            list_scroll4 = v.findViewById(R.id.list_scroll4);


            // tv_search_client_view.setHint("Search");
            // tv_search_client_view.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_light_grey_bg));

//            sp_matter = v.findViewById(R.id.sp_matter);
//            sp_matter_view = v.findViewById(R.id.sp_matter_view);
            tv_add_tag = v.findViewById(R.id.tv_add_tag);
            tv_edit_meta = v.findViewById(R.id.tv_edit_meta);
            btn_upload = v.findViewById(R.id.btn_upload);
            tv_firm = v.findViewById(R.id.tv_firm);
            ll_search_client_views = v.findViewById(R.id.ll_search_client_views);
            tv_search_client_views = v.findViewById(R.id.tv_search_client_views);
            //  tv_search_client_views.setHint("Search");
            //   tv_search_client_views.setBackground(getContext().getResources().getDrawable(R.drawable.rectangle_light_grey_bg));

            tv_name = v.findViewById(R.id.tv_name);
            ll_category = v.findViewById(R.id.ll_category);
            tv_name_view = v.findViewById(R.id.tv_name_view);
            tv_name_view.setText(R.string.client_name);
            matter_name = v.findViewById(R.id.matter_name);
            matter_name.setText(R.string.matter);
            merge_pdf = v.findViewById(R.id.merge_pdf);
            merge_pdf.setText(R.string.merge_pdf);
            merge_pdf.setBackground(getActivity().getDrawable(R.drawable.rectangle_light_grey_bg));
            rv_display_view_docs = v.findViewById(R.id.rv_display_view_docs);
            select_doc_type = v.findViewById(R.id.select_doc_type);
            select_doc_type.setText(R.string.select_groups);
            category_name_id = v.findViewById(R.id.category_name_id);
            category_name_id.setText(R.string.sub_categories);
            ll_groups = v.findViewById(R.id.ll_groups);


            siv_upload_document = v.findViewById(R.id.upload_icon);
            siv_view_document = v.findViewById(R.id.view_icon);
            spinnerLayout = v.findViewById(R.id.spinnerLayout);
//            btn_upload = v.findViewById(R.id.btn_upload);
            btn_upload.setText(R.string.upload);
            btn_cancel = v.findViewById(R.id.btn_cancel);
            btn_add_tags = v.findViewById(R.id.btn_add_tag);
            btn_add_tags.setText(R.string.add_tag);
            btn_add_tags.setVisibility(View.GONE);
            btn_browse = v.findViewById(R.id.btn_browse);
            btn_browse.setBackground(getActivity().getDrawable(R.drawable.rectangular_complete_blue_background));
            btn_browse.setText(R.string.browse_small);

            upload_group_layout = v.findViewById(R.id.upload_group_layout);
            rv_display_upload_groups_docs = v.findViewById(R.id.rv_display_upload_groups_docs);
            btn_group_cancel = v.findViewById(R.id.btn_group_cancel);
            btn_group_cancel.setVisibility(View.GONE);
            btn_group_submit = v.findViewById(R.id.btn_group_submit);
            btn_group_submit.setVisibility(View.GONE);

            view_group_layout = v.findViewById(R.id.view_group_layout);
            rv_display_view_groups_docs = v.findViewById(R.id.rv_display_view_groups_docs);
            rv_display_view_groups_docs.setBackground(getContext().getDrawable(R.drawable.rectangle_light_grey_bg));
            btn_group_view_cancel = v.findViewById(R.id.btn_group_view_cancel);
            btn_group_view_submit = v.findViewById(R.id.btn_group_view_submit);

//            tl_selected_file = v.findViewById(R.id.tl_selected_file);

            //modifying the name of add tag and edit meta textview..
            tv_add_tag = v.findViewById(R.id.tv_add_tag);
            tv_add_tag.setText(R.string.add_tag);
            tv_edit_meta = v.findViewById(R.id.tv_edit_meta);
            tv_edit_meta.setText(R.string.edit_meta);
            tv_client = v.findViewById(R.id.tv_client);
            tv_client.setText(R.string.client);
//            tv_client.setPadding(20,10,20,10);
            tv_firm = v.findViewById(R.id.tv_firm);
            tv_firm.setText(R.string.firm);
            tv_name = v.findViewById(R.id.tv_name);
            tv_name.setText(R.string.client_name);
            tv_matter = v.findViewById(R.id.tv_matter);
            tv_matter.setText(R.string.matter);
            tv_category = v.findViewById(R.id.tv_category);
            tv_category.setHint(R.string.sub_categories);
            tv_enable_download = v.findViewById(R.id.tv_enable_download);
            tv_enable_download.setText(R.string.enable_download);
            tv_disable_download = v.findViewById(R.id.tv_disable_download);
            tv_disable_download.setText(R.string.disable_download);
            tv_select_groups = v.findViewById(R.id.tv_select_groups);
            tv_select_group_name = v.findViewById(R.id.tv_select_group_name);
            tv_select_group_name.setText(R.string.select_groups);
            tv_select_groups.setText(R.string.select_groups);

//            tv_select_groups.setText(null);

            tv_selected_file = v.findViewById(R.id.tv_selected_file);

            ll_client_name = v.findViewById(R.id.ll_client_name);
            ll_matter = v.findViewById(R.id.ll_matter);
            ll_category = v.findViewById(R.id.ll_category);
            ll_groups = v.findViewById(R.id.ll_groups);
            ll_upload_docs = v.findViewById(R.id.ll_upload_docs);

            ll_view_docs = v.findViewById(R.id.ll_view_docs);
            ll_hide_document_details = v.findViewById(R.id.ll_hide_doc_details);
            ll_hide_document_details.setVisibility(View.GONE);

            category_name = v.findViewById(R.id.category_name);
            category_name.setText(R.string.sub_categories);
            upload_name = v.findViewById(R.id.upload_name);
            upload_name.setTextColor(getContext().getResources().getColor(R.color.white));
            upload_name.setText(R.string.upload);
            view_name = v.findViewById(R.id.view_name);
            view_name.setTextColor(getContext().getResources().getColor(R.color.white));
            view_name.setText(R.string.view);
            select_documents = v.findViewById(R.id.select_documents);
            select_documents.setText(R.string.select_document);

            chk_box_layout = v.findViewById(R.id.chk_box_layout);
            chk_box_layout.setAlpha(0.5F);
            chk_select_all = v.findViewById(R.id.chk_select_all);
            chk_select_all.getBackground().setAlpha(50);
            chk_select_all.setEnabled(false);
//            upload_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancel_icon,0,0,0);

//            tv_merge_pdf.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_light_grey_background));
//            tv_select_document_type=v.findViewById(R.id.tv_select_document_type);
//            tv_select_document_type.setText(R.string.select_document_type);
            rv_display_view_docs = v.findViewById(R.id.rv_display_view_docs);
            rv_documents = v.findViewById(R.id.rv_documents);


            //Enable the upload documents as the default view...
            siv_upload_document.setBackground(getContext().getResources().getDrawable(R.color.green_count_color));
            siv_upload_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_background_icon));


//            view_document();
            ll_matter_view = v.findViewById(R.id.ll_matter_view);
            ll_document_type_view = v.findViewById(R.id.ll_document_type_view);
            ll_client_name_view = v.findViewById(R.id.ll_client_name_view);
            ll_categories_layout = v.findViewById(R.id.ll_categories_layout);
            ll_search_client_view = v.findViewById(R.id.ll_search_client_view);
            ll_client_name = v.findViewById(R.id.ll_client_name);
            btn_add_tags = v.findViewById(R.id.btn_add_tag);
            category_name = v.findViewById(R.id.tv_category_name);
            category_name.setHint(R.string.sub_categories);
            tv_enable_download = v.findViewById(R.id.tv_enable_download);

            tv_disable_download = v.findViewById(R.id.tv_disable_download);
            ll_hide_document_details = v.findViewById(R.id.ll_hide_doc_details);
            ll_hide_document_details.setVisibility(View.GONE);
//            rv_documents = v.findViewById(R.id.rv_documents);

            //Make View Documents as Default View.....
            ll_upload_docs.setVisibility(View.GONE);
            ll_view_docs.setVisibility(View.VISIBLE);
            view_document();
            //..
            custom_spinner.setText("");
            custom_spinner2.setText("");
            list_matter.setVisibility(View.GONE);
            list_client.setVisibility(View.GONE);
            ll_matter.setVisibility(View.GONE);
            //...

            //..

            custom_spinner3.setText("");
            custom_spinner4.setText("");
            list_matter_view.setVisibility(View.GONE);
            list_client_view.setVisibility(View.GONE);
            ll_matter_view.setVisibility(View.GONE);
            //...

            //....

            custom_spinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clientsList.clear();
                    callClientWebservice();
                    if (ischecked)
                        list_client.setVisibility(View.VISIBLE);
                    else
                        list_client.setVisibility(View.GONE);
                    ischecked = !ischecked;
                }
            });
            custom_spinner2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ischecked_matter)
                        list_matter.setVisibility(View.VISIBLE);
                    else
                        list_matter.setVisibility(View.GONE);
                    ischecked_matter = !ischecked_matter;
                }
            });
            custom_spinner3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clientsList.clear();
                    callClientWebservice();
                    if (ischecked2)
                        list_client_view.setVisibility(View.VISIBLE);
                    else
                        list_client_view.setVisibility(View.GONE);
                    ischecked2 = !ischecked2;
                }
            });
            custom_spinner4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ischecked_matter2)
                        list_matter_view.setVisibility(View.VISIBLE);
                    else
                        list_matter_view.setVisibility(View.GONE);
                    ischecked_matter2 = !ischecked_matter2;
                }
            });
            //...

            siv_upload_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upload_documents();
                }
            });

            //view document option selection.....
            siv_view_document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view_document();
                }
            });

//            tv_select_groups = v.findViewById(R.id.tv_select_groups);
            tv_select_groups_view = v.findViewById(R.id.tv_select_groups_view);
            tv_select_groups_view.setHint(R.string.select_groups);

            // .....
            upload_group_layout.setVisibility(View.GONE);

            view_group_layout.setVisibility(View.GONE);

            //...upload Documents Group Selection
            tv_select_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ischecked_group) {
                        if (groupsList.isEmpty()) {
                            callGroupsWebservice();
                        } else {
                            GroupsPopup();
                        }
                    } else {
                        upload_group_layout.setVisibility(View.GONE);
                    }
                    ischecked_group = !ischecked_group;
                }
            });

            //...View Documents Group Selection
            tv_select_groups_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ischecked_group_view) {
                        if (groupsList.isEmpty()) {
                            callGroupsWebservice();
                        } else {
                            GroupsPopup();
                        }
                        view_group_layout.setVisibility(View.VISIBLE);
                    } else {
                        view_group_layout.setVisibility(View.GONE);
                    }
                    ischecked_group_view = !ischecked_group_view;
                }
            });

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
                    tv_firm.setTextColor(getContext().getResources().getColor(R.color.black));
                    tv_client.setTextColor(getContext().getResources().getColor(R.color.white));
                    rv_documents.removeAllViews();
                    ischecked = true;
                    hideFirmBackground();
                }
            });
            tv_firm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideClientBackground();
                    rv_display_upload_groups_docs.removeAllViews();
                }
            });
            tv_add_tag.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (is_clicked_add) {
                        AddTag();
                    } else {
                        Hide_Add_EditMeta();
                    }
                    is_clicked_edit = true;
                    is_clicked_add = !is_clicked_add;
                }
            });
            tv_edit_meta.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (is_clicked_edit) {
                        EditMeta();
                    } else {
                        Hide_Add_EditMeta();
                    }
                    is_clicked_add = true;
                    is_clicked_edit = !is_clicked_edit;
                }
            });

            btn_browse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissionREAD_EXTERNAL_STORAGE(getContext());
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
                    CATEGORY_TAG = "client";
//                    cv_view_documents.setVisibility(View.VISIBLE);
                    rv_display_view_docs.setVisibility(View.VISIBLE);
//                    callViewDocumentWebservice();
                }
            });
            tv_firm_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideviewClientBackground();
                    view_docs_list.clear();
                    view_group_layout.setVisibility(View.GONE);
                    ischecked_group_view = true;
                    ll_matter_view.setVisibility(View.GONE);
//                    rv_display_view_groups_docs.removeAllViews();
                    rv_display_view_docs.removeAllViews();
                    DOCUMENT_TYPE_TAG = "firm";
                    rv_display_view_docs.removeAllViews();
                    cv_view_documents.setVisibility(View.GONE);
//                    callViewDocumentWebservice();
//                    callclientfirmWebServices();
                }
            });
            merge_pdf.setOnClickListener(new View.OnClickListener() {

                boolean is_clicked = true;

                @Override
                public void onClick(View view) {
                    if (is_clicked) {
                        merge_pdf.setTextColor(getContext().getResources().getColor(R.color.white));
                        merge_pdf.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_button_green_count));
                    } else {
                        merge_pdf.setTextColor(getContext().getResources().getColor(R.color.black));
                        merge_pdf.setBackground(getContext().getResources().getDrawable(R.drawable.rectangular_light_grey_background));
                    }
                    is_clicked = !is_clicked;
                }
            });

            //Adding cancel functionalities in upload documents...
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count_file = 0;
                    clear_upload();
                }
            });
            btn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callUploadDocumentWebservice();
                }
            });

        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return v;
    }


    private void view_document() {
        siv_view_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_count_backgroung_icon));
        siv_view_document.setBackground(getContext().getResources().getDrawable(R.color.green_count_color));
        siv_upload_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.uploadwhiteupload1));
        siv_upload_document.setBackground(getContext().getResources().getDrawable(R.color.white));
        ll_client_name_view.setVisibility(View.VISIBLE);
        ll_view_docs.setVisibility(View.VISIBLE);
        view_group_layout.setVisibility(View.GONE);
        ll_upload_docs.setVisibility(View.GONE);
        ll_matter_view.setVisibility(View.GONE);
        rv_display_upload_groups_docs.removeAllViews();
        hideviewFirmBackground();
        mViewModel.setData("View Documents");
    }

    private void callViewDocumentWebservice() {
        try {
            JSONObject jsonObject = new JSONObject();
            progress_dialog = AndroidUtils.get_progress(getActivity());
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/documents/" + DOCUMENT_TYPE_TAG, "VIEW_DOCUMENT", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void hideviewFirmBackground() {
        VIEW_TAG = "Client";
        ischecked2 = true;
        tv_search_client_view.getText().clear();
        ll_matter_view.setVisibility(View.GONE);
        ll_client_name_view.setVisibility(View.VISIBLE);
        ll_categories_layout.setVisibility(View.GONE);
        ll_document_type_view.setVisibility(View.GONE);
        ll_search_client_view.setVisibility(View.VISIBLE);
        ll_search_client_views.setVisibility(View.GONE);
//        sp_documnet_type_view.setText("Select Groups");
        custom_spinner3.setText("");
        custom_spinner4.setText("");
        list_matter_view.setVisibility(View.GONE);
        list_client_view.setVisibility(View.GONE);
        ll_matter_view.setVisibility(View.GONE);

        tv_firm_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_firm_view.setTextColor(Color.BLACK);
        tv_client_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_client_view.setTextColor(Color.WHITE);
    }

    private void hideviewClientBackground() {
        VIEW_TAG = "Firm";
        CATEGORY_TAG = "firm";
        tv_search_client_views.getText().clear();
        ll_matter_view.setVisibility(View.GONE);
        ll_client_name_view.setVisibility(View.GONE);
        ll_document_type_view.setVisibility(View.VISIBLE);
        ll_search_client_view.setVisibility(View.GONE);
        ll_search_client_views.setVisibility(View.VISIBLE);
        ll_categories_layout.setVisibility(View.VISIBLE);
        tv_select_groups_view.setHint(R.string.select_groups);
        clearListData();
        custom_spinner3.setText("");
        custom_spinner4.setText("");
        tv_firm_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        tv_firm_view.setTextColor(Color.WHITE);

        tv_client_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_client_view.setTextColor(Color.BLACK);

    }

    private void upload_documents() {
        count_file = 0;
        tv_client.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_selected_file.setHint(R.string.select_documents);
        siv_view_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.eye_whitebackground_icon));
        siv_view_document.setBackground(getContext().getResources().getDrawable(R.color.white));
        siv_upload_document.setBackground(getContext().getResources().getDrawable(R.color.green_count_color));
        siv_upload_document.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_background_icon));
        ll_upload_docs.setVisibility(View.VISIBLE);
        ll_view_docs.setVisibility(View.GONE);
        rv_documents.removeAllViews();
        rv_display_upload_groups_docs.removeAllViews();
        hideFirmBackground();
        //..
        custom_spinner.setText("");
        custom_spinner2.setText("");
        list_matter.setVisibility(View.GONE);
        list_client.setVisibility(View.GONE);
        ll_matter.setVisibility(View.GONE);
        //...
        mViewModel.setData("Document Upload");
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
            tag_type_name = view.findViewById(R.id.tag_type_name);
            tag_type_name.setText(R.string.tag_type);
            tag_name = view.findViewById(R.id.tag_name);
            tag_name.setText(R.string.tag);
            tv_tag_type.setHint(R.string.tag_type);
            tv_tag_name.setHint(R.string.tag);
            header_name = view.findViewById(R.id.header_name);
            header_name.setText(R.string.add_tag);
            header_name.setGravity(Gravity.CENTER);
            header_name.setTextColor(getContext().getResources().getColor(R.color.white));
            header_name.setTextSize(20);
            final Button btn_add = view.findViewById(R.id.btn_add_tags);
            btn_add.setText(R.string.add);
            final AppCompatButton btn_cancel = view.findViewById(R.id.btn_cancel_tag);
            final AppCompatButton btn_save_tag = view.findViewById(R.id.btn_save_tag);
            final ImageView iv_cancel = view.findViewById(R.id.close_tags);
            iv_cancel.setImageResource(R.drawable.cancel_icon);
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
                                e.fillInStackTrace();
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
            LinearLayoutCompat chk_box_layout = view_added_tags.findViewById(R.id.chk_box_layout);
            chk_box_layout.setVisibility(View.GONE);
            CheckBox chk_selected_documents = view_added_tags.findViewById(R.id.chk_selected_documents);
            chk_selected_documents.setVisibility(View.GONE);
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

    @SuppressLint("MissingInflatedId")
    private void edit_tags(String tag_type, String tag_name, int position, View view_tag, TextView tv_tag_document_name) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_tags = inflater.inflate(R.layout.edit_tag, null);
        tv_edit_tag_type = view_edit_tags.findViewById(R.id.tv_edit_tag_type);
        tv_edit_tag_name = view_edit_tags.findViewById(R.id.tv_edit_tag_name);
        tag_type_edit = view_edit_tags.findViewById(R.id.tag_type_edit);
        tag_type_edit.setText(R.string.tag_type);
        tag_edit = view_edit_tags.findViewById(R.id.tag_edit);
        tag_edit.setText(R.string.tag);
        tv_edit_tag_type.setHint(R.string.tag_type);
        tv_edit_tag_name.setHint(R.string.tag_type);
        AppCompatButton btn_cancel = view_edit_tags.findViewById(R.id.btn_edit_cancel_tag);
        header_name_edit = view_edit_tags.findViewById(R.id.header_name_edit);
        header_name_edit.setText("Edit Tag");
        header_name_edit.setTextSize(20);
        header_name_edit.setGravity(Gravity.CENTER);
        header_name_edit.setTextColor(getContext().getResources().getColor(R.color.white));
        AppCompatButton btn_save_edited_tag = view_edit_tags.findViewById(R.id.btn_edit_save_tag);
        ImageView iv_close_edit_tags = view_edit_tags.findViewById(R.id.edit_close_tags);
        iv_close_edit_tags.setImageResource(R.drawable.cancel_icon);
        tv_edit_tag_type.setText(tag_type);
        tv_edit_tag_name.setText(tag_name);
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
                save_edited_tags(tv_edit_tag_type.getText().toString(), tv_edit_tag_name.getText().toString(), position, view_tag, dialog, tv_tag_document_name);
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
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void hideFirmBackground() {
        count_file = 0;
//        tv_select_groups_view.setText("");
        tv_selected_file.setHint(R.string.select_documents);
        UPLOAD_TAG = "Client";
        ll_category.setVisibility(View.GONE);
        ll_matter.setVisibility(View.VISIBLE);
        ll_groups.setVisibility(View.GONE);
        ll_client_name.setVisibility(View.VISIBLE);
        clearListData();
        rv_display_upload_groups_docs.removeAllViews();
        //..
        custom_spinner.setText("");
        custom_spinner2.setText("");
        list_matter.setVisibility(View.GONE);
        list_client.setVisibility(View.GONE);
        ll_matter.setVisibility(View.GONE);
        //...
        tv_client.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_client.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_firm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_firm.setTextColor(getContext().getResources().getColor(R.color.black));
    }

    private void hideClientBackground() {
        count_file = 0;
        tv_selected_file.setHint(R.string.select_documents);
        UPLOAD_TAG = "Firm";
        upload_group_layout.setVisibility(View.GONE);
        ischecked_group = true;
        ll_matter.setVisibility(View.GONE);
        ll_category.setVisibility(View.VISIBLE);
        ll_groups.setVisibility(View.VISIBLE);
        tv_select_groups.setHint(R.string.select_groups);
        ll_client_name.setVisibility(View.GONE);
        rv_documents.removeAllViews();
        tv_client.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_firm.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        tv_firm.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_client.setTextColor(getContext().getResources().getColor(R.color.black));
        clearListData();
    }

    private void clearListData() {
        ll_hide_document_details.setVisibility(View.GONE);
//        tv_selected_file.setText("");
        selected_groups_list.clear();
        selected_documents_list.clear();
//        tv_select_groups_view.setText("Select Groups");
        langList.clear();
        tags_list.clear();
        docsList.clear();
        groupsList.clear();
    }

    private void clear_upload() {
        ll_hide_document_details.setVisibility(View.GONE);
        tv_selected_file.setHint(R.string.select_documents);
//        tv_selected_file.setText("");
        selected_groups_list.clear();
        selected_documents_list.clear();
        langList.clear();
        tags_list.clear();
        docsList.clear();
        groupsList.clear();

        tv_select_groups.setHint(R.string.select_groups);
        tv_select_groups_view.setHint(R.string.select_groups);
    }

    private void callUploadDocumentWebservice() {
        try {
            if (Objects.equals(UPLOAD_TAG, "Client") && clientsList.isEmpty()) {
                AndroidUtils.showToast("Please Select a Client to Upload Documents", getContext());
            } else if (Objects.equals(UPLOAD_TAG, "Firm") && selected_groups_list.isEmpty()) {
                AndroidUtils.showToast("Please Select One Group to Upload Documents", getContext());
            } else {
                if (docsList.isEmpty()) {
                    AndroidUtils.showToast("Please Select Atleast one document", getContext());
                } else {
                    progress_dialog = AndroidUtils.get_progress(getActivity());
                    if (Objects.equals(UPLOAD_TAG, "Client")) {
                        for (int i = 0; i < docsList.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            JSONArray clients = new JSONArray();
                            JSONObject clients_jobject = new JSONObject();
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
                            //When the matter is chosen by the user.....
                            if (!matter_id.isEmpty()) {
                                JSONArray matter = new JSONArray();
                                matter.put(matter_id);
                                jsonObject.put("matters", matter);
                            }
                            jsonObject.put("name", docsList.get(i).getName());
                            jsonObject.put("description", docsList.get(i).getDescription());
                            jsonObject.put("expiration_date", docsList.get(i).getExpiration_date());
                            jsonObject.put("filename", docname);
                            jsonObject.put("category", "client");
                            jsonObject.put("clients", clients);
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
                    } else {
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
                            for (int k = 0; k < selected_groups_list.size(); k++) {
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
            }

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.fillInStackTrace();
        }
    }

//    private void view_documents() {
//        try {
//            if (view_docs_list.size() == 0) {
//                AndroidUtils.showToast("No Documents", getContext());
//            } else {
//                progress_dialog = AndroidUtils.get_progress(getActivity());
//                if (CATEGORY_TAG == "Client") {
//                    for (int i = 0; i < view_docs_list.size(); i++) {
//
//                        JSONObject jsonObject = new JSONObject();
//                        JSONArray clients = new JSONArray();
//                        JSONObject clients_jobject = new JSONObject();
//                        JSONArray matter = new JSONArray();
////                JSONArray tags = new JSONArray();
//                        String docname = "";
//                        DocumentsModel documentsModel = docsList.get(i);
//                        filename = documentsModel.getName();
//                        File new_file = documentsModel.getFile();
//                        String doc_type = "pdf";
//                        String content_string = new_file.getName().replace(".", "/");
//                        String[] content_type = content_string.split("/");
//                        if (content_type.length >= 2) {
//                            doc_type = content_type[1];
//                            docname = content_type[0];
//                        }
//                        for (int j = 1; j < clientsList.size(); j++) {
//                            if (clientsList.get(j).getId().matches(client_id)) {
//                                ClientsModel clientsModel = clientsList.get(j);
//                                clients_jobject.put("id", clientsModel.getId());
//                                clients.put(clients_jobject);
//                            }
//                        }
//                        matter.put(matter_id);
//                        jsonObject.put("category", clients);
//                        jsonObject.put("clients", client_id);
//                        jsonObject.put("matters", matter_id);
//                        jsonObject.put("groups", selected_groups_list);
//                        jsonObject.put("showpdfDocs", false);
//
//                        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display clientDocuments", jsonObject.toString());
////            AndroidUtils.showAlert(jsonObject.toString(),getContext());
//                    }
//                } else {
//                    if (CATEGORY_TAG == "Firm")
//                        for (int i = 0; i < view_docs_list.size(); i++) {
//                            currentpoistion++;
//                            JSONObject jsonObject = new JSONObject();
//                            JSONArray clients = new JSONArray();
//                            JSONObject clients_jobject = new JSONObject();
//
////                JSONArray tags = new JSONArray();
//                            String docname = "";
//                            DocumentsModel documentsModel = docsList.get(i);
//                            filename = documentsModel.getName();
//                            JSONArray groups = new JSONArray();
//                            for (int k = 0; k < selected_groups_list.size(); k++) {
//                                DocumentsModel documentsModel1 = selected_groups_list.get(k);
//                                groups.put(documentsModel1.getGroup_id());
//
//                            }
//                            File new_file = documentsModel.getFile();
//                            String doc_type = "pdf";
//                            String content_string = new_file.getName().replace(".", "/");
//                            String[] content_type = content_string.split("/");
//                            if (content_type.length >= 2) {
//                                doc_type = content_type[1];
//                                docname = content_type[0];
//                            }
//
//
//                            jsonObject.put("category", "firm");
//                            jsonObject.put("clients", clients);
//                            jsonObject.put("matters", matterlist);
//                            jsonObject.put("groups", selected_groups_list);
//                            jsonObject.put("showpdfDocs", false);
//
//                            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display clientDocuments", jsonObject.toString());
//
////            AndroidUtils.showAlert(jsonObject.toString(),getContext());
//                        }
//                }
//
//            }
//        } catch (Exception e) {
//            if (progress_dialog != null && progress_dialog.isShowing()) {
//                AndroidUtils.dismiss_dialog(progress_dialog);
//            }
//            e.fillInStackTrace();
//        }
//    }

    private void callClientWebservice() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/client/all/list", "Clients List", jsonObject.toString());
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
            //Displaying the files count.....
            Hide_Add_EditMeta();
            count_file++;
            tv_selected_file.setText(count_file + " files");

            load_documents(docsList, file_name, file);
        } else {
            file = getFile(getContext(), ImageURI);
            Log.i("FILE", "Info:" + file.toString());
            String file_name = file.getName();

            //Displaying the files count.....
            Hide_Add_EditMeta();
            count_file++;
            tv_selected_file.setText(count_file + " files");
//            DocumentsModel documentsModel = new DocumentsModel();
//            documentsModel.setName(file.getName());
//            docsList.add(documentsModel);
            load_documents(docsList, file_name, file);
//            docsList.add()
        }
    }

    //Removing the file and decrement the files count...
    public void remove_file(boolean ischecked) {
        if (ischecked) {
            count_file--;
            tv_selected_file.setText(count_file + " files");
        } else {
            tv_selected_file.setText(filename);
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
        //passing the context to list adatper class..
        adapter = new DocumentsListAdapter(docsList, tag, subtag, this, Documents.this);
        rv_documents.setAdapter(adapter);
        rv_documents.setHasFixedSize(true);

        //checking the check box whether all the list items are checked....
        chk_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chk_select_all.setChecked(isselect_all_checked);
                isselect_all_checked = !isselect_all_checked;
                adapter.selectOrDeselectAll(chk_select_all.isChecked());
            }
        });
    }

    private void hideDisableDownloadBackground() {
        DOWNLOAD_TAG = false;
        tv_enable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_enable_download.setTextColor(getContext().getColor(R.color.black));
        tv_disable_download.setTextColor(getContext().getColor(R.color.white));
        tv_disable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));

    }

//    private void Hide_AddTag() {
//        chk_select_all.setEnabled(false);
//        btn_upload.setVisibility(View.VISIBLE);
//        btn_add_tags.setVisibility(View.GONE);
//        tv_add_tag.setTextColor(getContext().getResources().getColor(R.color.black));
//        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
////        String tag = "Hide_Add_tag";
////        loadRecyclerview(tag, subtag);
//    }

    private void AddTag() {
        chk_box_layout.setAlpha(1F);
        chk_select_all.setEnabled(true);
        btn_upload.setVisibility(View.GONE);
        btn_add_tags.setVisibility(View.VISIBLE);
        tv_edit_meta.setTextColor(getContext().getResources().getColor(R.color.black));
        tv_add_tag.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_edit_meta.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        String tag = "add_tag";
        loadRecyclerview(tag, subtag);
    }

    //Hide when
    private void Hide_Add_EditMeta() {
        chk_box_layout.setAlpha(0.5F);
        is_clicked_edit = true;
        is_clicked_add = true;
        chk_select_all.setEnabled(false);
        chk_select_all.setChecked(false);
        btn_add_tags.setVisibility(View.GONE);
        btn_upload.setVisibility(View.VISIBLE);
        tv_edit_meta.setTextColor(getContext().getResources().getColor(R.color.black));
        tv_add_tag.setTextColor(getContext().getResources().getColor(R.color.black));
        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_edit_meta.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        String tag = "Hide_Add_Edit_tag";
        loadRecyclerview(tag, subtag);
    }

    private void EditMeta() {
        chk_box_layout.setAlpha(0.5F);
        chk_select_all.setEnabled(false);
        chk_select_all.setChecked(false);
        btn_upload.setVisibility(View.VISIBLE);
        btn_add_tags.setVisibility(View.GONE);
        tv_edit_meta.setTextColor(getContext().getResources().getColor(R.color.white));
        tv_add_tag.setTextColor(getContext().getResources().getColor(R.color.black));
        tv_add_tag.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_edit_meta.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_background));
        String tag = "edit_meta";
        loadRecyclerview(tag, subtag);
    }

    private void hideEnableDownloadBackground() {
        DOWNLOAD_TAG = true;
        tv_disable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_enable_download.setTextColor(getContext().getColor(R.color.white));
        tv_disable_download.setTextColor(getContext().getColor(R.color.black));
        tv_enable_download.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));

    }

    public static File getFile(Context context, Uri uri) throws IOException {
        File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.fillInStackTrace();
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
            ex.fillInStackTrace();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                        e.fillInStackTrace();
                    }
                } else if (httpResult.getRequestType().equals("Legal Matter")) {
                    JSONArray matters = result.getJSONArray("matterList");
                    loadMatters(matters);
                } else if (httpResult.getRequestType().equals("Upload Document")) {
                    boolean isError = result.getBoolean("error");
                    String msg = result.getString("msg");
                    AndroidUtils.showAlert(msg, getContext());
                    if (!isError) {
                        rv_documents.removeAllViews();
                        view_document();
                        clearListData();
                    }
//                    else {
//                        view_document();
//                        clearListData();
//                    }
                } else if (httpResult.getRequestType().equals("Groups")) {
                    JSONArray data = result.getJSONArray("data");
                    loadGroupsData(data);
                } else if (httpResult.getRequestType().equals("Display clientDocuments")) {
                    JSONArray data = result.getJSONArray("data");
                    //The Matter list must be call only we choose the client in view documents page.
                    if (ismatter_chosen) {
                        callLegalMatter();
                    }
                    load_view_doc(data);
                    Log.d("TAG_VIEW_CLIENT", data.toString());
                } else if (httpResult.getRequestType().equals("Display firmDocuments")) {
                    JSONArray data = result.getJSONArray("data");
                    load_view_doc(data);
                    Log.d("TAG_VIEW_CLIENT", data.toString());
                } else if (httpResult.getRequestType().equals("VIEW_DOCUMENT")) {
                    JSONArray docs = result.getJSONArray("docs");
                    laodViewDocuments(docs);
                    Log.d("TAG_view", docs.toString());
                } else if (httpResult.getRequestType().equals("Update Documents")) {
                    String msg = result.getString("msg");
                    AndroidUtils.showToast(msg, getContext());
                    view_docs_list.clear();
                    rv_display_view_docs.removeAllViews();
                    callViewDocumentWebservice();
                } else if (httpResult.getRequestType().equals("Delete Documents")) {
                    String msg = result.getString("msg");
                    AndroidUtils.showToast(msg, getContext());
                    view_docs_list.clear();
                    rv_display_view_docs.removeAllViews();
                    callViewDocumentWebservice();
                } else if (httpResult.getRequestType().equals("Display Documents")) {
                    JSONObject jsonObject = result.getJSONObject("data");
                    String url = jsonObject.getString("url");
                    loadDisplayDocuments(url);
                    Log.d("TAG_Image", url);

                }
            } catch (JSONException e) {
                e.fillInStackTrace();
                AndroidUtils.showAlert(e.getMessage(), getContext());
            }
        } else {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                AndroidUtils.showAlert(result.getString("msg"), getContext());
            } catch (JSONException e) {
                e.fillInStackTrace();
            }
        }
    }

    private void loadDisplayDocuments(String url) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.view_documents, null);
        ImageView iv_image = view.findViewById(R.id.doc_image);
        WebView pdfView = view.findViewById(R.id.webview); // Assuming the WebView has the id "pdfview"
        ImageView iv_close_edit_docs = view.findViewById(R.id.close_edit_docs);

        List<String> list = new ArrayList<>(Arrays.asList(CONTENT_TYPE.split("/")));

        if (list.get(1).equalsIgnoreCase("pdf")) {
            loadWeb(pdfView, iv_image, url);
        } else {
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

    private void loadWeb(WebView webView, ImageView imageView, String pdfUrl) {
        imageView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        // Enable JavaScript for proper PDF rendering
        webView.getSettings().setJavaScriptEnabled(true);

        // Load the PDF using Google Docs Viewer for better compatibility
        webView.loadUrl("https://coffer-east1.s3.amazonaws.com/prof-ACB10A6B53F9D27D/64d1cb6fa1db72042d7521f8?response-content-type=application%2Fpdf&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIZR2NZO3XRT7O54Q%2F20231116%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20231116T061851Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=2f83156b91cd10c789c19519bd84bd6b428dca979fe7440123ffb797f30fb37b\"\n" +
                pdfUrl);
    }

    private void load_view_doc(JSONArray docs) throws JSONException {
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
//                viewDocumentsModel.setOrigin(jsonObject.getString("origin"));
                viewDocumentsModel.setUploaded_by(jsonObject.getString("uploaded_by"));
                view_docs_list.add(viewDocumentsModel);
                Log.d("VIEW_POSITION", view_docs_list.get(i).toString());
            }
            loadViewDocumentsRecyclerview();
        } catch (JSONException e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
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
                Log.d("VIEW_POSITION", view_docs_list.get(i).toString());
            }
            loadViewDocumentsRecyclerview();
        } catch (JSONException e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadViewDocumentsRecyclerview() {
        try {
            if (view_docs_list.size() == 0) {
                rv_display_view_docs.removeAllViews();

                AndroidUtils.showToast("No documents to display", getContext());
            } else {
                rv_display_view_docs.setLayoutManager(new GridLayoutManager(getContext(), 1));
                View_documents_adapter adapter = new View_documents_adapter(view_docs_list, this, getContext());
                rv_display_view_docs.setAdapter(adapter);
                rv_display_view_docs.setHasFixedSize(true);
                tv_search_client_view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        adapter.getFilter().filter(tv_search_client_view.getText().toString());
                    }
                });
                tv_search_client_views.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        adapter.getFilter().filter(tv_search_client_views.getText().toString());
                    }
                });
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    private void loadGroupsData(JSONArray data) {
        groupsList.clear();
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
            e.fillInStackTrace();
            AndroidUtils.showAlert(e.getMessage(), getContext());
        }
    }

    @SuppressLint("MissingInflatedId")
    private void GroupsPopup() {
        upload_group_layout.setVisibility(View.VISIBLE);
        try {
            for (int i = 0; i < groupsList.size(); i++) {
                for (int j = 0; j < selected_groups_list.size(); j++) {
                    if (groupsList.get(i).getGroup_id().matches(selected_groups_list.get(j).getGroup_id())) {
                        DocumentsModel documentsModel = groupsList.get(i);
                        documentsModel.setChecked(true);
                    }
                }
            }

            // Upload Documents Group Selection
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rv_display_upload_groups_docs.setLayoutManager(layoutManager);
            rv_display_upload_groups_docs.setHasFixedSize(true);

            GroupsListAdapter documentsAdapter = new GroupsListAdapter(groupsList, Documents.class.newInstance(), new GroupsListAdapter.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(DocumentsModel documentsModel) {
                    if (documentsModel.isGroupChecked()) {
                        selected_groups_list.add(documentsModel);
                    } else {
                        // Remove the unchecked item from selected_groups_list
                        for (int i = 0; i < selected_groups_list.size(); i++) {
                            if (selected_groups_list.get(i).getGroup_id().equals(documentsModel.getGroup_id())) {
                                selected_groups_list.remove(i);

                                break; // Exit loop after removing the item
                            }
                        }
                    }

                    // Update TextView with selected groups
                    String[] value = new String[selected_groups_list.size()];
                    for (int i = 0; i < selected_groups_list.size(); i++) {
                        value[i] = selected_groups_list.get(i).getGroup_name();
                    }
                    String str = TextUtils.join(",", value);
                    tv_select_groups.setText(str);
                }
            });

            rv_display_upload_groups_docs.setAdapter(documentsAdapter);

            // View Documents Group Selection
            try {
                RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                rv_display_view_groups_docs.setLayoutManager(layoutManager1);
                rv_display_view_groups_docs.setHasFixedSize(true);

                GroupsListAdapter documentsAdapter1 = new GroupsListAdapter(groupsList, Documents.class.newInstance(), new GroupsListAdapter.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(DocumentsModel documentsModel) {
                        // Update selected groups list
                        if (documentsModel.isGroupChecked()) {
                            selected_groups_list.add(documentsModel);
                            cv_view_documents.setVisibility(View.VISIBLE);
                            rv_display_view_docs.setVisibility(View.VISIBLE);
                        } else {
                            // Remove the unchecked item from selected_groups_list
                            for (int i = 0; i < selected_groups_list.size(); i++) {
                                if (selected_groups_list.get(i).getGroup_id().equals(documentsModel.getGroup_id())) {
                                    selected_groups_list.remove(i);

                                    break; // Exit loop after removing the item
                                }
                            }
                        }


                        // Update TextView with selected groups
                        String[] value = new String[selected_groups_list.size()];
                        String[] value_id = new String[selected_groups_list.size()];
                        for (int i = 0; i < selected_groups_list.size(); i++) {
                            value[i] = selected_groups_list.get(i).getGroup_name();
                            value_id[i] = selected_groups_list.get(i).getGroup_id();
                        }

                        try {
                            array_group = new JSONArray(value_id);
                            callfilter_client_webservices();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        String str = TextUtils.join(",", value);
                        tv_select_groups_view.setText(str);
                        if (tv_select_groups_view.getText().toString().isEmpty()) {

                            rv_display_view_docs.setVisibility(View.GONE);
                        }
                        view_group_layout.setVisibility(View.GONE);
                        ischecked_group_view = true;
                    }
                });
                rv_display_view_groups_docs.setAdapter(documentsAdapter1);
            } catch (Exception e) {
                e.fillInStackTrace();
                AndroidUtils.showAlert(e.getMessage(), getContext());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (java.lang.InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadMatters(JSONArray matters) throws JSONException {
        //Adding a list first value as empty...
//        matterlist.add(0, new MattersModel());
        for (int i = 0; i < matters.length(); i++) {
            JSONObject jsonObject = matters.getJSONObject(i);
            MattersModel mattersModel = new MattersModel();
            mattersModel.setId(jsonObject.getString("id"));
            mattersModel.setTitle(jsonObject.getString("title"));
            mattersModel.setType(jsonObject.getString("type"));
            matterlist.add(mattersModel);
        }
        if (matterlist.size() == 0) {
            ll_matter_view.setVisibility(View.GONE);
            ll_matter.setVisibility(View.GONE);
        } else {
            ll_matter_view.setVisibility(View.VISIBLE);
            ll_matter.setVisibility(View.VISIBLE);
        }
        initMatter();
    }

    private void initMatter() {
        final CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), this.matterlist);
        Log.i("ArrayList", "Info:" + matterlist);
//        sp_matter.setAdapter(adapter);
        list_matter.setAdapter(adapter);
        custom_spinner2.setText("");
        custom_spinner4.setText("");
        list_matter_view.setAdapter(adapter);

        list_matter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                matter_name = Documents.this.clientsList.get(position).getName();
                matter_id = matterlist.get(position).getId();
                String matter_name = matterlist.get(position).getTitle();
                Log.d("Matter_value_name", matter_name);
                custom_spinner2.setText(matter_name);
                list_matter.setVisibility(View.GONE);
                ischecked_matter = true;
            }
        });

        list_matter_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                matter_name = Documents.this.clientsList.get(position).getName();
                matter_id = matterlist.get(position).getId();
                String matter_name = matterlist.get(position).getTitle();
                Log.d("Matter_value_name", matter_name);
                custom_spinner4.setText(matter_name);
                list_matter_view.setVisibility(View.GONE);
                ischecked_matter2 = true;
                //The matter list should not call.
                ismatter_chosen = false;
                callfilter_client_webservices();
                cv_view_documents.setVisibility(View.VISIBLE);
                rv_display_view_docs.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initUI(ArrayList<ClientsModel> clientsList) {
        CommonSpinnerAdapter adapter = new CommonSpinnerAdapter(getActivity(), this.clientsList);
        list_client.setAdapter(adapter);
        list_client_view.setAdapter(adapter);
//        tv_search_client.setAdapter(adapter);

        list_client.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                matter_name = Documents.this.clientsList.get(position).getName();
                client_id = clientsList.get(position).getId();
                String client_name = clientsList.get(position).getName();
                Log.d("Client_value_name", client_name);
                custom_spinner.setText(client_name);
                ll_matter.setVisibility(View.VISIBLE);
                matter_id = "";
                matterlist.clear();
                callLegalMatter();
                Log.d("Matter_list_number", "" + matterlist.size());
                list_client.setVisibility(View.GONE);
                ischecked = true;
            }
        });
        list_client_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                matter_name = Documents.this.clientsList.get(position).getName();
                selected_groups_list.clear();
                client_id = clientsList.get(position).getId();
                String client_name = clientsList.get(position).getName();
                Log.d("Client_value_name", client_name);
                custom_spinner3.setText(client_name);
                ll_matter_view.setVisibility(View.VISIBLE);
                CATEGORY_TAG = "client";
                custom_spinner4.setText("");
                matterlist.clear();
                matter_id = "";
                ismatter_chosen = true;
                callfilter_client_webservices();
                cv_view_documents.setVisibility(View.VISIBLE);
                rv_display_view_docs.setVisibility(View.VISIBLE);
                list_client_view.setVisibility(View.GONE);
                ischecked2 = true;
            }
        });
    }


    private void loadClients(JSONObject data) throws JSONException {
        JSONArray relationships = data.getJSONArray("relationships");
        //Adding a list first value as empty...
//        clientsList.add(0, new ClientsModel());
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
        // intUI(clientsList);
    }

    private void callLegalMatter() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            Log.d("Client_id", client_id);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/matter/all/" + client_id, "Legal Matter", jsonObject.toString());

        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.fillInStackTrace();
        }
    }


    @Override
    public void ViewTags(DocumentsModel documentsModel, ArrayList<DocumentsModel> itemsArrayList) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
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

                String tag_msg = key + " - " + documentsModel.getTags().get(key);
                tv_tag_name.setText(tag_msg);
                ll_existing_tags.addView(view_added_tags);
//                Object value = documentsModel.getTags().get(key);
            } catch (JSONException e) {
                e.fillInStackTrace();
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
        TextView tv_document_name = view_edit_documents.findViewById(R.id.tv_document_name);
        tv_document_name.setText(R.string.document_name);


        TextInputEditText tv_description = view_edit_documents.findViewById(R.id.edit_description);
        TextView description = view_edit_documents.findViewById(R.id.description);
        description.setText(R.string.description);

        AppCompatButton tv_exp_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
        TextView expiration_date_id = view_edit_documents.findViewById(R.id.expiration_date_id);
        expiration_date_id.setText(R.string.expiration_date);


        tv_doc_name.setText(documentsModel.getName());
        tv_description.setText(documentsModel.getDescription());
        //Expiration date field.....
        tv_exp_date.setText(documentsModel.getExpiration_date());
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
                try {
                    String myFormat = "dd-MM-yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    tv_exp_date.setText(sdf.format(myCalendar.getTime()));
//                    SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.US);
//
//                    // Set the timezone to India Standard Time
//                    sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//
//                    // Format the current date
//                    exp_date = sdf1.format(myCalendar.getTime());
//                    Log.d("orginal_date", sdf1.format(myCalendar.getTime()));

//                    String originalDateString = myCalendar.getTime().toString();
//
//                    SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
//                    SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
//                    Date originalDate = originalFormat.parse(originalDateString);

//                    desiredFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//                    assert originalDate != null;
//                    String newDateString = desiredFormat.format(originalDate);
//                    exp_date = newDateString;
////                postData.put("date", newDateString);
//                    tv_exp_date.setText(newDateString);
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        };
        tv_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
//        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String formattedDate = df.format(c);
//        tv_exp_date.setText("");

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
                        documentsModel1.setName(Objects.requireNonNull(tv_doc_name.getText()).toString());
                        documentsModel1.setDescription(Objects.requireNonNull(tv_description.getText()).toString());
                        documentsModel1.setExpiration_date(tv_exp_date.getText().toString());
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
        TextView tv_document_name = view_edit_documents.findViewById(R.id.tv_document_name);
        tv_document_name.setText(R.string.document_name);


        TextInputEditText tv_description = view_edit_documents.findViewById(R.id.edit_description);
        TextView description = view_edit_documents.findViewById(R.id.description);
        description.setText(R.string.description);
        AppCompatButton tv_exp_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
        TextView expiration_date_id = view_edit_documents.findViewById(R.id.expiration_date_id);
        expiration_date_id.setText(R.string.expiration_date);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        if (viewDocumentsModel.getExpiration_date().equalsIgnoreCase("NA") || viewDocumentsModel.getExpiration_date().equalsIgnoreCase("") || viewDocumentsModel.getExpiration_date().equalsIgnoreCase(null)) {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            tv_exp_date.setText(formattedDate);
        } else {
            String exp_date = viewDocumentsModel.getExpiration_date();
            Date date_new = AndroidUtils.stringToDateTimeDefault(exp_date, "MMM dd YYYY");
            String created = AndroidUtils.getDateToString(date_new, "dd-MM-yyyy");
            tv_exp_date.setText(created);
        }
        tv_doc_name.setText(viewDocumentsModel.getName());
        tv_description.setText(viewDocumentsModel.getDescription());
        tv_exp_date.setText(viewDocumentsModel.getExpiration_date());
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
                callUpdateDocumentWebservice(Objects.requireNonNull(tv_doc_name.getText()).toString(), tv_description.getText().toString(), tv_exp_date.getText().toString(), viewDocumentsModel.getId());
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
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("expiration_date", expiration_date);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/" + id, "Update Documents", jsonObject.toString());
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
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.delete_relationship, null);
            TextInputEditText tv_confirmation = view.findViewById(R.id.et_confirmation);
            String delete_msg = "Are you sure you want to delete " + viewDocumentsModel.getName() + " Document ?";
            tv_confirmation.setText(delete_msg);
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
        Log.d("IMage_name_content", CONTENT_TYPE);
        callDisplayDocumentWebservice(viewDocumentsModel.getId());
    }

    private void callDisplayDocumentWebservice(String id) {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.GET, "v3/document/" + id + "/view", "Display Documents", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    public void callfilter_client_webservices() {
        try {
            progress_dialog = AndroidUtils.get_progress(getActivity());
            JSONObject jsonObject = new JSONObject();
//            JSONArray groups1 = new JSONArray();
            if (Objects.equals(CATEGORY_TAG, "client")) {
                jsonObject.put("category", "client");
                jsonObject.put("clients", client_id);
                jsonObject.put("showPdfDocs", false);
                jsonObject.put("groups", null);
                //When the matter is chosen by the user.....
                if (!matter_id.isEmpty()) {
                    jsonObject.put("matters", matter_id);
                }
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display clientDocuments", jsonObject.toString());
                Log.d("Group_doc_view1", jsonObject.toString());
            } else if (Objects.equals(CATEGORY_TAG, "firm")) {
                jsonObject.put("category", "firm");
                jsonObject.put("clients", "");
                jsonObject.put("matters", "");
                jsonObject.put("showPdfDocs", false);
                jsonObject.put("groups", array_group);
                Log.d("Group_value_num", array_group.toString());

                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "Display firmDocuments", jsonObject.toString());
                Log.d("Group_doc_view1", jsonObject.toString());
            }
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
            jsonObject.put("docids", jsonArray);
            WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/document/delete", "Delete Documents", jsonObject.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
        }
    }

    private void callclientfirmWebServices() {
        try {
            if (selected_groups_list.isEmpty()) {
                AndroidUtils.showToast("Please select atleast one group", getContext());
            } else {
                if (Objects.equals(VIEW_TAG, "Firm"))

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
                        for (int k = 0; k < selected_groups_list.size(); k++) {
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

                        jsonObject.put("category", "firm");
                        jsonObject.put("matters", "");
                        jsonObject.put("groups", groups);
                        jsonObject.put("showPdfDocs", false);

                        if (doc_type.equalsIgnoreCase("apng") || doc_type.equalsIgnoreCase("avif") || doc_type.equalsIgnoreCase("gif") || doc_type.equalsIgnoreCase("jpeg") || doc_type.equalsIgnoreCase("png") || doc_type.equalsIgnoreCase("svg") || doc_type.equalsIgnoreCase("webp") || doc_type.equalsIgnoreCase("jpg")) {
                            jsonObject.put("content_type", "image/" + doc_type);
                        } else {
                            jsonObject.put("content_type", "application/" + doc_type);
                        }
//            AndroidUtils.showAlert(jsonObject.toString(),getContext());
//                        WebServiceHelper.callHttpViewWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "v3/document/filter", "View Document", new_file, jsonObject.toString());
                        rv_display_view_docs.setVisibility(View.VISIBLE);
                    }
            }
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progress_dialog);
            }
            e.fillInStackTrace();
        }
    }

    //checking the check box whether all the list items are checked....
    public void check_select_all(boolean check_status) {
        chk_select_all.setChecked(check_status);
    }

    @Override
    public void onCheckedChanged(DocumentsModel documentsModel) {

    }
}