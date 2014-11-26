package com.sensorberg.android.showcaseutils.Activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    public static final int SCAN_QR = 1;
    public static final String TEXT = "text";
    public static final String FORMAT = "format";

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent result = new Intent();
        result.putExtra(TEXT, rawResult.getText());
        result.putExtra(FORMAT, rawResult.getBarcodeFormat().toString());
        setResult(RESULT_OK, result);
        finish();
    }
}
