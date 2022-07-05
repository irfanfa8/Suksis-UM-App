package com.example.suksisumapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ETKS, ETPhone;
    private Button buttonEdit, buttonCancel;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    String[] races =  {"Malay","Chinese","Indian"};
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userID, race;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //initializing firebase auth object
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
        userID = firebaseAuth.getCurrentUser().getUid();

        ETKS = (EditText) findViewById(R.id.KSProfile);
        ETPhone = (EditText) findViewById(R.id.PhoneProfile);
        radioGroup = findViewById(R.id.radiogp);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        autoCompleteTxt = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(this,R.layout.race_item,races);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                race = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),race + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        buttonEdit.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonEdit){
            editUser();
        }

        if(v == buttonCancel){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }

    }

    private void editUser() {
        String ks_number = "KS" + ETKS.getText().toString().trim();
        String phone = ETPhone.getText().toString().trim();
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        String gender = radioButton.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(ks_number)){
            Toast.makeText(this,"Please enter ks number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(race)){
            Toast.makeText(this,"Please enter your race",Toast.LENGTH_LONG).show();
            return;
        }

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String fullname = documentSnapshot.getString("fullname");
                    String matric = documentSnapshot.getString("matric");
                    String user_type = documentSnapshot.getString("user_type");
                    String email = documentSnapshot.getString("email");
                    Map<String,Object> user = new HashMap<>();
                    user.put("fullname", fullname);
                    user.put("matric", matric);
                    user.put("user_type", user_type);
                    user.put("email", email);
                    user.put("ks_number",ks_number);
                    user.put("phone",phone);
                    user.put("race",race);
                    user.put("gender",gender);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is edited for "+ userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        finish();
        Toast.makeText(EditProfileActivity.this, "Edit successfullly!", Toast.LENGTH_LONG).show();
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        Toast.makeText(this,  String.valueOf(radioButton.getText()) + " selected",
                Toast.LENGTH_SHORT).show();
    }
}