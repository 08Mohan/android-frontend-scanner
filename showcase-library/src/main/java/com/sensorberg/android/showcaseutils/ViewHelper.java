package com.sensorberg.android.showcaseutils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ViewHelper {
    public static View wrapInScrollView(View rootView, Context context) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView.addView(rootView);
        return scrollView;
    }
}
