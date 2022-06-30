package com.example.suksisumapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QRActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId, fname, userType;

    //our database reference object
    DatabaseReference databaseAttendance;

    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    Button ButtonGenerate, ButtonScan, ButtonSearchAttendance, ButtonSearchDate;
    Button ButtonHome, ButtonQR, ButtonLetter, ButtonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //getting the reference of letters node
        databaseAttendance = FirebaseDatabase.getInstance().getReference("Attendances");

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

        //initialize button
        ButtonGenerate = findViewById(R.id.ButtonGenerate);
        ButtonScan = findViewById(R.id.ButtonScan);
        ButtonSearchAttendance = findViewById(R.id.ButtonSearchAttendance);

        //set button visible gone
        ButtonGenerate.setVisibility(View.GONE);
        ButtonScan.setVisibility(View.GONE);
        ButtonSearchAttendance.setVisibility(View.GONE);

        //getting fullname
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    fname = documentSnapshot.getString("fullname").trim();
                    if(documentSnapshot.getString("user_type").compareTo("Student") == 0){
                        userType = "Student";
                        ButtonScan.setVisibility(View.VISIBLE);
                    }
                    if(documentSnapshot.getString("user_type").compareTo("Staff") == 0){
                        userType = "Staff";
                        ButtonGenerate.setVisibility(View.VISIBLE);
                        ButtonSearchAttendance.setVisibility(View.VISIBLE);
                    }
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        ButtonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRActivity.this, QRGenerateActivity.class));
            }
        });

        ButtonSearchAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRActivity.this, SearchAttendanceActivity.class));
            }
        });

        ButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize intent integrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        QRActivity.this
                );
                //Set prompt text
                intentIntegrator.setPrompt("For flash use volume up key");
                //Set beep
                intentIntegrator.setBeepEnabled(true);
                //Locked orientation
                intentIntegrator.setOrientationLocked(true);
                //Set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);
                //Initialize scan
                intentIntegrator.initiateScan();
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
                startActivity(new Intent(QRActivity.this, HomeActivity.class));
                finish();
            }
        });
        ButtonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ButtonLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRActivity.this, LetterActivity.class));
                finish();
            }
        });
        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            //Initialize intent result
            IntentResult intentResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);

            //Check condition
            if (intentResult.getContents() != null){
                //When result content is not null
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        QRActivity.this
                );

                //Set title
                builder.setTitle("Take Selfie");
                String Result = intentResult.getContents();
                //Set message
                builder.setMessage("You need to take selfie with your current location!");

                //Set positive button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        addAttendance(Result);

                        Intent intent = new Intent(QRActivity.this, Selfie.class);

                        //Create the bundle
                        Bundle b = new Bundle();
                        //Add your data to bundle
                        b.putString("result", Result);
                        intent.putExtras(b);

                        //Dismiss dialog
                        dialog.dismiss();

                        startActivity(intent);
                    }
                });

                //Show alert dialog
                builder.show();
            }else {
                //When result content is null
                //Display toast
                Toast.makeText(getApplicationContext(), "Oooppps... You did not scan anything", Toast.LENGTH_SHORT).show();
            }

    }

    private void addAttendance(String Result) {

        //getting the values to save
        String name = fname;
        String date = Result;

        //getting current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy, H:mm a");
        String currentDate = df.format(calendar.getTime());

        //getting a unique id using push().getKey() method
        //it will create a unique id and we will use it as the Primary Key for our Letter
        String id = databaseAttendance.push().getKey();

        //creating an Letter Object
        Attendance attendance = new Attendance(id, name, date, currentDate);

        //Saving the Letter
        databaseAttendance.child(id).setValue(attendance);

        //displaying a success toast
        //Toast.makeText(this, "You have attend " + date + " training session!", Toast.LENGTH_LONG).show();
    }
}