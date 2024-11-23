package com.example.ezmathmobile.utilities;

/**
 * Constants model
 */
public class Constants {

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

    public class Reminders {
        public static final String KEY_COLLECTION_REMINDERS = "Reminders";
        public static final String KEY_REMINDER_ID = "reminder_id";
        public static final String KEY_REMINDER_TEXT = "reminder_text";
        public static final String KEY_REMINDER_DATETIME = "reminder_datetime";
        public static final String KEY_REMINDER_TYPE = "reminder_type";
        public static final String KEY_REMINDER_EXAM_ID = "reminder_exam_id";
    }

    public class Exam {
        public static final String KEY_COLLECTION_EXAMS = "Exams";
        public static final String KEY_EXAM_ID = "exam_id";
        public static final String KEY_CLASS_ID = "class_id";
        public static final String KEY_TEST_DATE = "test_date";
        public static final String KEY_TEST_TIME = "test_time";
    }
}
