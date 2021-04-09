package com.example.googlemapsnavbar3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Toast.makeText(context, "Geofence triggered",Toast.LENGTH_SHORT).show();
        context.sendBroadcast(new Intent("GEOFENCE_TRIGGER"));

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()) {
            Log.d("Geofence receiver", "onReceive: Error reciveing geofence event");
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for (Geofence geofence: geofenceList) {
            Log.d("Geofence receiver", geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch(transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER",Toast.LENGTH_SHORT).show();
                Log.d("Geofence receiver", "GEOFENCE_TRANSITION_ENTER");
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                Log.d("Geofence receiver", "GEOFENCE_TRANSITION_DWELL");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                Log.d("Geofence receiver", "GEOFENCE_TRANSITION_EXIT");
                break;
        }
    }

    public void startTimer(int ms) {
        CountDownTimer countDown = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // milliseconds (1000 in 1 sec) - update text if at least 1s remaining.
                if (millisUntilFinished > 1000) {
                    Log.d("PARSER TIMER TICK", "onTick: tick");
                    //this.durationView.setText(String.valueOf(millisUntilFinished/1000));
                }
            }

            @Override
            public void onFinish() {
                Log.d("PARSER TIMER FINISH", "onFinish: timer finished");

            }
        };

        countDown.start();
    }
}