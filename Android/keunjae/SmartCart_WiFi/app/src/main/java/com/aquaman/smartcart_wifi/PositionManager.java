package com.aquaman.smartcart_wifi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Created by Skj on 2017-08-26.
 */

public class PositionManager implements SensorEventListener{

    Context context;
    //double[] gravityData = new double[2];    //x축, y축 중력 가속도 데이터

    //가속도 센서를 통한 거리 측정 관련 데이터
    double[] accelerationX = {0, 0};    //x축 이전 및 현재 가속도 데이터
    double[] velocityX = {0, 0};    //x축 이전 및 현재 속도 데이터
    double[] positionX = {0, 0};    //x축 이동 거리
    long accelCurrentTime = 0;    //현재 데이터를 얻어 온 시간
    int countMoveEnded = 0;    //움직임이 없는 경우를 카운트하는 변수

    //double[] accelerationY = new double[2];    //y축 이전 및 현재 가속도 데이터
    //double[] velocityY = new double[2];    //y축 이전 및 현재 속도 데이터
    //double[] positionY = new double[2];    //y축 이동 거리

    //가속도와 자기계 센서를 통한 방위 측정 관련 데이터
    float[] acceleration = new float[3];    //x, y, z축 가속도 데이터
    float[] magnetism = new float[3];    //자력 데이터
    float[] R = new float[9];    //x, y, z축에 대한 모든 회전각
    float[] orientation = new float[3];    //방위값
    double orientationValue = 0;    //방위값 저장 변수
    int direction;    //동서남북 저장 변수 1 : north, 2 : east, 3 : south, 4 : west
    int countGotAverage = 0;    //평균 계산 횟수 카운팅 변수
    double averageValue = 0.0;    //평균 값


    //자이로스코프 센서를 통한 회전 각도 측정 관련 데이터
    long gyroCurrentTIme = 0;    //현재 데이터를 얻어온 시간
    double[] gyroZ = {0.0, 0.0};    //z축 회전 각속도
    double yaw = 0.0;    //z축 회전 각도(라디안)
    double radianToDegree = 180.0 / Math.PI;    //라디안을 각도로 변환시키는 상수
    double turnedDegree = 0.0;    //z축 회전 각도(도)

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

                //후에 자기계 센서를 위해 가속도 값 얻어오기
                System.arraycopy(event.values, 0, acceleration, 0, event.values.length);

                if (accelCurrentTime == 0)
                    accelCurrentTime = System.currentTimeMillis();
                else {
                    long accelLastTime = accelCurrentTime;
                    accelCurrentTime = System.currentTimeMillis();

                    //이전 가속도와 현재 가속도 측정 시간 gap 측정
                    //msec를 sec로 변환
                    double deltaT = ((double) (accelCurrentTime - accelLastTime) / 1000);
                    double accelerationWindow = 0.25;    //가속도 센서값 노이즈 판단 기준점
                    //Log.d("deltaT", Double.toString(deltaT));

                    //low-pass filter
                    //gravityData[0] = alpha * gravityData[0] + (1 - alpha) * event.values[0];    //x축의 중력 가속도

                    //측정된 데이터에서 가속도만을 알아내기 위해 중력 가속도 데이터 제거
                    //accelerationX[1] = event.values[0] - gravityData[0];

                    accelerationX[1] = (double)(event.values[0]);    //현재 가속도 값 얻어오기
                    //accelerationX[1] = Double.parseDouble(String.format("%.10f", accelerationX[1]));    //소수 열째자리까지로 표현
                    accelerationX[1] = FilteringWindow(accelerationX[1], accelerationWindow);    //노이즈 값 무시

                    //가속도 적분 -> 속도
                    velocityX[1] = CalcIntegral(velocityX[0], accelerationX[0], accelerationX[1], deltaT);
                    //velocityX[1] = Double.parseDouble(String.format("%.10f", velocityX[1]));    //소수 열째자리까지 표현

                    //속도 적분 -> 거리
                    positionX[1] = CalcIntegral(positionX[0], velocityX[0], velocityX[1], deltaT);
                    positionX[1] = Double.parseDouble(String.format("%.3f", positionX[1]));    //소수 셋째자리까지 표현

                    ((MainActivity) context).tvAcceleration.setText(Double.toString(accelerationX[1]));
                    ((MainActivity) context).tvVelocity.setText(Double.toString(velocityX[1]));
                    ((MainActivity) context).tvDistance.setText(Double.toString(positionX[1]));

                    //정지한 상태인지 체크
                    MovementEndCheck();

                    //현재 데이터를 이전 데이터로 저장
                    accelerationX[0] = accelerationX[1];
                    velocityX[0] = velocityX[1];
                    positionX[0] = positionX[1];

                }
                break;

