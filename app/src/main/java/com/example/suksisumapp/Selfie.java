package com.example.suksisumapp;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Selfie extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie_layout);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = getIntent().getExtras();
        String Result = bundle.getString("result");

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    Selfie.this
            );

            builder.setTitle("Success!");
            //Set message
            builder.setMessage("You have attend " + Result + " training session!");
            //Set positive button

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Dismiss dialog
                    dialog.dismiss();
                    startActivity(new Intent(Selfie.this, QRActivity.class));
                    finish();
                }
            });

            //Show alert dialog
            builder.show();
        }
    }
}
