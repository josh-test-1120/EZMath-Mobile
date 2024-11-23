package com.example.ezmathmobile.utilities;

import android.util.Log;

import com.example.ezmathmobile.models.Notification;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class TimeConverter {

    /**
     * This will convert a Firebase Timestamp into a localized date
     * @param timestamp this is the timestamp to convert to local date
     */
    public static String localizeDate(final Timestamp timestamp) {
        // Convert timestamp into date
        Date date = timestamp.toDate();
        // Convert Date to LocalDate
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // Convert LocalDate to date String and format it
        String dateString = localDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US));
        Log.d("Timestamp->Date",dateString);
        return dateString;
    }

    /**
     * This will convert a Firebase Timestamp into a localized time
     * @param timestamp this is the timestamp to convert to local time
     */
    public static String localizeTime(final Timestamp timestamp) {
        // Convert timestamp into date
        Date date = timestamp.toDate();
        // Convert Date to LocalTime
        LocalTime localTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
        // Covert LocalTime to time String and format it
        String timeString = localTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        Log.d("Timestamp->Date",timeString);
        return timeString;
    }

    public static int findLatestDate(final List<Notification> notifications) {
        // Variables
        int index = -1;
        LocalDate currentLatest = null;

        for (int x = 0; x < notifications.size(); x++) {
            // Convert timestamp into date
            Date date = notifications.get(x).examDate.toDate();
            // Convert Date to LocalDate
            LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
            if (currentLatest == null) {
                currentLatest = localDate;
                index = x;
            }
            else if (localDate.isAfter(currentLatest)) {
                currentLatest = localDate;
                index = x;
            }
        }
        return index;
    }

    public static HashMap<String,List<Notification>> sortByMonth(final List<Notification> notifications) {
        // Variables
        HashMap<String,List<Notification>> monthlyNotifications = new HashMap<>();
        List<Notification> groupedNotifications;

        for (int x = 0; x < notifications.size(); x++) {
            // Convert timestamp into date
            Date date = notifications.get(x).examDate.toDate();
            // Convert Date to LocalDate
            LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
            // Get the integer month
            String month = localDate.getMonth().toString();
            // Get the List from the Map
            groupedNotifications = monthlyNotifications.get(month);
            // Ensure the list exists
            if (groupedNotifications == null || groupedNotifications.size() == 0)
                groupedNotifications = new ArrayList<>();
            // Add the notification to the month list
            groupedNotifications.add(notifications.get(x));
            // Put the list back into the hash map
            monthlyNotifications.put(month,groupedNotifications);
        }
        return monthlyNotifications;
    }
}
