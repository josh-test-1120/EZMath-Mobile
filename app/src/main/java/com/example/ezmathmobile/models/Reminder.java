/**
 * Reminder object class for containing relevant field information to display in the block.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;

/**
 * This is the reminder class
 */
public class Reminder {
    /**
     * Fields of the Reminder object class.
     */
    //Reminder text.
    public String text;
    //Reminder type which includes test schedule confirmation,
    // initial day's test number notice, and warning 15 minutes before test.
    // Green -- 1
    // Red   -- 2
    // Blue  -- 3
    public String type;

    // Date
    public Timestamp date;

    /**
     * Empty constructor for serialization
     */
    public Reminder() {}

    /**
     * Constructor for the Reminder object
     * @param text String text to display
     * @param type String type of reminder
     * @param date Google Date type date of the reminder's creation.
     */
    public Reminder(String text, String type, Timestamp date) {
        this.text = text;
        this.type = type;
        this.date = date;
    }
}
