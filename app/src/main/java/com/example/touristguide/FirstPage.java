package com.example.touristguide;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.touristguide.Admin.AdminHomePage;
import com.example.touristguide.Tourist.TouristHomePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirstPage extends AppCompatActivity {


    private Button register_btn, login_btn;

    TextView skip;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private static final int REQUEST = 112;
    boolean mGPS;
    LocationManager mLocationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();




        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        FirebaseUser user = fAuth.getCurrentUser();


        if (user != null) {

            DocumentReference user_ref = fStore.collection("Users").document(user.getUid());

            user_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("Type").equals("2")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), TouristHomePage.class));

                    } else if (documentSnapshot.getString("Type").equals("1")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), AdminHomePage.class));

                    }

                }
            });


        }


        skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), TouristHomePage.class));

            }
        });

        register_btn = (Button) findViewById(R.id.button_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(FirstPage.this, RegisterActivity.class));
            }
        });

        login_btn = (Button) findViewById(R.id.button_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstPage.this, LoginActivity.class));
            }
        });


    }




}
