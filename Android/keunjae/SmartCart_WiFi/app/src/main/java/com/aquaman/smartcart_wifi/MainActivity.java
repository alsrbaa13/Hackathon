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
    TextView tvAcceleration, tvVelocity, tvDistance, tvOrientation, tvTurnedDegree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAcceleration = (TextView)findViewById(R.id.acceleromter);
        tvVelocity = (TextView)findViewById(R.id.velocity);
        tvDistance = (TextView)findViewById(R.id.distance);
        tvOrientation = (TextView)findViewById(R.id.orientation);
        tvTurnedDegree = (TextView)findViewById(R.id.turnedDegree);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        posManager = new PositionManager(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(posManager, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(posManager, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(posManager, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(posManager);
    }
}
