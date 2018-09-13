package com.example.vehicleparkingsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import static com.example.vehicleparkingsystem.utils.PreferencesUtility.LOGGED_IN_ID;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.LOGGED_IN_PREF;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.PARKING_LOCATION;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.START_TIME;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.START_TIME_PREF;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.START_TIME_STRING;

public class SaveSharedPreference {
    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setLoggedInId(Context context, String id){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(LOGGED_IN_ID, id);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getLoggedId(Context context){
        return getPreferences(context).getString(LOGGED_IN_ID, "-1");
    }

    public static void setStartTimeExists(Context context, boolean status) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(START_TIME_PREF, status);
        editor.apply();
    }

    public static void setStartTime(Context context, long startTime){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(START_TIME, startTime);
        editor.apply();
    }

    public static boolean getStartTimeExists(Context context) {
        return getPreferences(context).getBoolean(START_TIME_PREF, false);
    }

    public static long getStartTime(Context context){
        long startTime = SystemClock.uptimeMillis();

        return getPreferences(context).getLong(START_TIME, startTime);
    }

    public static void setStartTime(Context context, String startTime){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(START_TIME_STRING, startTime);
        editor.apply();
    }

    public static String getStartTimeString(Context context){
        return getPreferences(context).getString(START_TIME_STRING, "00:00 a.m.");
    }

    public static void setParkingLocation(Context context, String location){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(PARKING_LOCATION, location);
        editor.apply();
    }

    public static String getParkingLocation(Context context){
        return getPreferences(context).getString(PARKING_LOCATION, "");
    }
}
