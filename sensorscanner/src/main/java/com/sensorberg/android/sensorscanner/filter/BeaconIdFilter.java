package com.sensorberg.android.sensorscanner.filter;

import com.sensorberg.sdk.cluster.BeaconId;

public class BeaconIdFilter implements BeaconFilter {
    private final BeaconId filterId;

    public BeaconIdFilter(BeaconId filterId) {
        this.filterId = filterId;
    }

    @Override
    public boolean filter(BeaconId beaconId) {
        return beaconId.equals(filterId);
    }
}
