package com.example.touristguide.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.touristguide.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class AddPlace extends AppCompatActivity {


    ImageView choose_image;
    EditText place_description, place_name;
    TextView place_location;
    String place_description_txt, place_name_txt, collection = "Places", category = "أماكن", city = "الرياض";
    Spinner spinner;
    Spinner spinner2;

    String[] citys = new String[]{"الرياض", "جدة", "مكة المكرمة", "المدينة المنورة", "الأحساء","الدمام", "الطائف", "تبوك", "القطيف", "خميس مشيط", "أبها", "نجران"};
    String[] categorys = new String[]{"أماكن", "فنادق", "مطاعم", "مساجد", "فعاليات ", "رحلات"};
    String[] collections = new String[]{"Places", "Hotel", "Restaurant", "Mosque", "Events", "Trip"};


    Uri imuri;

    Button button_add_place;


    GoogleMap mMap;


    private static final int REQUEST = 112;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    CardView card6;
    ScrollView scroll;
    LatLng location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);

        card6 = findViewById(R.id.card6);
        scroll = findViewById(R.id.scroll);
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scroll.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        card6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scroll.requestDisallowInterceptTouchEvent(false);


                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        location = latLng;

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorys);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, citys);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categorys[i];
                collection = collections[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = citys[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        place_description = findViewById(R.id.place_description);
        place_name = findViewById(R.id.place_name);
        button_add_place = findViewById(R.id.button_add_place);


        choose_image = findViewById(R.id.choose_image);

        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });


        button_add_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place_description_txt = place_description.getText().toString().trim();
                place_name_txt = place_name.getText().toString().trim();
                boolean flag = true;

                if (place_description_txt.equals("")) {
                    place_description.setError("يجب اضافة وصف للموقع");flag = false;
                }
                if (place_name_txt.equals("")) {
                    place_name.setError("يجب تحديد اسم الموقع");flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "يجب اضافة صورة للموقع", Toast.LENGTH_LONG).show();flag = false;
                }
                if (location == null) {
                    Toast.makeText(getApplicationContext(), " يجب اختيار احداثيات اللموقع على الخارطة ", Toast.LENGTH_LONG).show();flag = false;
                }
                if (!flag) { return;
                }

                DocumentReference place_ref = fStore.collection(collection).document();
                Map<String, Object> place_data = new HashMap<>();

                place_data.put("city", city);
                place_data.put("description", place_description_txt);
                place_data.put("name", place_name_txt);
                place_data.put("latitude", location.latitude);
                place_data.put("longitude", location.longitude);

                uploadpic(place_ref.getId());

                place_ref.set(place_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddPlace.this, place_name_txt+" added successfully", Toast.LENGTH_SHORT).show();

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


        StorageReference ImagesRef = storageReference.child(collection+"/" + id);

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