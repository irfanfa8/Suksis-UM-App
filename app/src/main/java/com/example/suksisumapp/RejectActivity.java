package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
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

public class RejectActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId, fname, status, userType;

    //view objects
    ListView listViewRejected;
    TextView textViewAbsent;

    //a list to store all the artist from firebase database
    List<Letter> lettersRejected;

    //our database reference object
    DatabaseReference databaseLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject);

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
        listViewRejected = (ListView) findViewById(R.id.listViewRejected);
        textViewAbsent = (TextView) findViewById(R.id.textViewAbsent);

        //list to store letters
        lettersRejected = new ArrayList<>();

        //listview scroll
        listViewRejected.setNestedScrollingEnabled(false);

        //getting fullname
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    fname = documentSnapshot.getString("fullname").trim();
                    if(documentSnapshot.getString("user_type").compareTo("Student") == 0){
                        userType = "Student";
                    }
                    if(documentSnapshot.getString("user_type").compareTo("Staff") == 0){
                        userType = "Staff";
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
                lettersRejected.clear();

                String fullname = fname;

                if(userType != null && userType.equals("Staff")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Rejected")) {
                            //adding letter to the list
                            lettersRejected.add(letter);
                        }
                    }
                }

                if(userType != null && userType.equals("Student")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Rejected")) {
                            if (letter.getLetterName() != null && letter.getLetterName().equals(fullname)) {
                                //adding letter to the list
                                lettersRejected.add(letter);
                            }
                        }
                    }
                }
                //creating adapter
                LetterList rejectedAdapter = new LetterList(RejectActivity.this, lettersRejected);
                //attaching adapter to the listview
                listViewRejected.setAdapter(rejectedAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}