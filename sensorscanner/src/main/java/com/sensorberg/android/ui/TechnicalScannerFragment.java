package com.sensorberg.android.ui;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.sensorberg.android.sensorscanner.BeaconName;
import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.android.sensorscanner.RuntimeFilter;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.sensorscanner.filter.BeaconFilter;
import com.sensorberg.android.sensorscanner.nameProvider.CompetitorNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.NameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.SensorbergNameProvider;
import com.sensorberg.android.showcase.utils.TrackingConstants;
import com.sensorberg.sdk.cluster.BeaconId;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TechnicalScannerFragment extends BeaconScanFragmentWithSimulation implements BeaconFilter {


    private BeaconScanObject filterItem;
    private SensorScanner scanner;
    private MenuItem clearSingleBeaconMenuItem;
    private MenuItem keepAwakeMenuItem;
    private boolean stayingAwake;

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
            final int rssiFilter = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_LIMIT_RSSI);
            if (rssiFilter != 0) {
                scanner.addRuntimeFilter(new RuntimeFilter() {
                    @Override
                    public boolean matches(BeaconScanObject beaconScanObject) {
                        return beaconScanObject.getLastDistanceCalculation().rssi.min > -rssiFilter;
                    }
                });
            }
            final int distanceFilter = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_LIMIT_METERS);
            if (distanceFilter != 0) {
                scanner.addRuntimeFilter(new RuntimeFilter() {
                    @Override
                    public boolean matches(BeaconScanObject beaconScanObject) {
                        return beaconScanObject.getLastDistanceCalculation().distanceInMeters < distanceFilter;
                    }
                });
            }
        }
        return scanner;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.technical_scanner, menu);
        clearSingleBeaconMenuItem = menu.findItem(R.id.clear_single_beacon_filter);
        keepAwakeMenuItem = menu.findItem(R.id.keep_awake);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.order_by_distance){
            tracking.logEvent(TrackingConstants.MENU_ORDER_BY_DISTANCE);
            Toast.makeText(getActivity(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.order_by_rssi){
            tracking.logEvent(TrackingConstants.MENU_ORDER_BY_RSSI);
            Toast.makeText(getActivity(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.keep_awake) {
            tracking.logEvent(TrackingConstants.MENU_KEEP_AWAKE);
            if (stayingAwake) {
                doNotKeepScreenOn();
            } else {
                keepScreenOn();
            }
            return true;
        } else if (item.getItemId() == R.id.clear_single_beacon_filter) {
            filterItem = null;
            clearSingleBeaconMenuItem.setEnabled(false);
            scanner.start();
            scanner.clearCache();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void doNotKeepScreenOn() {
        stayingAwake = false;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keepAwakeMenuItem.setTitle(getString(R.string.menu_item_save_energy));
    }

    private void keepScreenOn() {
        stayingAwake = true;
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keepAwakeMenuItem.setTitle(getString(R.string.menu_item_keep_awake));
    }

    @Override
    public void onResume() {
        scanner.settings.exitTimeOut = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_EXIT_TIMEOUT);
        scanner.settings.scanTime = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_SCAN_MILIS);
        scanner.settings.pauseTime = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_PAUSE_MILIS);
        super.onResume();
    }

    @Override
    public void onPause() {
        doNotKeepScreenOn();
        super.onPause();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (filterItem == null){
            tracking.logEvent(TrackingConstants.TECHNICAL_SCANNER_FILTER_ITEM);
            filterItem = (BeaconScanObject) getListAdapter().getItem(position);
            Crouton.showText(getActivity(), getString(R.string.single_scan_crouton, filterItem.beaconId.toTraditionalString()), Style.INFO);
            clearSingleBeaconMenuItem.setEnabled(true);
        } else {
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .replace(containerId, new Plotfragment().setMyBeaconId(filterItem.beaconId))
                    .commit();
            clearSingleBeaconMenuItem.setEnabled(false);
            filterItem = null;
        }
        scanner.start();
        scanner.clearCache();

    }

    protected ScannedBeaconListAdapter.ContentFormatter getFormatter() {
        final NumberFormat decimalFormat = DecimalFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss.SSS");
        long maxAge = TechnicalSettingsFragment.getSetting(getActivity(), TechnicalSettingsFragment.SCANNER_STALE_TIME);
        return new RangeFormatter(maxAge) {
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
                        "rssi: " + decimalFormat.format(lastDistanceCalculation.rssi.avg) +
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
            getActivity().getActionBar().setTitle(getText(R.string.single_scan_title));
        }
    }
}
