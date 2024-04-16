package com.digicoffer.lauditor.LoginActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.digicoffer.lauditor.MainActivity;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.chatservice.ChatConnection;
import com.digicoffer.lauditor.chatservice.ChatConnectionService;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.digicoffer.lauditor.common_adapters.CommonSpinnerAdapter;
import com.google.android.material.textfield.TextInputEditText;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    TextInputEditText tet_email, tet_password;
    String firm_name, firm_password;
    ListView sp_firm;
    JSONObject firm_postData = new JSONObject();
    String firm_list_name = "";
    boolean ischecked = true;
    boolean has_firm;
    ArrayList<Dashboard_Model> dashboardModels = new ArrayList<>();
    TextView spinner_firm_view, tv_sign_in;
    AppCompatButton bt_submit;
    boolean isAllFieldsChecked = false;
    AlertDialog progress_dialog;
    Dialog ad_dialog;
    TextInputEditText et_firm_password;
    boolean response_true;
    private static ChatConnection mConnection;
    private static ChatConnectionService chatConnectionService;
    CheckBox checkBox;
    TextView tv_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkBox = findViewById(R.id.checkBox);
        tet_email = findViewById(R.id.et_login_email);
        tet_password = findViewById(R.id.et_login_password);
        tv_sign_in = findViewById(R.id.tv_sign_in);
        tv_sign_in.setText("Sign In");
        tv_sign_in.setTextSize(20);
        tv_sign_in.setTextColor(getColor(R.color.black));

        bt_submit = findViewById(R.id.Submit);
        tv_forgot_password = findViewById(R.id.textView);

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgetPasswordActivity
                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
                try {
                    isAllFieldsChecked = Validate();
//                    if (isAllFieldsChecked) {
//                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        });

        check_Bio_metric();

//        tet_email.setText("soundarya.v@digicoffer.com");
//        tet_email.setText("rajendra.sai@digicoffer.com");
//        tet_email.setText("soundaryavembaiyan@yahoo.com");

//        tet_email.setText("ragifi5243@jalunaki.com"); -- Only Dev2
//        tet_email.setText("vengadeshwaran82@gmail.com");
//        -- Only Staging
//        tet_password.setText("Test@123");
//        Login();

        // Initialize the button state  // Initially disabled
        checkFieldsNotEmpty();
        // TextWatcher for email and password EditTexts
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkFieldsNotEmpty();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsNotEmpty();
            }
        };

        tet_email.addTextChangedListener(textWatcher);
        tet_password.addTextChangedListener(textWatcher);

        // Set click listeners for the buttons
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isAllFieldsChecked = Validate();
                    if (isAllFieldsChecked) {
                        Login();
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        });
    }

    //checking the the Bio-Metric Check Box is Clicked or not
    private void Bio_metric_access() {
        //when the Check Box is checked then the data is stored in Internal Storage for the Bio-Metric Authentication.
        if (checkBox.isChecked()) {
            String Token = Constants.TOKEN;
            String email = Objects.requireNonNull(tet_email.getText()).toString().trim();
            String password = Objects.requireNonNull(tet_password.getText()).toString().trim();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putString("Token", Token);
            editor.putString("firm_id", firm_name);
            editor.putString("firm_password", firm_password);
            editor.putString("firm_list_name", firm_list_name);
            editor.putBoolean("has_firm", has_firm);
            editor.putString("Json_key", String.valueOf(Constants.jsonObject_dashboard));
            editor.putBoolean("Check_box", true);
            editor.apply();
            Constants.is_biometric = true;
        } else {
            Constants.is_biometric = false;
        }
    }

    private void checkFieldsNotEmpty() {
        String email = Objects.requireNonNull(tet_email.getText()).toString().trim();
        String password = Objects.requireNonNull(tet_password.getText()).toString().trim();

        // Check if both email and password are not empty
        boolean bothFieldsNotEmpty = !email.isEmpty() && !password.isEmpty();

        if (bothFieldsNotEmpty && isValidPassword(password)) {
            // Enable the submit button and set its color to the enabled state
            bt_submit.setEnabled(true);
            bt_submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        } else {
            // Disable the submit button and set its color to the disabled state
            bt_submit.setEnabled(false);
            bt_submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dullBlueColor)));
        }
    }

    public void check_Bio_metric() {
        //When the Bio-Metric is Checked Successfully.
        String email, password, Token1;
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences_bio = getSharedPreferences("BIO", Context.MODE_PRIVATE);
        Constants.Biometric_checked = sharedPreferences_bio.getBoolean("IS_Bio", false);
        firm_name = sharedPreferences.getString("firm_id", "");
        firm_list_name = sharedPreferences.getString("firm_list_name", "");
        firm_password = sharedPreferences.getString("firm_password", "");
        has_firm = sharedPreferences.getBoolean("has_firm", false);
        email = sharedPreferences.getString("email", "");
        password = sharedPreferences.getString("password", "");
        Token1 = sharedPreferences.getString("Token", "");
        String respose_v = sharedPreferences.getString("Json_key", "");
//            try {
//                FileInputStream inputStream = openFileInput(filename);
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                //The data is retrieving from the Internal Storage of the app and store it in Array..
//                String[] user_value = new String[2];
//                user_value[0] = bufferedReader.readLine();
//                user_value[1] = bufferedReader.readLine();
//                bufferedReader.close();
//
        Constants.is_biometric = sharedPreferences.getBoolean("Check_box", false);
//                //If the Data is retrieved from the Storage and Assign to the Respective field.
        Log.d("Bio-metric", "  " + firm_name + "     " + firm_password + "   " + Token1);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        if (Constants.is_biometric) {
            if (Constants.Biometric_checked) {
                try {
                    JSONObject probiz_data = new JSONObject(respose_v);
                    Constants.NAME = probiz_data.getString("name");
                    Constants.USER_ID = probiz_data.getString("user_id");
                    Constants.UID = probiz_data.getString("uid");
                    Constants.OLD_PASSWORD = Objects.requireNonNull(tet_password.getText()).toString();
                    Constants.PK = probiz_data.getString("pk");
                    Constants.PASSWORD_MODE = probiz_data.getString("password_mode");
                    Constants.IS_ADMIN = probiz_data.getBoolean("admin");
                    Constants.FIRM_NAME = probiz_data.getString("firm_name");
                    Constants.ROLE = probiz_data.getString("role");
                    Log.d("Response_value..", respose_v);
                } catch (JSONException e) {
                    e.fillInStackTrace();
                }
                Constants.TOKEN = Token1;
                checkBox.setChecked(true);
                tet_email.setText(email);
                tet_password.setText(password);
                if (Constants.Valid_Token)
                    Dashboard();
                else {
                    Login();
                }
            } else {
                startActivity(new Intent(this, biometric_page.class));
            }
        } else {
            Constants.Biometric_checked = false;
            checkBox.setChecked(false);
            tet_email.setText("");
            tet_password.setText("");
        }
    }

    private boolean isValidPassword(String password) {

        return password.length() >= 6;
    }

    private void Dashboard() {
        Constants.check_url();
//        https://apidev2.digicoffer.com/professional/v3/dashboard/layout
        JSONObject jsonObject = new JSONObject();
        Constants.base_URL = Constants.PROF_URL;
        WebServiceHelper.callHttpWebService(LoginActivity.this, LoginActivity.this, WebServiceHelper.RestMethodType.GET, Constants.Dashboard, "Dashboard", jsonObject.toString());
    }

    private void Login() {
        try {
            Constants.check_url();
            Constants.PROBIZ_TYPE = "PROFESSIONAL";
            Constants.base_URL = Constants.PROF_URL;
            JSONObject postData = new JSONObject();

            progress_dialog = AndroidUtils.get_progress(LoginActivity.this);
            postData.put("email", Objects.requireNonNull(tet_email.getText()).toString());
            postData.put("password", Objects.requireNonNull(tet_password.getText()).toString());
            WebServiceHelper.callHttpWebService(LoginActivity.this, LoginActivity.this, WebServiceHelper.RestMethodType.POST, "login", "LOGIN", postData.toString());
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }


    private boolean Validate() {
        if (Objects.requireNonNull(tet_email.getText()).toString().trim().isEmpty()) {
            tet_email.setError("Email is required");
            return false;
        }
        if (Objects.requireNonNull(tet_password.getText()).toString().trim().isEmpty()) {
            tet_password.setError("Password is required");
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View view) {

    }

    private void firm_login(final ArrayList<FirmsDo> list) {
//        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen);
//        builder.setBackground(getDrawable(R.drawable.rectangular_complete_blue_background));
//        LayoutInflater inflater = getLayoutInflater();
        final Dialog dialogLayout = new Dialog(this);
        dialogLayout.setContentView(R.layout.firm_login);
        TextView textview_firm = dialogLayout.findViewById(R.id.textview_firm);
        textview_firm.setTextColor(getColor(R.color.orange_color));
        textview_firm.setTextSize(12);
        textview_firm.setTypeface(Typeface.DEFAULT_BOLD);
        textview_firm.setText(R.string.multi_firm_text);
        sp_firm = dialogLayout.findViewById(R.id.sp_firm);
        sp_firm.setBackground(getDrawable(R.drawable.rectangular_white_background));
        spinner_firm_view = dialogLayout.findViewById(R.id.spinner_firm_view);
        spinner_firm_view.setBackground(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        spinner_firm_view.setPadding(30, 3, 3, 0);
        spinner_firm_view.setText("");
        CommonSpinnerAdapter<FirmsDo> adapter = new CommonSpinnerAdapter<>(this, list);
        sp_firm.setAdapter(adapter);
        sp_firm.setVisibility(View.GONE);
        spinner_firm_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.display_listview(ischecked, sp_firm);
            }
        });
        sp_firm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                firm_name = list.get(position).getValue();
                firm_list_name = list.get(position).getName();
                Log.d("FIRM_NAME_ID", firm_name);
                spinner_firm_view.setText(firm_list_name);
                sp_firm.setVisibility(View.GONE);
                ischecked = true;
            }
        });

        et_firm_password = (TextInputEditText) dialogLayout.findViewById(R.id.et_login_password);
