package com.example.touristguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touristguide.Admin.AdminHomePage;
import com.example.touristguide.Tourist.TouristHomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText email, password;
    private String email_txt, password_txt;

    private Button login;
    TextView tosignin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        login = (Button) findViewById(R.id.button_login);

        tosignin = (TextView) findViewById(R.id.tosignin);
        tosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email_txt = email.getText().toString();
                password_txt = password.getText().toString();

                boolean flag = true;

                if (email_txt.isEmpty()) {
                    email.setError("This field is required");
                    flag = false;
                }
                if (password_txt.isEmpty()) {
                    password.setError("This field is required");
                    flag = false;
                }
                if (!flag) {
                    return;
                }

                fAuth.signInWithEmailAndPassword(email_txt, password_txt)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = authResult.getUser();
                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());
                                user_ref.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.getString("Type").equals("1")) {
                                                    startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
                                                } else if (documentSnapshot.getString("Type").equals("2")) {
                                                    startActivity(new Intent(getApplicationContext(), TouristHomePage.class));
                                                }  }
                                        }); }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Check your Email and password and try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}