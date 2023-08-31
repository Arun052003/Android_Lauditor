package com.digicoffer.lauditor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class forgetpassword extends AppCompatActivity {
    Button submitButton;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        // Initialize the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize the submit button
        submitButton = findViewById(R.id.Submit);
        submitButton.setEnabled(false); // Disable the button initially

        // Initialize the email EditText
        emailEditText = findViewById(R.id.et_login_email);

        // Add a TextWatcher to the email EditText
        emailEditText.addTextChangedListener(new TextWatcher() {
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
                navigateToResetPasswordActivity();
            }
        });
    }

    // Handle Up button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate to the parent activity (resetpassword)
            navigateToResetPasswordActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper method to navigate to the resetpassword activity
    private void navigateToResetPasswordActivity() {
        Intent intent = new Intent(this, resetpassword.class);
        startActivity(intent);
    }

    // Helper method to validate an email
    private boolean isValidEmail(String email) {
        // You can implement your email validation logic here
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

