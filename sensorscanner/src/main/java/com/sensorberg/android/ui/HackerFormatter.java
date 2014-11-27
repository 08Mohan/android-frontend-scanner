package com.sensorberg.android.ui;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class HackerFormatter extends RangeFormatter {

    private final NumberFormat decimalFormat;

    public HackerFormatter() {
        decimalFormat = DecimalFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(1);
    }

    @Override
    public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder holder) {
        super.apply(beaconScanObject, holder);
        BeaconScanObject.BeaconScanDistance lastDistanceCalculation = beaconScanObject.getLastDistanceCalculation();
        if (lastDistanceCalculation != null) {
            holder.textviewFirstLine.setText(beaconScanObject.beaconName.first + "(" + lastDistanceCalculation.samplecount +" samples)");
        }
        else {
            holder.textviewFirstLine.setText(beaconScanObject.beaconName.first);
        }
        holder.textviewThirdline1.setText(beaconScanObject.hardwareAdress);
        if (beaconScanObject.getLastDistance() != null){
            holder.textviewThirdline2.setText(decimalFormat.format(beaconScanObject.getLastDistance()) + " m");
        } else {
            holder.textviewThirdline2.setText("∞ m");
        }

        holder.textviewSecondline.setText( beaconScanObject.beaconId.toTraditionalString());
    }
}
