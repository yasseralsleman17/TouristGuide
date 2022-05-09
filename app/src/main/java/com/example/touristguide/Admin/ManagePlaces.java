package com.example.touristguide.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.touristguide.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ManagePlaces extends AppCompatActivity {

    LinearLayout linear_places;
    ImageButton button_add_place;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    String[] collections = new String[]{"Places", "Hotel", "Restaurant", "Mosque", "Events", "Trip"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_places);


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        button_add_place = findViewById(R.id.button_add_place);

        button_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPlace.class));
            }
        });
        linear_places = findViewById(R.id.linear_places);

        for (int i = 0; i < collections.length; i++)
            getData(collections[i]);

    }

    void getData(String collection) {

        fStore.collection(collection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                showplaces(list.get(i), collection);

                            }
                        }
                    }
                });


    }

    private void showplaces(DocumentSnapshot data, String collection) {
        String place_id = data.getId();


        View view = getLayoutInflater().inflate(R.layout.place_card, null);
        ImageButton button_edit_place = view.findViewById(R.id.button_edit_place);
        ImageButton button_delete_place = view.findViewById(R.id.button_delete_place);
        TextView place_name = view.findViewById(R.id.place_name);
        ImageView place_image = view.findViewById(R.id.place_image);
        place_name.setText(data.getString("name"));
        final Uri[] im_uri = new Uri[1];
        storageReference.child("places/" + place_id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(place_image);
                        im_uri[0] = uri;
                        linear_places.addView(view);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
        button_edit_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
        button_delete_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(place_id, collection);
            }
        });
    }


    private void delete(String place_id, String collection) {


        fStore.collection(collection).document(place_id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        storageReference.child(collection + "/" + place_id).delete();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });


    }


}