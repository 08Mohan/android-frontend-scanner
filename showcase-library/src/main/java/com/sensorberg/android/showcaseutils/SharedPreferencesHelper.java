package com.sensorberg.android.showcaseutils;

import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static final String VIBRATION_ON_NOTIFICATIONS = "com.sensorberg.vibration_on_notifications";
    public static final String FOREGROUND_NOTIFICATIONS = "com.sensorberg.foreground_notifications";
    public static final String SERVICE_DISABLED = "com.sensorberg.service disabled";
    private static final String LED_ON_NOTIFICATIONS = "com.sensorberg.led_on_notifications";

    private SharedPreferences preferences;

    public SharedPreferencesHelper(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean foreGroundNotificationsEnabled() {
        return preferences.getBoolean(FOREGROUND_NOTIFICATIONS, true);
    }

    public boolean vibrationOnNotificationsEnabled() {
        return preferences.getBoolean(VIBRATION_ON_NOTIFICATIONS, true);
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
        return preferences.getBoolean(SERVICE_DISABLED, false);
    }

    public void setServiceDisabled(boolean value) {
        saveValueForKey(value, SERVICE_DISABLED);
    }

    public boolean ledOnNotificationsEnabled() {
        return preferences.getBoolean(LED_ON_NOTIFICATIONS, true);
    }
}
