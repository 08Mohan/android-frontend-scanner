package com.sensorberg.android.ui;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;

public abstract class RangeFormatter implements ScannedBeaconListAdapter.ContentFormatter{
    @Override
    public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder viewHolder) {
        //TODO:check if image caching is needed
        //show scan range icon
        if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_HIGH) {
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
