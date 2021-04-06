package com.example.googlemapsnavbar3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class CheckpointGeofenceGenerator {

    private String geofence_ID;
    private LatLng latLng;
    private float radius;

    private Context context;
    private Geofence geofence;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    public CheckpointGeofenceGenerator(Context context, String ID, LatLng latLng, float radius) {
        this.context = context;
        this.geofencingClient = LocationServices.getGeofencingClient(context);
        this.geofenceHelper = new GeofenceHelper(context);

        this.geofence_ID = ID;
        this.latLng = latLng;
        this.radius = radius;
    }

    public void create() {
        geofence = geofenceHelper.getGeofence(geofence_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = getPendingIntent();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Geofence success", "Geofence" + geofence_ID + "created");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Geofence error", geofenceHelper.getErrorString(e));
                        }
                    });
        }
    }

    public void remove(){

    }

    public boolean isCreated()
    {
        return true;
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(context, checkpointBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private class checkpointBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.

        }
    }
}


