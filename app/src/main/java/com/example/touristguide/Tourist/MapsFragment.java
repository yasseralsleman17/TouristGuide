package com.example.touristguide.Tourist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.touristguide.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MapsFragment extends Fragment {


    Spinner spinner;
    String[] categorys = new String[]{"أماكن", "فنادق", "مطاعم", "مساجد", "فعاليات ", "رحلات"};
    String[] collections = new String[]{"Places", "Hotel", "Restaurant", "Mosque", "Events", "Trip"};
    String collection = "Places", category = "أماكن";

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    GoogleMap mgoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categorys);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categorys[i];
                collection = collections[i];
                mgoogleMap.clear();

                addtomap();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mgoogleMap = googleMap;
                mgoogleMap.clear();

                addtomap();
            }
        });
        return view;
    }

    private void addtomap() {


        fStore.collection(collection).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            DocumentSnapshot data = null;
                            Double latitude = null, longitude = null;
                            for (int i = 0; i < list.size(); i++) {


                                data = list.get(i);

                                latitude = data.getDouble("latitude");
                                longitude = data.getDouble("longitude");

                                LatLng location = new LatLng(latitude, longitude);

                                mgoogleMap.addMarker(new MarkerOptions().position(location).title(data.getString("name")));


                            }
                        }
                    }
                });

    }


}