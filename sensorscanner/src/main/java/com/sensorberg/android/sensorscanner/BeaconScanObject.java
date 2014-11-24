package com.sensorberg.android.sensorscanner;

import com.sensorberg.sdk.cluster.BeaconId;

public class BeaconScanObject {

    public static class BeaconScanDistance{
        public final int samplecount;
        public final double averageRssi;
        public final double distanceInMeters;

        public BeaconScanDistance(int samplecount, double averageRssi, int calRssi) {
            this.samplecount = samplecount;
            this.averageRssi = averageRssi;
            this.distanceInMeters = getDistanceFromRSSI(this.averageRssi, calRssi);

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
    private Double lastDistance = null;
    public final String hardwareAdress;
    private final int calRssi;
    private BeaconScanDistance lastDistanceCalculation;

    public BeaconScanObject(BeaconId beaconId, BeaconName beaconName, double initialDistance, String hardwareAdress, int calRssi) {
        this.beaconId = beaconId;
        this.beaconName = beaconName;
        this.lastDistance = initialDistance;
        this.hardwareAdress = hardwareAdress;
        this.calRssi = calRssi;
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
