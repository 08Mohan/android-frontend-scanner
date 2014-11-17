package com.sensorberg.android.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.sensorscanner.nameProvider.CompetitorNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.GenericNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.MyBeaconsNameProvider;
import com.sensorberg.android.sensorscanner.nameProvider.SensorbergNameProvider;
import com.sensorberg.sdk.cluster.BeaconId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BeaconScanFragment extends ListFragment implements SensorScanner.Listener {

    private final List<BeaconScanObject> scanObjects;
    private int viewLayout;

    private OnItemClickListener onItemClickListener;
    private SensorScanner scanner;
    private ScannedBeaconListAdapter adapter;

    public BeaconScanFragment() {
        scanObjects = new ArrayList<>();
        this.viewLayout = R.layout.fragment_scan_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanner = new SensorScanner(getActivity());
        scanner
                .addNameProvider(new CompetitorNameProvider())
                .addNameProvider(new SensorbergNameProvider())
                .addNameProvider(new GenericNameProvider());
        scanner.setListener(this);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (this.onItemClickListener != null) {
            this.onItemClickListener.onItemClick(l, v, position, id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(this.viewLayout, container, false);

        adapter = new ScannedBeaconListAdapter(getActivity(), R.layout.scanlist_listitem, scanObjects);
        setListAdapter(adapter);
        return rootView;
    }

    @Override
    public void updateUI(List<BeaconScanObject> beacons) {
        scanObjects.clear();
        scanObjects.addAll(beacons);
        adapter.notifyDataSetChanged();
    }
}
