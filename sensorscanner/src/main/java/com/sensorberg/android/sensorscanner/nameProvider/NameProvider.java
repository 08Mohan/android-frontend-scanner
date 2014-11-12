package com.sensorberg.android.sensorscanner.nameProvider;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.sdk.cluster.BeaconId;

public interface NameProvider {

    /**
     *
     * @param beaconId the beaconID on which the name should be determined
     * @param beaconName the name Object which should be modified
     * @return true if the chain should continue
     */

    boolean provideName(BeaconId beaconId, BeaconName beaconName);
}
