package com.example.googlemapsnavbar3;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    private String origin;
    private String destination;
    private PlaceList places;

    public HttpHandler() {
    }

    /**
     * Takes in an origin and destination location in string form and calls teh the directions API
     * using those as parameters
     *
     * @param origin
     * @param destination
     * @param places checkpoints
     */
    public HttpHandler(String origin, String destination, PlaceList places) {
        this.origin = origin;
        this.destination = destination;
        this.places = places;
    }

    /**
     * Make http connection to direction API and retrieve JSON data in string form
     *
     * @return JSON data in string form
     */
    public String getJSON() {
        //generate api url
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?";
        String ori = "origin=" + this.origin;
        String dest = "&destination=" + this.destination;
        String checks = places.toApiString();
        String key = "AIzaSyAdyk3GTqps6e2APdTjsS1CDe8kjcBbn1k";
        String mode = "&mode=walking";
        urlString = urlString + ori + dest + checks + mode + key;

        Log.d("url",urlString); //debug msg to show url

        try {
            //turn string url into URL object
            URL url = new URL(urlString);
            //open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //store the direction API response stream
            InputStream inputStream = con.getInputStream();

            //turn the response stream into a string
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            //disconnect the http connection
            con.disconnect();

            //return the string (json)
            return sb.toString();

        } catch (Exception e) { //catch and display any errors
            Log.d("exception",e.toString());
        }
        return null; //return null if connection couldn't be made
    }

    /**
     * <p>Calls the OpenStreetMap API and saves the output to a string<b>Please don't spam this coz the API owners will get mad</b></p>
     * @param city The name of the city
     * @return     A string containg the XML from the API call.
     */
    public String getLocations(String city){
        String baseUrl = "https://overpass-api.de/api/interpreter?data=";
        String areaUrl = "area[name=" + city + "];";
        String tagUrl = "nwr[lit=yes][foot=yes](area);out center;";

        String finalUrl = baseUrl + areaUrl + tagUrl;

        try {
            //turn string url into URL object
            URL url = new URL(finalUrl);
            //open a connection to the URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //store the direction API response stream
            InputStream inputStream = con.getInputStream();

            //turn the response stream into a string
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            //disconnect the http connection
            con.disconnect();

            //return the string (json)
            return sb.toString();

        } catch (Exception e) { //catch and display any errors
            Log.d("exception",e.toString());
        }
        return null; //return null if connection couldn't be made
    }

}