//        et_firm_password.setText("Test@123");
        Button bt_submit = (Button) dialogLayout.findViewById(R.id.Submit);

        //Update password---
        TextView forget_psd = (TextView) dialogLayout.findViewById(R.id.forgetpassword);

        Button bt_cancel = (Button) dialogLayout.findViewById(R.id.Cancel);
        JSONObject postData = new JSONObject();
        firm_postData = postData;
//
        if ((Constants.is_biometric) && (Constants.Biometric_checked) && (has_firm)) {
            et_firm_password.setText(firm_password);
            spinner_firm_view.setText(firm_list_name);
            submit_firm_login();
        }
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            }
        });
        bt_submit.setEnabled(false);
        bt_submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dullBlueColor)));

        et_firm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidPassword(String.valueOf(s)) && (!firm_list_name.isEmpty())) {
                    bt_submit.setEnabled(true);
                    bt_submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                } else {
                    bt_submit.setEnabled(false);
                    bt_submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dullBlueColor)));
                }
            }
        });
        //Update password---
        forget_psd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Forget Password Page
                Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
                startActivity(intent);
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_firm_login();
            }
        });

        //Display a Firm Login Page to Full Screen page.
        Window window = dialogLayout.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(getDrawable(com.applandeo.materialcalendarview.R.drawable.background_transparent));
        }
        dialogLayout.show();
    }

    private void submit_firm_login() {
        try {
            String password = "";
            password = Objects.requireNonNull(et_firm_password.getText()).toString().trim();
            firm_password = password;
            if (firm_list_name.isEmpty()) {
                AndroidUtils.showToast("Firm is mandatory", LoginActivity.this);
            }
            if (!password.isEmpty() && isValidPassword(password)) {
                callfirmloginWebservice(et_firm_password, tet_email);
            } else {
                AndroidUtils.showToast("Password is mandatory", LoginActivity.this);
            }
        } catch (Exception e) {
            if (progress_dialog != null && progress_dialog.isShowing())
                AndroidUtils.dismiss_dialog(progress_dialog);
        }
    }

    private void callfirmloginWebservice(TextInputEditText et_firm_password, TextInputEditText tet_email) throws JSONException {
        progress_dialog = AndroidUtils.get_progress(LoginActivity.this);
        JSONObject postData = new JSONObject();
//        int firm_position = Integer.parseInt(firm_name);
        postData.put("email", Objects.requireNonNull(tet_email.getText()).toString());
        postData.put("userid", firm_name);
        postData.put("password", et_firm_password.getText().toString());
        WebServiceHelper.callHttpWebService(LoginActivity.this, LoginActivity.this, WebServiceHelper.RestMethodType.POST, "login", "LOGIN", postData.toString());
    }

    private void Dashboard_data(JSONArray jsonArray_Dashboard) throws JSONException {
        for (int i = 0; i < jsonArray_Dashboard.length(); i++) {
            JSONObject jsonObject = jsonArray_Dashboard.getJSONObject(i);
            String type = jsonObject.getString("type");
            JSONArray jsonArray_myday = jsonObject.getJSONArray("options");
            for (int j = 0; j < jsonArray_myday.length(); j++) {
                JSONObject jsonObject1 = jsonArray_myday.getJSONObject(j);
                Dashboard_Model dashboardModel = new Dashboard_Model();
                dashboardModel.setName(jsonObject1.getString("name"));
                dashboardModel.setSequence(jsonObject1.getString("sequence"));
                dashboardModels.add(dashboardModel);
            }
            Log.d("Dashboard_page", type + dashboardModels);
        }
    }

    public void save_xmpp_preference() {
//        String email = Objects.requireNonNull(et_Prof_Biz.getText()).toString().trim();

        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        String uid = Constants.UID;
        if (!Constants.ROLE.equalsIgnoreCase("admin")) {
            uid = uid + "_" + Constants.USER_ID;
        }

//        String existing_xmpp_jid = AndroidUtils.getSharedPreferenceStringData("xmpp_jid", this);
//        if (existing_xmpp_jid != null && !existing_xmpp_jid.equals(uid)) {
//            Intent i1 = new Intent(getApplicationContext(), ChatConnectionService.class);
//            stopService(i1);
//        }
        prefs.edit()
                .putString("xmpp_jid", uid)
                .putString("xmpp_password", Constants.TOKEN)
                .putBoolean("xmpp_logged_in", true)
                .apply();
//
        if (mConnection == null) {
            mConnection = new ChatConnection(this);
        }
        if (chatConnectionService == null) {
            chatConnectionService = new ChatConnectionService();
        }
        new JsonTask().execute(Constants.base_URL + "user/create/");

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progress_dialog != null && progress_dialog.isShowing())
            AndroidUtils.dismiss_dialog(progress_dialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                if (httpResult.getRequestType().equals("LOGIN")) {
                    if (!result.getBoolean("error")) {
                        JSONObject probiz_data = new JSONObject(result.getString("data"));
                        Constants.jsonObject_dashboard = probiz_data;
                        if (!probiz_data.getString("plan").equalsIgnoreCase("lauditor")) {
//                            AndroidUtils.showToast("Account not found", this);
                            AndroidUtils.showToast("Account not found", LoginActivity.this);
                            return;
                        }
                        String email = null;
                        email = Objects.requireNonNull(tet_email.getText()).toString().trim();
                        String password = null;
                        password = Objects.requireNonNull(tet_password.getText()).toString().trim();
                        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
                        prefs.edit()
                                .putString("email", email)
                                .putString("password", password)
                                .putBoolean("isLogin", true)
                                .putString("proBizType", Constants.PROBIZ_TYPE)
                                .apply();

                        Constants.TOKEN = result.getString("token");
                        Constants.NAME = probiz_data.getString("name");
                        Constants.USER_ID = probiz_data.getString("user_id");
                        Constants.UID = probiz_data.getString("uid");
                        Constants.OLD_PASSWORD = tet_password.getText().toString();
                        Constants.PK = probiz_data.getString("pk");
                        Constants.PASSWORD_MODE = probiz_data.getString("password_mode");
                        Constants.IS_ADMIN = probiz_data.getBoolean("admin");
                        Constants.FIRM_NAME = probiz_data.getString("firm_name");
                        Constants.ROLE = probiz_data.getString("role");
                        save_xmpp_preference();
                        if (Constants.PASSWORD_MODE.equals("normal")) //Checking for password_mode
                        {
                            AndroidUtils.showToast("Login Successful", this);
                            //After Successfully Login, Checking if the Bio-Metric Check box is Checked or Not.
                            Bio_metric_access();
                            Dashboard();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            if (ad_dialog != null && ad_dialog.isShowing())
                                ad_dialog.dismiss();
                        } else {
                            startActivity(new Intent(LoginActivity.this, reset_password_file.class));
                        }
                    } else {
                        if (result.has("firms")) {
                            ArrayList<FirmsDo> list = new ArrayList<>();
                            FirmsDo firmsDo;
                            JSONObject firms = result.getJSONObject("firms");
                            JSONArray adminJsonArray = firms.getJSONArray("lauditor");
                            if (adminJsonArray.length() == 0) {
                                String msg = "Account not found";
                                AndroidUtils.showToast(msg, LoginActivity.this);
                            } else if (adminJsonArray.length() > 1) {
                                for (int i = 0; i < adminJsonArray.length(); i++) {
                                    JSONObject obj = adminJsonArray.getJSONObject(i);
                                    String userid = obj.getString("id");
                                    firmsDo = new FirmsDo();
                                    firmsDo.setName(obj.getString("firmName"));
                                    firmsDo.setValue(obj.getString("id"));
                                    list.add(firmsDo);
                                }
                                has_firm = true;
                                firm_login(list);
                            } else {
                                JSONObject obj = adminJsonArray.getJSONObject(0);
                                String user = obj.getString("id");
                                Iterator<String> iter = obj.keys();
                                JSONObject postData = new JSONObject();
                                postData.put("email", Objects.requireNonNull(tet_email.getText()).toString());
                                postData.put("userid", user);
                                postData.put("password", Objects.requireNonNull(tet_password.getText()).toString());
                                WebServiceHelper.callHttpWebService(LoginActivity.this, LoginActivity.this, WebServiceHelper.RestMethodType.POST, "login", "LOGIN", postData.toString());
                            }
                        } else {
                            String error_msg = result.has("plan") && result.getString("plan").equals("lauditor") ? String.valueOf(result.get("msg")) : "Account not found";
//                            startActivity(new Intent(this, reset_password_file.class));
                            AndroidUtils.showToast(error_msg, LoginActivity.this);
//                            ((TextView) findViewById(R.id.tv_response)).setText(error_msg);
//                            AndroidUtils.showToast(error_msg, this);
                        }
                    }
                } else if (httpResult.getRequestType().equals("Dashboard")) {
                    JSONArray Dasboard_array = result.getJSONArray("cards");
                    response_true = true;
                    Constants.Valid_Token = true;
                    Dashboard_data(Dasboard_array);
                    if (Constants.Biometric_checked) {
                        if (response_true) {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                    }
                }
                Log.d("Response_Data", "" + Constants.jsonObject_dashboard);
            } catch (Exception e) {
                AndroidUtils.showToast(e.getMessage(), LoginActivity.this);
            }
        } else {
            AndroidUtils.showToast(httpResult.getResponseContent(), LoginActivity.this);
//            ((TextView) findViewById(R.id.tv_response)).setText((httpResult.getResponseContent()));
        }
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                mConnection.connect();

            } catch (IOException | SmackException | XMPPException e) {
                Log.d("Chat Error", "Something went wrong while connecting ,make sure the credentials are right and try again");
                e.fillInStackTrace();
                //Stop the service all together.
                chatConnectionService.stopSelf();
            }
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}