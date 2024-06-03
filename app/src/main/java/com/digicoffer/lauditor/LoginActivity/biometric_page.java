package com.digicoffer.lauditor.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.digicoffer.lauditor.Dashboard.Dashboard;
import com.digicoffer.lauditor.MainActivity;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class biometric_page extends AppCompatActivity {

    Button tv_log_on, btn_biometric, btn_password;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biometric_nav_layout);

        tv_log_on = findViewById(R.id.tv_log_on);
        btn_biometric = findViewById(R.id.btn_biometric);
        btn_password = findViewById(R.id.btn_password);

        tv_log_on.setText("Log On to Lauditor");
        tv_log_on.setTextSize(20);
        btn_biometric.setText("Log On with Biometric");
        btn_biometric.setBackground(getDrawable(R.drawable.rectangular_complete_blue_background));
        btn_password.setText("Log On with Password");

        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
        btn_biometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_login();
            }
        });
    }

    private void confirm_login() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                AndroidUtils.showToast("Device can't have the fingerprint", this);
                Log.d("Fingerprint_1", "Device can't have the fingerprint");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                AndroidUtils.showToast("Device fingerprint not Working", this);
                Log.d("Fingerprint_2", "Device fingerprint not Working");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                AndroidUtils.showToast("Finger Print is not Added", this);
                Log.d("Fingerprint_3", "Finger Print is not Added");
                break;
        }
//        Executor executor = ContextCompat.getMainExecutor(this);
        Executor executor = Executors.newSingleThreadExecutor();
        biometricPrompt = new BiometricPrompt(biometric_page.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
//                AndroidUtils.showToast("Mobile Password Verified successfully", getApplicationContext());
                Log.d("Fingerprint_success", "Mobile Password Verified successfully");
                // Get the SharedPreferences object
                SharedPreferences sharedPreferences = getSharedPreferences("BIO", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IS_Bio", true);
                editor.apply();
                Intent intent = new Intent(biometric_page.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Touch ID for \" Lauditor Dev\"")
                .setSubtitle("Authenticate through Biometrics.").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);

    }

    private void performLogout() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.clear();
        editor1.apply();
        SharedPreferences sharedPreferences_bio = getSharedPreferences("BIO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_bio.edit();
        editor.putBoolean("IS_Bio", false);
        editor.apply();
        Constants.is_biometric = false;
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}