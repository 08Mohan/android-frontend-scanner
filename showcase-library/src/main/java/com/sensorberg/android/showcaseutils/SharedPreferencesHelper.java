package com.sensorberg.android.showcaseutils;

import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static final String DEMO_ACCOUNT_API_KEY = "4b99af9f0edf71b1e40690f29a429a80da427122bb6285f58cf79285818666e3";
    private static final String DEFAULT_API_KEY = DEMO_ACCOUNT_API_KEY;
    public static final String API_KEY = "com.sensorberg.apiKey";
    public static final String VIBRATION_ON_NOTIFICATIONS = "com.sensorberg.vibration_on_notifications";
    public static final String FOREGROUND_NOTIFICATIONS = "com.sensorberg.foreground_notifications";
    public static final String SERVICE_DISABLED = "com.sensorberg.service disabled";
    private static final String LED_ON_NOTIFICATIONS = "com.sensorberg.led_on_notifications";

    private SharedPreferences preferences;

    public SharedPreferencesHelper(SharedPreferences preferences){
        this.preferences = preferences;
    }

    public void setAPIKey(String APIKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_KEY, APIKey);
        editor.commit();
    }

    public String getAPIKey() {
        return preferences.getString(API_KEY, DEFAULT_API_KEY);
    }

    public boolean foreGroundNotificationsEnabled() {
        return preferences.getBoolean(FOREGROUND_NOTIFICATIONS, false);
    }

    public boolean vibrationOnNotificationsEnabled() {
        return preferences.getBoolean(VIBRATION_ON_NOTIFICATIONS, false);
    }

    public void setEnableVibrationOnNotifications(boolean value) {
        saveValueForKey(value, VIBRATION_ON_NOTIFICATIONS);
    }

    public void setForegroundNotificationEnabled(boolean value) {
        saveValueForKey(value, FOREGROUND_NOTIFICATIONS);
    }

    public void setLedOnNotificationsEnabled(boolean value) {
        saveValueForKey(value, LED_ON_NOTIFICATIONS);
    }


    private void saveValueForKey(boolean value, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean isServiceDisabled() {
        return preferences.getBoolean(SERVICE_DISABLED, true);
    }

    public void setServiceDisabled(boolean value){
        saveValueForKey(value,SERVICE_DISABLED );
    }

    public boolean ledOnNotificationsEnabled() {
        return preferences.getBoolean(LED_ON_NOTIFICATIONS, false);
    }
}
