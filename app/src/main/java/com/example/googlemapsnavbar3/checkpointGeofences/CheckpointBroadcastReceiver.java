package com.example.googlemapsnavbar3.checkpointGeofences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class CheckpointBroadcastReceiver extends BroadcastReceiver {

    /**
     * <p>Triggers when a the user arrives checkpoint Geofence </p> <p>It cancels the old timer, and starts the next timer.</p>
     * <p>It also deletes the old geofence.</p>
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Checkpoint Receiver", "arrived at checkpoint");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        // This method is called when the BroadcastReceiver is receiving
        //Get the timer handler
        CheckpointTimerHandler timerHandler = CheckpointTimerHandler.getInstance();
        //Cancel the old timer and start the new timer
        timerHandler.nextCheckpoint();
        //Delete the (now) old geofence
        String requestID = geofenceList.get(0).getRequestId();
        List<String> requestIDList = new ArrayList<>();
        requestIDList.add(requestID);

        GeofencingClient client = new GeofencingClient(context);
        client.removeGeofences(requestIDList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Checkpoint Arrival", "Geofence removed");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Checkpoint Arrival", "failed to remove geofence");
                    }
                });
    }
}
