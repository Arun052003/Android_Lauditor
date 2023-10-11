package com.digicoffer.lauditor.Matter;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Matter.Adapters.DocumentsAdapter;
import com.digicoffer.lauditor.Matter.Models.AdvocateModel;
import com.digicoffer.lauditor.Matter.Models.ClientsModel;
import com.digicoffer.lauditor.Matter.Models.DocumentsModel;
import com.digicoffer.lauditor.Matter.Models.GroupsModel;
import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.Matter.Models.TeamModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.BottomSheetUploadFile;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pgpainless.key.selection.key.util.And;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

public class MatterDocuments extends Fragment implements AsyncTaskCompleteListener,View.OnClickListener ,BottomSheetUploadFile.OnPhotoSelectedListner{

    private TextView tv_tag_document_name,matter_date,tv_document_library,tv_device_drive,at_add_documents,tv_selected_file;
    private LinearLayout ll_added_tags, ll_add_documents,ll_selected_documents,ll_select_doc,ll_uploaded_documents;
    private Button btn_browse,btn_add_documents;
    String matter_title, case_number, case_type, description, dof,start_date,end_date, court, judge, case_priority, case_status;
    private JSONArray existing_opponents;
    TextInputEditText tv_tag_type, tv_tag_name;
    ArrayList<AdvocateModel> advocates_list = new ArrayList<>();
    private ImageView imageView;
    ArrayList<TeamModel> selected_tm_list = new ArrayList<>();
    ArrayList<ClientsModel> selected_clients_list = new ArrayList<>();
    ArrayList<DocumentsModel> tags_list = new ArrayList<>();
    BottomSheetUploadFile bottommSheetUploadDocument;
    AppCompatButton btn_cancel_save,btn_create;
    boolean [] selectedDocument;
    private Bitmap mSelectedBitmap;
    ArrayList<MatterModel> matterArraylist ;
    private File mSelectedUri;
    String ADAPTER_TAG = "Documents";
    ArrayList<DocumentsModel> documentsList = new ArrayList<>();
    ArrayList<DocumentsModel> selected_documents_list = new ArrayList<>();
    ArrayList<DocumentsModel> upload_documents_list = new ArrayList<>();
    JSONArray exisiting_group_acls;
    JSONArray existing_documents;
    JSONArray existing_documents_list;
    JSONArray existing_clients;
    JSONArray existing_members;
    JSONArray existing_groups_list;
    JSONArray existing_clients_list;
    JSONArray existing_tm_list;
    ArrayList<GroupsModel> selected_groups_list = new ArrayList<>();
    private AlertDialog progress_dialog;
    String filename;
    File file;
    Matter matter;
    private ArrayList<GroupsModel> groupsList = new ArrayList<>();
    private ArrayList<ClientsModel> clientsList = new ArrayList<>();
    private ArrayList<TeamModel> tmList = new ArrayList<>();
    private String uploaded_document_name,upload_description;
    private AbstractCollection<DocumentsModel> MergedList;
    private int changedCollection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.documents_matter, container, false);
        Calendar myCalendar = Calendar.getInstance();
        matter_date = view.findViewById(R.id.matter_date);
        tv_document_library = view.findViewById(R.id.tv_document_library);
        tv_document_library.setOnClickListener(this);
        tv_device_drive = view.findViewById(R.id.tv_device_drive);
        tv_device_drive.setOnClickListener(this);
        at_add_documents = view.findViewById(R.id.at_add_documents);
        tv_selected_file = view.findViewById(R.id.tv_selected_file);
        ll_add_documents = view.findViewById(R.id.ll_add_documents);
        ll_selected_documents = view.findViewById(R.id.ll_selected_documents);
        ll_uploaded_documents = view.findViewById(R.id.ll_uploaded_documents);
        ll_select_doc = view.findViewById(R.id.ll_select_doc);
        btn_browse = view.findViewById(R.id.btn_browse);
        btn_browse.setOnClickListener(this);
        btn_add_documents = view.findViewById(R.id.btn_add_documents);
        btn_add_documents.setOnClickListener(this);
        btn_cancel_save = view.findViewById(R.id.btn_cancel_save);
        btn_create = view.findViewById(R.id.btn_submit);
        btn_create.setOnClickListener(this);
        matter = (Matter) getParentFragment();
        matterArraylist  = matter.getMatter_arraylist();
        if (matterArraylist.size()!=0) {
            for (int i = 0; i < matterArraylist.size(); i++) {
                MatterModel matterModel = matterArraylist.get(i);
                if (matterModel.getClients_list() != null && matterModel.getClients() != null && matterModel.getGroups_list() != null && matterModel.getGroup_acls() != null) {
                    exisiting_group_acls = matterModel.getGroup_acls();
                    existing_clients = matterModel.getClients();
//                existing_members = matterModel.getMembers();
                    existing_groups_list = matterModel.getGroups_list();
                    existing_clients_list = matterModel.getClients_list();

//                existing_tm_list = matterModel.getMembers_list();
                if (matterModel.getDocuments() != null) {
                    existing_documents = matterModel.getDocuments();
                }
                if (matterModel.getDocuments_list() != null) {
                    existing_documents_list = matterModel.getDocuments_list();
                }
                    try {
                        for (int g = 0; g < exisiting_group_acls.length(); g++) {
                            GroupsModel groupsModel = new GroupsModel();
                            JSONObject jsonObject = exisiting_group_acls.getJSONObject(g);
                            groupsModel.setGroup_id(jsonObject.getString("id"));
                            groupsModel.setGroup_name(jsonObject.getString("name"));
                            groupsModel.setChecked(jsonObject.getBoolean("isChecked"));
                            selected_groups_list.add(groupsModel);
                        }
                        if (existing_documents!=null){
                            for (int d=0;d<existing_documents.length();d++){
                                DocumentsModel documentsModel = new DocumentsModel();
                                JSONObject jsonObject =existing_documents.getJSONObject(d);
                                documentsModel.setDocid(jsonObject.getString("docid"));
                                documentsModel.setName(jsonObject.getString("name"));
                                documentsModel.setUser_id(jsonObject.getString("user_id"));
                                documentsModel.setDoctype(jsonObject.getString("doctype"));
                                selected_documents_list.add(documentsModel);
                            }
                        }
                        if(existing_documents_list!=null)
                        {
                            for (int ed=0;ed<existing_documents_list.length();ed++){
                                DocumentsModel documentsModel = new DocumentsModel();
                                JSONObject jsonObject =existing_documents_list.getJSONObject(ed);
                                documentsModel.setDocid(jsonObject.getString("docid"));
                                documentsModel.setName(jsonObject.getString("name"));
                                documentsModel.setUser_id(jsonObject.getString("user_id"));
                                documentsModel.setDoctype(jsonObject.getString("doctype"));
                                documentsList.add(documentsModel);
                            }

                            for (int k=0;k<existing_groups_list.length();k++){
                                GroupsModel groupsModel = new GroupsModel();
                                JSONObject jsonObject = existing_groups_list.getJSONObject(k);
                                groupsModel.setGroup_id(jsonObject.getString("id"));
                                groupsModel.setGroup_name(jsonObject.getString("name"));
                                groupsList.add(groupsModel);
                            }

                            for (int m=0;m<existing_clients.length();m++){
                                ClientsModel clientsModel = new ClientsModel();
                                JSONObject jsonObject = existing_clients.getJSONObject(m);
                                clientsModel.setClient_id(jsonObject.getString("id"));
                                clientsModel.setClient_name(jsonObject.getString("name"));
                                clientsModel.setClient_type(jsonObject.getString("type"));
                                selected_clients_list.add(clientsModel);
                            }
                            for (int c=0;c<existing_clients_list.length();c++){
                                ClientsModel clientsModel = new ClientsModel();
                                JSONObject jsonObject = existing_clients_list.getJSONObject(c);
                                clientsModel.setClient_id(jsonObject.getString("id"));
                                clientsModel.setClient_name(jsonObject.getString("name"));
                                clientsModel.setClient_type(jsonObject.getString("type"));
                                clientsList.add(clientsModel);
                            }
//
                        }
                        if(matterModel.getMembers()!=null) {
                            existing_members = matterModel.getMembers();
                            try{
                                for (int t = 0; t < existing_members.length(); t++) {
                                    TeamModel teamModel = new TeamModel();
                                    JSONObject jsonObject = existing_members.getJSONObject(t);
                                    teamModel.setTm_id(jsonObject.getString("id"));
                                    teamModel.setTm_name(jsonObject.getString("name"));
                                    teamModel.setUser_id(jsonObject.getString("user_id"));
                                    selected_tm_list.add(teamModel);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (matterModel.getMembers_list()!=null){
                            existing_tm_list = matterModel.getMembers_list();
                            try{

                                for (int d = 0; d < existing_tm_list.length(); d++) {
                                    TeamModel teamModel = new TeamModel();
                                    JSONObject jsonObject = existing_tm_list.getJSONObject(d);
                                    teamModel.setTm_id(jsonObject.getString("id"));
                                    teamModel.setTm_name(jsonObject.getString("name"));
                                    teamModel.setUser_id(jsonObject.getString("user_id"));
                                    tmList.add(teamModel);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (matterArraylist.get(i).getOpponent_advocate() != null) {
                            existing_opponents = matterArraylist.get(i).getOpponent_advocate();
                            try {
                                for (int j = 0; j < existing_opponents.length(); j++) {
                                    try {
                                        JSONObject jsonObject = existing_opponents.getJSONObject(j);
                                        AdvocateModel advocateModel = new AdvocateModel();
                                        advocateModel.setAdvocate_name(jsonObject.getString("name"));
                                        advocateModel.setNumber(jsonObject.getString("phone"));
                                        advocateModel.setEmail(jsonObject.getString("email"));
                                        advocates_list.add(advocateModel);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (matterModel.getMatter_title()!=null){
                            matter_title = matterModel.getMatter_title();
                        }
                        if(matterModel.getCase_number()!=null){
                            case_number = matterModel.getCase_number();
                        }
                        if(matterModel.getCase_type()!=null){
                            case_type = matterModel.getCase_type();
                        }
                        if(matterModel.getDescription()!=null){
                            description = matterModel.getDescription();
                        }
                        if(matterModel.getDate_of_filing()!=null){
                            dof =matterModel.getDate_of_filing();
                        }
                        if (matterModel.getStart_date()!=null){
                            start_date = matterModel.getStart_date();
                        }
                        if (matterModel.getEnd_date()!=null){
                            end_date = matterModel.getEnd_date();
                        }
                        if (matterModel.getCourt()!=null){
                            court = matterModel.getCourt();
                        }
                        if(matterModel.getJudge()!=null){
                            judge = matterModel.getJudge();
                        }
                        if(matterModel.getCase_priority()!=null){
                            case_priority = matterModel.getCase_priority();
                        }
                        if(matterModel.getStatus()!=null){
                            case_status = matterModel.getStatus();
                        }
                        if (selected_documents_list.size()!=0){
                            loadSelectedDocuments();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            }

        }
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
                matter_date.setText(sdf.format(myCalendar.getTime()));
            }
        };
        matter_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        return  view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_document_library:
                loadDocumentLibraryUI();
                break;
            case R.id.tv_device_drive:
                loadDeviceDriveUI();
                break;
            case R.id.btn_add_documents:
                if(documentsList.size()==0) {
                    callDocumentsWebService();
                }else
                {
                    DocumentsPopUp();
                }
                break;
            case R.id.btn_browse:
                checkPermissionREAD_EXTERNAL_STORAGE(getContext());
                break;
            case R.id.btn_submit:
                submitMatterInformation();
                break;


        }
    }

    private void submitMatterInformation() {

        if(selected_documents_list.size()==0&&upload_documents_list.size()==0){
            AndroidUtils.showToast("Please add atleast one document from existing documents or upload a new one",getContext());
        }else{
            try {
                if (upload_documents_list.size()!=0){
                    try{
//                            changedCollection = upload_documents_list.size();
                        for (int i = 0; i < upload_documents_list.size(); i++) {
//                            changedCollection--;
                            String name = upload_documents_list.get(i).getName();
                            JSONArray new_clients = new JSONArray();
                            JSONArray new_groups = new JSONArray();
                            JSONObject clients_jobject = new JSONObject();
                            JSONArray matter = new JSONArray();
//                JSONArray tags = new JSONArray();
                            String docname = "";
                            DocumentsModel documentsModel = upload_documents_list.get(i);
//                           MergedList.addAll(Collections.singleton(documentsModel));
                            filename = documentsModel.getName();
                            File new_file = documentsModel.getFile();
                            String doc_type = "pdf";
                            String content_string = new_file.getName().replace(".", "/");
                            String[] content_type = content_string.split("/");
                            if (content_type.length >= 2) {
                                doc_type = content_type[1];
                                docname = content_type[0];
                            }

                            for (int j = 0; j < selected_clients_list.size(); j++) {
//                                if (sele.get(j).getId().matches(client_id)) {
                                ClientsModel clientsModel = selected_clients_list.get(j);
                                clients_jobject.put("id", clientsModel.getClient_id());
                                clients_jobject.put("type", clientsModel.getClient_type());
                                new_clients.put(clients_jobject);
                            }
                            for(int g=0;g<selected_groups_list.size();g++){
                                GroupsModel groupsModel = selected_groups_list.get(g);
                                new_groups.put(groupsModel.getGroup_id());
                            }

                            JSONObject jsonObject = new JSONObject();
//                            matter.put(matter_id);
                            uploaded_document_name = name;
                            upload_description = upload_documents_list.get(i).getDescription();
                            jsonObject.put("name", name);
                            jsonObject.put("description", upload_documents_list.get(i).getDescription());
                            jsonObject.put("filename", docname);
                            jsonObject.put("category", "client");
                            jsonObject.put("clients", new_clients);
                            jsonObject.put("groups",new_groups);
                            jsonObject.put("downloadDisabled","false");
                            if (upload_documents_list.get(i).getTags_list() == null) {
                                jsonObject.put("tags", "");
                            } else {
                                jsonObject.put("tags", upload_documents_list.get(i).getTags_list());
                            }

                            if (doc_type.equalsIgnoreCase("apng") || doc_type.equalsIgnoreCase("avif") || doc_type.equalsIgnoreCase("gif") || doc_type.equalsIgnoreCase("jpeg") || doc_type.equalsIgnoreCase("png") || doc_type.equalsIgnoreCase("svg") || doc_type.equalsIgnoreCase("webp") || doc_type.equalsIgnoreCase("jpg")) {
                                jsonObject.put("content_type", "image/" + doc_type);
                            } else {
                                jsonObject.put("content_type", "application/" + doc_type);
                            }
                            progress_dialog = AndroidUtils.get_progress(getActivity());
                            upload_documents_list.remove(documentsModel);
                            WebServiceHelper.callHttpUploadWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "v3/document/upload", "Upload Document", new_file, jsonObject.toString());
                        }
//            AndroidUtils.showAlert(jsonObject.toString(),getContext());
                    } catch (JSONException e) {
                        if (progress_dialog != null && progress_dialog.isShowing())
                            AndroidUtils.dismiss_dialog(progress_dialog);
                        e.printStackTrace();
                    }
                }else{
                    submitMatter();
                }


//                matter.loadDocuments();

                }
             catch (Exception e) {
                 if (progress_dialog != null && progress_dialog.isShowing())
                     AndroidUtils.dismiss_dialog(progress_dialog);
                e.printStackTrace();
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
    private void callDocumentsWebService() {
        try{
        progress_dialog = AndroidUtils.get_progress(getActivity());
        JSONArray group_acls = new JSONArray();
        JSONObject postdata = new JSONObject();
        for (int i = 0; i < selected_groups_list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            GroupsModel groupsModel = selected_groups_list.get(i);
            group_acls.put(groupsModel.getGroup_id());
        }
        postdata.put("group_acls", group_acls);
        postdata.put("attachment_type", "documents");
        WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.PUT, "matter/attachments", "Documents",postdata.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

    private void loadDeviceDriveUI() {
//        selected_documents_list.clear();
//        documentsList.clear();
        ll_selected_documents.removeAllViews();
        loadUploadedDocuments();
//        at_add_documents.setText("");
//        tv_selected_file.setText("");
        tv_document_library.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_device_drive.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        ll_add_documents.setVisibility(View.GONE);
        ll_select_doc.setVisibility(View.VISIBLE);
//        ll_selected_documents.removeAllViews();
//        documentsList.clear();
        at_add_documents.setText("Select Document");
    }

    private void loadDocumentLibraryUI() {
//        selected_documents_list.clear();
//        documentsList.clear();
        ll_uploaded_documents.removeAllViews();
        loadSelectedDocuments();
//        at_add_documents.setText("");
//        tv_selected_file.setText("");
        tv_document_library.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_device_drive.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        ll_add_documents.setVisibility(View.VISIBLE);
        ll_select_doc.setVisibility(View.GONE);
    }
    private void BottomSheetUploadfile() {
        bottommSheetUploadDocument = new BottomSheetUploadFile();
        bottommSheetUploadDocument.show(getParentFragmentManager(), "");
        bottommSheetUploadDocument.setTargetFragment(MatterDocuments.this, 1);
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

            load_documents( file_name, file);
        } else {
            file = getFile(getContext(), ImageURI);
            Log.i("FILE", "Info:" + file.toString());
            String file_name = file.getName();
            tv_selected_file.setText(file_name);
//            DocumentsModel documentsModel = new DocumentsModel();
//            documentsModel.setName(file.getName());
//            docsList.add(documentsModel);
            load_documents( file_name, file);
//            docsList.add()
        }

    }
    private void load_documents( String file_name, File file) {
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
        upload_documents_list.add(documentsModel);
//        MergedList.addAll(Collections);
//        if (docsList.size() == 1) {
//            ll_hide_document_details.setVisibility(View.VISIBLE);
//            hideDisableDownloadBackground();
//        } else if (docsList.size() < 1) {
//            ll_hide_document_details.setVisibility(View.GONE);
////            hideDisableDownloadBackground();
//        }
        String tag = "view_tags";
        loadUploadedDocuments();
//        loadSelectedDocuments();
//        loadRecyclerview(tag, subtag);

//            ll_documents.addView(view);
//        }
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
            selected_documents_list.add(documentsModel);
//            loadSelectedDocuments();
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
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("Documents")) {
                    JSONArray data = result.getJSONArray("documents");
//                    AndroidUtils.showAlert(data.toString(),getContext());
                    loadDocumentsData(data);
                }else if (httpResult.getRequestType().equals("Upload Document")) {
                    boolean error = result.getBoolean("error");
                    if (error){
                        AndroidUtils.showAlert(result.getString("msg"),getContext());
                    }else{
                        String docid = result.getString("docid");
//                        AndroidUtils.showAlert(result.getString("msg"),getContext());
                        addDocsToMatter(docid);
                        submitMatterInformation();

//
                    }
                }else if(httpResult.getRequestType()=="Create Matter"){
                    boolean error = result.getBoolean("error");
                    String msg = result.getString("msg");
                    if (error){
                        AndroidUtils.showToast(msg,getContext());
                    }
                    else
                    {
                        AndroidUtils.showToast(msg,getContext());
                        matter.loadMatterInformation();
                        matterArraylist.clear();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitMatter() {
        try {
            if(upload_documents_list.size()==0) {
                String Matter_type = "legal";
                JSONObject postdata = new JSONObject();
                JSONArray clients = new JSONArray();
                JSONArray documents = new JSONArray();
                JSONArray group_acls = new JSONArray();
                JSONArray members = new JSONArray();
                JSONArray opponent_advocates = new JSONArray();

                for (int i = 0; i < selected_clients_list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    ClientsModel clientsModel = selected_clients_list.get(i);
                    jsonObject.put("id", clientsModel.getClient_id());
                    jsonObject.put("type", clientsModel.getClient_type());
                    clients.put(jsonObject);
                }

                for (int i = 0; i < selected_documents_list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    DocumentsModel documentsModel = selected_documents_list.get(i);
                    jsonObject.put("docid", documentsModel.getDocid());
                    jsonObject.put("doctype", documentsModel.getDoctype());
                    jsonObject.put("user_id", documentsModel.getUser_id());
                    documents.put(jsonObject);
                }

                for (int i = 0; i < selected_groups_list.size(); i++) {
                    GroupsModel groupsModel = selected_groups_list.get(i);
                    group_acls.put(groupsModel.getGroup_id());
                }

                for (int i = 0; i < selected_tm_list.size(); i++) {
                    TeamModel teamModel = selected_tm_list.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", teamModel.getTm_id());
                    members.put(jsonObject);
                }

                for (int i = 0; i < advocates_list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    AdvocateModel advocateModel = advocates_list.get(i);
                    jsonObject.put("name", advocateModel.getAdvocate_name());
                    jsonObject.put("email", advocateModel.getEmail());
                    jsonObject.put("phone", advocateModel.getNumber());
                    opponent_advocates.put(jsonObject);
                }
                postdata.put("title", matter_title);


                postdata.put("affidavit_filing_date", "");
                postdata.put("affidavit_isfiled", "");


                postdata.put("description", description);

                postdata.put("priority", case_priority);
                postdata.put("status", case_status);
                postdata.put("clients", clients);
                postdata.put("documents", documents);
                postdata.put("group_acls", group_acls);
                postdata.put("members", members);
                postdata.put("opponent_advocates", opponent_advocates);
                if (Constants.MATTER_TYPE=="Legal") {
                    postdata.put("judges", judge);
                    postdata.put("date_of_filling", dof);
                    postdata.put("court_name", court);
                    postdata.put("case_number", case_number);
                    postdata.put("case_type", case_type);
                    Matter_type = "legal";
                }else{
                    postdata.put("startdate",start_date);
                    postdata.put("closedate",end_date);
                    postdata.put("matter_number", case_number);
                    postdata.put("matter_type", case_type);
                    Matter_type = "general";
                }
                WebServiceHelper.callHttpWebService(this, getContext(), WebServiceHelper.RestMethodType.POST, "matter/"+Matter_type+"/create", "Create Matter", postdata.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addDocsToMatter(String docid) {
        try {
            DocumentsModel documentsModel = new DocumentsModel();
//            JSONObject jsonObject = new JSONObject();
            documentsModel.setDocid(docid);
            documentsModel.setName(uploaded_document_name);
            documentsModel.setUser_id(Constants.USER_ID);
            documentsModel.setDoctype("general");
            documentsModel.setDescription(upload_description);
            selected_documents_list.add(documentsModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDocumentsData(JSONArray data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                DocumentsModel documentsModel = new DocumentsModel();
                documentsModel.setDocid(jsonObject.getString("docid"));
                documentsModel.setDescription(jsonObject.getString("description"));
//                documentsModel.setDocid(jsonObject.getString("docid"));
                documentsModel.setDoctype(jsonObject.getString("doctype"));
                documentsModel.setName(jsonObject.getString("name"));
                documentsModel.setUser_id(jsonObject.getString("user_id"));
                documentsList.add(documentsModel);
            }
            DocumentsPopUp();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void DocumentsPopUp() {
        try{
            for (int i = 0; i < documentsList.size(); i++) {
                for (int j = 0; j < selected_documents_list.size(); j++) {
                    if (documentsList.get(i).getDocid().matches(selected_documents_list.get(j).getDocid())) {
                        DocumentsModel documentsModel = documentsList.get(i);
                        documentsModel.setChecked(true);
//                        selected_groups_list.set(j,documentsModel);

                    }
                }
            }
            selected_documents_list.clear();
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
//            ADAPTER_TAG = "Documents";
            DocumentsAdapter documentsAdapter = new DocumentsAdapter(documentsList);
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
                    for (int i = 0; i < documentsAdapter.getDocumentsList().size(); i++) {
                        DocumentsModel documentsModel = documentsAdapter.getDocumentsList().get(i);
                        if (documentsModel.isChecked()) {
                            selected_documents_list.add(documentsModel);
                            //                           jsonArray.put(selected_documents_list.get(i).getGroup_name());
                        }
                    }


                    loadSelectedDocuments();
//                    loadSelectedClients();
//                    loadSelectedGroups();
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
private void loadUploadedDocuments(){
    String[] value = new String[upload_documents_list.size()];
    for (int i = 0; i < upload_documents_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
        value[i] = upload_documents_list.get(i).getName();

    }

    String str = String.join(",", value);
//    at_add_documents.setText(str);
    tv_selected_file.setText(str);
//        selected_tm.setVisibility(View.VISIBLE);
    ll_uploaded_documents.removeAllViews();
    ll_selected_documents.removeAllViews();
    for(int i=0;i<upload_documents_list.size();i++){
        View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.document_tag, null);
        TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
        tv_opponent_name.setText(upload_documents_list.get(i).getName());
        ImageView iv_edit_tag = view_opponents.findViewById(R.id.iv_edit_tag);
        ImageView iv_edit_document = view_opponents.findViewById(R.id.iv_edit_document);
        ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
        iv_remove_opponent.setTag(i);
        iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = 0;
                    if (v.getTag() instanceof Integer) {
                        position = (Integer) v.getTag();
                        v = ll_uploaded_documents.getChildAt(position);
                        ll_uploaded_documents.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                        DocumentsModel documentsModel = upload_documents_list.get(position);
                        documentsModel.setChecked(false);
                        upload_documents_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                        String[] value = new String[upload_documents_list.size()];
                        for (int i = 0; i < upload_documents_list.size(); i++) {
                            value[i] = upload_documents_list.get(i).getName();
                        }

                        String str = String.join(",", value);
//                        at_add_documents.setText(str);
                        tv_selected_file.setText(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AndroidUtils.showAlert(e.getMessage(), getContext());
                }
            }
        });
        iv_edit_tag.setTag(i);
        iv_edit_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position  = 0;
                if (v.getTag() instanceof Integer){
                    position = (Integer) v.getTag();
                    v= ll_uploaded_documents.getChildAt(position);
                    DocumentsModel documentsModel = upload_documents_list.get(position);
                    try {
                        open_add_tags_popup(documentsModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    edit_tags();
                }
                  }
        });
        iv_edit_document.setTag(i);
        iv_edit_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0;
                if (v.getTag() instanceof Integer);
                position = (Integer) v.getTag();
                v= ll_uploaded_documents.getChildAt(position);
                DocumentsModel documentsModel1 = upload_documents_list.get(position);
                EditDocuments(documentsModel1.getName(),documentsModel1.getDescription(),documentsModel1.getFile(),position,v);

            }
        });
//        iv_edit_opponent.setVisibility(View.GONE);
        ll_uploaded_documents.addView(view_opponents);
    }
}
    private void loadSelectedDocuments() {
        String[] value = new String[selected_documents_list.size()];
        for (int i = 0; i < selected_documents_list.size(); i++) {
//                                value += "," + family_members.get(i);
//                               value.add(family_members.get(i));
            value[i] = selected_documents_list.get(i).getName();

        }

        String str = String.join(",", value);
        at_add_documents.setText(str);
//        tv_selected_file.setText(str);
//        selected_tm.setVisibility(View.VISIBLE);
        ll_uploaded_documents.removeAllViews();
        ll_selected_documents.removeAllViews();
        for(int i=0;i<selected_documents_list.size();i++){
            View view_opponents = LayoutInflater.from(getContext()).inflate(R.layout.edit_opponent_advocate, null);
            TextView tv_opponent_name = view_opponents.findViewById(R.id.tv_opponent_name);
            tv_opponent_name.setText(selected_documents_list.get(i).getName());
            ImageView iv_edit_opponent = view_opponents.findViewById(R.id.iv_edit_opponent);
            ImageView iv_remove_opponent = view_opponents.findViewById(R.id.iv_remove_opponent);
            iv_remove_opponent.setTag(i);
            iv_remove_opponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = 0;
                        if (v.getTag() instanceof Integer) {
                            position = (Integer) v.getTag();
                            v = ll_selected_documents.getChildAt(position);
                            ll_selected_documents.removeView(v);
//                            ll_selected_groups.addView(view_opponents,position);
                            DocumentsModel documentsModel = selected_documents_list.get(position);
                            documentsModel.setChecked(false);
                            selected_documents_list.remove(position);
//                            selected_groups_list.set(position, groupsModel);
                            String[] value = new String[selected_documents_list.size()];
                            for (int i = 0; i < selected_documents_list.size(); i++) {
                                value[i] = selected_documents_list.get(i).getName();
                            }

                            String str = String.join(",", value);
                            at_add_documents.setText(str);
                            tv_selected_file.setText(str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AndroidUtils.showAlert(e.getMessage(), getContext());
                    }
                }
            });
            iv_edit_opponent.setVisibility(View.GONE);
            ll_selected_documents.addView(view_opponents);
        }
    }
    private void open_add_tags_popup(DocumentsModel documentsModel) throws JSONException {
        tags_list.clear();
        if (documentsModel.getTags_list()!=null) {
//            int i=0;
//            for (int i=0;i<tags_list.size();i++) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(documentsModel.getTags_list());
                Iterator<String> iter = documentsModel.getTags_list().keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = documentsModel.getTags_list().getString(key);
                    DocumentsModel documentsModel1 = new DocumentsModel();
                    documentsModel1.setTag_type(key);
                    documentsModel1.setTag_name(value);
                    tags_list.add(documentsModel1);
                }
//            }
        }
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
        add_tags_listing();
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
                     if(tv_tag_type.getText().toString().equals("")){
                        tv_tag_type.setError("Please enter tag type");
                        tv_tag_type.requestFocus();
                    }else if(tv_tag_name.getText().toString().equals("")){
                        tv_tag_name.setError("Please enter tag name");
                        tv_tag_name.requestFocus();
                    }else{
                        add_tags_listing();
                    }

                }
            });
            btn_save_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tags_list.size() != 0) {

//                        subtag = "view_tags";

                    String tag = "add_tag";
//                        for(int i=0;i<docsList.size();i++){
                    for (int j = 0; j < upload_documents_list.size(); j++) {
                        if (documentsModel.getName().matches(upload_documents_list.get(j).getName())) {
                            DocumentsModel documentsModel = upload_documents_list.get(j);

                            JSONObject tags = new JSONObject();
                            for (int t = 0; t < tags_list.size(); t++) {


                                try {
                                    tags.put(tags_list.get(t).getTag_type(), tags_list.get(t).getTag_name());

//                                            selected_documents_list.add(documentsModel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            documentsModel.setTags_list(tags);
                        }
                    }
//                    for (int i = 0; i < documentsList.size(); i++) {
                        for (int j = 0; j < upload_documents_list.size(); j++) {
//                            if () {
                                DocumentsModel documentsModel = upload_documents_list.get(j);
                                documentsModel.setTags_list(upload_documents_list.get(j).getTags_list());
                                upload_documents_list.set(j, documentsModel);
//                            }
                        }
//                    }
//                    AndroidUtils.showAlert(selected_documents_list.toString(),getContext());
//                            }
//                        }
//                    adapter.getList_item().clear();
//                    chk_select_all.setChecked(false);
//                    for (int i=0;i<adapter.getList_item().size();i++){
////                        DocumentsModel documentsModel =
//                    }
//                    loadRecyclerview(tag, subtag);
                    dialog.dismiss();

                }
                }
            });
            dialog.setCancelable(false);
            dialog.setView(view);
            dialog.show();
//        } else {
//            AndroidUtils.showToast("Please select atleast one document to add tags", getContext());
//        }
    }

    private void add_tags_listing() {
        ll_added_tags.removeAllViews();
        if(!(tv_tag_name.getText().toString().equals(""))&&(!(tv_tag_type.getText().toString().equals("")))){
            DocumentsModel documentsModel = new DocumentsModel();
            documentsModel.setTag_type(tv_tag_type.getText().toString());
            documentsModel.setTag_name(tv_tag_name.getText().toString());
            tags_list.add(documentsModel);
        }

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

    private void EditDocuments(String name, String description,File file, int position, View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view_edit_documents = inflater.inflate(R.layout.edit_meta_data, null);
        ImageView iv_cancel_edit_doc = view_edit_documents.findViewById(R.id.close_edit_docs);
        AppCompatButton btn_close_edit_docs = view_edit_documents.findViewById(R.id.btn_cancel_edit_docs);
        TextInputEditText tv_doc_name = view_edit_documents.findViewById(R.id.edit_doc_name);
        TextInputEditText tv_description = view_edit_documents.findViewById(R.id.edit_description);
        AppCompatButton tv_exp_date = view_edit_documents.findViewById(R.id.tv_expiration_date);
        tv_exp_date.setVisibility(View.GONE);
        tv_doc_name.setText(name);
        tv_description.setText(description);

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
                try {
                    DocumentsModel documentsModel = new DocumentsModel();
                    documentsModel.setName(tv_doc_name.getText().toString());
                    documentsModel.setDescription(tv_description.getText().toString());
                    documentsModel.setFile(file);
                    upload_documents_list.set(position, documentsModel);
                   dialog.dismiss();
                    loadUploadedDocuments();
                } catch (Exception e) {
                    e.printStackTrace();
                    AndroidUtils.showAlert(e.getMessage(), getContext());
                }
            }
        });
        dialog.setCancelable(false);
        dialog.setView(view_edit_documents);
        dialog.show();

    }
}

