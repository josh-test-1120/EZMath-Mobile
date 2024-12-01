package com.example.ezmathmobile.models;

import androidx.annotation.NonNull;

import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;

public class Time {
    private Timestamp time;

    public Time(Timestamp time) {
        this.time = time;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        return TimeConverter.localizeTime(this.time);
    }
}
