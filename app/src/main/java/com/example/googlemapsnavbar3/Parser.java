package com.example.googlemapsnavbar3;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Parser extends AsyncTask<Void, Void, String> {

    private GoogleMap googleMap;
    private TextView durationView;
    private String origin;
    private String destination;
    private Context context;

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    BroadcastReceiver broadcastReceiver;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "DEST_GEOFENCE";
    private CountDownTimer countDownTimer;

    private int durationValue;
    private double lat;
    private double lng;

    
    private PlaceList checkpoints;

    /**
     * Constructor used to initialize class variables
     *
     * @param googleMap GoogleMap instance used for accessing its elements
     * @param durationText The time to arrive at destination text box
     * @param origin The start location of the route
     * @param destination The destination location of the route
     * @param context The context of the fragment calling this constructor
     * @param checkpoints  checkpoints
     */
    public Parser(GoogleMap googleMap, TextView durationText, String origin, String destination, Context context, PlaceList checkpoints) {
        this.googleMap = googleMap;
        this.durationView = durationText;
        this.origin = origin;
        this.destination = destination;
        this.context = context;
        this.checkpoints = checkpoints;
        this.durationValue = 0;

        geofenceHelper = new GeofenceHelper(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        broadcastReceiver = new GeofenceBroadcastReceiver();
    }

    /**
     * turns JSON string into JSON objects and array in order to extract relevant data
     *
     * @param arg0 Void so no arguments are used
     * @return encoded polyline string
     */
    @Override
    protected String doInBackground(Void...arg0) {
        //Create instance of httpHandler and call its getJSON() method
        HttpHandler httpHandler = new HttpHandler(origin,destination,checkpoints);
        //Get JSON string
        String jsonString = httpHandler.getJSON();

        //Make sure string isn't empty
        if (jsonString != null) {
            //Turn string into JSON objects / arrays
            try {
                //Turn the entire string into one JSON object
                JSONObject jsonObj = new JSONObject(jsonString);

                //Get the JSON array containing data such as the route legs, summary and polyline_overview
                JSONArray routes = jsonObj.getJSONArray("routes");

                //from the routes array, extract the overview_polyline object
                JSONObject overviewPolylineObj = routes.getJSONObject(0).getJSONObject("overview_polyline");

                //store the encoded_polyline value in a string
                String encodedPolyline = overviewPolylineObj.getString("points");

                //get duration of route, display it on map text view
                JSONArray legsArr = routes.getJSONObject(0).getJSONArray("legs");
                //Log.d("legsObj log",legsObj.getString(0));

                for(int i = 0; i < legsArr.length(); i++) {
                    JSONObject duration  = legsArr.getJSONObject(i).getJSONObject("duration");
                    this.durationValue += duration.getInt("value");

                }


                //Log.d("duration log",duration.toString());


                this.durationView.setText(String.valueOf(durationValue));
                //Log.d("durationValue log", String.valueOf(durationValue));

                //Get lat and lng of destination
                JSONObject endLocation  = legsArr.getJSONObject(legsArr.length() - 1).getJSONObject("end_location");
                this.lat = endLocation.getDouble("lat");
                this.lng = endLocation.getDouble("lng");
                //Log.d("lat log", String.valueOf(lat));
                // Log.d("lng log", String.valueOf(lng));


                return encodedPolyline;

            } catch (final JSONException e) {
                Log.e("Json error", "Json parsing error: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Called after background task is done, draws polyline on map and starts arrival timer
     *
     * @param encodedPolyline takes in the encoded polyline string from finished doInBackground()
     *                        method
     */
    @Override
    protected void onPostExecute(String encodedPolyline) {

        //draw route polyline on map
        List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);
        this.googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        //create geofence at destination
        this.addGeofence(200);

        //Create new circle at destination coordinates
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat,lng))
                .radius(200); // In meters

        //Draw circle on map
        Circle circle = this.googleMap.addCircle(circleOptions);

        //Start timer
        startTimer(durationValue*1000);

        //TODO: this is useless get rid?
        IntentFilter filter = new IntentFilter();
    }


    /**
     * Returns the lat and lng of destination
     *
     * @return the LatLng object of destination
     */
    public LatLng getLatLng() {
        return new LatLng(lat,lng);
    }

    /**
     * Create a geofence that detects when the user has arrived at the destination
     *
     * @param radius the size of the geofence in meters
     */
    private void addGeofence(float radius) {
        //Log.d("PARSER GEOFENCE", "trying to add geofence ");

        //build the geofence object
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, new LatLng(lat,lng), radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        //register the geofence if location permissions are granted
        //otherwise log the error
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Geofence success", "Geofence added");
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

    /**
     * Begins the countdown timer to countdown until the user is estimated to arrive at their
     * destination. Updates the durationView text box each second with the updated time left
     *
     * @param millis the estimated time to arrive at destination in milliseconds
     */
    private void startTimer(int millis) {
        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                durationView.setText(String.valueOf(millisUntilFinished/1000));
            }

            public void onFinish() {
                durationView.setText("YOU DIDNT MAKE IT >:(");
            }
        }.start();

    }

    /**
     * stops the countdown timer to when the user is supposed to arrive at the destination, meant to
     * be called when the destination geofence is triggered before countdown reaches 0.
     */
    public void stopTimer() {
        countDownTimer.cancel();
    }


}
