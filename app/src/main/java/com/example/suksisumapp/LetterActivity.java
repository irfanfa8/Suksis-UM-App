package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LetterActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    //our database reference object
    DatabaseReference databaseLetter;

    private String userId, fname, status, userType;
    Button ButtonHome, ButtonQR, ButtonLetter, ButtonProfile;
    FloatingActionButton buttonAddLetter, buttonApprovedLetter, buttonRejectedLetter;
    TextView textViewAbsent;

    //view objects
    ListView listViewPending;

    //a list to store all the artist from firebase database
    List<Letter> letters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);

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
        textViewAbsent = (TextView) findViewById(R.id.textViewAbsent);
        listViewPending = (ListView) findViewById(R.id.listViewPending);

        //list to store letters
        letters = new ArrayList<>();

        buttonAddLetter = (FloatingActionButton) findViewById(R.id.buttonAddLetter);
        buttonAddLetter.setVisibility(View.GONE);
        buttonAddLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LetterActivity.this, AddLetterActivity.class);
                startActivity(intent);
            }
        });

        //getting fullname
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    fname = documentSnapshot.getString("fullname").trim();
                    if(documentSnapshot.getString("user_type").compareTo("Student") == 0){
                        buttonAddLetter.setVisibility(View.VISIBLE);
                        userType = "Student";
                        textViewAbsent.setText("Your pending letter");
                        listViewPending.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Letter letter = letters.get(i);
                                showUpdateDeleteDialog(letter.getLetterId(), letter.getLetterName(), letter.getLetterTitle(), letter.getLetterDate(), letter.getLetterStatus(), letter.getLetterURL());
                                return true;
                            }
                        });
                    }
                    if(documentSnapshot.getString("user_type").compareTo("Staff") == 0){
                        userType = "Staff";
                        listViewPending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Letter letter = letters.get(i);
                                showApproveRejectDialog(letter.getLetterId(), letter.getLetterName(), letter.getLetterTitle(), letter.getLetterDate(), letter.getLetterURL());
                            }
                        });
                        listViewPending.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Letter letter = letters.get(i);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setType("application/pdf");
                                intent.setData(Uri.parse(letter.getLetterURL()));
                                startActivity(intent);
                                return true;
                            }
                        });
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        buttonApprovedLetter = findViewById(R.id.buttonApprovedLetter);
        buttonApprovedLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LetterActivity.this, ApproveActivity.class));
            }
        });

        buttonRejectedLetter = findViewById(R.id.buttonRejectedLetter);
        buttonRejectedLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LetterActivity.this, RejectActivity.class));
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
                startActivity(new Intent(LetterActivity.this, HomeActivity.class));
                finish();
            }
        });
        ButtonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LetterActivity.this, QRActivity.class));
                finish();
            }
        });
        ButtonLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LetterActivity.this, ProfileActivity.class));
                finish();
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
                letters.clear();

                String fullname = fname;

                if(userType != null && userType.equals("Staff")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Pending")) {
                            //adding letter to the list
                            letters.add(letter);
                        }
                    }
                }

                if(userType != null && userType.equals("Student")) {

                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting letter
                        Letter letter = postSnapshot.getValue(Letter.class);
                        //add filter
                        if (letter.getLetterStatus() != null && letter.getLetterStatus().equals("Pending")) {
                            if (letter.getLetterName() != null && letter.getLetterName().equals(fullname)) {
                                //adding letter to the list
                                letters.add(letter);
                            }
                        }
                    }
                }

                //creating adapter
                LetterList pendingAdapter = new LetterList(LetterActivity.this, letters);
                //attaching adapter to the listview
                listViewPending.setAdapter(pendingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean updateLetter(String id, String name, String title, String date, String status, String url) {
        //getting the specified letter reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Letters").child(id);

        //updating letter
        Letter letter = new Letter(id, name, title, date, status, url);
        dR.setValue(letter);
        Toast.makeText(getApplicationContext(), "Letter Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private void showApproveRejectDialog(final String letterId, String letterName, String letterTitle, String letterDate, String letterURL) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.approve_letter_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonApprove = (Button) dialogView.findViewById(R.id.buttonApprove);
        final Button buttonReject = (Button) dialogView.findViewById(R.id.buttonReject);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle(letterTitle);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "Approved";
                updateLetter(letterId, letterName, letterTitle, letterDate, status, letterURL);
                b.dismiss();
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "Rejected";
                updateLetter(letterId, letterName, letterTitle, letterDate, status, letterURL);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteLetter(letterId);
                b.dismiss();
            }
        });
    }

    private void showUpdateDeleteDialog(String letterId, String letterName, String letterTitle, String letterDate, String letterStatus, String letterURL) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_letter_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextReasonUp = (EditText) dialogView.findViewById(R.id.editTextReasonUp);
        final Button buttonDate = (Button) dialogView.findViewById(R.id.buttonDate);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateLetter);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteLetter);

        editTextReasonUp.setText(letterTitle);
        buttonDate.setText(letterDate);

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
                new DatePickerDialog(LetterActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dialogBuilder.setTitle("Edit Letter");
        final AlertDialog c = dialogBuilder.create();
        c.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextReasonUp.getText().toString().trim();
                if (!TextUtils.isEmpty(title)) {
                    updateLetter(letterId, letterName, title, letterDate, letterStatus, letterURL);
                    c.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteLetter(letterId);
                c.dismiss();
            }
        });
    }


    private boolean deleteLetter(String id) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Letters").child(id);

        //removing artist
        dR.removeValue();

        Toast.makeText(getApplicationContext(), "Letter Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

}