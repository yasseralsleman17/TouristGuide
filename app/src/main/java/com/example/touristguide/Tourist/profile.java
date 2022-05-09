package com.example.touristguide.Tourist;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.touristguide.FirstPage;
import com.example.touristguide.R;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
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


    TextView name, email;
    LinearLayout linear_edit_profile, linear_notification, linear_about_us, linear_logout;
    ImageView add_profile_image;

    CircularImageView profile_image;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;

    Uri imuri;

    String email_txt, fname_txt, lname_txt, phone_txt, city_txt, password_txt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        linear_edit_profile = view.findViewById(R.id.linear_edit_profile);
        linear_notification = view.findViewById(R.id.linear_notification);
        linear_about_us = view.findViewById(R.id.linear_about_us);
        linear_logout = view.findViewById(R.id.linear_logout);

        profile_image = view.findViewById(R.id.profile_image);

        add_profile_image = view.findViewById(R.id.add_profile_image);

        add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });

        linear_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                View view = getLayoutInflater().inflate(R.layout.edit_profile, null);
                EditText fname, lname, phone, city, password;
                fname = (EditText) view.findViewById(R.id.editText_first_name);
                lname = (EditText) view.findViewById(R.id.editText_last_name);
                phone = (EditText) view.findViewById(R.id.editText_phone);
                city = (EditText) view.findViewById(R.id.editText_city);
                password = (EditText) view.findViewById(R.id.editText_password);

                fname.setText(fname_txt);
                lname.setText(lname_txt);
                phone.setText(phone_txt);
                city.setText(city_txt);
                password.setText(password_txt);
                Button save = (Button) view.findViewById(R.id.button_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        fname_txt = fname.getText().toString();
                        lname_txt = lname.getText().toString();
                        phone_txt = phone.getText().toString();
                        city_txt = city.getText().toString();
                        String password_txt_new = password.getText().toString();

                        boolean flag = true;
                        if (fname_txt.isEmpty()) {
                            fname.setError("This field is required");
                            flag = false;
                        }
                        if (lname_txt.isEmpty()) {
                            lname.setError("This field is required");
                            flag = false;
                        }
                        if (password_txt.isEmpty()) {
                            password.setError("This field is required");
                            flag = false;
                        }
                        if (!isValidPassword(password_txt)) {
                            password.setError("invalid password");
                            flag = false;
                        }

                        if (phone_txt.isEmpty()) {
                            phone.setError("This field is required");
                            flag = false;
                        }
                        if (city_txt.isEmpty()) {
                            city.setError("This field is required");
                            flag = false;
                        }

                        if (!flag) {
                            return;
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email_txt, password_txt);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updatePassword(password_txt_new).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentReference user_ref = fStore.collection("Users").document(user.getUid());
                                                        Map<String, Object> userdata = new HashMap<>();
                                                        userdata.put("FirstName", fname_txt);
                                                        userdata.put("LastName", lname_txt);
                                                        userdata.put("Phone", phone_txt);
                                                        userdata.put("Password", password_txt_new);
                                                        userdata.put("City", city_txt);
                                                        user_ref.update(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                                Toast.makeText(getActivity(), "your profile edited successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Log.d("TAG", "Error password not updated");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("TAG", "Error auth failed");

                                        }
                                    }
                                });



                    }
                });
                dialog.setContentView(view);
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        linear_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.notification, null);


                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(view);


                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                dialog.show();

            }
        });

        linear_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.about_us, null);


                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(view);

                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                dialog.show();

            }
        });


        linear_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), FirstPage.class));

            }
        });
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = fAuth.getCurrentUser();


        fStore.collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                email_txt = documentSnapshot.getString("Email");
                fname_txt = documentSnapshot.getString("FirstName");
                lname_txt = documentSnapshot.getString("LastName");
                phone_txt = documentSnapshot.getString("Phone");
                city_txt = documentSnapshot.getString("City");
                password_txt = documentSnapshot.getString("Password");

                name.setText(fname_txt + " " + lname_txt);
                email.setText(email_txt);

                storageReference.child("Profile" + "/").child(user.getUid()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Glide.with(getActivity())
                                        .load(uri)
                                        .into(profile_image);
                            }
                        });
            }
        });


        return view;
    }


    private void openfilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imuri = data.getData();
            profile_image.setImageURI(imuri);
            uploadpic(user.getUid());
        }
    }


    private void uploadpic(String id) {

        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading image . . . . ");
        pd.show();


        StorageReference ImagesRef = storageReference.child("Profile" + "/" + id);

        ImagesRef.putFile(imuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();

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