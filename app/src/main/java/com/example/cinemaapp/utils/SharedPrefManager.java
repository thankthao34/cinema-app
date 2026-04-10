package com.example.cinemaapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "moviemax_prefs";
    private final SharedPreferences preferences;

    public SharedPrefManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setOnboardingDone(boolean done) {
        preferences.edit().putBoolean("onboarding_done", done).apply();
    }

    public boolean isOnboardingDone() {
        return preferences.getBoolean("onboarding_done", false);
    }
}
