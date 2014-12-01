package com.sensorberg.android.ui;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.sdk.internal.Clock;

public abstract class RangeFormatter implements ScannedBeaconListAdapter.ContentFormatter{

    private final long maxAge;

    protected RangeFormatter(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder viewHolder) {
        if (System.currentTimeMillis() - beaconScanObject.getLastDistanceCalculation().timestamp.getTime() > maxAge) {
            viewHolder.rangeIcon.setImageResource(R.drawable.range0);
        } else if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_MID) {
            viewHolder.rangeIcon.setImageResource(R.drawable.range1);
        } else if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_LOW) {
            viewHolder.rangeIcon.setImageResource(R.drawable.range2);
        } else {
            viewHolder.rangeIcon.setImageResource(R.drawable.range3);
        }
    }
}
