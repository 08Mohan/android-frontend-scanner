package com.sensorberg.android.showcase.Tracking;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;

public interface Tracking extends Application.ActivityLifecycleCallbacks {

    public static final Tracking NONE = new Tracking() {
        @Override
        public void logEvent(String name) {

        }

        @Override
        public void logEvent(String eventName, boolean checked) {

        }

        @Override
        public void logFragmentDisplay(Class<? extends Fragment> aClass) {

        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    void logEvent(String name);

    void logEvent(String eventName, boolean checked);

    void logFragmentDisplay(Class<? extends Fragment> aClass);
}
