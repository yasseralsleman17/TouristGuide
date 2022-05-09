package com.example.touristguide;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.touristguide.Admin.AdminHomePage;
import com.example.touristguide.Tourist.TouristHomePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    Button get_started;
    boolean mGPS;
    LocationManager mLocationManager = null;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String next = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        next = "Tourist";

                    } else if (documentSnapshot.getString("Type").equals("1")) {
                        next = "Admin";


                    }

                }
            });


        }


        get_started = findViewById(R.id.get_started);
        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Checking GPS is enabled
                mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!mGPS) {

                    showdialog();


                } else {

                    switch (next) {
                        case "None":
                            startActivity(new Intent(getApplicationContext(), FirstPage.class));
                            break;
                        case "Admin":
                            startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
                            break;
                        case "Tourist":
                            startActivity(new Intent(getApplicationContext(), TouristHomePage.class));
                            break;
                    }


                }
            }
        });


    }

    private void turnGPSOn() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

        mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Checking GPS is enabled
        mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!mGPS) {

            showdialog();


        }

    }

    private void showdialog() {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(MainActivity.this);
        popDialog.setCancelable(false);
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        TextView text = new TextView(MainActivity.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        lp.setMargins(10, 10, 10, 10);
        text.setLayoutParams(lp);
        text.setText("enable GPS to continue");

        linearLayout.addView(text);


        popDialog.setTitle("GPS");

        popDialog.setView(linearLayout);


        popDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        turnGPSOn();
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        });

        popDialog.create();
        popDialog.show();


    }


}