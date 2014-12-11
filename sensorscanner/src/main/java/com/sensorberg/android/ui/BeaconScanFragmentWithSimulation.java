package com.sensorberg.android.ui;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.sdk.resolver.SensorbergSimulator;
import com.sensorberg.sdk.scanner.ScanEventType;

public abstract class BeaconScanFragmentWithSimulation extends BeaconScanFragmentWithTotalCount{

    public static String API_TOKEN = null;


    private static final int SIMULATE_EXIT = 1;
    private static final int SIMULATE_ENTRY = 2;
    private static final int SIMULATE_DELAYED_ENTRY = 3;

    private SensorbergSimulator simulator;
    private BeaconScanObject menuObject;
    private BeaconScanObject delayedBeaconScanobject;



    @Override
    public void onStop() {
        final BeaconScanObject object = delayedBeaconScanobject;
        delayedBeaconScanobject = null;
        if (object != null){
            getListView().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    simulator.simulateEvent(object, ScanEventType.ENTRY);
                }
            }, 2000);
        }
        super.onStop();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        simulator = new SensorbergSimulator(activity, API_TOKEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerForContextMenu(getListView());
    }

    @Override
    public void onPause() {
        unregisterForContextMenu(getListView());
        super.onPause();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v == getListView()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menuObject = (BeaconScanObject) getListAdapter().getItem(info.position);
            menu.setHeaderTitle(menuObject.beaconName.name);

            menu.add(0, SIMULATE_EXIT, 1, "Simulate Exit");
            menu.add(0, SIMULATE_ENTRY, 2, "Simulate Entry");
            menu.add(0, SIMULATE_DELAYED_ENTRY, 2, "Simulate Entry when leaving the app");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case SIMULATE_EXIT: {
                simulator.simulateEvent(menuObject, ScanEventType.EXIT);
                menuObject = null;
                return true;
            }
            case SIMULATE_ENTRY: {
                simulator.simulateEvent(menuObject, ScanEventType.ENTRY);
                menuObject = null;
                return true;
            }
            case SIMULATE_DELAYED_ENTRY: {
                delayedBeaconScanobject = menuObject;
                Toast.makeText(getActivity(), "Now close the app", Toast.LENGTH_SHORT).show();
                menuObject = null;
                return true;
            }
        }
        return false;
    }

}
