package com.sensorberg.android.sensorscanner;

import com.sensorberg.sdk.cluster.BeaconId;

public class BeaconScanObject {

    public static class BeaconScanDistance{
        public final int samplecount;
        public final RSSIContainer.MaxMinAvg rssi;
        public final double distanceInMeters;

        public BeaconScanDistance(int samplecount, RSSIContainer.MaxMinAvg rssiReadings, int calRssi) {
            this.samplecount = samplecount;
            this.rssi = rssiReadings;
            this.distanceInMeters = getDistanceFromRSSI(this.rssi.avg, calRssi);

        }

        public BeaconScanDistance(BeaconScanDistance that) {
            this.samplecount = that.samplecount;
            this.rssi = new RSSIContainer.MaxMinAvg(that.rssi);
            this.distanceInMeters = that.distanceInMeters;
        }

        public BeaconScanDistance(int samplecount, int rssi, int calRssi) {
            this(samplecount, new RSSIContainer.MaxMinAvg(rssi), calRssi);
        }

        public static double getDistanceFromRSSI(double rssi, int calRssi) {
            double dist;
            double near = rssi / calRssi;
            if (near < 1.0f) {
                dist = Math.pow(near, 10);
            } else {
                dist =  ((0.89976f) * Math.pow(near, 7.7095f) + 0.111f);
            }
            return dist;
        }
    }

    public final BeaconId beaconId;
    public final BeaconName beaconName;
    public final String hardwareAdress;
    public final int calRssi;

    private Double lastDistance = null;
    private BeaconScanDistance lastDistanceCalculation;

    public BeaconScanObject(BeaconId beaconId, BeaconName beaconName, double initialDistance, String hardwareAdress, int calRssi, int rssi) {
        this.beaconId = beaconId;
        this.beaconName = beaconName;
        this.lastDistance = initialDistance;
        this.hardwareAdress = hardwareAdress;
        this.calRssi = calRssi;
        this.lastDistance = initialDistance;
        this.lastDistanceCalculation = new BeaconScanDistance(1, rssi, calRssi);
    }

    public BeaconScanObject(BeaconId beaconId) {
        this.beaconId = beaconId;
        this.beaconName = null;
        this.hardwareAdress = null;
        this.calRssi = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeaconScanObject that = (BeaconScanObject) o;

        if (!beaconId.equals(that.beaconId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return beaconId.hashCode();
    }

    public Double getLastDistance() {
        return lastDistance;
    }

    public BeaconScanDistance getLastDistanceCalculation() {
        return lastDistanceCalculation;
    }

    public void setLastDistance(BeaconScanDistance lastDistance) {
        this.lastDistance = lastDistance.distanceInMeters;
        this.lastDistanceCalculation = lastDistance;
    }
}
