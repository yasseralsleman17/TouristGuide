package com.example.touristguide.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.touristguide.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {


    ImageView choose_image;
    EditText event_description, event_name;
    Button button_add_event;

    String event_description_txt, event_name_txt;

    Uri imuri;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        event_description = findViewById(R.id.event_description);
        event_name = findViewById(R.id.event_name);
        button_add_event = findViewById(R.id.button_add_event);

        choose_image = findViewById(R.id.choose_image);

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });

        button_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event_description_txt = event_description.getText().toString().trim();
                event_name_txt = event_name.getText().toString().trim();

                boolean flag = true;


                if (event_description_txt.equals("")) {
                    event_description.setError("يجب اضافة وصف للحدث");
                    flag = false;
                }
                if (event_name_txt.equals("")) {
                    event_name.setError("يجب تحديد اسم الحدث");
                    flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "يجب اضافة صورة للحدث", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                if (!flag) {
                    return;
                }

                DocumentReference event_ref = fStore.collection("Events").document();

                Map<String, Object> event_data = new HashMap<>();

                event_data.put("description", event_description_txt);
                event_data.put("name", event_name_txt);

                uploadpic(event_ref.getId());

                event_ref.set(event_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(getApplicationContext(), ManageEvents.class));

                    }
                });


            }
        });


    }


    private void openfilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imuri = data.getData();
            choose_image.setImageURI(imuri);
        }
    }

    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("Events/" + id);

        ImagesRef.putFile(imuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded Successfully", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to upload image......", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}