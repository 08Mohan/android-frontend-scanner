package com.sensorberg.android.sensorscanner.nameProvider;

import android.util.Pair;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.sdk.cluster.BeaconId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBeaconsNameProvider implements NameProvider {
    final Map<BeaconId, String> storage;

    public MyBeaconsNameProvider(List<Pair<BeaconId, String>> beacons) {
        storage = new HashMap<>();
        for (Pair<BeaconId, String> beacon : beacons) {
            storage.put(beacon.first, beacon.second);
        }
    }

    @Override
    public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
        String name = storage.get(beaconId);
        if(name != null){
            beaconName.first = name;
            return false;
        }
        return true;
    }
}
