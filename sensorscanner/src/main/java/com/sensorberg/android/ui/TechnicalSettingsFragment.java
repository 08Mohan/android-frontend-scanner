package com.sensorberg.android.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

public class TechnicalSettingsFragment extends Fragment {

    private final static class Setting {
        final int min;
        final int max;
        final String name;
        private final String preferencesKey;
        public int defaultValue;

        public Setting(int min, int max, int defaultValue, String name, String preferencesKey) {
            this.min = min;
            this.max = max;
            this.name = name;
            this.preferencesKey = preferencesKey;
            this.defaultValue = defaultValue;

        }
    }

    static Collection<Setting> settings = new ArrayList<>();

    public static final Setting SAMPLE_RATE = new Setting(0, 10, 5, "Sample Rate", "scanner.sampleRate");
    public static final Setting SECONDS_TO_PLOT = new Setting(0, 10, 5, "Seconds to Plot", "scanner.secondsToPlot");
    public static final Setting EXIT_TIMEOUT = new Setting(0, 10000, 9000, "Exit Timeout", "scanner.exiTimeout");

    static {
        settings.add(SAMPLE_RATE);
        settings.add(SECONDS_TO_PLOT);
        settings.add(EXIT_TIMEOUT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        SharedPreferences sharedPreferences = getPrefs(inflater.getContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        for (final Setting setting : settings) {

            SeekBar seekBar = new SeekBar(inflater.getContext());
            seekBar.setMax(setting.max);
            linearLayout.addView(seekBar);
            final TextView label = new TextView(inflater.getContext());

            linearLayout.addView(label);

            if (sharedPreferences.contains(setting.preferencesKey)){
                try {
                    seekBar.setProgress(sharedPreferences.getInt(setting.preferencesKey, setting.defaultValue));
                } catch (ClassCastException e){ //Weird android bug
                    e.printStackTrace();
                    seekBar.setProgress(setting.defaultValue);
                }
            } else {
                seekBar.setProgress(setting.defaultValue);
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    label.setText(setting.name + ":" + seekBar.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt(setting.preferencesKey, seekBar.getProgress());
                    editor.apply();
                }
            });
            label.setText(setting.name + ":" + seekBar.getProgress());
        }
        return linearLayout;
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("com.sensorberg.android.showcase.technicalSettings", Context.MODE_PRIVATE);


    }

    public static int getSetting(Context context, Setting setting){
        try {
            return getPrefs(context).getInt(setting.preferencesKey, setting.defaultValue);
        } catch (ClassCastException e){
            return setting.defaultValue;
        }

    }
}
