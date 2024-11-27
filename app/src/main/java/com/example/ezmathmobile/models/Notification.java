package com.example.ezmathmobile.models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * This is the Notification model for notification objects
 */
public class Notification implements Comparable{
    public String description;
    public String message;
    public String examName;
    public String userid;
    public String type;
    public String typeid;
    public int id ;
    public Timestamp examDate;
//    public LocalTime examTime;
//    public LocalDate examDate;

    /**
     * This is the Notification constructor
     * @param id The ID of the notification
     * @param userid The user ID of the notification recipient
     * @param description The description of the notification
     * @param message The message of the notification
     * @param examName The message of the notification
//     * @param examTime The message of the notification
     * @param examDate The message of the notification
     */
    public Notification(int id, String userid, String description, String message,
                        String examName, String type, Timestamp examDate) {
        this.id = id;
        this.userid = userid;
        this.description = description;
        this.message = message;
        this.type = type;
        this.examDate = examDate;
//        this.examTime = examTime;
        this.examName = examName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Notification() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

//    public LocalTime getExamTime() {
//        return examTime;
//    }
//
//    public void setExamTime(LocalTime examTime) {
//        this.examTime = examTime;
//    }

    public Timestamp getExamDate() {
        return examDate;
    }

    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "description='" + description + '\'' +
                ", message='" + message + '\'' +
                ", examName='" + examName + '\'' +
                ", userid='" + userid + '\'' +
                ", type='" + type + '\'' +
                ", typeid='" + typeid + '\'' +
                ", id=" + id +
                ", examDate=" + examDate +
                '}';
    }

    /**
     * CompareTo override to implement comparable for sorting
     * @param o this is the other object to compare to
     * @return integer representing the comparison
     */
    @Override
    public int compareTo(Object o) {
        Date currentDate = this.getExamDate().toDate();
        Notification other = (Notification) o;
        Date otherDate = other.getExamDate().toDate();
        if (currentDate.equals(otherDate)) return 0;
        else if (currentDate.before(otherDate)) return 1;
        else return -1;
    }

    //    @ServerTimestamp
//    public Date getTimestamp() { return mTimestamp; }
//
//    public void setTimestamp(Date timestamp) { mTimestamp = timestamp; }
}
