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

import com.example.googlemapsnavbar3.detectOffCourse.GeofenceBroadcastReceiver3;
import com.example.googlemapsnavbar3.detectOffCourse.GeofenceHelper3;
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
    private GeofenceHelper3 geofenceHelper3;
    BroadcastReceiver broadcastReceiver3;
    private final GoogleMap googleMap;
    private float radius;
    private int geofence_ID;
    private long time;
    private LatLng latLng;

    public GeofenceBuilder3(Context context, int ID, LatLng latLng, float radius, long time, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;

        this.geofence_ID = ID;
        this.latLng = latLng;
        this.radius = radius;
        this.time = time;

        this.context = context;
        this.latLng = latLng;

        geofenceHelper3 = new GeofenceHelper3(context);
        geofencingClient3 = LocationServices.getGeofencingClient(context);
        broadcastReceiver3 = new GeofenceBroadcastReceiver3();
    }

    private void addGeofence3() {
        //build the geofence object
        Geofence geofence = geofenceHelper3.getGeofence3(geofence_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper3.getGeofencingRequest3(geofence);
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
