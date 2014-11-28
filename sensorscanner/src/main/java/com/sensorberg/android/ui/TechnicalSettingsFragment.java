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

    public static final Setting SAMPLE_RATE = new Setting(0, 10, 5, "/s", "Sample Rate", "scanner.sampleRate");
    public static final Setting SECONDS_TO_PLOT = new Setting(0, 10, 5, "s", "Seconds to Plot", "scanner.secondsToPlot");
    public static final Setting EXIT_TIMEOUT = new Setting(0, (int) Constants.Time.ONE_HOUR, 9000, "ms", "Exit Timeout", "scanner.exiTimeout");

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

            final SeekBar seekBar = new SeekBar(inflater.getContext());
            seekBar.setMax(setting.max);

            final TextView label = new TextView(inflater.getContext());

            LinearLayout group = new LinearLayout(inflater.getContext());
            group.setOrientation(LinearLayout.HORIZONTAL);
            final EditText editText = new EditText(inflater.getContext());
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
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
                        editText.setError("Value too high");
                        return;
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
                }
            });


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
            label.setText(setting.name + ":" + seekBar.getProgress() + setting.unit);

            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));


            linearLayout.addView(label);
            linearLayout.addView(seekBar);
            group.addView(editText);
            group.addView(resetToDefaults);
            linearLayout.addView(group);

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
