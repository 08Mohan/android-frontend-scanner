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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ScannedBeaconListAdapter extends ArrayAdapter<BeaconScanObject> {

    private final ContentFormatter contentFormatter;

    public ScannedBeaconListAdapter(Context context, int resource, List<BeaconScanObject> objects, ContentFormatter contentFormatter) {
        super(context, resource, objects);
        this.contentFormatter = contentFormatter;
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

        contentFormatter.apply(beaconScanObject, holder);

        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


    public static class ViewHolder {
        TextView textviewFirstLine;
        TextView textviewSecondline1;
        TextView textviewSecondline2;
        TextView textviewLastline;
        ImageView rangeIcon;
    }

    public static interface ContentFormatter{
        public void apply(BeaconScanObject beaconScanObject, ViewHolder viewHolder);
    }
}
