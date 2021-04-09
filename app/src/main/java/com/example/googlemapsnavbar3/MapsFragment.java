package com.example.googlemapsnavbar3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private int FINE_LOCATION_ACCESS_CODE = 10001;
    private LatLng current_latLng;
    private FusedLocationProviderClient fusedLocationClient;
    private Parser parser;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            //get move camera to cardiff / add marker
            LatLng cardiff = new LatLng(51.4822, -3.1812);
            //googleMap.addMarker(new MarkerOptions().position(cardiff).title("Marker in Cardiff"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cardiff, 16));

            //Get timer text box
            TextView tview = getView().getRootView().findViewById(R.id.durationText);
            tview.setText("");

            //Get destination edit field
            EditText destinationEditText = getView().getRootView().findViewById(R.id.editTextDestination);

            updateCurrentLocation();
            enableUserLocation();


            //Add search button functionality
            Button searchButton = getView().getRootView().findViewById(R.id.buttonSearch);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    updateCurrentLocation();
                    getBackgroundPermission();
                    Log.d("HALLO ", "onClick: ");

                    String startLocation = current_latLng.latitude + "," + current_latLng.longitude;
                    String destination = (String) destinationEditText.getText().toString();

                    boolean fetched = false;
                    PlaceList checkpoints = null;

                    try {
                        File file = getContext().getFileStreamPath("safePlaces.txt");
                        if (file.exists()){
                            PlaceList places = PlaceFileHandler.loadPlacesFromFile("safePlaces.txt", getContext());
                            Place loc1 = Place.stringToPlace(destination, getContext());
                            Place loc2 = Place.stringToPlace("Cardiff+Castle", getContext());
                            places.sortByDistanceFromVector(loc1,loc2);
                            checkpoints =  places.getUpToNthLocation(3);
                            fetched = true;
                        }
                        else{
                            PlaceFileHandler.savePlacesToFile("safePlaces.txt", getContext());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        Parser parser;
                        if (fetched){
                            parser = new Parser(googleMap, tview, startLocation, destination, getContext(),checkpoints);
                        }
                        else{
                            parser = new Parser(googleMap, tview, startLocation, destination,getContext(), new PlaceList());
                        }
                        parser.execute();
                    }
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize fusedlocationclient which is used for getting current user location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //register the receiver which receives from geofenceBroadcastReceiver
        getActivity().registerReceiver(mReceiver, new IntentFilter("GEOFENCE_TRIGGER"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    /**
     *adds button to locate current location and move cam to it
     */
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
            }
        }
    }

    /**
     * Ask the user for background permission
     */
    private void getBackgroundPermission() {
        if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            return; }
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.common_google_play_services_enable_title)
                .setMessage(R.string.app_name)
                .setPositiveButton(R.string.common_signin_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // this request will take user to Application's Setting page
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2000);
                    }
                })
                .setNegativeButton(R.string.common_open_on_phone, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Check if a specific permission has been given by the user
     *
     * @param permission the permission to check
     * @return true if permission given
     */
    private boolean checkSinglePermission(String permission) {
        return ContextCompat.checkSelfPermission(this.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Updates current location on map, kinda only works on initial start up
     */
    private void updateCurrentLocation() {

        if(checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null) {
                                current_latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                //Log.d("GET CURRENT LOCATION", "onSuccess: latLng " + current_latLng.toString());
                            } else {
                                //Log.d("CURRENT LOCATION NULL", "CURRENT LOCATION IS NULL ");
                            }

                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("CURRENT LOCATION ERROR", "onFailure: " + e.getLocalizedMessage());
                        }
                    });
        }
    }



    /**
     * This receives a broadcast any time a geofence is triggered
     * then calls parser's stop timer to stop arrival timer
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("broadcast receiver frag"," onRecieve"); //do something with intent
            parser.stopTimer();
        }
    };
}