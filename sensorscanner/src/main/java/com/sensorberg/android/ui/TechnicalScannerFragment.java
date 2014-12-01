package com.sensorberg.android.ui;

import android.view.View;
import android.widget.ListView;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.sensorscanner.filter.BeaconFilter;
import com.sensorberg.android.sensorscanner.nameProvider.CompetitorNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.NameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.SensorbergNameProvider;
import com.sensorberg.sdk.cluster.BeaconId;
import com.sensorberg.sdk.settings.Settings;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TechnicalScannerFragment extends BeaconScanFragmentWithTotalCount implements BeaconFilter {


    private BeaconScanObject filterItem;
    private SensorScanner scanner;

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    private int containerId;


    @Override
    protected SensorScanner getScanner() {
        if(scanner == null){
            scanner = new SensorScanner(getActivity())
                    .addFilter(this)
                    .addNameProvider(new CompetitorNameProvider())
                    .addNameProvider(new SensorbergNameProvider())
                    .addNameProvider(new NameProvider() {
                        @Override
                        public boolean provideName(BeaconId beaconId, BeaconName beaconName) {
                            if (beaconName.manufacturer == null){
                                beaconName.manufacturer = "Unknown";
                            }
                            return true;
                        }
                    });
        }
        return scanner;
    }

    @Override
    public void onResume() {
        scanner.settings.exitTimeOut = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.EXIT_TIMEOUT);
        scanner.settings.scanTime = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_SCAN_MILIS);
        scanner.settings.pauseTime = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_PAUSE_MILIS);
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        if (filterItem == null){
            filterItem = (BeaconScanObject) getListAdapter().getItem(position);
            Crouton.showText(getActivity(), "showing only " + filterItem.beaconId.toTraditionalString(), Style.INFO);
        } else {
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .replace(containerId, new Plotfragment().setMyBeaconId(filterItem.beaconId))
                    .commit();
            Crouton.showText(getActivity(), "showing all items ", Style.INFO);
            filterItem = null;
        }
        scanner.clearCache();

    }

    protected ScannedBeaconListAdapter.ContentFormatter getFormatter() {
        final NumberFormat decimalFormat = DecimalFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss.SSS");
        return new RangeFormatter() {
            @Override
            public void apply(BeaconScanObject beaconScanObject, ScannedBeaconListAdapter.ViewHolder viewHolder) {
                super.apply(beaconScanObject, viewHolder);
                BeaconScanObject.BeaconScanDistance lastDistanceCalculation = beaconScanObject.getLastDistanceCalculation();

                viewHolder.textviewFirstLine.setText(beaconScanObject.beaconName.manufacturer + " (" + lastDistanceCalculation.samplecount + "samples )");

                long timeSinceBeacon = System.currentTimeMillis() - beaconScanObject.getLastDistanceCalculation().timestamp.getTime();

                viewHolder.textviewSecondline.setText(
                        "last seen: " + dateFormat.format(beaconScanObject.getLastDistanceCalculation().timestamp) +
                        " since: " + timeSinceBeacon+ "\n" +
                        decimalFormat.format(lastDistanceCalculation.distanceInMeters) +  "m " +
                        "rssi: " + decimalFormat.format(lastDistanceCalculation.rssi.avg) + "db " +
                        "calRssi:" + beaconScanObject.calRssi +
                        "\n" + beaconScanObject.beaconId.getUuid().toString());

                viewHolder.textviewThirdline1.setText(beaconScanObject.beaconId.getMajorId() + ":" + beaconScanObject.beaconId.getMinorId());
                viewHolder.textviewThirdline2.setText(beaconScanObject.hardwareAdress);
            }
        };
    }

    @Override
    public boolean filter(BeaconId beaconId) {
        if (filterItem != null){
            return beaconId.equals(filterItem.beaconId);
        }

        return true;
    }

    @Override
    protected void setTitle(int size) {
        if (filterItem == null){
            super.setTitle(size);
        } else {
            getActivity().getActionBar().setTitle(filterItem.beaconName.name);
        }
    }
}
