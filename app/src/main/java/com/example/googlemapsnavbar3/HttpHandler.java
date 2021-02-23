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

    public HttpHandler() {
        this.origin = "Cardiff+Castle";
        this.destination = "Cardiff+Bay";
    }

    public HttpHandler(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    //Make http connection to direction API and retrieve JSON data in string form
    public String getJSON() {
        //generate api url
        //TODO: create separate function to generate api URL based on user input
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?";
        String ori = "origin=" + this.origin;
        String dest = "&destination=" + this.destination;
        String key = "&key=!!!!!!!!!!!ENTER_API_KEY_HERE!!!!!!!!!!";
        String mode = "&mode=walking";
        urlString = urlString + ori + dest + mode + key;

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
}
