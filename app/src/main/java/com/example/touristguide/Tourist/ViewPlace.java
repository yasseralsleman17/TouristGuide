package com.example.touristguide.Tourist;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.touristguide.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPlace extends AppCompatActivity {


    LinearLayout linear_comment;

    String place_id, type;

    TextView description, place_name;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;
    ImageView place_image;


    GoogleMap mgoogleMap;
    ImageView add_comment;
    EditText editText_comment;

    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            place_id = extras.getString("id");
            type = extras.getString("type");
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = fAuth.getCurrentUser();

        fStore.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                user_name = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
            }
        });

        linear_comment = findViewById(R.id.linear_comment);
        editText_comment = findViewById(R.id.editText_comment);
        add_comment = findViewById(R.id.add_comment);


        description = findViewById(R.id.description);
        place_name = findViewById(R.id.place_name);
        place_image = findViewById(R.id.place_image);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mgoogleMap = googleMap;
            }
        });


        fStore.collection(type).document(place_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot data) {

                        description.setText(data.getString("description"));
                        place_name.setText(data.getString("name"));

                        Long latitude = (Long) data.getLong("latitude");
                        Long longitude = (Long) data.getLong("longitude");

                        LatLng location = new LatLng(latitude, longitude);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(location);
                        markerOptions.title(data.getString("name"));
                        mgoogleMap.clear();
                        mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
                        mgoogleMap.addMarker(markerOptions);
                        storageReference.child(type + "/").child(place_id).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .load(uri)
                                                .into(place_image);
                                    }
                                });

                    }
                });


        fStore.collection("Comment").document(place_id).collection("Sub_Com").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.isEmpty()) {
                    return;
                } else {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (int i = 0; i < list.size(); i++) {
                        showComment(list.get(i));

                    }

                }
            }
        });


        add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_txt = editText_comment.getText().toString().trim();

                if (comment_txt.equals("")) return;

                editText_comment.setText("");
                DocumentReference favorite_ref = fStore.collection("Comment").document(place_id).collection("Sub_Com").document();

                Map<String, Object> favorite_data = new HashMap<>();

                favorite_data.put("user_name", user_name);
                favorite_data.put("comment", comment_txt);

                favorite_ref.set(favorite_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        View view = getLayoutInflater().inflate(R.layout.comment, null);


                        TextView comment = view.findViewById(R.id.comment);
                        TextView name = view.findViewById(R.id.name);


                        name.setText(user_name);
                        comment.setText(comment_txt);


                        linear_comment.addView(view);
                    }
                });
            }
        });


    }

    private void showComment(DocumentSnapshot documentSnapshot) {

        View view = getLayoutInflater().inflate(R.layout.comment, null);


        TextView comment = view.findViewById(R.id.comment);
        TextView name = view.findViewById(R.id.name);


        name.setText(documentSnapshot.getString("user_name"));
        comment.setText(documentSnapshot.getString("comment"));


        linear_comment.addView(view);

    }
}