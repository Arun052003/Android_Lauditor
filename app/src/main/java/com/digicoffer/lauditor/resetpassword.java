package com.digicoffer.lauditor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.digicoffer.lauditor.LoginActivity.LoginActivity;

public class resetpassword extends AppCompatActivity {
    Button SubmitButton;
    EditText passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        SubmitButton = findViewById(R.id.Submit);
        passwordEditText = findViewById(R.id.et_login_email);
        confirmPasswordEditText = findViewById(R.id.et_login_password);

        // Disable the reset button initially
        SubmitButton.setEnabled(false);

        // Add TextWatchers to the password and confirm password EditTexts
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePassword();
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePassword();
            }
        });

        // Set click listener for the reset button
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if passwords match and meet strength criteria
                if (validatePasswords()) {
                    // Show a dialog popup message for successful password reset
                    showSuccessDialog();
                } else {
                    // Show a toast or message indicating the password requirements
                    Toast.makeText(resetpassword.this, "Passwords do not match or do not meet criteria", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean validatePasswords() {
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                return password.equals(confirmPassword) && isStrongPassword(password);
            }
        });
    }

    // Validate the password and confirm password fields
    private void validatePassword() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        boolean passwordsMatch = password.equals(confirmPassword);

        // Enable the reset button if passwords match and meet strength criteria
        SubmitButton.setEnabled(passwordsMatch && isStrongPassword(password));
    }

    // Validate password strength (example criteria)
    private boolean isStrongPassword(String password) {
        return password.length() >= 8; // Example: Password length should be at least 8 characters
    }

    // Show a dialog popup message for successful password reset
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Reset Successful")
                .setMessage("Your password has been successfully reset.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Navigate to the login activity
                        Intent intent = new Intent(resetpassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Finish the current activity
                    }
                })
                .setCancelable(false)
                .show();
    }

    // Handle Up button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

