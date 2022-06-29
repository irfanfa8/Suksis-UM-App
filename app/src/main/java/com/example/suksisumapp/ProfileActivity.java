package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Button ButtonHome, ButtonQR, ButtonLetter, ButtonProfile, ButtonEditProfile, ButtonLogOut;
    private TextView TVFullname, TVMatric, TVEmail, TVKS, TV_KS, TVPhone, TV_Phone, TVRace, TV_Race, TVGender, TV_Gender;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LogInActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        userId = firebaseAuth.getCurrentUser().getUid();

        TVFullname = findViewById(R.id.TVUserFullname);
        TVMatric = findViewById(R.id.TVUserMatric);
        TVEmail = findViewById(R.id.TVEmail);
        TVKS = findViewById(R.id.TVKSNumber);
        TV_KS = findViewById(R.id.textViewKSNumber);
        TVPhone = findViewById(R.id.TVNoPhone);
        TV_Phone = findViewById(R.id.textViewNoPhone);
        TVRace = findViewById(R.id.TVRace);
        TV_Race = findViewById(R.id.textViewRace);
        TVGender = findViewById(R.id.TVGender);
        TV_Gender = findViewById(R.id.textViewGender);

        DocumentReference userReference = firebaseFirestore.collection("users").document(userId);
        userReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    TVFullname.setText(documentSnapshot.getString("fullname"));
                    TVMatric.setText(documentSnapshot.getString("matric"));
                    TVEmail.setText(documentSnapshot.getString("email"));
                    if(documentSnapshot.getString("phone") == null){
                        TVPhone.setVisibility(View.GONE);
                        TV_Phone.setVisibility(View.GONE);
                    }else {
                        TVPhone.setText(documentSnapshot.getString("phone"));
                    }
                    if(documentSnapshot.getString("ks_number") == null){
                        TVKS.setVisibility(View.GONE);
                        TV_KS.setVisibility(View.GONE);
                    }else {
                        TVKS.setText(documentSnapshot.getString("ks_number"));
                    }if(documentSnapshot.getString("race") == null){
                        TVRace.setVisibility(View.GONE);
                        TV_Race.setVisibility(View.GONE);
                    }else {
                        TVRace.setText(documentSnapshot.getString("race"));
                    }if(documentSnapshot.getString("gender") == null){
                        TVGender.setVisibility(View.GONE);
                        TV_Gender.setVisibility(View.GONE);
                    }else {
                        TVGender.setText(documentSnapshot.getString("gender"));
                        ButtonEditProfile.setVisibility(View.GONE);
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        ButtonEditProfile = findViewById(R.id.buttonEditProfile);
        ButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        ButtonLogOut = findViewById(R.id.buttonLogOut);
        ButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(ProfileActivity.this, LogInActivity.class));
            }
        });

        //Bottom Navigation
        ButtonHome = (Button) findViewById(R.id.homePage);
        ButtonQR = (Button) findViewById(R.id.qrPage);
        ButtonLetter = (Button) findViewById(R.id.letterPage);
        ButtonProfile = (Button) findViewById(R.id.profilePage);
        ButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            }
        });
        ButtonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, QRActivity.class));
                finish();
            }
        });
        ButtonLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, LetterActivity.class));
                finish();
            }
        });
        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}