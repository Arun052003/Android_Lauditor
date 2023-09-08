package com.digicoffer.lauditor.LoginActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;
import com.digicoffer.lauditor.Webservice.WebServiceHelper;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import java.util.Objects;

public class ForgetPassword extends AppCompatActivity implements AsyncTaskCompleteListener {
    private Button submitButton;
    private TextInputEditText tetEmail;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize the TextInputEditText for email
        tetEmail = findViewById(R.id.et_login_email);

        // Initialize the submit button
        submitButton = findViewById(R.id.Submit);
        submitButton.setEnabled(false); // Disable the button initially

        // Add a TextWatcher to the email TextInputEditText
        tetEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed in this case
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Enable the submit button if a valid email is entered
                boolean isValidEmail = isValidEmail(s.toString());
                submitButton.setEnabled(isValidEmail);
            }
        });

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isAllFieldsChecked = validate();
                    if (isAllFieldsChecked) {
                        resetPassword();

                        //navigateToLoginActivity();
                       // Toast.makeText(getApplicationContext(),"Please check your email for temporary password.",Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {
        if (progressDialog != null && progressDialog.isShowing())
            AndroidUtils.dismiss_dialog(progressDialog);
        if (httpResult.getResult() == WebServiceHelper.ServiceCallStatus.Success) {
            try {
                JSONObject result = new JSONObject(httpResult.getResponseContent());

                // Check if the API request type is "RESET"
                if (httpResult.getRequestType().equals("RESET")) {
                    if (!result.getBoolean("error")) {
                        String message = result.getString("msg");
                        showPopupMessage(message);
                        navigateToLoginActivity();
                    }
                    else {
                        // Handle the case where the reset request was not successful
                        // You can display an error message to the user
                        AndroidUtils.showToast("Password reset failed", ForgetPassword.this);
                    }
                }
            } catch (Exception e) {
                AndroidUtils.showToast(e.getMessage(), ForgetPassword.this);
            }
        } else {
            // Handle the case where the API request itself failed
            AndroidUtils.showToast(httpResult.getResponseContent(), ForgetPassword.this);
        }
    }

    // Handle Up button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate to the login activity without calling Reset()
            navigateToLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper method to navigate to the login activity
    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    // Helper method to validate an email
    private boolean isValidEmail(String email) {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void resetPassword() {
        try {
            Constants.PROBIZ_TYPE = "PROFESSIONAL";
            Constants.base_URL = Constants.PROF_URL;
            JSONObject postData = new JSONObject();

            progressDialog = AndroidUtils.get_progress(ForgetPassword.this);
            postData.put("email", Objects.requireNonNull(tetEmail.getText()).toString());
            WebServiceHelper.callHttpWebService(this, ForgetPassword.this,
                    WebServiceHelper.RestMethodType.PUT, "reset-pwd", "RESET", postData.toString());
        } catch (Exception e) {
            if (progressDialog != null && progressDialog.isShowing()) {
                AndroidUtils.dismiss_dialog(progressDialog);
            }
        }
    }

    private boolean validate() {
        String email;
        email = Objects.requireNonNull(tetEmail.getText()).toString().trim();
        if (email.isEmpty()) {
            tetEmail.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            tetEmail.setError("Invalid email format");
            return false;
        }
        return true;
    }
    private void showPopupMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
