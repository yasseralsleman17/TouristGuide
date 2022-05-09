package com.example.touristguide.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.touristguide.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ManageEvents extends AppCompatActivity {


    LinearLayout linear_event;
    ImageButton button_add_event;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        button_add_event = findViewById(R.id.button_add_event);

        button_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddEvent.class));
            }
        });
        linear_event = findViewById(R.id.linear_event);


        fStore.collection("Events").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                showevent(list.get(i));

                            }
                        }
                    }
                });

    }
        private void showevent(DocumentSnapshot data) {
            String event_id = data.getId();


            View view = getLayoutInflater().inflate(R.layout.admin_show_event, null);
            ImageButton button_edit_event = view.findViewById(R.id.button_edit_event);
            ImageButton button_delete_event = view.findViewById(R.id.button_delete_event);
            TextView event_name = view.findViewById(R.id.event_name);
            ImageView event_image = view.findViewById(R.id.event_image);
            event_name.setText(data.getString("name"));
            final Uri[] im_uri = new Uri[1];
            storageReference.child("Events/" + event_id).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(event_image);
                            im_uri[0] = uri;
                            linear_event.addView(view);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
            button_edit_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }

            });
            button_delete_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(event_id);
                }
            });
        }


        private void delete(String event_id) {

            fStore.collection("Events").document(event_id).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            storageReference.child("Events/" + event_id).delete();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });


        }
}