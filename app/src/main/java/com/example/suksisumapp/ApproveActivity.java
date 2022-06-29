package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ApproveActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId, fname, status, userType;

    //view objects
    ListView listViewApproved;
    TextView textViewAbsent;

    //a list to store all the artist from firebase database
    List<Letter> lettersApproved;

    //our database reference object
    DatabaseReference databaseLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

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

        //getting the reference of letters node
        databaseLetter = FirebaseDatabase.getInstance().getReference("Letters");

        //getting views
        listViewApproved = (ListView) findViewById(R.id.listViewApproved);
        textViewAbsent = (TextView) findViewById(R.id.textViewAbsent);

        //list to store letters
        lettersApproved = new ArrayList<>();

        //getting fullname
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    fname = documentSnapshot.getString("fullname").trim();
                    if(documentSnapshot.getString("user_type").compareTo("Student") == 0){
                        userType = "Student";
                        return;
                    }
                    if(documentSnapshot.getString("user_type").compareTo("Staff") == 0){
                        userType = "Staff";
                        return;
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        databaseLetter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                lettersApproved.clear();

                String fullname = fname;

                if(userType != null && userType.equals("Staff")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Approved")) {
                            //adding letter to the list
                            lettersApproved.add(letter);
                        }
                    }
                }

                if(userType != null && userType.equals("Student")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Approved")) {
                            if (letter.getLetterName() != null && letter.getLetterName().equals(fullname)) {
                                //adding letter to the list
                                lettersApproved.add(letter);
                            }
                        }
                    }
                }

                //creating adapter
                LetterList approvedAdapter = new LetterList(ApproveActivity.this, lettersApproved);
                //attaching adapter to the listview
                listViewApproved.setAdapter(approvedAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}