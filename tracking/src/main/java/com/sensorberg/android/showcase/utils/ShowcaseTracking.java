package com.sensorberg.android.showcase.utils;

import android.app.Fragment;
import android.content.Context;

import com.sensorberg.android.showcase.Tracking.FlurryTracking;
import com.sensorberg.android.showcase.fragments.ShowcaseBeaconScanFragment;

import java.util.HashMap;
import java.util.Map;

public class ShowcaseTracking extends FlurryTracking {



    public static final String BEACON_EVENT_RESOLVED = "Beacon-Event resolved";
    public static final String BEACON_EVENT_DETECTED = "Beacon-Event detected";

    public static final String BEACON_EVENT_WEBSITE_PRESENTED = "Beacon-Event-Website presented";

    public static final String LISTED_DETECTED_BEACONS = "Listed detected Beacons";

    public static final String API_KEY_QR_CODE_SCANNED = "API-Key QRCode scanned";
    public static final String API_KEY_QR_CODE_SCANING_STARTED = "QRCode scanning";

    public static final String API_KEY_CUSTOMIZED = "API-Key customized";

    public static final String DEMO_API_KEY_ENABLED = "Demo-API-Key enabled";
    public static final String CUSTOM_API_KEY_ENABLED = "Custom API-Key enabled";
    public static final String VIRTUAL_BEACON_ENABLED = "Virtual Beacon enabled";
    public static final String VIRTUAL_BEACON_DISABLED = "Virtual Beacon disabled";

    public static final String IBEACON_NOTIFICATIONS_ENABLED = "iBeacon notifications enabled";
    public static final String IBEACON_NOIFICATIONS_DISABLED = "iBeacon notifications disabled";

    public static Map<Class, String> keys = new HashMap<>();

    static {
        keys.put(ShowcaseBeaconScanFragment.class, LISTED_DETECTED_BEACONS);
    }

    public ShowcaseTracking(Context context, String apiKey) {
        super(context, apiKey);
    }

    @Override
    public void logFragmentDisplay(Class<? extends Fragment> aClass) {
        String resolvedValue = keys.get(aClass);
        if (resolvedValue != null) {
            logEvent(resolvedValue);
        } else {
            super.logFragmentDisplay(aClass);
        }
    }
}
