package com.sensorberg.android.sensorscanner;

import com.sensorberg.sdk.cluster.BeaconId;

public interface BeaconFilter {

    boolean filter(BeaconId beaconId);
}
