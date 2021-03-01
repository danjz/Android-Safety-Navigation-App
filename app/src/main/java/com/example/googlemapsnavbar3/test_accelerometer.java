package com.example.googlemapsnavbar3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class test_accelerometer extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;//define sensor Manager
    private TextView tvx;
    private  TextView tvy;
    private  TextView tvz;
    private  TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_accelerometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//obtain sensor Manager

        tvx = (TextView)findViewById(R.id.tvx);
        tvy = (TextView)findViewById(R.id.tvy);
        tvz = (TextView)findViewById(R.id.tvz);
        temp = (TextView)findViewById(R.id.temp);

        Button start = (Button)findViewById(R.id.bt_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(test_accelerometer.this,"run",Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(test_accelerometer.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                sensorManager.registerListener(test_accelerometer.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        Button end = (Button)findViewById(R.id.bt_end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(test_accelerometer.this);
                Toast.makeText(test_accelerometer.this,"stop",Toast.LENGTH_SHORT).show();
                tvx.setText("ACC_X:");
                tvy.setText("ACC_Y:");
                tvz.setText("ACC_Z:");
            }
        });

        Button back = (Button)findViewById(R.id.bt_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume()
    {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {

            float[] values = event.values;
//            float[] gravity = new float[3];
//
//            // alpha 由 t / (t + dT)得到
//            // t为滤波器时间常量，为传感器单次采样时间
//            // dT为采样频率
//            final float alpha = (float) 0.1;
//            for(int i = 0 ;i < 3; i++){
//                gravity[i]  = alpha * gravity[i] + (1 - alpha) * values[i];
//                values[i] = values[i] - gravity[i];
//            }

            tvx.setText("ACC_X: " + Float.toString(values[0]));
            tvy.setText("ACC_Y: " + Float.toString(values[1]));
            tvz.setText("ACC_Z: " + Float.toString(values[2]));


            if(values[0] > 5){
                temp.setText("dangerous");
                temp.setTextColor(Color.RED);
            }
            else{
                temp.setText("safe");
                temp.setTextColor(Color.GREEN);
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

}