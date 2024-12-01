package com.example.ezmathmobile.models;

import android.util.Log;

import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;

public class Scheduled implements Comparable {
    public String id, name, examid, userid;
    public Timestamp date;

    public Scheduled() {}

    public Scheduled(String name, String examid, String userid, Timestamp date) {
        this.name = name;
        this.examid = examid;
        this.userid = userid;
        this.date = date;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getExamid() {
        return examid;
    }

    public void setExamid(String examid) {
        this.examid = examid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * CompareTo override to implement comparable for sorting
     * @param o this is the other object to compare to
     * @return integer representing the comparison
     */
    @Override
    public int compareTo(Object o) {
        Date currentDate = this.getDate().toDate();
        Scheduled other = (Scheduled) o;
        Date otherDate = other.getDate().toDate();
        if (currentDate.equals(otherDate)) return 0;
        else if (currentDate.before(otherDate)) return 1;
        else return -1;
    }

    private void updateNotifications() {
        // Initialize the database
        FirebaseFirestore database;
        database = FirebaseFirestore.getInstance();

        // Update the notifications table
        database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                .whereEqualTo(Constants.Notifications.KEY_SCHEDULED_TYPEID,examid)
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    // New record
                    if (queryDocuments.isEmpty()) {
                        HashMap<String, String> notification = new HashMap<>();
                        // Create the hash map for entry into databases
                        notification.put(Constants.Notifications.KEY_SCHEDULED_TYPE, "exam");
                        notification.put(Constants.Notifications.KEY_SCHEDULED_USERID, userid);
                        notification.put(Constants.Notifications.KEY_SCHEDULED_TYPEID, examid);
                        // Create new record
                        database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                                .add(notification)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Shceduled -> Notification Update","complete");
                                })
                                .addOnFailureListener(exception -> {
                                    Log.d("Shceduled -> Notification Update","exception: " + exception.toString());
                                });
                    }
                    // Existing record
                    else {
                        // Get the information from the database and update the record
                        Notification notification = queryDocuments.getDocuments().get(0).toObject(Notification.class);
                        notification.setType("exam");
                        notification.setTypeid(examid);
                        notification.setUserid(userid);
                    }
                });
    }

    public void updateReminders() {

    }

    public void syncCollections() {
        // Update the notifications collection
        updateNotifications();
        // Update the reminders table
        updateReminders();
    }
}
