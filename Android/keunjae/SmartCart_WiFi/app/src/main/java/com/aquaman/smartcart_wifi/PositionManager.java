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
    double[] gravityData = new double[2];    //x축, y축 중력 가속도 데이터
    double[] accelerationX = {0, 0};    //x축 이전 및 현재 가속도 데이터
    double[] velocityX = {0, 0};    //x축 이전 및 현재 속도 데이터
    double[] positionX = {0, 0};    //x축 이동 거리

    double[] accelerationY = new double[2];    //y축 이전 및 현재 가속도 데이터
    double[] velocityY = new double[2];    //y축 이전 및 현재 속도 데이터
    double[] positionY = new double[2];    //y축 이동 거리
    long currentTime = 0;    //현재 데이터를 얻어 온 시간
    double deltaT = 0;    //이전 데이터를 얻어 온 시간과 현재 데이터를 얻어온 시간 사이 시간 변화량
    int countMoveEnded = 0;    //움직임이 없는 경우를 카운트하는 변수

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
                    long lastTime = currentTime;
                    currentTime = System.currentTimeMillis();
                    //Log.d("지난 시간", Float.toString(lastTime));
                    //Log.d("지금 시간", Float.toString(currentTime));
                    deltaT = ((double)(currentTime - lastTime) / 1000);   //msec를 sec로 변환
                    Log.d("deltaT", Double.toString(deltaT));

                    //low-pass filter
                    //gravityData[0] = alpha * gravityData[0] + (1 - alpha) * event.values[0];    //x축의 중력 가속도

                    //측정된 데이터에서 가속도만을 알아내기 위해 중력 가속도 데이터 제거
                    //accelerationX[1] = event.values[0] - gravityData[0];

                    accelerationX[1] = event.values[0];    //현재 가속도 값 얻어오기
                    accelerationX[1] = Double.parseDouble(String.format("%.10f", accelerationX[1]));    //소수 셋째자리까지로 표현
                    FilteringWindow();    //노이즈 값 무시

                    //가속도 적분
                    velocityX[1] = CalcIntegral(velocityX[0], accelerationX[0], accelerationX[1], deltaT);
                    velocityX[1] = Double.parseDouble(String.format("%.10f", velocityX[1]));    //소수 셋째자리까지 표현

                    //속도 적분
                    positionX[1] = CalcIntegral(positionX[0], velocityX[0], velocityX[1], deltaT);
                    positionX[1] = Double.parseDouble(String.format("%.3f", positionX[1]));    //소수 셋째자리까지 표현

                    //Log.d("가속도", Double.toString(accelerationX[1]));
                    //Log.d("속도", Double.toString(velocityX[1]));
                    //Log.d("거리", Double.toString(positionX[1]));

                    //현재 데이터를 이전 데이터로 저장
                    accelerationX[0] = accelerationX[1];
                    velocityX[0] = velocityX[1];
                    positionX[0] = positionX[1];

                    String accelStr = Double.toString(accelerationX[0]);
                    String veloStr = Double.toString(velocityX[0]);
                    String posStr = Double.toString(positionX[0]);

                    ((MainActivity) context).txtAccel.setText(accelStr);
                    ((MainActivity) context).txtVelo.setText(veloStr);
                    ((MainActivity) context).txtDis.setText(posStr);

                    MovementEndCheck();
                }
        }
    }

    //움직임이 없을 때 가속도 값 노이즈 발생 시 제거하는 메소드
    void FilteringWindow() {
        final double window = 0.25;    //노이즈인지 판단하는 기준점
        if(Math.abs(accelerationX[1]) <= window)
            accelerationX[1] = 0;
    }

    //움직임이 없을 때 속도를 강제로 0으로 세팅하는 메소드
    //움직임이 없으면 FilteringWindow() 메소드로 가속도가 0으로 다운되니 속도도 0으로 다운시킴
    void MovementEndCheck() {
        //if(Math.abs(velocityX[1]) <= 0.1)
        //    countMoveEnded++;
        if(accelerationX[1] == 0)
            countMoveEnded++;
        else
            countMoveEnded = 0;
        if(countMoveEnded >= 5) {
            velocityX[1] = 0;
            velocityX[0] = 0;
        }
    }

    //적분 수행 메소드
    //baseData : 이전까지의 속도/거리, prevData : 이전 가속도/속도, curData : 현재 가속도/속도, time : 시간 변화량
    public double CalcIntegral(double baseData, double prevData, double curData, double time) {
        double integratedData = 0;

        //이전 데이터와 현재 데이터의 평균을 높이로 하여 그래프 상 직사각형의 넓이를 구함
        integratedData = baseData + (prevData + ((curData - prevData) / 2)) * time;
        return integratedData;
    }

}
