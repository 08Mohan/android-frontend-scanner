package com.sensorberg.android.sensorscanner;

import java.util.Comparator;

public class BeaconSorter {

    public static final Comparator<BeaconScanObject> ALPHABETICAL = new Comparator<BeaconScanObject>() {
        @Override
        public int compare(BeaconScanObject lhs, BeaconScanObject rhs) {
            return lhs.beaconId.toTraditionalString().compareTo(rhs.beaconId.toTraditionalString());
        }
    };
}
