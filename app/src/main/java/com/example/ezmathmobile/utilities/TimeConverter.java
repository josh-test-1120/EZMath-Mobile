package com.example.ezmathmobile.utilities;

import android.util.Log;

import com.example.ezmathmobile.models.Notification;
import com.example.ezmathmobile.models.Scheduled;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimeConverter {

    // Constants
    public static final String TimestampPattern = "yyyy-MM-dd HH:mm:ss";
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
        Log.d("TimeConverter: Timestamp->Date",dateString);
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
        Log.d("TimeConverter: Timestamp->Date",timeString);
        return timeString;
    }

    /**
     * This will find the most recent notification by date
     * @param notifications This is a List of notifications
     * @return an integer that specifies the list index of the notification
     */
    public static int findClosestDate(final List<Notification> notifications) {
        // Variables
        int index = -1;
        Date currentLatest = null, latest = null;

        for (int x = 0; x < notifications.size(); x++) {
            // Convert timestamp into date
            Date date = notifications.get(x).examDate.toDate();
            if (currentLatest == null) {
                currentLatest = date;
                index = x;
            }
            else if (date.before(currentLatest) && date.after(new Date())) {
                currentLatest = date;
                index = x;
            }
        }
        return index;
    }

    /**
     * This will find the most recent notification by date
     * @param notifications This is a List of notifications
     * @return an integer that specifies the list index of the notification
     */
    public static int findLatestDate(final List<Notification> notifications) {
        // Variables
        int index = -1;
        LocalDate currentLatest = null, latest = null;

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
        // Convert timestamp into date
        Date date = timestamp.toDate();
        // Setup the formatter for the conversion
        SimpleDateFormat formatter = new SimpleDateFormat(TimestampPattern);
        // Convert the object to a string
        String timestampString = formatter.format(date);
        // Log the output
        Log.d("TimeConverter: Timestamp->String",timestampString);
        // Return the string
        return timestampString;
    }

    /**
     * This will convert a string representation of firestore timestamp
     * to a timestamp object
     * @param timestampString the string representation of the timestamp
     * @return Timestamp object from the string
     */
    public static Timestamp stringToTimestamp(String timestampString) {
        // Try catch to handle exceptions from conversion
        try {
            // Setup the formatter for the conversion
            SimpleDateFormat formatter = new SimpleDateFormat(TimestampPattern);
            // Create the new date object from the string
            Date date = formatter.parse(timestampString);
            // Log the output
            Log.d("TimeConverter: String->Timestamp",new Timestamp(date).toString());
            // return a Timestamp object from the Date object
            return new Timestamp(date);
        // Handle the exceptions
        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing errors appropriately
            return null;
        }
    }

    public static LinkedHashMap<String,List<Notification>> sortByMonth(final List<Notification> notifications) {
        // Variables
        HashMap<String,List<Notification>> monthlyNotifications = new HashMap<>();
        LinkedHashMap<String,List<Notification>> sortedNotifications = new LinkedHashMap<>();
        List<Notification> groupedNotifications;

        for (int x = 0; x < notifications.size(); x++) {
            // Convert timestamp into date
            Date date = notifications.get(x).examDate.toDate();
            // Check for date after current date
            if (date.after(new Date())) {
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
        }
        // Get the months and sort them
        Set<String> months = monthlyNotifications.keySet();
        List<String> monthsSorted = new ArrayList<>(months);
        Collections.sort(monthsSorted);

        // Sort the collections and put them in a LinkedHashMap
        // to preserve order
        for (String month : monthsSorted) {
            // Get the List from the Map
            groupedNotifications = monthlyNotifications.get(month);
            // Convert timestamp into date
            if (groupedNotifications != null) Collections.sort(groupedNotifications, Collections.reverseOrder());
            // Put the list back into the hash map
            sortedNotifications.put(month,groupedNotifications);
        }
        // return the sorted HashMap
        return sortedNotifications;
    }

    public static <T> LinkedHashMap<String,List<Scheduled>> sortByMonthExams(final List<Scheduled> exams) {
        // Variables
        HashMap<String,List<Scheduled>> monthlyScheduled = new HashMap<>();
        LinkedHashMap<String,List<Scheduled>> sortedScheduled = new LinkedHashMap<>();
        List<Scheduled> groupedScheduled;

        for (int x = 0; x < exams.size(); x++) {
            // Convert timestamp into date
            Date date = exams.get(x).getDate().toDate();
            // Check for date after current date
            if (date.after(new Date())) {
                // Convert Date to LocalDate
                LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
                // Get the integer month
                String month = localDate.getMonth().toString();
                // Get the List from the Map
                groupedScheduled = monthlyScheduled.get(month);
                // Ensure the list exists
                if (groupedScheduled == null || groupedScheduled.size() == 0)
                    groupedScheduled = new ArrayList<>();
                // Add the notification to the month list
                groupedScheduled.add(exams.get(x));
                // Put the list back into the hash map
                monthlyScheduled.put(month,groupedScheduled);
            }
        }
        // Get the months and sort them
        Set<String> months = monthlyScheduled.keySet();
        List<String> monthsSorted = new ArrayList<>(months);
        Collections.sort(monthsSorted);

        // Sort the collections and put them in a LinkedHashMap
        // to preserve order
        for (String month : monthsSorted) {
            // Get the List from the Map
            groupedScheduled = monthlyScheduled.get(month);
            // Convert timestamp into date
            if (groupedScheduled != null) Collections.sort(groupedScheduled, Collections.reverseOrder());
            // Put the list back into the hash map
            sortedScheduled.put(month,groupedScheduled);
        }
        // return the sorted HashMap
        return sortedScheduled;
    }
}
