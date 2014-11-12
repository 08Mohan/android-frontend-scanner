package com.sensorberg.android.sensorscanner.nameProvider;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.sdk.cluster.BeaconId;

public class SensorbergNameProvider implements NameProvider{
    @Override
    public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
        if (beaconId.getNormalizedUUIDString().contains("7367672374000000FFFF0000FFFF000")){
            beaconName.first = beaconId.toTraditionalString().replace("73676723-7400-0000-ffff-0000ffff000","SB-");
            return false;
        }
        return true;
    }
}
