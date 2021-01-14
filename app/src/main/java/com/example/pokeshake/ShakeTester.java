package com.example.pokeshake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

import java.util.ArrayList;

public class ShakeTester extends AppCompatActivity implements SensorEventListener {
    private TextView xTextView, yTextView, zTextView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerAvail,itsNotFirstTime = false;
    private float currX, currY, currZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThreshold = 5f;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_tester);

        xTextView = findViewById(R.id.xTextView);
        yTextView = findViewById(R.id.yTextView);
        zTextView = findViewById(R.id.zTextView);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvail = false;
        }
        else{
            xTextView.setText("Accelerometer sensor is not available");
            isAccelerometerAvail = false;
        }
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
    protected void onResume(){
        super.onResume();
        if(isAccelerometerAvail){
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(isAccelerometerAvail){
            sensorManager.unregisterListener(this);
        }
    }
}