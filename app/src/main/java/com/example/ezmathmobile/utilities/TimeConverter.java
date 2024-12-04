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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This is the time converter class that handles export and import
 * of dates and times into a timestamp
 * multiplexor and demultiplexor for dates and times wrapped together inside timestamps
 */
public class TimeConverter {

    // Constants
    public static final String TimestampPattern = "yyyy-MM-dd HH:mm:ss"; // direct export
    public static final String AdjustedTimestampPattern = "MMM dd, yyyy hh:mm:ss a"; // custom export

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
     * This will convert a Firebase Timestamp into a localized date
     * @param timestamp this is the timestamp to convert to local date
     */
    public static String localizeDayOnly(final Timestamp timestamp) {
        // Convert timestamp into date
        Date date = timestamp.toDate();
        // Convert Date to LocalDate
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // Convert LocalDate to date String and format it
        String dateString = localDate.format(DateTimeFormatter.ofPattern("EEE dd").withLocale(Locale.US));
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
     * Must adhere to the following string pattern: "yyyy-MM-dd HH:mm:ss"
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

    /**
     * This will take in a custom pattern and parse a string of date
     * and time and create a timestamp from both sets of stringified results
     * @param date this is a string of date formatted
     * @param time this is a string of time formatted
     * @return a timestamp object that reflects the supplied date and time
     */
    public static Timestamp customStringToTimestamp(String date, String time) {
        // Combine the strings
        String combinedString = date + " " + time;
        // Try catch to handle exceptions from conversion
        try {
            // Setup the formatter for the conversion
            SimpleDateFormat formatter = new SimpleDateFormat(AdjustedTimestampPattern);
            // Create the new date object from the string
            Date dateObject = formatter.parse(combinedString);
            // Log the output
            Log.d("TimeConverter: String->Timestamp",new Timestamp(dateObject).toString());
            // return a Timestamp object from the Date object
            return new Timestamp(dateObject);
            // Handle the exceptions
        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing errors appropriately
            return null;
        }
    }

    /**
     * Take calendar information and turn it into a timestamp
     * that can be passed around the views and stored in the database
     * @param day an integer of the day of month
     * @param month and integer of the month of year
     * @param year an integer of the year in 4 digits
     * @return a timestamp object created from calendar information
     */
    public static Timestamp calendarInfoToTimestamp(int day, int month, int year) {
        // Initialize the calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        // Add the month and the day
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.YEAR, year);
        Date date = calendar.getTime();
        Log.d("TimeConverter: Calendar->Timestamp ",date.toString());
        return new Timestamp(date);
    }

    /**
     * Sort the list of Notifications to by timestamps
     * @param notifications this is a list of notifications
     * @return a LinkedHashMap that orders the notifications by month in sorted order
     */
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
                String month = localDate.getMonth().toString() + localDate.getYear();
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
        List<String> years2025 = new ArrayList<>();
        Collections.sort(monthsSorted);
        Log.d("TimeConverter: sortByMonth -> Year check", monthsSorted.toString());


        // Sort the collections and put them in a LinkedHashMap
        // to preserve order
        // Handle 2024
        for (String month : monthsSorted) {
            if (month.contains("2024")) {
                // Get the List from the Map
                groupedNotifications = monthlyNotifications.get(month);
                // Convert timestamp into date
                if (groupedNotifications != null) Collections.sort(groupedNotifications);
                // Sanitize the year from the month
                month = month.replace("2024", "");
                // Put the list back into the hash map
                sortedNotifications.put(month,groupedNotifications);
            }
            // Send to 2025 list
            else years2025.add(month);
        }
        // Sort the 2025 months in reverse order
        Collections.sort(years2025, Collections.reverseOrder());
        // Handle 2025
        for (String month : years2025) {
            if (month.contains("2025")) {
                // Get the List from the Map
                groupedNotifications = monthlyNotifications.get(month);
                Log.d("TimeConverter: sortByMonth -> Group notifs", groupedNotifications.toString());
                // Convert timestamp into date
                if (groupedNotifications != null) Collections.sort(groupedNotifications);
                // Sanitize the year from the month
                month = month.replace("2025", "");
                // Put the list back into the hash map
                sortedNotifications.put(month,groupedNotifications);
                //years2025.add(month);
            }
        }
        Log.d("TimeConverter: sortByMonth -> Final sorted", sortedNotifications.keySet().toString());

        // return the sorted HashMap
        return sortedNotifications;
    }

    /**
     * Sort the list of scheduled exams to by timestamps
     * @param exams this is a list of scheduled exams
     * @return a LinkedHashMap that orders the scheduled exams by month in sorted order
     */
    public static LinkedHashMap<String,List<Scheduled>> sortByMonthExams(final List<Scheduled> exams) {
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
                String month = localDate.getMonth().toString() + localDate.getYear();
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
        List<String> years2025 = new ArrayList<>();
        Collections.sort(monthsSorted);
        Log.d("TimeConverter: sortByMonthExams -> Year check", monthsSorted.toString());

        // Sort the collections and put them in a LinkedHashMap
        // to preserve order
        // Handle 2024
        for (String month : monthsSorted) {
            if (month.contains("2024")) {
                // Get the List from the Map
                groupedScheduled = monthlyScheduled.get(month);
                // Convert timestamp into date
                if (groupedScheduled != null) Collections.sort(groupedScheduled);
                // Sanitize the year from the month
                month = month.replace("2024", "");
                // Put the list back into the hash map
                sortedScheduled.put(month,groupedScheduled);
            }
            // Send to 2025 list
            else years2025.add(month);
        }
        // Sort the 2025 months in reverse order
        Collections.sort(years2025, Collections.reverseOrder());
        // Handle 2025
        for (String month : years2025) {
            if (month.contains("2025")) {
                // Get the List from the Map
                groupedScheduled = monthlyScheduled.get(month);
                Log.d("TimeConverter: sortByMonthExams -> Group notifs ", groupedScheduled.toString());
                // Convert timestamp into date
                if (groupedScheduled != null) Collections.sort(groupedScheduled);
                // Sanitize the year from the month
                month = month.replace("2025", "");
                // Put the list back into the hash map
                sortedScheduled.put(month,groupedScheduled);
                //years2025.add(month);
            }
        }
        Log.d("TimeConverter: sortByMonthExams -> Final sorted", sortedScheduled.keySet().toString());

        // return the sorted HashMap
        return sortedScheduled;
    }
}
