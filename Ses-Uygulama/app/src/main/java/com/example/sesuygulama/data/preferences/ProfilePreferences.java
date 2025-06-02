package com.example.sesuygulama.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfilePreferences {
    private static final String PREFS_NAME = "app_profile_prefs";
    private static final String KEY_ACTIVE_PROFILE_ID = "active_profile_id";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void setActiveProfileId(Context context, String profileId) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (profileId == null) {
            editor.remove(KEY_ACTIVE_PROFILE_ID);
        } else {
            editor.putString(KEY_ACTIVE_PROFILE_ID, profileId);
        }
        editor.apply();
    }

    public static String getActiveProfileId(Context context) {
        return getSharedPreferences(context).getString(KEY_ACTIVE_PROFILE_ID, null);
    }
    public static void clearActiveProfileId(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_ACTIVE_PROFILE_ID);
        editor.apply();
    }
}