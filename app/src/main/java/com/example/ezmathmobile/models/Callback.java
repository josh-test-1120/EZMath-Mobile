package com.example.ezmathmobile.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

public class Callback implements Runnable {
    public DocumentSnapshot document;

    @Override
    public void run() {
        User user =  document.toObject(User.class);
        Log.d("User:",user.toString());
    }
}
