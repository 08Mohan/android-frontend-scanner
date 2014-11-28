package com.sensorberg.android.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LimitLine;
import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.SensorScanner;
import com.sensorberg.android.sensorscanner.filter.BeaconIdFilter;
import com.sensorberg.sdk.cluster.BeaconId;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Plotfragment extends Fragment implements SensorScanner.Listener {

    private int sampleRate;
    private int secondsToShow;
    private LineChart chart;
    private SensorScanner scanner;
    private ArrayDeque<BeaconScanObject.BeaconScanDistance > readings;
    private BeaconId myBeaconId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chart = new LineChart(getActivity());
        sampleRate = TechnicalSettingsFragment.getSetting(inflater.getContext(), TechnicalSettingsFragment.SAMPLE_RATE);
        secondsToShow = TechnicalSettingsFragment.getSetting(inflater.getContext(), TechnicalSettingsFragment.SECONDS_TO_PLOT);

        scanner = new SensorScanner(getActivity());
        scanner.addFilter(new BeaconIdFilter(myBeaconId));
        scanner.setSampleRate(sampleRate);
        readings = new ArrayDeque<>();
        chart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "long press", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return chart;
    }

    @Override
    public void onResume() {
        super.onResume();
        scanner.setListener(this);
        scanner.start();
    }

    @Override
    public void onPause() {
        scanner.stop();
        super.onPause();
    }

    @Override
    public void updateUI(List<BeaconScanObject> beacons) {
        BeaconScanObject.BeaconScanDistance reading = beacons.size() == 1 ? beacons.get(0).getLastDistanceCalculation() : new BeaconScanObject.BeaconScanDistance(0,0,0);
        readings.add(new BeaconScanObject.BeaconScanDistance(reading));
        if (readings.size() > secondsToShow * sampleRate){
            readings.removeFirst();
        }


        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> averageRssi = new ArrayList<Entry>();
        ArrayList<Entry> minRssi = new ArrayList<Entry>();
        ArrayList<Entry> maxRssi = new ArrayList<Entry>();
        ArrayList<Entry> distanceInMeters =  new ArrayList<>();
        ArrayList<Entry> samplecount =  new ArrayList<>();

        int i = 0;
        for (BeaconScanObject.BeaconScanDistance beaconScanDistance : readings) {
            xVals.add(String.valueOf(secondsToShow * sampleRate+ 1 - i));
            averageRssi.add(new Entry(-beaconScanDistance.rssi.avg, i));
            minRssi.add(new Entry(-beaconScanDistance.rssi.min, i));
            maxRssi.add(new Entry(-beaconScanDistance.rssi.max, i));
            distanceInMeters.add(new Entry((float) beaconScanDistance.distanceInMeters, i));
            samplecount.add(new Entry((float) beaconScanDistance.samplecount, i));
            i++;
        }

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(getDataSet(averageRssi, "Average Rssi", Color.BLACK, 4f));
        dataSets.add(getDataSet(minRssi, "Min Rssi", Color.LTGRAY, 1f));
        dataSets.add(getDataSet(maxRssi, "Max Rssi", Color.DKGRAY, 1f));
        dataSets.add(getDataSet(distanceInMeters, "Distance", Color.RED, 4f));
        dataSets.add(getDataSet(samplecount, "Sample Count", Color.GREEN, 4f));


        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        LimitLine calRssi = new LimitLine(-beacons.get(0).calRssi);
        calRssi.setLineWidth(4f);
        calRssi.enableDashedLine(10f, 10f, 0f);
        calRssi.setDrawValue(true);
        calRssi.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT);


        data.addLimitLine(calRssi);
        chart.setData(data);
        chart.invalidate();
    }


    private LineDataSet getDataSet(ArrayList<Entry> values, String name, int color, float circleSize) {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, name);

        set1.setCircleSize(circleSize);
        if (circleSize > 1) {
            set1.enableDashedLine(10f, 5f, 0f);
            set1.setLineWidth(1f);
        }
        set1.setColor(color);
        set1.setCircleColor(color);

        set1.setFillAlpha(65);
        set1.setFillColor(color);
        return set1;
    }


    public Plotfragment setMyBeaconId(BeaconId myBeaconId) {
        this.myBeaconId = myBeaconId;
        return this;
    }
}
