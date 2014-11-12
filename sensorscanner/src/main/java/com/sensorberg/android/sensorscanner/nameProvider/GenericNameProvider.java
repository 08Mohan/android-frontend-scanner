package com.sensorberg.android.sensorscanner.nameProvider;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.sdk.cluster.BeaconId;

public class GenericNameProvider implements NameProvider {
    @Override
    public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
        beaconName.first = beaconId.toTraditionalString();
        return true;
    }
}
