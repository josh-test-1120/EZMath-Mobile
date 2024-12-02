package com.example.ezmathmobile.models;

import androidx.annotation.NonNull;

import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;

/**
 * This is the Time class that wraps a Timestamp
 * so that toString can be used to show the proper string format
 */
public class Time {
    // Private variables
    private Timestamp time;

    /**
     * Default constructor for the Time class
     * @param time this is the timestamp to wrap and stringify
     */
    public Time(Timestamp time) {
        this.time = time;
    }

    /**
     * Getter for the timestamp
     * @return timestamp of date and time
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Setter for the timestamp
     * @param time timestamp of the date and time
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Override the toString method to return a Stringified result
     * of the time
     * @return
     */
    @NonNull
    @Override
    public String toString() {
        return TimeConverter.localizeTime(this.time);
    }
}
