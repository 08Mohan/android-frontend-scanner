package com.sensorberg.android.sensorscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sensorberg.android.sensorscanner.filter.BeaconFilter;
import com.sensorberg.android.sensorscanner.nameProvider.NameProvider;
import com.sensorberg.android.ui.TechnicalSettingsFragment;
import com.sensorberg.sdk.Constants;
import com.sensorberg.sdk.cluster.BeaconId;
import com.sensorberg.sdk.internal.AndroidPlattform;
import com.sensorberg.sdk.internal.Plattform;
import com.sensorberg.sdk.scanner.ScanEvent;
import com.sensorberg.sdk.scanner.ScanEventType;
import com.sensorberg.sdk.scanner.Scanner;
import com.sensorberg.sdk.scanner.ScannerListener;
import com.sensorberg.sdk.settings.Settings;
import com.sensorberg.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SensorScanner implements ScannerListener, Scanner.RssiListener {

    private static final String TAG = SensorScanner.class.getSimpleName();


    private static final int MSG_NOTIFY_LISTENER = 1;
    public final DynamicSettings settings;

    private List<BeaconFilter> beaconFilters = Collections.emptyList();
    private List<NameProvider> nameProviders = Collections.emptyList();
    private Listener listener = NONE;
    private final ResponderHandler handler;
    private Set<BeaconScanObject> storage = new HashSet<>();
    private RSSIContainer rssiContainer = new RSSIContainer();
    private Timer rssiTimer;

    private final Scanner scanner;
    private long sampleWindow = 1000;
    private final List<RuntimeFilter> runtimeFilters;

    @Override
    public void onRssiUpdated(BeaconId beaconId, Integer nextRssi) {
        if(filter(beaconId)){
            return;
        }
        rssiContainer.addNewRssiReading(beaconId, nextRssi);
    }

    public void setSampleWindow(long sampleWindow) {
        this.sampleWindow = sampleWindow;
    }

    public void addRuntimeFilter(RuntimeFilter runtimeFilter) {
        this.runtimeFilters.add(runtimeFilter);
    }

    public boolean isScanning() {
        return scanner.isScanRunning();
    }

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
                List data;
                synchronized (storage) {
                    data = new ArrayList<>(storage);
                }
                for (RuntimeFilter runtimeFilter : runtimeFilters) {
                    data = ListUtils.filter(data, runtimeFilter);
                }
                Collections.sort(data, BeaconSorter.BY_DISTANCE);

                listener.updateUI(data);
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
        settings = new DynamicSettings(platform, platform.getSettingsSharedPrefs());
        settings.exitTimeOut = TechnicalSettingsFragment.getSetting(context, TechnicalSettingsFragment.SCANNER_EXIT_TIMEOUT);
        settings.scanTime = TechnicalSettingsFragment.getSetting(context, TechnicalSettingsFragment.PLOT_SCAN_MILIS);
        settings.pauseTime = TechnicalSettingsFragment.getSetting(context, TechnicalSettingsFragment.PLOT_PAUSE_MILIS);


        scanner = new Scanner(settings, platform);
        handler = new ResponderHandler(this, Looper.myLooper());

        runtimeFilters = new ArrayList<>();
    }

    public void start(){
        scanner.hostApplicationInForeground();
        scanner.addScannerListener(this);
        scanner.setRssiListener(this);
        scanner.start();
        rssiTimer = new Timer();
        rssiTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (storage) {
                    for (BeaconScanObject beaconScanObject : storage) {
                        BeaconScanObject.BeaconScanDistance distance = rssiContainer.distance(beaconScanObject.beaconId);
                        if (distance != null) {
                            beaconScanObject.setLastDistance(distance);
                        }
                    }
                }
                updateUI();
            }
        }, sampleWindow, sampleWindow);
    }

    public void pause(){

    }

    public void clearCache(){
        scanner.clearCache();
        storage.clear();
        rssiContainer.clear();
    }


    public void stop(){
        scanner.removeScannerListener(this);
        scanner.stop();
        rssiTimer.cancel();
        rssiTimer = null;
    }

    @Override
    public void onScanEventDetected(ScanEvent event) {
        if (event.getEventMask() == ScanEventType.ENTRY.getMask()){
            if(filter(event.getBeaconId())){
                return;
            }
            BeaconName name = new BeaconName();
            for (NameProvider nameProvider : nameProviders) {
                if (!nameProvider.provideName(event.getBeaconId(), name)){
                    break;
                }
            }

            double initialDistance = rssiContainer.addInitialRssi(event);
            synchronized (storage) {
                storage.add(new BeaconScanObject(event.getBeaconId(), name, initialDistance, event.getHardwareAdress(), event.getCalRssi(), event.getInitialRssi()));
            }
        } else {
            rssiContainer.removeBeaconId(event.getBeaconId());
            synchronized (storage) {
                storage.remove(new BeaconScanObject(event.getBeaconId()));
            }
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

    private boolean filter(BeaconId beaconId){
        for (BeaconFilter beaconFilter : beaconFilters) {
            if(!beaconFilter.filter(beaconId)){
                return true;
            }
        }
        return false;
    }

    private static final Listener NONE = new Listener() {
        @Override
        public void updateUI(List<BeaconScanObject> beacons) {

        }
    };

    public interface Listener {
        void updateUI(List<BeaconScanObject> beacons);
    }

    public static class DynamicSettings extends Settings {

        public long exitTimeOut = Constants.Time.ONE_SECOND * 9;
        public long scanTime = Long.MAX_VALUE;
        public long pauseTime = 1;

        public DynamicSettings(Plattform platform, SharedPreferences preferences) {
            super(platform, preferences);
        }

        @Override
        public long getExitTimeout() {
            return exitTimeOut;
        }

        @Override
        public long getForeGroundScanTime() {
            return scanTime;
        }

        @Override
        public long getForeGroundWaitTime() {
            return pauseTime;
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
