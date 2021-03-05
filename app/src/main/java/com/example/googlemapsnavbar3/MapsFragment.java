package com.example.googlemapsnavbar3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private int FINE_LOCATION_ACCESS_CODE = 10001;
    private double dest_lat;
    private double dest_lng;
    private LatLng dest_latLng;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "DEST_GEOFENCE";

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng cardiff = new LatLng(51.4822, -3.1812);
            googleMap.addMarker(new MarkerOptions().position(cardiff).title("Marker in Cardiff"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cardiff, 16));

            TextView tview = getView().getRootView().findViewById(R.id.durationText);
            tview.setText("Duration in seconds");

            EditText destinationEditText = getView().getRootView().findViewById(R.id.editTextDestination);


            Button searchButton = getView().getRootView().findViewById(R.id.buttonSearch);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {

                    boolean fetched = false;
                    PlaceList checkpoints = null;
                    try {
                        File file = getContext().getFileStreamPath("safePlaces.txt");
                        if (file.exists()){
                            PlaceList places = PlaceFileHandler.loadPlacesFromFile("safePlaces.txt", getContext());
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
                        String destination = (String) destinationEditText.getText().toString();
                        Parser parser;
                        if (fetched){
                            parser = new Parser(googleMap, tview, "Cardiff+Castle", destination, checkpoints);
                        }
                        else{
                            parser = new Parser(googleMap, tview, "Cardiff+Castle", destination, new PlaceList());
                        }
                        parser.execute();
                        dest_latLng = parser.getLatLng();
                        addGeofence(dest_latLng,GEOFENCE_RADIUS);
                    }
                }
            });
            enableUserLocation(googleMap);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeofenceHelper(getContext());
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

    private void enableUserLocation(GoogleMap mMap) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show dialog for why permission is needed, then ask for permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //have permission

            } else {
                //no permission
            }

        }
    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
}