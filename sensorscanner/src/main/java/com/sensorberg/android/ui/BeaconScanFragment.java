package com.sensorberg.android.ui;

import android.app.Activity;
import android.app.ListFragment;
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

import java.util.ArrayList;
import java.util.List;

public abstract class BeaconScanFragment extends ListFragment implements SensorScanner.Listener {

    private final List<BeaconScanObject> scanObjects;
    protected int viewLayout;

    private SensorScanner scanner;
    private ScannedBeaconListAdapter adapter;

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
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scanner, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pauseResume){
            if (scanner.isScanning()) {
                item.setTitle("Resume");
                scanner.stop();
            } else {
                item.setTitle("Stop");
                scanner.start();
            }
        }
        return false;
    }



    @Override
    public void onResume() {
        super.onResume();
        scanner.start();
    }

    @Override
    public void onPause() {
        scanner.stop();
        super.onPause();
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
        return rootView;
    }

    protected abstract ScannedBeaconListAdapter.ContentFormatter getFormatter();

    @Override
    public void updateUI(List<BeaconScanObject> beacons) {
        scanObjects.clear();
        scanObjects.addAll(beacons);
        adapter.notifyDataSetChanged();
    }
}
