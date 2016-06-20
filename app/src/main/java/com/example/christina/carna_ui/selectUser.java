package com.example.christina.carna_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class selectUser extends AppCompatActivity {

    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchDevices = new Intent(getApplicationContext(), homescreen.class);
                startActivity(searchDevices);
            }
        });

    }
}
