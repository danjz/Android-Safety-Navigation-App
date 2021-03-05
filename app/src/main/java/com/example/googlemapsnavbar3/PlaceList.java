package com.example.googlemapsnavbar3;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaceList {

    private ArrayList<Place> places;

    public PlaceList(){
        this.places = new ArrayList<>();
    }

    public PlaceList(ArrayList<Place> locations){
        this.places = locations;
    }

    public void add(Place location){
        places.add(location);
    }

    public ArrayList<Place> getLocations(){
        return places;
    }

    /**
     * <p>Gets the top N places from a PlaceList</p>
     * @param n The number of places
     * @return An arrayList of places
     * @author Ricky Chu
     */
    public PlaceList getUpToNthLocation(int n){
        ArrayList<Place> newPlaces = new ArrayList<>();

        for (int i = 0; i < n; i++){
            newPlaces.add(this.places.get(i));
        }

        PlaceList newPlaceList = new PlaceList(newPlaces);
        return newPlaceList;
    }

    /**
     * <p>Sorts the list of locations by the distance from another location</p>
     * @param location The location from which the 'sorting by distance' occurs
     * @author Ricky Chu
     */
    private void sortByDistanceFromLocation(Place location){
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place l1, Place l2) {
                double dist1 = l1.distanceFromPlace(location);
                double dist2 = l2.distanceFromPlace(location);
                return Double.compare(dist1, dist2);
            }
        });
    }

    /**
     * <p>Sorts the list of places by the distance from 2 other locations</p>
     * @param start The start places of the route
     * @param end   The end places of the route
     * @author Ricky Chu
     */
    private void sortByDistanceFromVector(Place start, Place end){
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place l1, Place l2) {
                double dist1 = l1.distanceFromVector(start,end);
                double dist2 = l2.distanceFromVector(start,end);
                return Double.compare(dist1, dist2);
            }
        });
    }

    /**
     * <p>Converts a PlaceList to a string to be added to a google maps api call</p>
     * @return The string
     * @author Ricky Chu
     */
    public String toApiString(){
        String output = "&waypoints=";
        for (Place place: places){
            String lat = Double.toString(place.getLatitude());
            String lon = Double.toString(place.getLongitude());
            output = output + lat + "%2C";
            output = output + lon + "%7C";
        }
        return output;
    }
}
