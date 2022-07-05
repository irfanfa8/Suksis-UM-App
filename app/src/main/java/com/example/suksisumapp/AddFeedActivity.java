package com.example.suksisumapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddFeedActivity extends AppCompatActivity
{
    Button btn_post, btn_cancel;
    int Hour, Minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        final EditText et_feed_title = findViewById(R.id.et_feed_title);
        final Button et_feed_date = findViewById(R.id.buttonDateFeed);
        final Button et_feed_time = findViewById(R.id.buttonTimeFeed);
        final EditText et_feed_note = findViewById(R.id.et_feed_note);
        btn_post = findViewById(R.id.btn_post);
        btn_cancel = findViewById(R.id.btn_cancel);

        //date selector
        et_feed_date.setOnClickListener(new View.OnClickListener() {
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
                        et_feed_date.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(AddFeedActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //time selector
        et_feed_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddFeedActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Hour = hourOfDay;
                                Minute = minute;

                                String time = Hour + ":" + Minute;

                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24Hours.parse(time);
                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                            "HH:mm"
                                    );
                                    et_feed_time.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(Hour, Minute);
                timePickerDialog.show();
            }
        });

        btn_cancel.setOnClickListener(v->
        {
            Intent intent = new Intent(AddFeedActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        DAOFeeds dao =new DAOFeeds();
        Feeds feed_edit = (Feeds)getIntent().getSerializableExtra("EDIT");

        if(feed_edit !=null)
        {
            btn_post.setText("Update");
            et_feed_title.setText(feed_edit.getFeedTitle());
            et_feed_date.setText(feed_edit.getFeedDate());
            et_feed_time.setText(feed_edit.getFeedTime());
            et_feed_note.setText(feed_edit.getFeedNote());
            btn_cancel.setVisibility(View.GONE);
        }
        else
        {
            btn_post.setText("Submit");
            btn_cancel.setVisibility(View.VISIBLE);
        }

        btn_post.setOnClickListener(v->
        {
            //getting the values to save
            String feed_title = et_feed_title.getText().toString().trim();
            String feed_date = et_feed_date.getText().toString().trim();
            String feed_time = et_feed_time.getText().toString().trim();
            String feed_note = et_feed_note.getText().toString().trim();

            //getting current date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy, H:mm a");
            String date = df.format(calendar.getTime());

            //creating an Feed Object
            Feeds feeds = new Feeds(feed_title, feed_date, feed_time, feed_note, date);

            //add data
            if(feed_edit==null) {

                //checking if the value is provided
                if (!feed_title.equals("") && !feed_date.equals("Date") && !feed_time.equals("Time") && !feed_note.equals("")) {
                    dao.add(feeds).addOnSuccessListener(suc ->
                    {
                        Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddFeedActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }).addOnFailureListener(er ->
                    {
                        Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    //if the value is not given displaying a toast
                    Toast.makeText(this, "Please fill all the detail", Toast.LENGTH_LONG).show();
                }
            }

            //update data
            else
            {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("feedTitle", feed_title);
                hashMap.put("feedDate", feed_date);
                hashMap.put("feedTime", feed_time);
                hashMap.put("feedNote", feed_note);
                hashMap.put("Date", date);
                dao.update(feed_edit.getKey(), hashMap).addOnSuccessListener(suc ->
                {
                    Toast.makeText(this, "Post is updated", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(er ->
                {
                    Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}