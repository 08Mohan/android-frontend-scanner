package com.sensorberg.android.showcase.Tracking;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;

public class FlurryTracking implements Tracking {

    private final Context context;
    private final String apiKey;

    public FlurryTracking(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    @Override
    public void logEvent(String name){
        FlurryAgent.logEvent(name);
    }

    @Override
    public void logEvent(String eventName, boolean checked) {
        FlurryAgent.logEvent(eventName, checked);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        FlurryAgent.onStartSession(this.context, this.apiKey);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        FlurryAgent.onEndSession(this.context);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
