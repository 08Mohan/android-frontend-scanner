package com.sensorberg.android.sensorscanner.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sensorberg.android.sensorscanner.sample.fragments.SampleBeaconScanFragment;
import com.sensorberg.android.ui.TechnicalScannerFragment;
import com.sensorberg.android.ui.TechnicalSettingsFragment;


public class AppFragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SampleBeaconScanFragment())
                .commit();
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(getString(R.strings.));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcome, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_technical_scanner) {
            TechnicalScannerFragment newFragment = new TechnicalScannerFragment();
            newFragment.setContainerId(R.id.container);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, newFragment)
                    .commit();
            return true;
        } else if (id == R.id.action_technical_settings) {
            TechnicalSettingsFragment newFragment = new TechnicalSettingsFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, newFragment)
                    .commit();
            return true;
        } else if (id == R.id.action_simple_scanner){
            SampleBeaconScanFragment newFragment = new SampleBeaconScanFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, newFragment)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
