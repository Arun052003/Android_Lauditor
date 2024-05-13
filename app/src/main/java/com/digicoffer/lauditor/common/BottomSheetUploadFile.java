package com.digicoffer.lauditor.common;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.FileUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;

public class BottomSheetUploadFile extends BottomSheetDialogFragment implements View.OnClickListener {
    Button bt_camera,bt_library,bt_close,bt_document_lib;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 123;
    private static final int PICK_FILE_REQUEST_CODE = 1888;
    private static final int PICK_DOCFILE_REQUEST_CODE = 1999;
    private static final int PICKFILE_RESULT_CODE = 124;
    File file;

    OnPhotoSelectedListner onPhotoSelectedListner;
    public interface OnPhotoSelectedListner{
        void getImagepath(File imagepath, Uri uri) throws IOException;
        void getImageBitmap(Bitmap bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.upload_file_options,
                container, false);
        bt_camera = v.findViewById(R.id.bt_camera);
        bt_close = v.findViewById(R.id.bt_cancel);
        bt_library = v.findViewById(R.id.bt_photo_lib);
        bt_document_lib = v.findViewById(R.id.bt_document_lib);
        bt_camera.setOnClickListener(this);
        bt_library.setOnClickListener(this);
        bt_document_lib.setOnClickListener(this);
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_photo_lib:
//                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    i.setType("*/*");
                mGetContent.launch("*/*");
//                    startActivityForResult(i, PICK_FILE_REQUEST_CODE);
                break;

            case R.id.bt_camera:
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                break;
            case R.id.bt_document_lib:
//                Intent intent = new Intent();
//                intent.setType("*/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(Intent.createChooser(intent, "Select"), 200);

                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//                startActivity(chooseFile);
                startActivityForResult(chooseFile, 200);
//                Intent intent = new Intent();
//                intent.setType("*/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Choose File to Upload"), 200);
                break;
        }

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContext().getContentResolver().query(uri,
//                            filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContext().getContentResolver().query(uri,null,
//                             null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();


//                    String fileName = "";
//                    String scheme = uri.getScheme();
//                    if (scheme.equals("file")) {
//                        fileName = uri.getLastPathSegment();
//                    }
//                    else if (scheme.equals("content")) {
//                        String[] proj = { MediaStore.Images.Media.TITLE };
//                        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
//                        if (cursor != null && cursor.getCount() != 0) {
//                            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
//                            cursor.moveToFirst();
//                            fileName = cursor.getString(columnIndex);
//                        }
//                        if (cursor != null) {
//                            cursor.close();
//                        }
//                    }
//
//
                    if (uri != null) {
                        File file = new File(uri.getPath());
                        try {
                            onPhotoSelectedListner.getImagepath(file, uri);
                        } catch (IOException e) {
                            e.fillInStackTrace();
                        }
                        getDialog().dismiss();
                        // Handle the returned Uri
                    }
                }
            });


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
//            Uri  selectedimageURI = data.getData();
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(column_index);
            cursor.close();
            File file = new File(picturePath);
            try {
                onPhotoSelectedListner.getImagepath(file, selectedImage);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            getDialog().dismiss();
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            onPhotoSelectedListner.getImageBitmap(bitmap);
            getDialog().dismiss();
        } else if (requestCode == 200){
            getDialog().dismiss();
            if (data == null) {
                return;
            }

//            Uri uri = data.getData();
//            String paths = FileUtils.getPath(getContext(), uri);
//            file = new File(paths);
//            onPhotoSelectedListner.getImagepath(file);
//            Log.d("File Path : ", "" + paths);
//            if (paths != null) {
//                all_file_name.setText("" + new File(paths).getName());
//            }
//            all_file_path = paths;
            try {
                Uri imageuri = data.getData();
                if (imageuri != null) {
                    try {
                        String path = FileUtils.getPath(getContext(), data.getData());
                        file = new File(path);
                        onPhotoSelectedListner.getImagepath(file, imageuri);
                    } catch (Exception e) {
                        AndroidUtils.showToast("File Access error: Please check the file name without any spaces.", getContext());

                    }
                } else {
                    AndroidUtils.showToast("File Access error: Please check the file name without any spaces.", getContext());

                }
            } catch (Exception e) {
                AndroidUtils.showToast("File Access error:Please check your file ", getContext());
            }
            getDialog().dismiss();
//            Uri path = data.getData();
//                    try {
//                        InputStream inputStream = this.getContext().getContentResolver().openInputStream(path);
//                        byte[] pdfByte = new byte[inputStream.available()];
//                        inputStream.read(pdfByte);
//                        try (FileOutputStream fos = new FileOutputStream("pathname")) {
//                            fos.write(pdfByte);
//                        }
//                    }  catch (IOException e) {
//                        e.fillInStackTrace();
//                    }
        }else{
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
//            BottomSheetUploadFile f = (BottomSheetUploadFile) getChildFragmentManager().findFragmentById(R.id.id_framelayoutt);
//            MainActivity activity;
//        if ((context instanceof OnPhotoSelectedListner)) {
//            activity = (MainActivity) context;
            onPhotoSelectedListner = (OnPhotoSelectedListner) getTargetFragment();
//        }
        } catch (ClassCastException e) {
            Log.e(TAG,"onAttach: ClasscatchException" + e.getMessage());
            e.fillInStackTrace();
        }
        super.onAttach(context);
    }
}

