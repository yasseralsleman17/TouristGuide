package com.example.touristguide.Tourist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.touristguide.LoginActivity;
import com.example.touristguide.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class TouristHomePage extends AppCompatActivity {


    BottomNavigationView bottom_navigation;
    BottomAppBar bottomAppBar;

    FloatingActionButton fab;
    Context context;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_home_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setBackground(null);
        bottomAppBar = findViewById(R.id.bottom_app_bar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();


        bottom_navigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MapsFragment())
                        .commit();
            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (user == null && !(item.getItemId() == R.id.home)) {
                final AlertDialog.Builder popDialog = new AlertDialog.Builder(TouristHomePage.this);

                popDialog.setTitle("you should log in to continue: ");

                popDialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                popDialog.create();
                popDialog.show();
                return true;
            }
            if(item.getItemId()==R.id.date) return true;
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new Home();
                    break;
                case R.id.date:
                    selectedFragment = new Date();
                    break;
                case R.id.favorite:
                    selectedFragment = new Favorite();
                    break;
                case R.id.profil:
                    selectedFragment = new profile();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };


}