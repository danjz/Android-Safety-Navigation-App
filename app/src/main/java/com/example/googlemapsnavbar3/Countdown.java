package com.example.googlemapsnavbar3;

import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
This "Countdown.java" is related to FallAndFightDetection. When the accelerometer value
is up to the threshold, the Countdown timer will start. If users do not cancel the Countdown timer
within 20 seconds, the app will automatically contact with the emergency contact.
 */

public class Countdown extends AppCompatActivity {
    private TextView tvRenamingTime;
    private boolean isOk;
    private boolean isDo;

    private CountDownTimer timer = new CountDownTimer(20*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvRenamingTime.setText(formatTime(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            isOk = false;
            tvRenamingTime.setText("00:00");
            if(!isDo){
                //send message with emergency contact
                //callPhone()
            }

            Intent intent = new Intent(Countdown.this,FallAndFightDetection.class);
            startActivity(intent);
        }
    };

    private String formatTime(long millisecond) {
        int minute;
        int second;

        minute = (int)((millisecond/1000)/60);
        second = (int)((millisecond/1000)%60);

        if(minute<10){
            if(second<10){
                return "0"+minute+":"+"0"+second;
            }else{
                return "0"+minute+":"+second;
            }
        }else{
            if(second<10){
                return minute+":"+"0"+second;
            }else{
                return minute+":"+second;
            }
        }
    }
}
