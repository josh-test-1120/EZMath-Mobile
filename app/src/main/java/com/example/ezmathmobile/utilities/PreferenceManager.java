package com.example.ezmathmobile.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Manager that will store preferences
 * about the user that can be checked while the program
 * is running
 */
public class PreferenceManager {
    // Instance variables
    private final SharedPreferences sharedPreferences;

    /**
     * This is the constructor
     * @param context this is the current Context to get preference data from
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.User.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    /**
     * Put a boolean value into the preference hash
     * @param key this is string index
     * @param value this is boolean to assign to the index
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    /**
     * Get a boolean value from the preference hash
     * @param key this is string index to get
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }

    /**
     * Put a string value into the preference hash
     * @param key this is string index
     * @param value this is string to assign to the index
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    /**
     * Get a string value from the preference hash
     * @param key this is string index
     */
    public String getString(String key) {
        return sharedPreferences.getString(key,null);
    }

    /**
     * Clear the preferences hash of all indexes
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
