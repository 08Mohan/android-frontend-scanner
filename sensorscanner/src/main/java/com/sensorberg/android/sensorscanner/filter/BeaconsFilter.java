package com.sensorberg.android.sensorscanner.filter;

import com.sensorberg.sdk.cluster.BeaconId;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeaconsFilter implements BeaconFilter {

    private final Set<BeaconId> myBeacons;

    public BeaconsFilter(List<BeaconId> beaconIds) {
        myBeacons = new HashSet<>(beaconIds);
    }

    @Override
    public boolean filter(BeaconId beaconId) {
        return myBeacons.contains(beaconId);
    }
}
