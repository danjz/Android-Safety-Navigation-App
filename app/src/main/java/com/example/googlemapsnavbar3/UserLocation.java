package com.example.googlemapsnavbar3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
    private GeoPoint mUserLocation;
    private @ServerTimestamp Date timestamp;
    private User user;

    public UserLocation(GeoPoint mUserLocation, Date timestamp, User user) {
        this.mUserLocation = mUserLocation;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UserLocation() {

    }

    public GeoPoint getmUserLocation() {
        return mUserLocation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setmUserLocation(GeoPoint mUserLocation) {
        this.mUserLocation = mUserLocation;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
