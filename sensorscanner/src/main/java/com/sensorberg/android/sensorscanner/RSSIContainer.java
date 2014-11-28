package com.sensorberg.android.sensorscanner;

import android.util.Log;

import com.sensorberg.sdk.cluster.BeaconId;
import com.sensorberg.sdk.scanner.ScanEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sensorberg.android.sensorscanner.BeaconScanObject.BeaconScanDistance;

public class RSSIContainer {

    private static final String TAG = RSSIContainer.class.getSimpleName();

    private final HashMap<BeaconId, List<Integer>> storage = new HashMap<>();
    private final Object storageMonitor = new Object();
    private final Map<BeaconId, Integer> calibrationStorage = new HashMap<>();


    public void addNewRssiReading(BeaconId beaconId, Integer nextRssi) {
        synchronized (storageMonitor) {
            List<Integer> rssis = storage.get(beaconId);
            if (rssis != null) {
                rssis.add(nextRssi);
            } else {
                Log.e(TAG, "no previous value found for" + beaconId);
            }
        }
    }

    public double addInitialRssi(ScanEvent event) {
        synchronized (storageMonitor) {
            ArrayList<Integer> initialList = new ArrayList<>();
            initialList.add(event.getInitialRssi());
            storage.put(event.getBeaconId(), initialList);
            calibrationStorage.put(event.getBeaconId(), event.getCalRssi());
        }
        return BeaconScanDistance.getDistanceFromRSSI(event.getCalRssi(), event.getInitialRssi());
    }

    public void removeBeaconId(BeaconId beaconId) {
        synchronized (storageMonitor){
            storage.remove(beaconId);
        }
    }

    public BeaconScanDistance distance(BeaconId beaconId){
        List<Integer> values;
        synchronized (storageMonitor){
            List<Integer> integers = storage.get(beaconId);
            if (integers == null){
                return null;
            }
            values = new ArrayList<>(integers);
            integers.clear();
        }
        if (values.size() == 0){
            return null;
        }
        Integer calRssi = calibrationStorage.get(beaconId);

        if (calRssi == null){
            return null;
        }
        MaxMinAvg rssiReadings = avg(values);

        return new BeaconScanDistance(values.size(), rssiReadings, calRssi);
    }

    private static MaxMinAvg avg(List<Integer> values){
        int sum = 0;
        int max = values.get(0);
        int min = values.get(0);

        for (Integer value : values) {
            sum += value;
            if (value > max) max = value;
            if (value < min) min= value;
        }
        return new MaxMinAvg(max, min, (float) sum / values.size());
    }

    public void clear() {
        synchronized (storageMonitor){
            storage.clear();
            calibrationStorage.clear();
        }
    }

    public static final class MaxMinAvg {
        public MaxMinAvg(int max, int min, float avg) {
            this.max = max;
            this.min = min;
            this.avg = avg;
        }

        public int max;
        public int min;
        public float avg;

        public MaxMinAvg(int rssi) {
            this.avg = rssi;
            this.max = rssi;
            this.min = rssi;
        }

        public MaxMinAvg(MaxMinAvg that) {
            this.avg = that.avg;
            this.min = that.min;
            this.max = that.max;
        }
    }
}
