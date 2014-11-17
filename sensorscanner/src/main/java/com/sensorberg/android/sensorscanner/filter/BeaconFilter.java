package com.sensorberg.android.sensorscanner.filter;

import com.sensorberg.sdk.cluster.BeaconId;

/**
 * a class to filter beaconsss
 */

public interface BeaconFilter {

    /**
     * @param beaconId the input beacon id
     * @return true if this beacon should be displayed
     */
    boolean filter(BeaconId beaconId);
}
