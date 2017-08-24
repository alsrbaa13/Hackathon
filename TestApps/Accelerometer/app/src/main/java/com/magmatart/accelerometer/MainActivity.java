package com.magmatart.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SensorManager sensorManager;
    private SensorEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textview);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        eventListener = new AccmeterListener();
        sensorManager.registerListener(eventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(eventListener);
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        sensorManager.unregisterListener(eventListener);
        super.onDestroy();
    }

    private class AccmeterListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event){
            textView.setText("X : " + event.values[0] + "\nY : " + event.values[1] + "\nZ : " + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accyracy){

        }
    }
}
