package com.example.ezmathmobile.models;

import com.google.firebase.Timestamp;
import java.util.Date;

/**
 * This is the Notification model for notification objects
 */
public class Notification implements Comparable {
    // Private variables
    public String id, description, message, examName, userid, type, typeid ;
    public Timestamp examDate;

    /**
     * Empty constructor for serialization
     */
    public Notification() {}

    /**
     * This is the Notification constructor
     * @param id The ID of the notification
     * @param userid The user ID of the notification recipient
     * @param description The description of the notification
     * @param message The message of the notification
     * @param examName The message of the notification
     * @param examDate The message of the notification
     */
    public Notification(String id, String userid, String description, String message,
                        String examName, String type, Timestamp examDate) {
        this.id = id;
        this.userid = userid;
        this.description = description;
        this.message = message;
        this.type = type;
        this.examDate = examDate;
        this.examName = examName;
    }

    /**
     * Getter for the notification type
     * @return string of the notification type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for the notification type
     * @param type string of the notification type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the notification description
     * @return string of the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the notification description
     * @param description string of the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the notification message
     * @return string of the notification message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the notification message
     * @param message string of the notification message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the notification exam name
     * @return string of the exam name in the notification
     */
    public String getExamName() {
        return examName;
    }

    /**
     * Setter for the notification exam name
     * @param examName string of the exam name in the notification
     */
    public void setExamName(String examName) {
        this.examName = examName;
    }

    /**
     * Getter for the notification ID
     * @return string of the notification ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the notification ID
     * @param id string of the notification ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the notification userID
     * @return string of the notification userID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Setter for the notification userID
     * @param userid string of the notification userID
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * Getter for the notification typeID (ExamID)
     * @return string of the examID inside the notification
     */
    public String getTypeid() {
        return typeid;
    }

    /**
     * Setter for the notification typeID (ExamID)
     * @param typeid string of the examID inside the notification
     */
    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    /**
     * Getter for the notification Exam date and time
     * @return timestamp of the notification exam date and time
     */
    public Timestamp getExamDate() {
        return examDate;
    }

    /**
     * Setter for the notification Exam date and time
     * @param examDate timestamp of the notification exam date and time
     */
    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    /**
     * Override of the toString method
     * @return String of properties in the object
     */
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
}
