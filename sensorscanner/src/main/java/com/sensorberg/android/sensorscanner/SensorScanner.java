package com.sensorberg.android.sensorscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sensorberg.android.sensorscanner.nameProvider.NameProvider;
import com.sensorberg.sdk.Constants;
import com.sensorberg.sdk.cluster.BeaconId;
import com.sensorberg.sdk.internal.AndroidPlattform;
import com.sensorberg.sdk.internal.Plattform;
import com.sensorberg.sdk.scanner.EventEntry;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;
import com.sensorberg.sdk.scanner.Scanner;
import com.sensorberg.sdk.scanner.ScannerListener;
import com.sensorberg.sdk.settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SensorScanner implements ScannerListener, Scanner.RssiListener {
    private static final String TAG = SensorScanner.class.getSimpleName();


    private static final int MSG_NOTIFY_LISTENER = 1;

    private List<BeaconFilter> beaconFilters = Collections.emptyList();
    private List<NameProvider> nameProviders = Collections.emptyList();
    private Listener listener = NONE;
    private final ResponderHandler handler;
    private SortedSet<BeaconScanObject> storage = new TreeSet<>(BeaconSorter.ALPHABETICAL);

    private final Scanner scanner;


    private static class ResponderHandler extends android.os.Handler {
        private final SensorScanner mResponder;

        ResponderHandler(SensorScanner mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what){
            case MSG_NOTIFY_LISTENER:
                listener.updateUI(storage);
                break;
            default:{
                Log.e(TAG, "unknown message " + msg.what);
            }
        }
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        if (listener == null) listener = NONE;
        this.listener = listener;
    }

    public SensorScanner(Context context){
        Plattform platform = new AndroidPlattform(context);
        Settings settings = new MySettings(platform, platform.getSettingsSharedPrefs());

        scanner = new Scanner(settings, platform);
        handler = new ResponderHandler(this, Looper.myLooper());
    }

    public void start(){
        scanner.hostApplicationInForeground();
        scanner.addScannerListener(this);
        scanner.setRssiListener(this);
        scanner.start();
    }

    public void pause(){

    }

    public void clearCache(){
        scanner.clearCache();
    }


    public void stop(){
        scanner.removeScannerListener(this);
        scanner.setRssiListener(null);
        scanner.stop();
    }

    @Override
    public void onScanEventDetected(ScanEvent event) {
        if (event.getEventMask() == ScanEventType.ENTRY.getMask()){
            for (BeaconFilter beaconFilter : beaconFilters) {
                if(beaconFilter.filter(event.getBeaconId())){
                    return;
                }
            }
            BeaconName name = new BeaconName();
            for (NameProvider nameProvider : nameProviders) {
                if (!nameProvider.provideName(event.getBeaconId(), name)){
                    break;
                }
            }
            storage.add(new BeaconScanObject(event.getBeaconId(), name));
        } else {
            storage.remove(new BeaconScanObject(event.getBeaconId(), null));
        }
        updateUI();
    }

    @Override
    public void onScanFailed(Throwable cause) {
        storage.clear();
        updateUI();
    }

    private void updateUI() {
        handler.sendEmptyMessage(MSG_NOTIFY_LISTENER);
    }

    @Override
    public void onScanStopped() {
        storage.clear();
        updateUI();
    }

    @Override
    public void onScanStarted() {

    }

    @Override
    public void onRssiChanged(BeaconId beaconId, EventEntry eventEntry) {

    }

    public SensorScanner addNameProvider(NameProvider nameProvider){
        if (nameProviders.size() == 0){
            nameProviders = new ArrayList<>();
        }
        nameProviders.add(nameProvider);
        return this;
    }

    public SensorScanner addFilter(BeaconFilter filter){
        if(beaconFilters.size() == 0){
            beaconFilters = new ArrayList<>();
        }
        beaconFilters.add(filter);
        return this;
    }

    private static final Listener NONE = new Listener() {
        @Override
        public void updateUI(Iterable<BeaconScanObject> beacons) {

        }
    };

    public interface Listener {
        void updateUI(Iterable<BeaconScanObject> beacons);
    }

    private class MySettings extends Settings {
        public MySettings(Plattform platform, SharedPreferences preferences) {
            super(platform, preferences);
        }

        @Override
        public long getExitTimeout() {
            return Constants.Time.ONE_SECOND * 4;
        }

        @Override
        public long getForeGroundScanTime() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getForeGroundWaitTime() {
            return 10;
        }

        @Override
        public long getBackgroundScanTime() {
            return getForeGroundScanTime();
        }

        @Override
        public long getBackgroundWaitTime() {
            return getForeGroundWaitTime();
        }
    }

}
