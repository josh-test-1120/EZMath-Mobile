package com.example.ezmathmobile.utilities;

/**
 * Constants model
 */
public class Constants {

    /**
     * These are the constants for the User Collections
     */
    public class User {
        // Hash keys for database contracts
        public static final String KEY_COLLECTION_USERS = "Users";
        public static final String KEY_FIRSTNAME = "first_name";
        public static final String KEY_LASTNAME = "last_name";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_USERID = "userid";
        public static final String KEY_IS_SIGNED_IN = "isSignedIn";
        public static final String KEY_PREFERENCE_NAME = "EzMathMobilePreference";
        public static final String KEY_IMAGE = "image";
    }

    /**
     * These are the constants for the User Collections
     */
    public class Reminders {
        public static final String KEY_COLLECTION_REMINDERS = "Reminders";
        public static final String KEY_REMINDER_ID = "reminder_id";
        public static final String KEY_REMINDER_TEXT = "reminder_text";
        public static final String KEY_REMINDER_DATETIME = "reminder_datetime";
        public static final String KEY_REMINDER_TYPE = "reminder_type";
        public static final String KEY_REMINDER_EXAM_ID = "reminder_exam_id";
    }

    /**
     * These are the constants for the Exam Collections
     */
    public class Exam {
        public static final String KEY_COLLECTION_EXAMS = "Exams";
        public static final String KEY_CLASS_ID = "classid";
        public static final String KEY_TEST_TIME = "times";
        public static final String KEY_TEST_NAME = "name";
    }

    /**
     * These are the constants for the Scheduled Collections
     */
    public class Scheduled {
        public static final String KEY_COLLECTION_SCHEDULED = "Scheduled";
        public static final String KEY_SCHEDULED_DATE = "date";
        public static final String KEY_SCHEDULED_TIME = "time";
        public static final String KEY_SCHEDULED_EXAMID = "examid";
        public static final String KEY_SCHEDULED_USERID = "userid";
        public static final String KEY_SCHEDULED_SCHEDULEID = "id";
    }

    /**
     * These are the constants for the Notifications Collections
     */
    public class Notifications {
        public static final String KEY_COLLECTION_NOTIFICATION = "Notifications";
        public static final String KEY_SCHEDULED_TYPE = "type";
        public static final String KEY_SCHEDULED_TYPEID = "typeid";
        public static final String KEY_SCHEDULED_USERID = "userid";
        public static final String KEY_SCHEDULED_NOTIFICATIONID = "id";
    }
}
