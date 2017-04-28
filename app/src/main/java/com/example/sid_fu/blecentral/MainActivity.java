package com.example.sid_fu.blecentral;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@TargetApi(23)
public class MainActivity extends AppCompatActivity {

    private static final int SCAN_INTERVAL_MS = 250;
    private static final String TAG = "MainActivity";

    private Handler scanHandler = new Handler();
    private List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
    private ScanSettings scanSettings;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);
        beginScanning();
    }

    public void beginScanning() {
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH);
        //scanSettingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        scanSettings = scanSettingsBuilder.build();

        scanHandler.post(scanRunnable);
    }

    private BluetoothLeScanner scanner;
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
            scanner.startScan(scanFilters, scanSettings, scanCallback);
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            int rssi = result.getRssi();
            byte[] bytes = result.getScanRecord().getBytes();
            Log.e(TAG,String.valueOf(rssi)+"=="+String.valueOf(callbackType)+result.getScanRecord().toString());
            // do something with RSSI value
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG,"error:"+String.valueOf(errorCode));
            // a scan error occurred
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanner.stopScan(scanCallback);
    }
}
