package com.example.suksisumapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail, editTextPassword;
    private EditText editTextFullname, editTextMatric;
    private EditText editTextStaffPassword;
    private View textInputLayoutStaffPassword;
    private Button buttonSignup;

    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private RadioGroup radioGroup;
    private RadioButton radioButton;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;
    private String userID;
    public String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.emailRegister);
        editTextPassword = (EditText) findViewById(R.id.passwordRegister);
        textViewSignin = (TextView) findViewById(R.id.loginText);

        editTextFullname = (EditText) findViewById(R.id.fullnameRegister);
        editTextMatric = (EditText) findViewById(R.id.matricNumberRegister);
        radioGroup = findViewById(R.id.radiogp);
        editTextStaffPassword = (EditText) findViewById(R.id.passwordStaffRegister);
        editTextStaffPassword.setVisibility(View.GONE);
        textInputLayoutStaffPassword = findViewById(R.id.textInputLayoutStaffPassword);
        textInputLayoutStaffPassword.setVisibility(View.GONE);

        buttonSignup = (Button) findViewById(R.id.buttonSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        String fullname = editTextFullname.getText().toString().trim();
        String matric = editTextMatric.getText().toString().trim();
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);
        String user_type = radioButton.getText().toString().trim();
        String staff_pass = editTextStaffPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }


        if(TextUtils.isEmpty(fullname)){
            Toast.makeText(this,"Please enter fullname",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(matric)){
            Toast.makeText(this,"Please enter matric number",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(user_type)){
            Toast.makeText(this,"Please select user type",Toast.LENGTH_LONG).show();
            return;
        }

        if(user_type.equals("Staff") && staff_pass.equals("jurulatih123")){
        }else if(user_type.equals("Student")){
        } else {
            Toast.makeText(this,"Staff code is wrong!",Toast.LENGTH_LONG).show();
            return;
        }


        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Registration Success",Toast.LENGTH_LONG).show();
                            finish();

                            //try
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fullname",fullname);
                            user.put("user_type",user_type);
                            user.put("email", email);
                            user.put("matric",matric);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });


                            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                        }else{
                            //display some message here
                            Toast.makeText(RegisterActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, LogInActivity.class));
        }

    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        if(String.valueOf(radioButton.getText()).equals("Staff")){
            editTextStaffPassword.setVisibility(View.VISIBLE);
            textInputLayoutStaffPassword.setVisibility(View.VISIBLE);
        }
        if(String.valueOf(radioButton.getText()).equals("Student")){
            editTextStaffPassword.setVisibility(View.GONE);
            textInputLayoutStaffPassword.setVisibility(View.GONE);
        }

        Toast.makeText(this,  String.valueOf(radioButton.getText()) + " selected",
                Toast.LENGTH_SHORT).show();
    }
}