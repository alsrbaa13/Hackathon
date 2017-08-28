package com.aquaman.smartcart_wifi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SensorManager mSensorManager;
    PositionManager posManager;
    //AccemeterListener eventListener;
    TextView txtAccel, txtVelo, txtDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAccel = (TextView)findViewById(R.id.acceleromter);
        txtVelo = (TextView)findViewById(R.id.velocity);
        txtDis = (TextView)findViewById(R.id.distance);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        posManager = new PositionManager(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(posManager, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(posManager);
    }
}
