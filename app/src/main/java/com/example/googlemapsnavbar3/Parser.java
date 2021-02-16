package com.example.googlemapsnavbar3;

import android.os.AsyncTask;
import android.util.Log;

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

    public Parser(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    //doInBackground turns JSON string into JSON objects and array
    //in order to extract relevant data, in this case it extracts
    //encoded polyline string and returns it
    @Override
    protected String doInBackground(Void...arg0) {
        //Create instance of httpHandler and call its getJSON() method
        HttpHandler httpHandler = new HttpHandler();
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




}
