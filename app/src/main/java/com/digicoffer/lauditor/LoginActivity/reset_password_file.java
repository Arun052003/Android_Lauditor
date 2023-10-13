package com.digicoffer.lauditor.LoginActivity;

import static com.digicoffer.lauditor.LoginActivity.ValidationUtils.isValidPassword;
import static com.digicoffer.lauditor.common.Constants.password;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import java.util.Objects;


public class reset_password_file extends AppCompatActivity implements AsyncTaskCompleteListener {
    private AlertDialog progressDialog;
    private TextInputEditText password1,password2;

    private Button submit,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_file);

        password1 = findViewById(R.id.et_login_password);
        password2 = findViewById(R.id.et_login_password2);


        submit = findViewById(R.id.Submit);
        submit.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                checkFields();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }
        };

        password1.addTextChangedListener(textWatcher);
        password2.addTextChangedListener(textWatcher);

        cancel = findViewById(R.id.Cancel);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password_check1=password1.getText().toString();
                String password_check2=password2.getText().toString();
                if(isValidPassword(password_check1)) {
                    if (password_check1.equals(password_check2)) {
                        reset_pwd();
                    } else {
                        showPopupMessage("Password Mismatch! Please check it");
                    }
                }else
                    showPopupMessage("Password is not Valid");
            }
        });




        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(reset_password_file.this,LoginActivity.class));

            }
        });
    }
    private void checkFields() {
        String password1Text = password1.getText().toString().trim();
        String password2Text = password2.getText().toString().trim();

        if (isValidPassword(password1Text) && password1Text.equals(password2Text)) {
            submit.setEnabled(true);
            submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        } else {
            submit.setEnabled(false);
            submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dullBlueColor)));
        }
    }

    @Override
    public void onClick(View view) {

    }

    //Getting response for the Update API..
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog != null && progressDialog.isShowing())
            AndroidUtils.dismiss_dialog(progressDialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());
                // Check if the API request type is "RESET"
                if (httpResult.getRequestType().equals("UPDATE")) {
                    if (!result.getBoolean("error")) {
                        String message = result.getString("msg");
                        showPopupMessage(message);
                        navigateToLoginActivity();
                    }else {
                        // Handle the case where the reset request was not successful
                        AndroidUtils.showToast("Password reset failed", reset_password_file.this);
                    }
                }
            } catch (Exception e) {
                AndroidUtils.showToast(e.getMessage(), reset_password_file.this);
            }
        } else {
            // Handle the case where the API request itself failed
            AndroidUtils.showToast(httpResult.getResponseContent(), reset_password_file.this);
        }
    }
    private void navigateToLoginActivity()
    {
        startActivity(new Intent(this,LoginActivity.class));
    }
    private void reset_pwd()
    {
        try {
            Constants.PROBIZ_TYPE = "PROFESSIONAL";
            Constants.base_URL = Constants.PROF_URL;
            JSONObject postData = new JSONObject();

            progressDialog=AndroidUtils.get_progress(reset_password_file.this);
            postData.put("old_password",Constants.OLD_PASSWORD);
            postData.put("password",Objects.requireNonNull(password1.getText().toString()));
            //showPopupMessage("valid");
            String urlpath = "password/"+Constants.PK+"/user/"+Constants.USER_ID+"/update";
            WebServiceHelper.callHttpWebService(reset_password_file.this, reset_password_file.this, WebServiceHelper.RestMethodType.PUT, urlpath, "UPDATE", postData.toString());
            Log.e("Reset Password path Success",":"+urlpath);
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing())
                AndroidUtils.dismiss_dialog(progressDialog);
        }
    }
    private void showPopupMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidPassword(String password) {

        return password.length() >= 8;
    }
}


