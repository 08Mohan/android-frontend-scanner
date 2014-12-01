package com.sensorberg.android.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sensorberg.sdk.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class TechnicalSettingsFragment extends Fragment {

    private final static class Setting {
        final int min;
        final int max;
        private final String unit;
        final String name;
        private final String preferencesKey;
        public int defaultValue;

        public Setting(int min, int max, int defaultValue, String unit, String name, String preferencesKey) {
            this.min = min;
            this.max = max;
            this.unit = unit;
            this.name = name;
            this.preferencesKey = preferencesKey;
            this.defaultValue = defaultValue;
        }

    }

    static Collection<Setting> settings = new ArrayList<>();

    public static final Setting SAMPLE_WINDOW = new Setting(100, (int) Constants.Time.ONE_HOUR, 1000, "ms", "Sample Window", "scanner.sampleWindow");
    public static final Setting SAMPLE_WINDOWS_TO_PLOT = new Setting(1, 60, 5, "window", "Plot length", "scanner.windowsToPlot");


    public static final Setting PLOT_SCAN_MILIS = new Setting(1, (int) Constants.Time.ONE_HOUR, (int) (10 * Constants.Time.ONE_MINUTE), "ms", "Scan Time (Plot)", "plot.scanMilis");
    public static final Setting PLOT_PAUSE_MILIS = new Setting(1, (int) (10 * Constants.Time.ONE_MINUTE), 5, "ms", "Scan Pause (Plot)", "plot.pauseMilis");

    public static final Setting SCANNER_SCAN_MILIS = new Setting(1, (int) Constants.Time.ONE_HOUR, (int) (10 * Constants.Time.ONE_MINUTE), "ms", "Scan Time (Scanner)", "scanner.scanMilis");
    public static final Setting SCANNER_PAUSE_MILIS = new Setting(1, (int) (10 * Constants.Time.ONE_MINUTE), 5, "ms", "Scan Pause (Scanner)", "scanner.pauseMilis");
    public static final Setting SCANNER_EXIT_TIMEOUT = new Setting(1, (int) Constants.Time.ONE_HOUR, 9000, "ms", "Exit Timeout", "scanner.exiTimeout");

    public static final Setting SCANNER_LIMIT_METERS = new Setting(1, 120, 0, "m", "Limit", "scanner.limitMeters");
    public static final Setting SCANNER_LIMIT_RSSI = new Setting(1, 120, 0, "rssi", "Limit (Rssi) negative", "scanner.limitRssi");
    public static final Setting SCANNER_STALE_TIME = new Setting(1000, 10 * 1000, 2000, "ms", "Scanner stale time", "scanner.staleTime");



    static {
        settings.add(SAMPLE_WINDOW);
        settings.add(SAMPLE_WINDOWS_TO_PLOT);

        settings.add(PLOT_SCAN_MILIS);
        settings.add(PLOT_PAUSE_MILIS);
        settings.add(SCANNER_SCAN_MILIS);

        settings.add(SCANNER_PAUSE_MILIS);
        settings.add(SCANNER_EXIT_TIMEOUT);
        settings.add(SCANNER_EXIT_TIMEOUT);
        settings.add(SCANNER_LIMIT_METERS);
        settings.add(SCANNER_LIMIT_RSSI);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(16, 16, 16, 16);
        SharedPreferences sharedPreferences = getPrefs(inflater.getContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        for (final Setting setting : settings) {

            int value = sharedPreferences.getInt(setting.preferencesKey, setting.defaultValue);


            final SeekBar seekBar = new SeekBar(inflater.getContext());
            seekBar.setMax(setting.max);

            final TextView label = new TextView(inflater.getContext());

            LinearLayout group = new LinearLayout(inflater.getContext());
            group.setOrientation(LinearLayout.HORIZONTAL);
            final EditText editText = new EditText(inflater.getContext());

            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            editText.setText(String.valueOf(value));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int value;
                    try {
                        value = Integer.parseInt(s.toString());
                    }
                    catch (NumberFormatException e){
                        editText.setError("Not a number");
                        return;
                    }

                    if (value > setting.max){
                        editText.setError("Value might be too high");
                    }
                    if (value < setting.min){
                        editText.setError("Value too low");
                    }

                    seekBar.setProgress(value);

                }
            });

            Button resetToDefaults = new Button(inflater.getContext());
            resetToDefaults.setText("Reset to defaults");


            resetToDefaults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBar.setProgress(setting.defaultValue);
                    editText.setText(String.valueOf(setting.defaultValue));
                }
            });


            seekBar.setProgress(value);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    label.setText(setting.name + ":" + progress + setting.unit);
                    if (fromUser) {
                        editText.setText(String.valueOf(progress));
                    } else {
                        editor.putInt(setting.preferencesKey, seekBar.getProgress());
                        editor.apply();
                    }
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
            label.setText(setting.name + ":" + value + setting.unit);

            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));


            label.setPadding(0, 16, 0, 0);

            linearLayout.addView(label);
            linearLayout.addView(seekBar);
            group.addView(editText);
            group.addView(resetToDefaults);
            linearLayout.addView(group);

        }

        return com.sensorberg.android.showcaseutils.ViewHelper.wrapInScrollView(linearLayout, inflater.getContext());
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
