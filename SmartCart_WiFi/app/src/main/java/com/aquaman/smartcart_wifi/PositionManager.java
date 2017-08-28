package com.aquaman.smartcart_wifi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;


/**
 * Created by Skj on 2017-08-26.
 */

public class PositionManager implements SensorEventListener{

    Context context;
    float[] gravityData = new float[2];    //x축, y축 중력 가속도 데이터
    float[] accelerationX = {0, 0};    //x축 이전 및 현재 가속도 데이터
    float[] velocityX = {0, 0};    //x축 이전 및 현재 속도 데이터
    float[] positionX = {0, 0};    //x축 이동 거리

    float[] accelerationY = new float[2];    //y축 이전 및 현재 가속도 데이터
    float[] velocityY = new float[2];    //y축 이전 및 현재 속도 데이터
    float[] positionY = new float[2];    //y축 이동 거리
    float lastTime = 0, currentTime = 0;
    float deltaT = 0;


    public PositionManager(Context context) {
        this.context = context;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            //가속도 센서의 데이터 변화에 따른 처리
            case Sensor.TYPE_ACCELEROMETER:
                //중력 가속도 데이터를 구하기 위해 사용되는 low-pass filter 적용 시 사용되는 비율 데이터
                //t : 센서가 가속도의 63%를 인지하기 위해 걸리는 시간
                //dt : 이벤트 전송율 혹 이벤트 전송속도
                //alpha = t / (t + dt)
                //final float alpha = (float) 0.8;

                if (currentTime == 0)
                    currentTime = System.currentTimeMillis();
                else {
                    lastTime = currentTime;
                    currentTime = System.currentTimeMillis();
                    //Log.d("지난 시간", Float.toString(lastTime));
                    //Log.d("지금 시간", Float.toString(currentTime));
                    deltaT = ((currentTime - lastTime) / 1000);   //밀리초를 초로 변환
                    Log.d("deltaT", Float.toString(deltaT));

                    //low-pass filter
                    //gravityData[0] = alpha * gravityData[0] + (1 - alpha) * event.values[0];    //x축의 중력 가속도

                    //측정된 데이터에서 가속도만을 알아내기 위해 중력 가속도 데이터 제거
                    //accelerationX[1] = event.values[0] - gravityData[0];

                    //가속도 적분 -> 속도, 속도 적분 -> 거리
                    velocityX[1] = CalcIntegral(velocityX[0], accelerationX[0], accelerationX[1], deltaT);
                    positionX[1] = CalcIntegral(positionX[0], velocityX[0], velocityX[1], deltaT);

                    //Log.d("가속도", Float.toString(accelerationX[1]));
                    //Log.d("속도", Float.toString(velocityX[1]));
                    //Log.d("거리", Float.toString(positionX[1]));

                    //현재 데이터를 이전 데이터로 저장
                    accelerationX[0] = accelerationX[1];
                    velocityX[0] = velocityX[1];
                    positionX[0] = positionX[1];

                    String accelStr = Float.toString(accelerationX[0]);
                    String veloStr = Float.toString(velocityX[0]);
                    String posStr = Float.toString(positionX[0]);

                    ((MainActivity) context).txtAccel.setText(accelStr);
                    ((MainActivity) context).txtVelo.setText(veloStr);
                    ((MainActivity) context).txtDis.setText(posStr);


                }
        }
    }
    //적분 수행 메소드
    //baseData : 이전까지의 속도/거리, prevData : 이전 가속도/속도, curData : 현재 가속도/속도, time : 시간 변화량
    public float CalcIntegral(float baseData,float prevData, float curData, float time) {
        float integratedData = 0;
        integratedData = baseData + (prevData + ((curData - prevData) / 2)) * time;

        return integratedData;
    }

}
