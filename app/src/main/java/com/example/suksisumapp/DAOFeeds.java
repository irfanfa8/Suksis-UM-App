package com.example.suksisumapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOFeeds {

    private DatabaseReference databaseReference;
    public DAOFeeds()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Feeds.class.getSimpleName());
    }

    //add data
    public Task<Void> add(Feeds feeds)
    {
        return databaseReference.push().setValue(feeds);
    }

    //update data
    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    //remove data
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {
            return databaseReference.orderByKey();
        }
        return databaseReference.orderByKey().startAfter(key);
    }

    public Query get()
    {
        return databaseReference;
    }
}

