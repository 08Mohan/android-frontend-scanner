package com.sensorberg.android.openplayground.showcase.debug.test.robotiumHelper;

import android.app.Activity;
import android.app.Instrumentation;

import com.robotium.solo.Solo;

/**
 * Created by falkorichter on 03/12/14.
 */
public class MySolo extends Solo{
    private  Activity activity;

    public MySolo(Instrumentation instrumentation, Activity activity) {
        super(instrumentation, activity);
        this.activity = activity;
    }


    public void clickOnViewWithString(int stringId) {
        clickOnView(getView(activity.getText(stringId).toString()));
    }

    public void clickOnText(int stringid) {
        clickOnText(activity.getString(stringid));
    }

}
