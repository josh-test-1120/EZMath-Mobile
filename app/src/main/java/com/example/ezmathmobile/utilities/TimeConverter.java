package com.example.ezmathmobile.utilities;

import android.util.Log;

import com.example.ezmathmobile.models.Notification;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

    /**
     * This will find the most recent notification by date
     * @param notifications This is a List of notifications
     * @return an integer that specifies the list index of the notification
     */
    public static int findLatestDate(final List<Notification> notifications) {
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

    /**
     * This will convert a timestamp to string representation
     * @param timestamp a timestamp from firestore
     * @return string representation of the timestamp
     */
    public static String timestampToString(Timestamp timestamp) {
        String pattern = "yyyy-MM-dd HH:mm:ss";

        // Convert timestamp into date
        Date date = timestamp.toDate();
        // Convert Date to LocalDate
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());

        return localDate.toString();
    }

    /**
     * This will convert a string representation of firestore timestamp
     * to a timestamp object
     * @param timestampString the string representation of the timestamp
     * @return Timestamp object from the string
     */
    public static Timestamp stringToTimestamp(String timestampString) {
        try {
            String pattern = "yyyy-MM-dd HH:mm:ss";

            SimpleDateFormat formatter = new SimpleDateFormat(pattern);

            Date date = formatter.parse(timestampString);

            // Create a Timestamp object from the Date object
            return new Timestamp(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle parsing errors appropriately
        }
    }
}
