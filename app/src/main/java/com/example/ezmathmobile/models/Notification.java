package com.example.ezmathmobile.models;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This is the Notification model for notification objects
 */
public class Notification {
    public String description, message, examName;
    public int id, studentID;
    public LocalTime examTime;
    public LocalDate examDate;

    /**
     * This is the Notification constructor
     * @param id The ID of the notification
     * @param studentID The studentID of the notification recipient
     * @param description The description of the notification
     * @param message The message of the notification
     * @param examName The message of the notification
     * @param examTime The message of the notification
     * @param examDate The message of the notification
     */
    public Notification(int id, int studentID, String description, String message, String examName, LocalTime examTime, LocalDate examDate) {
        this.id = id;
        this.studentID = studentID;
        this.description = description;
        this.message = message;
        this.examDate = examDate;
        this.examTime = examTime;
        this.examName = examName;
    }
}
