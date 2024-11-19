/**
 * This is a Model ReminderDateBlock that contains a List of Reminder object and date header text.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.models;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReminderDateBlock {
    /**
     * ReminderDateBlock fields that includes List of reminders and their corresponding date
     */
    // Reminder List
    public List<Reminder> reminderList;

    // Created date of the reminder
    public String date;

    /**
     * Constructor for the ReminderDateBlock
     * @param reminderlist Arraylist of reminders
     * @param date Date of the reminders
     */
    public ReminderDateBlock(List<Reminder> reminderlist, String date) {
        this.reminderList = reminderlist;
        this.date = date;
    }

}
