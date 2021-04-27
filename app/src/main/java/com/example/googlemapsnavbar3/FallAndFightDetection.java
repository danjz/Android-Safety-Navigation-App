package com.example.googlemapsnavbar3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

public class FallAndFightDetection extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;//define sensor Manager
    private Vibrator vibrator;// define a vibrator

    private TextView tvx;
    private  TextView tvy;
    private  TextView tvz;
    private  TextView tv_max;
    private float max = 0;
    private  TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_detectionfallandfight);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//obtain sensor Manager
        vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);//obtain vibrator

        tvx = (TextView)findViewById(R.id.tvx);
        tvy = (TextView)findViewById(R.id.tvy);
        tvz = (TextView)findViewById(R.id.tvz);
        tv_max = (TextView)findViewById(R.id.tv_max);
        temp = (TextView)findViewById(R.id.temp);

        Button start = (Button)findViewById(R.id.bt_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FallAndFightDetection.this,"run",Toast.LENGTH_SHORT).show();
                sensorManager.unregisterListener(FallAndFightDetection.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                sensorManager.registerListener(FallAndFightDetection.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        Button end = (Button)findViewById(R.id.bt_end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(FallAndFightDetection.this);
                Toast.makeText(FallAndFightDetection.this,"stop",Toast.LENGTH_SHORT).show();
                tvx.setText("ACC_X:");
                tvy.setText("ACC_Y:");
                tvz.setText("ACC_Z:");
                tv_max.setText("tri_Max:");
            }
        });

        Button back = (Button)findViewById(R.id.bt_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(FallAndFightDetection.this);
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

            tvx.setText("ACC_X: " + Float.toString(values[0]));
            tvy.setText("ACC_Y: " + Float.toString(values[1]));
            tvz.setText("ACC_Z: " + Float.toString(values[2]));

            //calculate the max accelerometer values among x,y,z.
            max = Math.max(values[0],values[1]);
            max = Math.max(max,values[2]);
            tv_max.setText("ACC_Max: " + Float.toString(max));

            float g = 9.8f;

            //the value of triA is the threshold of accelerometer
            if(max > 2.5 * g){

                vibrator.vibrate(1000);//Duration of vibration

                temp.setText("dangerous");
                temp.setTextColor(Color.RED);

                //start to count down (20s)
                //Intent intent = new Intent(FallAndFightDetection.this, Countdown.class);
                //startActivity(intent);
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
