package com.sensorberg.android.openplayground.showcase.debug.test;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.robotium.solo.Timeout;
import com.sensorberg.android.openplayground.showcase.debug.test.robotiumHelper.MySolo;
import com.sensorberg.android.sensorscanner.sample.AppFragmentActivity;
import com.sensorberg.android.sensorscanner.sample.R;

import java.io.File;


public class Screenshots extends ActivityInstrumentationTestCase2<AppFragmentActivity> {
  	private MySolo solo;
    private int i = 0;
    private Point deviceSize;
    private File screenshotFolder;

    public Screenshots() {
		super(AppFragmentActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new MySolo(getInstrumentation(), getActivity());
		getActivity();

        deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);
        solo.getConfig().screenshotFileType = Solo.Config.ScreenshotFileType.PNG;

        screenshotFolder = new File(solo.getConfig().screenshotSavePath);
        screenshotFolder.delete();
        screenshotFolder.mkdirs();

        assertTrue(screenshotFolder.exists());
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        assertTrue(screenshotFolder.listFiles().length > 0);
        super.tearDown();
  	}
  
	public void testRun() {
		solo.waitForActivity(AppFragmentActivity.class, 2000);
		Timeout.setSmallTimeout(2000);

        screenshot();

        solo.clickOnActionBarItem(R.id.action_technical_settings);

        screenshot();

        solo.clickOnActionBarItem(R.id.action_technical_scanner);

        screenshot();

        //only show the first beacon
        solo.clickInList(1);
        //give it some time to show the beacon
        solo.sleep(2000);

        screenshot();

        //switch to the plot fragment
        solo.clickInList(1);

        //wait for some values to appear
        solo.sleep(5000);

        screenshot();
	}

    private void screenshot() {

        solo.sleep(500);
        String name = "screenshot" + i++;


        solo.takeScreenshot(name);
//        Spoon.screenshot(getActivity(), name);
    }
}
