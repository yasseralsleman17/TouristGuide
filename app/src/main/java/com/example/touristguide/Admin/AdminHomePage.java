package com.example.touristguide.Admin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.touristguide.LoginActivity;
import com.example.touristguide.MainActivity;
import com.example.touristguide.R;
import com.example.touristguide.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomePage extends AppCompatActivity {

    private Button button_places, button_log_out;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth=FirebaseAuth.getInstance();
        button_places = (Button) findViewById(R.id.button_places);
        button_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), AddPlace.class));
            }
        });

        button_log_out = (Button) findViewById(R.id.button_log_out);
        button_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}