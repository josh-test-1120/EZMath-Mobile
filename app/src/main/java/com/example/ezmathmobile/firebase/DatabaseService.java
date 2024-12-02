package com.example.ezmathmobile.firebase;

import android.content.Intent;
import android.util.Log;

import com.example.ezmathmobile.activities.MainActivity;
import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Class for handling database interactions
 */
public class DatabaseService {
    // Instance variables
    private String dbName;
    private FirebaseFirestore database;
    public final List<DocumentSnapshot> documents = new LinkedList<>();

    /**
     * Default constructor
     * @param dbName this is the name of the database
     */
    public DatabaseService(String dbName) {
        this.dbName = dbName;
        // Initialize the database object
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Get the document by ID
     * @param id this is the string ID of the document
     * @return
     */
    public DocumentSnapshot getID(String id) {
        final DocumentSnapshot[] documentSnapshot = new DocumentSnapshot[1];
        //List<DocumentSnapshot> documents = new LinkedList<>();
        DocumentSnapshot documentSnapshot1 = null;
        int timeout = 0;
        // Get from firebase database
        database.collection(dbName).document(id)
                // Get the record
                .get()
                // If no error fetching
                .addOnCompleteListener(task -> {
                    //Log.d("This is the status of task: %s", Boolean.toString(task.isSuccessful()));
                    //Log.d("This is the status of task: %s", task.getResult().toString());
                    if (task.isSuccessful() && task.getResult() != null) {
                        //Log.d("Database record was found %s","True");
                        // Get the database document data
                        //documentSnapshot[0] = task.getResult();
                        documents.add(task.getResult());
                        Log.d("Inner Size:",Integer.toString(documents.size()));
                        //Log.d("Database:",documentSnapshot[0].toString());
                        //Log.d("Inner DB:",documents.get(0).toString());
                    }
                });
//        while (documents.size() < 1 && timeout < 500) {
//            timeout++;
//        }
//        Log.d("Wait",Integer.toString(timeout));
//        Log.d("Size:",Integer.toString(documents.size()));
        if (documentSnapshot[0] != null ) Log.d("Database:",documentSnapshot[0].toString());
        if (documents.size() > 0 ) Log.d("Outer DB:",documents.get(0).toString());
        return documentSnapshot[0];
    }
}