             //방향 센서 데이터 변화에 따른 처리
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, magnetism, 0, event.values.length);    //현재 자기계 센서값 읽어오기
                SensorManager.getRotationMatrix(R, null, acceleration, magnetism);    //회전 행렬 값 얻어오기
                SensorManager.getOrientation(R, orientation);    //방위값 저장

//                if(orientation[0] > 0.3)
//                    direction = 2;
//                else if(orientation[0] >= -0.3 && orientation[0] <= 0.3)
//                    direction = 1;
//                else if(orientation[0] < -0.3)
//                    direction = 3;
                //방위 부호가 마이너스이면 플러스로 변환
                if(Math.toDegrees((double)orientation[0]) < 0.0)
                    orientationValue = Math.toDegrees((double)orientation[0]) + 360.0;
                else
                    orientationValue = Math.toDegrees((double)orientation[0]);

                //평균값 계산이 완료되면 다음 동작 수행
                //if(CountCalcAverage(orientationValue) == 10)

                ((MainActivity) context).tvOrientation.setText(Double.toString(orientationValue));
                break;

            //자이로 센서 데이터 변화에 따른 처리
            case Sensor.TYPE_GYROSCOPE:
                if(gyroCurrentTIme == 0)
                    gyroCurrentTIme = System.currentTimeMillis();
                else {
                    long gyroLastTime = gyroCurrentTIme;
                    gyroCurrentTIme = System.currentTimeMillis();

                    //시간 변화량 측정
                    double deltaT = ((double)(gyroCurrentTIme - gyroLastTime) / 1000);    //msec -> sec
                    double gyroWindow = 0.02;

                    //z축 회전 각속도 측정 및 노이즈 무시
                    gyroZ[1] = event.values[2];
                    gyroZ[1] = FilteringWindow(gyroZ[1], gyroWindow);

                    //z축 회전 각속도 적분 -> 회전한 각도
                    yaw = CalcIntegral(yaw, gyroZ[0], gyroZ[1], deltaT);
                    turnedDegree = yaw * radianToDegree;    //radian -> degree
                    ((MainActivity)context).tvTurnedDegree.setText(Double.toString(turnedDegree));

                    //현재 각속도를 이전 각속도로 저장
                    gyroZ[0] = gyroZ[1];
                }
                break;

        }
    }

    //움직임이 없을 떄 센서 값 노이즈 발생 시 제거하는 메소드
    double FilteringWindow(double value, double window) {
        //final double window = 0.25;    //노이즈인지 판단하는 기준점
        if(Math.abs(value) <= window)
            value = 0;
        return value;
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
    //baseData : 이전까지의 속도/거리, prevData : previous 가속도/속도, curData : current 가속도/속도, time : 시간 변화량
    double CalcIntegral(double baseData, double prevData, double curData, double time) {
        double integratedData = 0;

        //이전 데이터와 현재 데이터의 평균을 높이로 하여 그래프 상 직사각형의 넓이를 구함
        integratedData = baseData + (prevData + ((curData - prevData) / 2)) * time;
        return integratedData;
    }

    //10번 방위 값을 읽어와 방위값의 평균을 구하는 메소드
    int CountCalcAverage(double value) {
        if(countGotAverage < 10) {
            averageValue += value;
            countGotAverage++;
            averageValue = averageValue / (double) countGotAverage;
            return countGotAverage;
        }
        else
            return 0;
    }

}
