package com.example.vehicleparkingsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.example.vehicleparkingsystem.utils.PreferencesUtility.LOGGED_IN_ID;
import static com.example.vehicleparkingsystem.utils.PreferencesUtility.LOGGED_IN_PREF;

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
}
