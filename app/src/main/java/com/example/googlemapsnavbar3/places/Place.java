package com.example.googlemapsnavbar3.places;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Place {

    Location location = new Location("");

    public Place(double latitude, double longitude){
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
    }

    public Place(Location location){
        this.location = location;
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public LatLng toLatLng(){
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    /**
     * <p>Creates a Place object from a string</p>
     * @param name The name of the place
     * @param context The context of the application
     * @return The Place object
     * @throws IOException
     */
    public static Place stringToPlace(String name, Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = geocoder.getFromLocationName(name, 1);

        double lat = addressList.get(0).getLatitude();
        double lon = addressList.get(0).getLongitude();

        Place newPlace = new Place(lat, lon);
        return newPlace;
    }

    /**
     * <p>Gets the distance to another place. Uses the haversine formuala</p>
     * @param destination The place to find the distance to
     * @return         The distance to the place
     */
    public double distanceFromPlace(Place destination){
        //Haversine Formula https://en.wikipedia.org/wiki/Haversine_formula

        double radius = 6371;
        //Earth's approx radius in KM

        double latDiff = Math.toRadians(this.getLatitude() - destination.getLatitude());
        double lonDiff = Math.toRadians(this.getLongitude() - destination.getLongitude());

        double newLat = Math.toRadians(destination.getLatitude());
        double oldLat = Math.toRadians(this.getLatitude());

        double a = Math.pow(Math.sin(latDiff/2),2) +
                Math.cos(newLat)*Math.cos(oldLat)* Math.pow(Math.sin(lonDiff/2),2);
        //a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)

        double b = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //b = 2.atan2(√a, √(1−a))
        return radius * b;
    }

    /**
     * <p>The Spherical Mercator Projection. This treats the earth as a sphere (it's actually an ellipsoid)</p>
     * <p>It maps longitude and latitude onto a 2D plane</p>
     * @return A double[] containing the X,Y coordinates of the projection.
     */
    public double[] mercatorProjection(){
        //MAKE THIS PRIVATE ON RELEASE
        double radius = 6371;
        //Earth's approx radius in KM

        double[] coords = new double[2];

        double x = Math.toRadians(getLatitude()) * radius;
        double y = Math.log(Math.tan(Math.PI / 4 + Math.toRadians(getLongitude() / 2))) * radius;
        //ln(tan(pi/4 + rad(lat/2))*r

        coords[0] = x;
        coords[1] = y;

        return coords;
    }

    /**
     * <p>Gets the distance from a vector defined by 2 places</p>
     * @param start The start place
     * @param end   The end place
     * @return      The distance from the vector
     */
    public double distanceFromVector(Place start, Place end){
        double[] v1 = new double[2];
        double[] v2 = new double[2];

        double[] p0 = this.mercatorProjection();
        double[] p1 = start.mercatorProjection();
        double[] p2 = end.mercatorProjection();

        // Move Vectors to the origin
        v1[0] = p0[0] - p1[0];
        v1[1] = p0[1] - p1[1];

        v2[0] = p2[0] - p1[0];
        v2[1] = p2[1] - p1[1];

        double dotProd = (v1[0] * v2[0]) + (v1[1] * v2[1]);
        double v2Norm = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));
        double proportion = dotProd/Math.pow(v2Norm, 2);
        // dot(v1,v2)/(norm(v2)^2) finds the projection of the point onto the vector.
        // More specifically, the proportion of vector on which the projection occurs.


        double[] v3 = new double[2];
        //if the point is before the line
        if (proportion > 0){
            v3[0] = p0[0] - p1[0];
            v3[1] = p0[1] - p1[1];
        }
        //if the point is after the line
        else if (proportion < 1){
            v3[0] = p0[0] - p2[0];
            v3[1] = p0[1] - p2[1];
        }
        //if the point is on the line
        else{
            v3[0] = p0[0] - (p1[0] + proportion * v1[0]);
            v3[1] = p0[0] - (p1[1] + proportion * v1[1]);
        }

        return Math.sqrt(Math.pow(v3[0],2) + Math.pow(v3[1], 2));
    }
}
