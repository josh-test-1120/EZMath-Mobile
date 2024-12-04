package com.example.ezmathmobile.models;

import android.util.Log;
import android.widget.Toast;

import com.example.ezmathmobile.adaptors.ExamPageAdaptor;
import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * This is the Scheduled Exam object
 * Extends comparable to be used in sorting
 */
public class Scheduled implements Comparable, Serializable {
    // Private variables
    public String id, name, examid, userid;
    public Timestamp date;

    /**
     * Empty constructor for serialization
     */
    public Scheduled() {}

    /**
     * Default constructor for creating a new scheduled exam
     * @param name This is the name of the exam
     * @param examid This is the examID
     * @param userid This is the userID
     * @param date This the date and time the exam is scheduled
     */
    public Scheduled(String name, String examid, String userid, Timestamp date) {
        this.name = name;
        this.examid = examid;
        this.userid = userid;
        this.date = date;
    }

    /**
     * Getter for ID in Firestore
     * @return the ID of the scheduled exam
     */
    public String getId() { return id; }

    /**
     * Setter for the ID in Firestore
     * @param id string ID of the scheduled exam
     */
    public void setId(String id) { this.id = id; }

    /**
     * Getter for the exam name
     * @return string of the exam name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the exam name
     * @param name string of the exam name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the exam date and time
     * @return timestamp of the date and time
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * Setter for the exam date and time
     * @param date timestamp of the date and time
     */
    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * Getter for the examid
     * @return string of the underlying exam ID (not the scheduled exam ID)
     */
    public String getExamid() {
        return examid;
    }

    /**
     * Setter for the examid
     * @param examid string of the underlying exam ID (not the scheduled exam ID)
     */
    public void setExamid(String examid) {
        this.examid = examid;
    }

    /**
     * Getter for the userID
     * @return string of the user ID associated with scheduled exam
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Setter for the userID
     * @param userid string of the user ID associated with scheduled exam
     */
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

    /**
     * Update the Notification collection
     * This allows the model to control updates from individual instances
     * This allows notifications to be created or updated when exams are scheduled
     */
    private void updateNotifications() {
        // Initialize the database
        FirebaseFirestore database;
        database = FirebaseFirestore.getInstance();

        // Update the notifications table
        database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                .whereEqualTo(Constants.Notifications.KEY_SCHEDULED_USERID,userid)
                .whereEqualTo(Constants.Notifications.KEY_SCHEDULED_TYPEID,id)
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    // New record
                    if (queryDocuments.isEmpty()) {
                        HashMap<String, String> notification = new HashMap<>();
                        // Create the hash map for entry into databases
                        notification.put(Constants.Notifications.KEY_SCHEDULED_TYPE, "exam");
                        notification.put(Constants.Notifications.KEY_SCHEDULED_USERID, userid);
                        notification.put(Constants.Notifications.KEY_SCHEDULED_TYPEID, id);
                        // Create new record
                        database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                                .add(notification)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Scheduled -> Notification Update","complete");
                                })
                                .addOnFailureListener(exception -> {
                                    Log.d("Scheduled -> Notification Failures","exception: " + exception.toString());
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
                })
                .addOnFailureListener(exception -> {
                    Log.d("Scheduled -> Notification Failure","exception: " + exception.toString());
                });
    }

    /**
     * Update the Notification collection
     * This allows the model to control updates from individual instances
     * This allows notifications to be delete when scheduled exams are deleted
     */
    private void deleteNotifications() {
        // Initialize the database
        FirebaseFirestore database;
        database = FirebaseFirestore.getInstance();

        // Update the notifications table
        database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                .whereEqualTo(Constants.Notifications.KEY_SCHEDULED_USERID,userid)
                .whereEqualTo(Constants.Notifications.KEY_SCHEDULED_TYPEID,id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Scheduled -> Notification Delete","get notif ID");
                    // Get the notification ID
                    Notification notif = queryDocumentSnapshots.getDocuments().get(0).toObject(Notification.class);
                    notif.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                    // Now go delete it
                    database.collection(Constants.Notifications.KEY_COLLECTION_NOTIFICATION)
                            .document(notif.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Log.d("Scheduled -> Notification Delete","complete");
                            })
                            .addOnFailureListener(exception -> {
                                Log.d("Scheduled -> Notification Delete","exception: " + exception.toString());
                            });
                })
                .addOnFailureListener(exception -> {
                    Log.d("Scheduled -> Notification Delete","exception: " + exception.toString());
                });
    }

    /**
     * Synchronize the object into all dependent collections
     */
    public void syncCollections() {
        // Update the notifications collection
        updateNotifications();
    }

    /**
     * Unsynchronize the object into all dependent collections
     */
    public void unSyncCollections() {
        // Update the notifications collection
        deleteNotifications();
    }
}
