package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddLetterActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    //our database reference object
    DatabaseReference databaseLetter;

    EditText editTextTitle;
    Button buttonDate, buttonSubmit;
    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_letter);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //getting the reference of letters node
        databaseLetter = FirebaseDatabase.getInstance().getReference("Letters");

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
        String userId = firebaseAuth.getCurrentUser().getUid();

        //displaying logged in user name
        //textViewUserEmail.setText(user.getEmail());

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){

                    fname = documentSnapshot.getString("fullname");

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        editTextTitle = (EditText) findViewById(R.id.editTextReason);
        buttonDate = (Button) findViewById(R.id.buttonDate);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmitLetter);

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                        buttonDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(AddLetterActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLetter();
            }
        });
    }

    private void addLetter() {
        //getting the values to save
        String name = fname;
        String title = editTextTitle.getText().toString().trim();
        String date = buttonDate.getText().toString().trim();

        //getting a unique id using push().getKey() method
        //it will create a unique id and we will use it as the Primary Key for our Letter
        String id = databaseLetter.push().getKey();
        String status = "Pending";

        //checking if the value is provided
        if (!TextUtils.isEmpty(title)) {

            //creating an Letter Object
            Letter letter = new Letter(id, name, title, date, status);

            //Saving the Letter
            databaseLetter.child(id).setValue(letter);


            //displaying a success toast
            Toast.makeText(this, "Letter added", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(AddLetterActivity.this, LetterActivity.class);
            startActivity(intent);
            finish();

        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_LONG).show();
        }
    }
}