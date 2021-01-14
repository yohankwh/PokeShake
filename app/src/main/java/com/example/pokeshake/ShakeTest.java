package com.example.pokeshake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ShakeTest extends Fragment implements SensorEventListener {
    private TextView xTextView, yTextView, zTextView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerAvail,itsNotFirstTime = false;
    private float currX, currY, currZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThreshold = 5f;
    private Vibrator vibrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View view = inflater.inflate(R.layout.fragment_shake_test, container, false);
        xTextView = view.findViewById(R.id.xTextView);
        yTextView = view.findViewById(R.id.yTextView);
        zTextView = view.findViewById(R.id.zTextView);

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvail = false;
        }
        else{
            xTextView.setText("Accelerometer sensor is not available");
            isAccelerometerAvail = false;
        }
       return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xTextView.setText(event.values[0]+"m/s2");
        yTextView.setText(event.values[1]+"m/s2");
        zTextView.setText(event.values[2]+"m/s2");

        currX = event.values[0];
        currY = event.values[1];
        currZ = event.values[2];

        if(itsNotFirstTime){
            xDifference = Math.abs(lastX - currX);
            yDifference = Math.abs(lastY - currY);
            zDifference = Math.abs(lastZ - currZ);

            if((xDifference > shakeThreshold && yDifference > shakeThreshold)||(xDifference > shakeThreshold && zDifference > shakeThreshold) ||
                    (yDifference > shakeThreshold && zDifference > shakeThreshold)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                else{
                    vibrator.vibrate(500);
                    //deprecated on API
                }
            }

        }

        lastX = currX;
        lastY = currY;
        lastZ = currZ;
        itsNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume(){
        super.onResume();
        if(isAccelerometerAvail){
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(isAccelerometerAvail){
            sensorManager.unregisterListener(this);
        }
    }
}