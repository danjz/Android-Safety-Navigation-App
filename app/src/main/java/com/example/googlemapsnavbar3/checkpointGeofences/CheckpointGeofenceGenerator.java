package com.example.googlemapsnavbar3.checkpointGeofences;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.googlemapsnavbar3.GeofenceHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class CheckpointGeofenceGenerator {

    private String geofence_ID;
    private LatLng latLng;
    private float radius;
    private long time;

    private Context context;
    private Geofence geofence;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private GoogleMap googleMap;
    private PendingIntent pendingIntent;

    private Circle circle;

    public CheckpointGeofenceGenerator(Context context, String ID, LatLng latLng, float radius, long time, GoogleMap googleMap) {
        this.context = context;
        this.geofencingClient = LocationServices.getGeofencingClient(context);
        this.geofenceHelper = new GeofenceHelper(context);
        this.googleMap = googleMap;

        this.geofence_ID = ID;
        this.latLng = latLng;
        this.radius = radius;
        this.time = time;

        
    }

    public void create() {
        geofence = geofenceHelper.getGeofence(geofence_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent);
            Log.d("Geofence success", geofence_ID + "created");
            //Create new circle at destination coordinates
            CircleOptions checkCircleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(200); // In meters
            circle = googleMap.addCircle(checkCircleOptions);
            CheckpointTimerHandler timerHandler = CheckpointTimerHandler.getInstance();
            timerHandler.addCheckpoint(time*1000); //* 1000 to convert to milliseconds
        }
    }

    public void remove(){
        geofencingClient.removeGeofences(getPendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Checkpoint Arrival", "Geofence removed");
                        circle.remove();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Checkpoint Arrival", "failed to remove geofence");
                    }
                });
    }

    private PendingIntent getPendingIntent(){
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(context, CheckpointBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

}


