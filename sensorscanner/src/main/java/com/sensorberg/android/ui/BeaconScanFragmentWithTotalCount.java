package com.sensorberg.android.ui;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.android.ui.BeaconScanFragment;

import java.util.List;

public abstract class BeaconScanFragmentWithTotalCount extends BeaconScanFragment{

    @Override
    public void updateUI(List<BeaconScanObject> beacons) {
        super.updateUI(beacons);
        if (isAdded()) {
            setTitle(beacons.size());
        }
    }

    protected void setTitle(int size) {
        try {
            getActivity().getActionBar().setTitle(getString(R.string.title_beacon_scanner_with_count, size));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        getActivity().getActionBar().setTitle(null);
        super.onDetach();
    }
}
