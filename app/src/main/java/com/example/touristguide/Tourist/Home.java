package com.example.touristguide.Tourist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.touristguide.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    CardView card1, card2, card3, card4, card5, card6;


    Uri[] images = {};
    String[] titles = {};
    String[] description = {};
    CoordinatorLayout coordinatorLayout;

    LinearLayout linear_event;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        linear_event = view.findViewById(R.id.linear_event);


        getEvents();


        coordinatorLayout = view.findViewById(R.id.coordinator_layout);


        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);
        card4 = view.findViewById(R.id.card4);
        card5 = view.findViewById(R.id.card5);
        card6 = view.findViewById(R.id.card6);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Places");
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Restaurant");
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Hotel");
                startActivity(intent);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Mosque");
                startActivity(intent);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Events");
                startActivity(intent);
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectCity.class);
                intent.putExtra("type", "Trip");
                startActivity(intent);
            }
        });


        return view;
    }

    private void getEvents() {


        fStore.collection("Events").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {

                            return;
                        } else {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            DocumentSnapshot data;

                            images = new Uri[list.size()];
                            titles = new String[list.size()];
                            description = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                int finalI = i;
                                data = list.get(i);


                                String event_id = data.getId();

                                titles[finalI] = data.getString("name");

                                description[finalI] = data.getString("description");

                                storageReference.child("Events/").child(event_id).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                images[finalI] = uri;

                                                if (finalI == (list.size()-1) )

                                                    show_event();

                                            }
                                        });

                            }


                        }
                    }
                });
    }

    private void show_event() {
        linear_event.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.show_event_card, null);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPagerMain);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getActivity(), images, R.layout.item, titles, description);


        mViewPager.setAdapter(mViewPagerAdapter);

        linear_event.addView(view);

    }


}
