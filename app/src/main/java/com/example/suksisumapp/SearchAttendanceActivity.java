package com.example.suksisumapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.util.Locale;

public class SearchAttendanceActivity extends AppCompatActivity {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    //our database reference object
    DatabaseReference databaseAttendance;

    EditText ETSearchName, ETSearchDate;
    FloatingActionButton BtnSearch;
    String userId, fname, userType;

    ListView listViewAttendance;

    List<Attendance> attendances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_attendance);

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
        databaseAttendance = FirebaseDatabase.getInstance().getReference("Attendances");

        //getting listviews
        listViewAttendance = (ListView) findViewById(R.id.listViewAttendance);

        ETSearchName = findViewById(R.id.et_search_name);
        ETSearchDate = findViewById(R.id.et_search_date);
        BtnSearch = findViewById(R.id.btn_search_attendance);

        ETSearchDate.setOnClickListener(new View.OnClickListener() {
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
                        ETSearchDate.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(SearchAttendanceActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //list to store letters
        attendances = new ArrayList<>();

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

        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //attaching value event listener
                databaseAttendance.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String SearchName = ETSearchName.getText().toString().trim();
                        String SearchDate = ETSearchDate.getText().toString().trim();

                        //clearing the previous artist list
                        attendances.clear();

                        if (!TextUtils.isEmpty(SearchName) && !TextUtils.isEmpty(SearchDate)) {
                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting letter
                                Attendance attendance = postSnapshot.getValue(Attendance.class);

                                if (attendance.getAttendanceName() != null && attendance.getAttendanceName().toLowerCase().contains(SearchName.toLowerCase())) {
                                    if (attendance.getAttendanceDate() != null && attendance.getAttendanceDate().contains(SearchDate)) {
                                        //adding letter to the list
                                        attendances.add(attendance);
                                    }
                                }
                            }

                            //creating adapter
                            AttendanceList attendanceAdapter = new AttendanceList(SearchAttendanceActivity.this, attendances);
                            //attaching adapter to the listview
                            listViewAttendance.setAdapter(attendanceAdapter);

                            Toast.makeText(SearchAttendanceActivity.this, "Searching for " + SearchName + " and " + SearchDate, Toast.LENGTH_SHORT).show();
                            ETSearchName.getText().clear();
                            ETSearchDate.getText().clear();

                        }else if (!TextUtils.isEmpty(SearchName)) {

                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting letter
                                Attendance attendance = postSnapshot.getValue(Attendance.class);

                                if (attendance.getAttendanceName() != null && attendance.getAttendanceName().toLowerCase().contains(SearchName.toLowerCase())) {
                                    //adding letter to the list
                                    attendances.add(attendance);
                                }
                            }

                            //creating adapter
                            AttendanceList attendanceAdapter = new AttendanceList(SearchAttendanceActivity.this, attendances);
                            //attaching adapter to the listview
                            listViewAttendance.setAdapter(attendanceAdapter);

                            Toast.makeText(SearchAttendanceActivity.this, "Searching for " + SearchName, Toast.LENGTH_SHORT).show();
                            ETSearchName.getText().clear();

                        }else if (!TextUtils.isEmpty(SearchDate)) {

                            //iterating through all the nodes
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //getting letter
                                Attendance attendance = postSnapshot.getValue(Attendance.class);

                                if (attendance.getAttendanceDate() != null && attendance.getAttendanceDate().contains(SearchDate)) {
                                    //adding letter to the list
                                    attendances.add(attendance);
                                }
                            }

                            //creating adapter
                            AttendanceList attendanceAdapter = new AttendanceList(SearchAttendanceActivity.this, attendances);
                            //attaching adapter to the listview
                            listViewAttendance.setAdapter(attendanceAdapter);

                            Toast.makeText(SearchAttendanceActivity.this, "Searching for " + SearchDate, Toast.LENGTH_SHORT).show();
                            ETSearchDate.getText().clear();

                        }else{
                            Toast.makeText(SearchAttendanceActivity.this, "Please enter name or date!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}