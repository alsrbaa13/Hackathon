package com.magmatart.wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import static com.magmatart.wifiscanner.R.id.checkbox;
import static com.magmatart.wifiscanner.R.id.text;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    WifiManager mWifiManager;
    List<ScanResult> scanDatas;
    IntentFilter mIntentFilter;
    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!(mWifiManager.isWifiEnabled())){
            mWifiManager.setWifiEnabled(true);
        }

        mText = (TextView)findViewById(R.id.wifitext);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int accessPermissionResult = checkSelfPermission(android.Manifest.permission.ACCESS_WIFI_STATE);
            int changePermissionResult = checkSelfPermission(android.Manifest.permission.CHANGE_WIFI_STATE);
            int locationPermissionResult = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            if((accessPermissionResult == PackageManager.PERMISSION_DENIED) || (changePermissionResult == PackageManager.PERMISSION_DENIED) || (locationPermissionResult == PackageManager.PERMISSION_DENIED))
            {
                /*
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_WIFI_STATE)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, 1000);
                    }
                }
                else{
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, 1000);
                }
                */

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.CHANGE_WIFI_STATE, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }

            /*
            if(changePermissionResult == PackageManager.PERMISSION_DENIED){
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.CHANGE_WIFI_STATE)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(new String[]{android.Manifest.permission.CHANGE_WIFI_STATE}, 1000);
                    }
                }
                else{
                    requestPermissions(new String[]{android.Manifest.permission.CHANGE_WIFI_STATE}, 1000);
                }
            }
            */
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        mIntentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(wifiReceiver, mIntentFilter);

        mWifiManager.startScan();
    }

    @Override
    protected void onPause(){
        super.onPause();
        getApplicationContext().unregisterReceiver(wifiReceiver);
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuffer buffer = new StringBuffer("");
            final String action = intent.getAction();

            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                scanDatas = mWifiManager.getScanResults();

                for(ScanResult result : scanDatas){
                    buffer.append("SSID : " + result.SSID + "\nLevel : " + result.level + "\nBSSID : " + result.BSSID + "\nVenueName : " + result.venueName)
                            .append("\nC-Width : " + result.channelWidth + "\nFreq : " + result.frequency + "\nT-Stmp : " + result.timestamp + "\n\n");
                }
                mText.setText(buffer);
                buffer = null;
                buffer = new StringBuffer("");
            }
        }
    };
}
