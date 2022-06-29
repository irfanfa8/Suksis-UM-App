package com.example.suksisumapp;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;

public class FeedVH extends RecyclerView.ViewHolder
{
    public TextView feed_title;
    public TextView feed_date;
    public TextView feed_time;
    public TextView feed_note;
    public TextView date;
    public TextView txt_option;
    public FeedVH(@NonNull View itemView)
    {
        super(itemView);

        feed_title = itemView.findViewById(R.id.feed_title);
        feed_date = itemView.findViewById(R.id.feed_date);
        feed_time = itemView.findViewById(R.id.feed_time);
        feed_note = itemView.findViewById(R.id.feed_note);
        date = itemView.findViewById(R.id.date);
        txt_option = itemView.findViewById(R.id.txt_option);
        if (HomeActivity.textViewUserType.getText().toString().compareTo("Student") == 0){
            txt_option.setVisibility(View.GONE);
        }

    }
}
