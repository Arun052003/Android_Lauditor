package com.digicoffer.lauditor.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.digicoffer.lauditor.R;

public class reset_password_file extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        Button submit=findViewById(R.id.bt_submit_firm_login);
        Button cancel=findViewById(R.id.Cancel);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(reset_password_file.this, LoginActivity.class));
            }
        });



    }
}