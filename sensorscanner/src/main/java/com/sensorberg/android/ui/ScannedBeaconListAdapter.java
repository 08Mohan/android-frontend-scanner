package com.sensorberg.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensorberg.android.sensorscanner.BeaconScanObject;
import com.sensorberg.android.sensorscanner.R;

import java.util.List;
import java.util.UUID;

public class ScannedBeaconListAdapter extends ArrayAdapter<BeaconScanObject> {

    private static final int VIEW_TYPE_SENSORBERG_ID = 0;
    private static final int VIEW_TYPE_OTHER_ID = 1;
    private StringBuffer sb = new StringBuffer();

    public ScannedBeaconListAdapter(Context context, int resource, List<BeaconScanObject> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Context context = this.getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.scanlist_listitem, null);
            holder = new ViewHolder();
            holder.textviewFirstLine = (TextView) convertView.findViewById(R.id.textview_firstline);
            holder.textviewSecondline1 = (TextView) convertView.findViewById(R.id.textview_secondline1);
            holder.textviewSecondline2 = (TextView) convertView.findViewById(R.id.textview_secondline2);
            holder.textviewLastline = (TextView) convertView.findViewById(R.id.textview_lastline);
            holder.rangeIcon = (ImageView) convertView.findViewById(R.id.rangeIcon);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BeaconScanObject beaconScanObject = getItem(position);

        holder.textviewFirstLine.setText(beaconScanObject.beaconName.first);
        holder.textviewSecondline1.setText(beaconScanObject.beaconName.second);
        holder.textviewSecondline2.setText(beaconScanObject.beaconName.third);
        holder.textviewLastline.setText(beaconScanObject.beaconId.toTraditionalString());


        //TODO:check if image caching is needed
        //show scan range icon
        if (beaconScanObject.getLastRSSI() < Constants.BEACON_RANGE_LOW) {
            holder.rangeIcon.setImageResource(R.drawable.range0);
        } else if (beaconScanObject.getLastRSSI() < Constants.BEACON_RANGE_MID) {
            holder.rangeIcon.setImageResource(R.drawable.range1);
        } else if (beaconScanObject.getLastRSSI() < Constants.BEACON_RANGE_HIGH) {
            holder.rangeIcon.setImageResource(R.drawable.range2);
        } else {
            holder.rangeIcon.setImageResource(R.drawable.range3);
        }


        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    private static class ViewHolder {
        TextView textviewFirstLine;
        TextView textviewSecondline1;
        TextView textviewSecondline2;
        TextView textviewLastline;
        ImageView rangeIcon;
    }
}
