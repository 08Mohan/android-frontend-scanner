package com.sensorberg.android.ui;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.showcase.Tracking.Tracking;
import com.sensorberg.android.showcase.utils.TrackingConstants;
import com.sensorberg.sdk.internal.AndroidPlattform;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public abstract class BeaconScanFragment extends ListFragment implements SensorScanner.Listener {

    public static Tracking tracking = Tracking.NONE;

    private final List<BeaconScanObject> scanObjects;
    protected int viewLayout;

    private SensorScanner scanner;
    private ScannedBeaconListAdapter adapter;
    private MenuItem activityIndicator;
    private Crouton noBluetoothCrouton;
    private BroadcastReceiver blueoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    bluetoothNotTurnedOn();
                    break;
                case BluetoothAdapter.STATE_ON:
                    noBluetoothCrouton.cancel();
                    scanner.start();
                    break;
            }
        }
    };

    private AndroidPlattform plattform;
    private boolean isPausedFromMenu = false;


    public BeaconScanFragment() {
        scanObjects = new ArrayList<>();
        this.viewLayout = R.layout.fragment_scan_list;
    }

    protected abstract SensorScanner getScanner();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanner = getScanner();
        scanner.setListener(this);
        setHasOptionsMenu(true);

        noBluetoothCrouton = Crouton.makeText(getActivity(), R.string.error_crouton_bluetooth_not_turned_on,
                new Style.Builder(Style.ALERT)
                        .setConfiguration(new Configuration.Builder()
                                        .setDuration(Configuration.DURATION_INFINITE)
                                        .build()
                        )
                        .build());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.scanner, menu);
        activityIndicator = menu.findItem(R.id.action_activity_indicator);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pauseResume || item.getItemId() == R.id.action_activity_indicator){
            if (scanner.isScanning()) {
                tracking.logEvent(TrackingConstants.MENU_SCANNER_STOPPED);
                item.setTitle(getString(R.string.scanner_menu_resume));
                activityIndicator.setVisible(false);
                scanner.stop();
                this.isPausedFromMenu = true;
            } else {
                tracking.logEvent(TrackingConstants.MENU_SCANNER_RESUMED);
                isPausedFromMenu = false;
                item.setTitle(getString(R.string.scanner_menu_pause));
                activityIndicator.setVisible(true);
                scanner.start();
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPausedFromMenu) {
            scanner.start();
        }
        IntentFilter bluetoothIntents = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if (!plattform.isBluetoothEnabled()) {
            bluetoothNotTurnedOn();
        }
        getActivity().registerReceiver(blueoothReceiver, bluetoothIntents);
    }

    @Override
    public void onPause() {
        scanner.stop();
        getActivity().unregisterReceiver(blueoothReceiver);
        super.onPause();
    }

    @Override
    public void onDetach() {
        noBluetoothCrouton.cancel();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        scanner.setListener(null);
        scanner = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(this.viewLayout, container, false);

        adapter = new ScannedBeaconListAdapter(getActivity(), R.layout.scanlist_listitem, scanObjects, getFormatter());
        setListAdapter(adapter);

        plattform = new AndroidPlattform(inflater.getContext());
        return rootView;
    }

    protected abstract ScannedBeaconListAdapter.ContentFormatter getFormatter();

    @Override
    public void updateUI(List<BeaconScanObject> beacons) {
        scanObjects.clear();
        scanObjects.addAll(beacons);
        adapter.notifyDataSetChanged();
    }

    private void bluetoothNotTurnedOn() {
        scanner.stop();
        scanner.clearCache();
        noBluetoothCrouton.show();
    }
}
