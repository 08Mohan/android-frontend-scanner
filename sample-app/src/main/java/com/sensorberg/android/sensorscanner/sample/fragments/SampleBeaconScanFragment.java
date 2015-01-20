package com.sensorberg.android.sensorscanner.sample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.sensorscanner.nameProvider.CompetitorNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.NameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.SensorbergNameProvider;
import com.sensorberg.android.ui.BeaconScanFragmentWithTotalCount;
import com.sensorberg.android.ui.Constants;
import com.sensorberg.android.ui.RangeFormatter;
import com.sensorberg.android.ui.ScannedBeaconListAdapter;
import com.sensorberg.sdk.cluster.BeaconId;

public class SampleBeaconScanFragment extends BeaconScanFragmentWithTotalCount {

    private String minorFormat;
    private String majorFormat;
    private String unknownName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        minorFormat = getString(R.string.scanner_minorFormat);
        majorFormat = getString(R.string.scanner_majorFormat);
        unknownName = getString(R.string.scanner_unknownBeaconName);
    }

    @Override
    protected SensorScanner getScanner() {
        return new SensorScanner(getActivity())
                .addNameProvider(new SensorbergNameProvider())
                .addNameProvider(new CompetitorNameProvider())
                .addNameProvider(new NameProvider() {
                    @Override
                    public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
                        if (beaconName.name == null) {
                            beaconName.name = unknownName + "-Beacon";
                        }
                        if (beaconName.manufacturer == null) {
                            beaconName.manufacturer = unknownName + "\n" + beaconId.getUuid().toString();
                        }
                        return true;
                    }
                });
    }

    @Override
    protected ScannedBeaconListAdapter.ContentFormatter getFormatter() {
        return new RangeFormatter(Constants.MAX_BEACON_AGE) {
            @Override
            public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder viewHolder) {
                super.apply(beaconScanObject, viewHolder);
                viewHolder.textviewFirstLine.setText(beaconScanObject.beaconName.name);
                viewHolder.textviewSecondline.setText("Vendor: " + beaconScanObject.beaconName.manufacturer);
                viewHolder.textviewThirdline1.setText(String.format(majorFormat, beaconScanObject.beaconId.getMajorId()));
                viewHolder.textviewThirdline2.setText(String.format(minorFormat, beaconScanObject.beaconId.getMinorId()));
            }
        };
    }
}
