package com.example.googlemapsnavbar3;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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

    private GeofenceHelper geofenceHelper;
    private double lat;
    private double lng;
    
    private PlaceList checkpoints;

    public Parser(GoogleMap googleMap, TextView durationText, String origin, String destination, PlaceList checkpoints) {
        this.googleMap = googleMap;
        this.durationView = durationText;
        this.origin = origin;
        this.destination = destination;
        this.checkpoints = checkpoints;
    }

    public Parser(GoogleMap googleMap, TextView durationText, String origin, String destination) {
        this.googleMap = googleMap;
        this.durationView = durationText;
        this.origin = origin;
        this.destination = destination;
    }

    //doInBackground turns JSON string into JSON objects and array
    //in order to extract relevant data, in this case it extracts
    //encoded polyline string and returns it
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
                //Turn the string into one JSON object
                JSONObject jsonObj = new JSONObject(jsonString);

                //Get the JSON array containing data such as the route legs, summary and polyline_overview
                JSONArray routes = jsonObj.getJSONArray("routes");

                //from the routes array, extract the overview_polyline object
                JSONObject overviewPolylineObj = routes.getJSONObject(0).getJSONObject("overview_polyline");

                //store the encoded_polyline value in a string
                String encodedPolyline = overviewPolylineObj.getString("points");

                //get duration of route, display it on map text view
                JSONArray legsObj = routes.getJSONObject(0).getJSONArray("legs");
                Log.d("legsObj log",legsObj.getString(0));
                JSONObject duration  = legsObj.getJSONObject(0).getJSONObject("duration");
                Log.d("duration log",duration.toString());
                int durationValue = duration.getInt("value");
                Log.d("durationValue log", String.valueOf(durationValue));
                this.durationView.setText(String.valueOf(durationValue));

                //TODO: get latlng of destination and create geofence around it
                JSONObject endLocation  = legsObj.getJSONObject(0).getJSONObject("end_location");
                this.lat = endLocation.getDouble("lat");
                this.lng = endLocation.getDouble("lng");
                Log.d("lat log", String.valueOf(lat));
                Log.d("lng log", String.valueOf(lng));


                return encodedPolyline;

            } catch (final JSONException e) {
                Log.e("Json error", "Json parsing error: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String encodedPolyline) {
        List<LatLng> decodedPath = PolyUtil.decode(encodedPolyline);
        this.googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));

    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public LatLng getLatLng() {
        return new LatLng(lat,lng);
    }




}
