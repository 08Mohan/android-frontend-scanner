package com.sensorberg.android.ui;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class HackerFormatter implements ScannedBeaconListAdapter.ContentFormatter {

    private final NumberFormat decimalFormat;

    public HackerFormatter() {
        decimalFormat = DecimalFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(1);
    }

    @Override
    public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder holder) {
        BeaconScanObject.BeaconScanDistance lastDistanceCalculation = beaconScanObject.getLastDistanceCalculation();
        if (lastDistanceCalculation != null) {
            holder.textviewFirstLine.setText(beaconScanObject.beaconName.first + "(" + lastDistanceCalculation.samplecount +" samples)");
        }
        else {
            holder.textviewFirstLine.setText(beaconScanObject.beaconName.first);
        }
        holder.textviewSecondline1.setText(beaconScanObject.hardwareAdress);
        if (beaconScanObject.getLastDistance() != null){
            holder.textviewSecondline2.setText(decimalFormat.format(beaconScanObject.getLastDistance()) + " m");
        } else {
            holder.textviewSecondline2.setText("∞ m");
        }

        holder.textviewLastline.setText( beaconScanObject.beaconId.toTraditionalString());


        //TODO:check if image caching is needed
        //show scan range icon
        if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_HIGH) {
            holder.rangeIcon.setImageResource(R.drawable.range0);
        } else if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_MID) {
            holder.rangeIcon.setImageResource(R.drawable.range1);
        } else if (beaconScanObject.getLastDistance() > Constants.BEACON_RANGE_LOW) {
            holder.rangeIcon.setImageResource(R.drawable.range2);
        } else {
            holder.rangeIcon.setImageResource(R.drawable.range3);
        }
    }
}
