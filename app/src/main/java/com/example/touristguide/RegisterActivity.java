package com.example.touristguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touristguide.Tourist.TouristHomePage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    private EditText fname     ,lname     ,phone        ,city     ,email      ,password   ;
    String           fname_txt ,lname_txt ,phone_txt    ,city_txt ,email_txt  ,password_txt;

    private Button register;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        fname = (EditText) findViewById(R.id.editText_first_name);
        lname = (EditText) findViewById(R.id.editText_last_name);
        phone = (EditText) findViewById(R.id.editText_phone);
        city = (EditText) findViewById(R.id.editText_city);
        email = (EditText) findViewById(R.id.editText_email);
        password = (EditText) findViewById(R.id.editText_password);
        register = (Button) findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname_txt    = fname.getText().toString();
                lname_txt    = lname.getText().toString();
                phone_txt    = phone.getText().toString();
                city_txt      = city.getText().toString();
                email_txt    = email.getText().toString();
                password_txt = password.getText().toString();

                boolean flag = true;
                if (fname_txt.isEmpty()){
                    fname.setError("This field is required");
                    flag = false;
                }
                if (lname_txt.isEmpty()){
                    lname.setError("This field is required");
                    flag = false;
                }
                if (password_txt.isEmpty()){
                    password.setError("This field is required");
                    flag = false;
                }
                if(!isValidPassword(password_txt)){
                    password.setError("invalid password");
                    flag = false;
                }
                if (email_txt.isEmpty()){
                    email.setError("This field is required");
                    flag = false;
                }
                if (phone_txt.isEmpty()){
                    phone.setError("This field is required");
                    flag = false;
                }
                if (city_txt.isEmpty()){
                    city.setError("This field is required");
                    flag = false;
                }

                if (!flag){
                    return;
                }



                fAuth.createUserWithEmailAndPassword(email_txt, password_txt)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                FirebaseUser user = fAuth.getCurrentUser();
                                DocumentReference user_ref = fStore.collection("Users").document(user.getUid());
                                Map<String, Object> userdata = new HashMap<>();

                                userdata.put("FirstName", fname_txt);
                                userdata.put("LastName", lname_txt);
                                userdata.put("Email", email_txt);
                                userdata.put("Phone", phone_txt);
                                userdata.put("Password", password_txt);
                                userdata.put("City", city_txt);
                                userdata.put("Type", "2");

                                user_ref.set(userdata)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), TouristHomePage.class));
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.toString() + "Failed to Create Account,try again later", Toast.LENGTH_SHORT).show();
                            }
                        });




            }
        });




    }


    private boolean isValidPassword(String password_txt) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9]{6,24}");

        return !TextUtils.isEmpty(password_txt) && PASSWORD_PATTERN.matcher(password_txt).matches();
    }

}