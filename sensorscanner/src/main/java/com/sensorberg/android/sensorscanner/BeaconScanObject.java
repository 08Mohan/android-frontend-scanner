package com.sensorberg.android.sensorscanner;

import com.sensorberg.sdk.cluster.BeaconId;

public class BeaconScanObject {

    public final BeaconId beaconId;
    public final BeaconName beaconName;

    public BeaconScanObject(BeaconId beaconId, BeaconName beaconName) {
        this.beaconId = beaconId;
        this.beaconName = beaconName;
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
}
