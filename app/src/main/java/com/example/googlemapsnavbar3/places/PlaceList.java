package com.example.googlemapsnavbar3.places;


import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PlaceList implements Iterable<Place> {

    private ArrayList<Place> places;
    private int pointer = 0;

    public PlaceList(){
        this.places = new ArrayList<>();
    }

    public PlaceList(ArrayList<Place> locations){
        this.places = locations;
    }

    public PlaceList(List<LatLng> latLngList){
        this.places = new ArrayList<>();
        for (LatLng latLng: latLngList){
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            this.places.add(new Place(latitude, longitude));
        }
    }

    public Place getCurrentCheckpoint(){
        Place place = places.get(pointer);
        return place;
    }

    /**
     * Returns the time to the next checkpoint
     * @return
     */
    public int timeToNextCheckpoint(){
        Place current = places.get(pointer);
        Place next = places.get(pointer + 1);

        Double distance = current.distanceFromPlace(next);
        //in kilometers

        //Average walking speed is 5 km/h but we'll assume 3.5 to be safe
        int time = (int) Math.floor(distance/3.5);
        time = time * 3600;
        return time;
    }

    public void goTonextCheckpoint(){
        pointer++;
    }

    public void add(Place location){
        places.add(location);
    }

    /**
     * Returns an iterator over elements of type place.
     * @return an Iterator.
     */
    @NonNull
    @Override
    public Iterator<Place> iterator() {
        return places.iterator();
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
    public void sortByDistanceFromLocation(Place location){
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
    public void sortByDistanceFromVector(Place start, Place end){
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
