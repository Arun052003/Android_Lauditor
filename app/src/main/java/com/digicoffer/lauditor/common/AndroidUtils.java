package com.digicoffer.lauditor.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.chatservice.ChatConnection;
import com.digicoffer.lauditor.chatservice.ChatConnectionService;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AndroidUtils {
    static Calendar f_Calendar = Calendar.getInstance();
    static int attempts = 1;

    public static void logMsg(String msg) {
    }

    public static void showValidationALert(String title, String message, Context context) {

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.show();

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }
    public static  void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                123);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    public static void showAlert(String message, Context context) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Alert");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.show();

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }

    public static void showToast(String message, Context context) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void storeSharedPreferenceString(String key, String value, Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static String getSharedPreferenceStringData(String key, Context activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString(key, null);
    }

    public static String isNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            realPath = cursor.getString(idx);
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }

    //Method to Check If SD Card is mounted or not
    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static AlertDialog get_progress(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.loading, null);
//        ProgressBar progress = (ProgressBar) dialogLayout.findViewById(R.id.progress);
//        Sprite doubleBounce = new DoubleBounce();
//        progress.setIndeterminateDrawable(doubleBounce);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(dialogLayout);
        dialog.show();
        return dialog;
    }


    public static void dismiss_dialog(AlertDialog dialog)
    {
        dialog.dismiss();
    }

    public static Date stringToDateTimeDefault(String dateTime, String format) {
        Date result = null;
        try {
            if (dateTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.parse(dateTime);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String getDateToString(Date date, String formats) {
        String result = "";
        try {
            if (date == null) {

                date = f_Calendar.getTime();
            }
            Format dateFormat = new SimpleDateFormat(formats);
            result = dateFormat.format(date);
        } catch (Exception e) {
            // Utils.handleServerException("Utils.java", "getDateToString",
            // e.getMessage(), e);
        }
        return result;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager objConnectivityManager;
        boolean isNetworkAvailable = false;

        try {
            objConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (objConnectivityManager.getActiveNetworkInfo() != null
//                    && objConnectivityManager.getActiveNetworkInfo().isAvailable()
//                    && objConnectivityManager.getActiveNetworkInfo().isConnected()) {
//                isNetworkAvailable = true;
//            }
        } catch (Exception e) {
            e.getMessage();
            AndroidUtils.logMsg("@ commonmethods isNetworkAvailable(): " + e.toString());
        } finally {
            objConnectivityManager = null;
        }
        return isNetworkAvailable;
    }
    public static String getDateSelectedFormt(String original_format, String selected_formate, String date) {
        Date converted_date = AndroidUtils.stringToDateTimeDefault(date, original_format);
        return AndroidUtils.getDateToString(converted_date, selected_formate);
    }

    public static String getDocTypeValue(String value) {
        String name = "";
        switch (value) {
            case "driver_license":
                name = "Driver Licence";
                break;
            case "passport":
                name = "Passport";
                break;
            case "aadhar":
                name = "AADHAR Card";
                break;
            case "pancard":
                name = "PAN Card";
                break;
            case "voterid":
                name = "Voter ID";
                break;
            case "nic":
                name = "National Identity Document (DNI)";
                break;
            case "ssc":
                name = "Social Security Card";
                break;
            case "cpf":
                name = "Cadastro de Pessoas Físicas (CPF)";
                break;
        }
        return name;
    }

    public static String get_affiliationType(String value) {
        String affiliation_type = "";
        switch (value) {
            case "citz":
                affiliation_type = "Citizen";
                break;
            case "dcitz":
                affiliation_type = "Dual Citizenship";
                break;
            case "pr":
                affiliation_type = "Permanent Resident";
                break;
            case "tvs":
                affiliation_type = "Temporary Resident - Student";
                break;
            case "tvw":
                affiliation_type = "Temporary Resident - Work";
                break;
        }
        return affiliation_type;
    }

    public static String getemailpattern()
    {
        return "[a-zA-Z0-9._-]+@[a-z-]+\\.+[a-z]+";
    }
    public  static  String getmobilepattern(){
        return  "[0-9]";
    }

    public static void remove_credential_preference(Context context)
    {
//        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = mySPrefs.edit();
//        editor.remove("emial");
//        editor.remove("password");
//        editor.remove("proBizType");
//        editor.apply();
    }

    public static void displayFile(String url, Context context) {
//        Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString() + "/Download/acrobat-pdf.pdf");
        String str_path = Environment.getExternalStorageDirectory().toString() + File.separator + url;
//        String str_path = FileUtils.download_path(getContext(), Uri.fromFile(new File(url)));
        Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(str_path));
        try {
            File pdfFile = new File(str_path);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(photoURI, "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent intent = Intent.createChooser(target, "Open File");
            context.startActivity(intent);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static void send_notification(final Context context, final String UID, final String msg, final String subject) {

        if (ChatConnectionService.getState().equals(ChatConnection.ConnectionState.CONNECTED)) {
            attempts = 0;
            //Send the message to the server
            Intent intent = new Intent(ChatConnectionService.SEND_MESSAGE);
            intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_BODY,
                    msg);
            intent.putExtra(ChatConnectionService.BUNDLE_TO, UID);
            intent.putExtra(ChatConnectionService.BUNDLE_MESSAGE_SUBJECT, subject);
            context.sendBroadcast(intent);
        } else {
//            AndroidUtils.showToast("Client not connected to server ,Message not sent!, reconnecting Please wait", context);
            attempts++;
            if (!ChatConnectionService.getState().equals(ChatConnection.ConnectionState.CONNECTED))
                reconnecXMPPServer(context);
            Handler handler = new Handler();
            final int finalAttempts = attempts;
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(finalAttempts < 5) {
                        send_notification(context, UID, msg, subject);
                    }
                    else
                        attempts = 0;
                }
            }, 5000);
        }
    }

    public static String getFileType(String filename)
    {
        String type = "";
        String name = filename.substring(filename.lastIndexOf(".")+1, filename.length());
        if(name.toLowerCase().equals("png") || name.toLowerCase().equals("jpg") || name.toLowerCase().equals("JPEG"))
        {
            type = "image/jpeg";
        }
        else if(name.toLowerCase().equals("pdf"))
        {
            type = "image/pdf";
        }
        else if(name.toLowerCase().equals("docx")){
            type = "image/docx";
        }
        return type;
    }

    public static void reconnecXMPPServer(final Context context)
    {
        Intent i1 = new Intent(context, ChatConnectionService.class);
        context.stopService(i1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                context.startService(new Intent(context, ChatConnectionService.class));
            }
        }, 5000);
    }



}
