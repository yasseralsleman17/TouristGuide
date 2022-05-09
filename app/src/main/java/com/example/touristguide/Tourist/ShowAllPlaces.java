package com.example.touristguide.Tourist;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.touristguide.LoginActivity;
import com.example.touristguide.R;
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

public class ShowAllPlaces extends AppCompatActivity {
    ImageButton imageButton2;

    String type, city;

    TextView textView8;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user = null;


    LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_places);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString("type");
            city = extras.getString("city");
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = fAuth.getCurrentUser();

        linear = findViewById(R.id.linear);

        textView8 = findViewById(R.id.textView8);
        switch (type) {
            case "Places":
                textView8.setText("الأماكن");
                break;
            case "Restaurant":
                textView8.setText("المطاعم");
                break;
            case "Hotel":
                textView8.setText("الفنادق");
                break;
            case "Mosque":
                textView8.setText("المساجد");
                break;
            case "Events":
                textView8.setText("الفعاليات");
                break;
            case "Trip":
                textView8.setText("الرحلات");
                break;

            default:
                break;


        }

        fStore.collection(type).whereEqualTo("city", city).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (int i = 0; i < list.size(); i++) {
                                showplaces(list.get(i));

                            }
                        }
                    }
                });


    }

    private void showplaces(DocumentSnapshot data) {
        String place_id = data.getId();
        String place_name_txt = data.getString("name");

        View view = getLayoutInflater().inflate(R.layout.user_place_card, null);
        TextView place_name = view.findViewById(R.id.place_name);
        ImageView add_favorite = view.findViewById(R.id.add_favorite);
        ImageView place_image = view.findViewById(R.id.place_image);
        place_name.setText(place_name_txt);
        final Uri[] im_uri = new Uri[1];
        storageReference.child(type + "/" + place_id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(place_image);
                        im_uri[0] = uri;
                        linear.addView(view);

                    }
                });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewPlace.class);
                intent.putExtra("id", place_id);
                intent.putExtra("type", type);
                startActivity(intent);

            }
        });


        add_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null) {
                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(ShowAllPlaces.this);

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
                } else {
                    DocumentReference favorite_ref = fStore.collection("Favorite").document(user.getUid()).collection("Sub_Fav").document();
                    Map<String, Object> favorite_data = new HashMap<>();
                    favorite_data.put("collection", type);
                    favorite_data.put("name", place_name_txt);
                    favorite_data.put("place_id", place_id);
                    favorite_ref.set(favorite_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), place_name_txt + " Added to favorite successfully", Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            }
        });

    }

}