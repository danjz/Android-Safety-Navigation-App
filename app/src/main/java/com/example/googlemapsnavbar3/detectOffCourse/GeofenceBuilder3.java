package com.example.googlemapsnavbar3.detectOffCourse;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.googlemapsnavbar3.GeofenceBroadcastReceiver;
import com.example.googlemapsnavbar3.GeofenceHelper;
import com.example.googlemapsnavbar3.PlaceList;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

public class GeofenceBuilder3 {
    /**Using hardcoded Geofence ID atm for testing... (Need to change to accept an array of checkpoints)
     **Radius is intended to be hardcoded...
     **/
    private Context context;
    private GeofencingClient geofencingClient3;
    private GeofenceHelper geofenceHelper3;
    BroadcastReceiver broadcastReceiver3;
    private final float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "Test";
    private LatLng latLng;

    public GeofenceBuilder3(Context context, LatLng latLng) {

        this.context = context;
        this.latLng = latLng;

        geofenceHelper3 = new GeofenceHelper(context);
        geofencingClient3 = LocationServices.getGeofencingClient(context);
        broadcastReceiver3 = new GeofenceBroadcastReceiver();
    }

    private void addGeofence3() {
        //build the geofence object
        Geofence geofence = geofenceHelper3.getGeofence(GEOFENCE_ID, latLng, GEOFENCE_RADIUS,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper3.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper3.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient3.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Geofence success", "Geofence added");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Geofence error", geofenceHelper3.getErrorString(e));
                        }
                    });
        }
    }
}
